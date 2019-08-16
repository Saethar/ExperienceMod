package com.Saethar.Experience.util.handlers;

import com.Saethar.Experience.blocks.machines.testerer.TileEntityTesterer;
import com.Saethar.Experience.util.Reference;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TileEntityHandler 
{
	public static void registerTileEntities()
	{
		GameRegistry.registerTileEntity(TileEntityTesterer.class, new ResourceLocation(Reference.MOD_ID + ":testerer"));
	}
}
