package com.Saethar.Experience.util.handlers;

import com.Saethar.Experience.blocks.machines.testerer.ContainerTesterer;
import com.Saethar.Experience.blocks.machines.testerer.GuiTesterer;
import com.Saethar.Experience.blocks.machines.testerer.TileEntityTesterer;
import com.Saethar.Experience.util.Reference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == Reference.GUI_TESTERER) return new ContainerTesterer(player.inventory, (TileEntityTesterer)world.getTileEntity(new BlockPos(x,y,z)));
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if(ID == Reference.GUI_TESTERER) return new GuiTesterer(player.inventory, (TileEntityTesterer)world.getTileEntity(new BlockPos(x,y,z)));
		return null;
	}
}
