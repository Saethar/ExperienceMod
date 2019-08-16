package com.Saethar.Experience.items;

import com.Saethar.Experience.Main;
import com.Saethar.Experience.init.ModItems;
import com.Saethar.Experience.util.IHasModel;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBase extends Item implements IHasModel{

	public ItemBase(String name) 
	{
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.MATERIALS);
		
		ModItems.ITEMS.add(this);
	}
	@Override
	public void registerModel()
	{
		Main.proxy.registerItemRenderer(this, 0, "inventory");
	}

}
