package org.vivecraft.gui.framework;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import org.vivecraft.settings.VRSettings;

public class GuiVROptionSlider extends GuiVROptionButton
{
    private double sliderValue = 1.0D;
    public boolean dragging;
    private final double minValue;
    private final double maxValue;

    public GuiVROptionSlider(int id, int x, int y, int width, int height, VRSettings.VrOptions option, double min, double max)
    {
        super(id, x, y, width, height, option, "", (p) ->
        {
        });
        this.minValue = min;
        this.maxValue = max;
        Minecraft minecraft = Minecraft.getInstance();
        this.sliderValue = this.enumOptions.normalizeValue(minecraft.vrSettings.getOptionFloatValue(this.enumOptions));
        this.setMessage(new TextComponent(minecraft.vrSettings.getButtonDisplayString(this.enumOptions)));
    }

    public GuiVROptionSlider(int id, int x, int y, VRSettings.VrOptions option, double min, double max)
    {
        this(id, x, y, 150, 20, option, min, max);
    }

    protected int getHoverState(boolean mouseOver)
    {
        return 0;
    }

    protected void onDrag(double p_93636_, double pMouseX, double p_93638_, double pMouseY)
    {
        this.setValueFromMouse(p_93636_);
        super.onDrag(p_93636_, pMouseX, p_93638_, pMouseY);
    }

    private void setValueFromMouse(double p_setValueFromMouse_1_)
    {
        Minecraft minecraft = Minecraft.getInstance();
        this.sliderValue = (double)((float)(p_setValueFromMouse_1_ - (double)(this.x + 4)) / (float)(this.width - 8));
        this.sliderValue = Mth.clamp(this.sliderValue, 0.0D, 1.0D);
        double d0 = this.enumOptions.denormalizeValue((float)this.sliderValue);
        minecraft.vrSettings.setOptionFloatValue(this.enumOptions, (float)d0);
        this.sliderValue = this.enumOptions.normalizeValue((float)d0);
        this.setMessage(new TextComponent(minecraft.vrSettings.getButtonDisplayString(this.enumOptions)));
    }

    protected void renderBg(PoseStack p_93661_, Minecraft pMatrixStack, int pMinecraft, int pMouseX)
    {
        if (this.visible)
        {
            pMatrixStack.getTextureManager().bindForSetup(WIDGETS_LOCATION);
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int i = (this.isHovered() ? 2 : 1) * 20;
            this.blit(p_93661_, this.x + (int)(this.sliderValue * (double)(this.width - 8)), this.y, 0, 46 + i, 4, 20);
            this.blit(p_93661_, this.x + (int)(this.sliderValue * (double)(this.width - 8)) + 4, this.y, 196, 46 + i, 4, 20);
        }
    }

    public void onClick(double p_93634_, double pMouseX)
    {
        this.sliderValue = (p_93634_ - (double)(this.x + 4)) / (double)(this.width - 8);
        this.sliderValue = Mth.clamp(this.sliderValue, 0.0D, 1.0D);
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.vrSettings.setOptionFloatValue(this.enumOptions, (float)this.enumOptions.denormalizeValue((float)this.sliderValue));
        this.setMessage(new TextComponent(minecraft.vrSettings.getButtonDisplayString(this.enumOptions)));
        this.dragging = true;
    }

    protected int getYImage(boolean p_93668_)
    {
        return 0;
    }

    public void onRelease(double p_93669_, double pMouseX)
    {
        this.dragging = false;
    }
}
