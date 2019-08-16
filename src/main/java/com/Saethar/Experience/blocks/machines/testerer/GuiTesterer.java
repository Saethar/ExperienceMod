package com.Saethar.Experience.blocks.machines.testerer;

import com.Saethar.Experience.util.Reference;
import com.Saethar.Experience.blocks.machines.testerer.ContainerTesterer;
import com.Saethar.Experience.blocks.machines.testerer.TileEntityTesterer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiTesterer extends GuiContainer
{
	private static final ResourceLocation TEXTURES = new ResourceLocation(Reference.MOD_ID + ":textures/gui/gui_testerer.png");
	private final InventoryPlayer player;
	private final TileEntityTesterer tileentity;
	
	public GuiTesterer(InventoryPlayer player, TileEntityTesterer tileentity)
	{
		super(new ContainerTesterer(player, tileentity));
		this.player = player;
		this.tileentity = tileentity;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String tileName = this.tileentity.getDisplayName().getUnformattedText();
		this.fontRenderer.drawString(tileName, (this.xSize / 2 - this.fontRenderer.getStringWidth(tileName) / 2), 8, 4210752);
		this.fontRenderer.drawString(this.player.getDisplayName().getUnformattedText(), 122, this.ySize - 96 + 2, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.getTextureManager().bindTexture(TEXTURES);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
		
		if(TileEntityTesterer.isTempering(tileentity))
		{
			int k = this.getBurnLeftScaled(13);
			this.drawTexturedModalRect(this.guiLeft + 45, this.guiTop + 46 + 12 - k, 176, 12 - k, 14, k + 1);
			int h = this.getCoolLeftScaled(13);
			this.drawTexturedModalRect(this.guiLeft + 81, this.guiTop + 46 + 12 - h, 176, 12 - h, 14, h + 1);
		}
		
		int l = this.getProgressScaled(24);
		this.drawTexturedModalRect(this.guiLeft + 90, this.guiTop + 35, 176, 14, l + 1, 16);
	}
	
	private int getBurnLeftScaled(int pixels)
	{
		int i = this.tileentity.getField(2);
		if(i == 0) i = 200;
		return this.tileentity.getField(0) * pixels / 1;
	}
	
	private int getCoolLeftScaled(int pixels)
	{
		int i = this.tileentity.getField(1);
		if(i == 0) i = 200;
		return this.tileentity.getField(3) * pixels / 1;
	}
	
	private int getProgressScaled(int pixels)
	{
		int i = this.tileentity.getField(4);
		int j = this.tileentity.getField(5);
		return j != 0 && i != 0 ? i * pixels / j : 0;
	}
}
