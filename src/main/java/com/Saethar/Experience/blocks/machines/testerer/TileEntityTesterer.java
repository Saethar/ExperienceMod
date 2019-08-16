package com.Saethar.Experience.blocks.machines.testerer;

import com.Saethar.Experience.blocks.machines.testerer.BlockTesterer;
import com.Saethar.Experience.blocks.recipes.TestererRecipes;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileEntityTesterer extends TileEntity implements ITickable
{
	private ItemStackHandler handler = new ItemStackHandler(4);
	private String customName;
	private ItemStack smelting = ItemStack.EMPTY;
	
	private int coolTime;
	private int currentCoolTime;
	private int burnTime;
	private int currentBurnTime;
	private int cookTime;
	private int totalCookTime = 200;

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) 
	{
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
		else return false;
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) 
	{
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) this.handler;
		return super.getCapability(capability, facing);
	}
	
	public boolean hasCustomName() 
	{
		return this.customName != null && !this.customName.isEmpty();
	}
	
	public void setCustomName(String customName) 
	{
		this.customName = customName;
	}
	
	@Override
	public ITextComponent getDisplayName() 
	{
		return this.hasCustomName() ? new TextComponentString(this.customName) : new TextComponentTranslation("container.temperer");
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		this.handler.deserializeNBT(compound.getCompoundTag("Inventory"));
		this.coolTime = compound.getInteger("CoolTime");
		this.burnTime = compound.getInteger("BurnTime");
		this.cookTime = compound.getInteger("CookTime");
		this.totalCookTime = compound.getInteger("CookTimeTotal");
		this.currentBurnTime = getItemBurnTime((ItemStack)this.handler.getStackInSlot(2));
		
		if(compound.hasKey("CustomName", 8)) this.setCustomName(compound.getString("CustomName"));
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) 
	{
		super.writeToNBT(compound);
		compound.setInteger("CoolTime", (short)this.coolTime);
		compound.setInteger("BurnTime", (short)this.burnTime);
		compound.setInteger("CookTime", (short)this.cookTime);
		compound.setInteger("CookTimeTotal", (short)this.totalCookTime);
		compound.setTag("Inventory", this.handler.serializeNBT());
		
		if(this.hasCustomName()) compound.setString("CustomName", this.customName);
		return compound;
	}
	//Beginning of Burning
	public boolean isTempering() 
	{
		return this.burnTime > 0 || this.coolTime > 0;
	}
	
	
	@SideOnly(Side.CLIENT)
	public static boolean isTempering(TileEntityTesterer te) 
	{
		return te.getField(0) > 0;
	}
	
	public void update() 
	{	
		if(this.isTempering())
		{
			--this.burnTime;
			--this.coolTime;
			BlockTesterer.setState(true, world, pos);
		}
		
		ItemStack inputs = this.handler.getStackInSlot(0);
		ItemStack fuel0 = this.handler.getStackInSlot(2);
		ItemStack fuel1 = this.handler.getStackInSlot(1);
		
		if(this.isTempering() || !fuel1.isEmpty() && !this.handler.getStackInSlot(0).isEmpty() || this.handler.getStackInSlot(1).isEmpty())
		{
			if(!this.isTempering() && this.canSmelt())
			{
				this.burnTime = getItemBurnTime(fuel0);
				this.currentBurnTime = burnTime;
				this.coolTime = getItemCoolTime(fuel1);
				this.currentCoolTime = coolTime;
				
				if(this.isTempering() && !fuel1.isEmpty())
				{
					Item item = fuel1.getItem();
					fuel1.shrink(1);
					
					if(fuel1.isEmpty())
					{
						ItemStack item1 = item.getContainerItem(fuel1);
						this.handler.setStackInSlot(2, item1);
					}
				}
				
				if(this.isTempering() && !fuel0.isEmpty())
				{
					Item item = fuel0.getItem();
					fuel0.shrink(1);
					
					if(fuel0.isEmpty())
					{
						ItemStack item1 = item.getContainerItem(fuel0);
						this.handler.setStackInSlot(2, item1);
					}
				}
			}
		}
		
		if(this.isTempering() && this.canSmelt() && cookTime > 0)
		{
			cookTime++;
			if(cookTime == totalCookTime)
			{
				if(handler.getStackInSlot(3).getCount() > 0)
				{
					handler.getStackInSlot(3).grow(1);
				}
				else
				{
					handler.insertItem(3, smelting, false);
				}
				
				smelting = ItemStack.EMPTY;
				cookTime = 0;
				return;
			}
		}
		else
		{
			if(this.canSmelt() && this.isTempering())
			{
				ItemStack output = TestererRecipes.getInstance().getTemperResult(inputs);
				if(!output.isEmpty())
				{
					smelting = output;
					cookTime++;
					inputs.shrink(1);
					handler.setStackInSlot(0, inputs);
				}
			}
		}
	}
	
	private boolean canSmelt() 
	{
		if(((ItemStack)this.handler.getStackInSlot(0)).isEmpty()) return false;
		else 
		{
			ItemStack result = TestererRecipes.getInstance().getTemperResult((ItemStack)this.handler.getStackInSlot(0));	
			if(result.isEmpty()) return false;
			else
			{
				ItemStack output = (ItemStack)this.handler.getStackInSlot(3);
				if(output.isEmpty()) return true;
				if(!output.isItemEqual(result)) return false;
				int res = output.getCount() + result.getCount();
				return res <= 64 && res <= output.getMaxStackSize();
			}
		}
	}
	
	public static int getItemBurnTime(ItemStack fuel0) 
	{
		if(fuel0.isEmpty()) return 0;
		else 
		{
			Item item = fuel0.getItem();

			if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.AIR) 
			{
				Block block = Block.getBlockFromItem(item);

				if (block == Blocks.WOODEN_SLAB) return 150;
				if (block.getDefaultState().getMaterial() == Material.WOOD) return 300;
				if (block == Blocks.COAL_BLOCK) return 16000;
			}

			if (item instanceof ItemTool && "WOOD".equals(((ItemTool)item).getToolMaterialName())) return 200;
			if (item instanceof ItemSword && "WOOD".equals(((ItemSword)item).getToolMaterialName())) return 200;
			if (item instanceof ItemHoe && "WOOD".equals(((ItemHoe)item).getMaterialName())) return 200;
			if (item == Items.STICK) return 100;
			if (item == Items.COAL) return 1600;
			if (item == Items.LAVA_BUCKET) return 20000;
			if (item == Item.getItemFromBlock(Blocks.SAPLING)) return 100;
			if (item == Items.BLAZE_ROD) return 2400;

			return GameRegistry.getFuelValue(fuel0);
		}
	}
		
	public static boolean isItemBurn(ItemStack fuel0)
	{
		return getItemBurnTime(fuel0) > 0;
	}
	public static int getItemCoolTime(ItemStack fuel1) 
	{
		if(fuel1.isEmpty()) return 0;
		else 
		{
			Item item = fuel1.getItem();

			if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.AIR) 
			{
				Block block = Block.getBlockFromItem(item);

				if (block == Blocks.ICE) return 1600;
				if (block == Blocks.SNOW) return 600;
				if (block == Blocks.PACKED_ICE) return 16000;
			}

			if (item == Items.SNOWBALL) return 150;

			return GameRegistry.getFuelValue(fuel1);
		}
	}
		
	public static boolean isItemCool(ItemStack fuel1)
	{
		return getItemCoolTime(fuel1) > 0;
	}
	//End of Burning Part
	public boolean isUsableByPlayer(EntityPlayer player) 
	{
		return this.world.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
	}

	public int getField(int id) 
	{
		switch(id) 
		{
		case 0:
			return this.burnTime;
		case 1:
			return this.coolTime;
		case 2:
			return this.currentBurnTime;
		case 3:
			return this.currentCoolTime;
		case 4:
			return this.cookTime;
		case 5:
			return this.totalCookTime;
		default:
			return 0;
		}
	}

	public void setField(int id, int value) 
	{
		switch(id) 
		{
		case 0:
			this.burnTime = value;
			break;
		case 1:
			this.coolTime = value;
			break;
		case 2:
			this.currentBurnTime = value;
			break;
		case 3:
			this.currentCoolTime = value;
			break;
		case 4:
			this.cookTime = value;
			break;
		case 5:
			this.totalCookTime = value;
		}
	}
}
