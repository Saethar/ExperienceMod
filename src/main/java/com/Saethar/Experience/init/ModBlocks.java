package com.Saethar.Experience.init;

import java.util.ArrayList;
import java.util.List;

import com.Saethar.Experience.blocks.BlockBase;
import com.Saethar.Experience.blocks.machines.BlockTemperer;
import com.Saethar.Experience.blocks.machines.testerer.BlockTesterer;
import com.Saethar.Experience.items.ItemBase;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class ModBlocks 
{

	public static final List<Block> BLOCKS = new ArrayList<Block>();
	
	//Blocks
	public static final Block RESILIENT_BRICK_BLOCK = new BlockBase("resilient_brick_block", Material.ROCK);
	
	//Machines
	public static final Block TEMPERER = new BlockTemperer("temperer");
	public static final Block TESTERER = new BlockTesterer("testerer");
}
