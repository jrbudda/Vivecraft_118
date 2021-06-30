package org.vivecraft.gui.settings;

import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.Color;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.optifine.Config;
import org.vivecraft.gui.framework.GuiVROptionButton;
import org.vivecraft.gui.framework.GuiVROptionsBase;
import org.vivecraft.settings.VRHotkeys;
import org.vivecraft.settings.VRSettings;

public class GuiRenderOpticsSettings extends GuiVROptionsBase
{
    static VRSettings.VrOptions[] monoDisplayOptions = new VRSettings.VrOptions[] {VRSettings.VrOptions.MONO_FOV, VRSettings.VrOptions.DUMMY, VRSettings.VrOptions.FSAA};
    static VRSettings.VrOptions[] openVRDisplayOptions = new VRSettings.VrOptions[] {VRSettings.VrOptions.RENDER_SCALEFACTOR, VRSettings.VrOptions.MIRROR_DISPLAY, VRSettings.VrOptions.FSAA, VRSettings.VrOptions.STENCIL_ON, VRSettings.VrOptions.HANDHELD_CAMERA_RENDER_SCALE, VRSettings.VrOptions.HANDHELD_CAMERA_FOV, VRSettings.VrOptions.RELOAD_EXTERNAL_CAMERA, VRSettings.VrOptions.MIRROR_EYE};
    static VRSettings.VrOptions[] MROptions = new VRSettings.VrOptions[] {VRSettings.VrOptions.MIXED_REALITY_UNITY_LIKE, VRSettings.VrOptions.MIXED_REALITY_RENDER_HANDS, VRSettings.VrOptions.MIXED_REALITY_KEY_COLOR, VRSettings.VrOptions.MIXED_REALITY_FOV, VRSettings.VrOptions.MIXED_REALITY_UNDISTORTED, VRSettings.VrOptions.MONO_FOV, VRSettings.VrOptions.MIXED_REALITY_ALPHA_MASK, VRSettings.VrOptions.MIXED_REALITY_RENDER_CAMERA_MODEL};
    static VRSettings.VrOptions[] UDOptions = new VRSettings.VrOptions[] {VRSettings.VrOptions.MONO_FOV};
    static VRSettings.VrOptions[] TUDOptions = new VRSettings.VrOptions[] {VRSettings.VrOptions.MIXED_REALITY_FOV, VRSettings.VrOptions.MIXED_REALITY_RENDER_CAMERA_MODEL};
    private float prevRenderScaleFactor = this.settings.renderScaleFactor;
    private float prevHandCameraResScale = this.settings.handCameraResScale;

    public GuiRenderOpticsSettings(Screen par1Screen)
    {
        super(par1Screen);
    }

    public void init()
    {
        this.vrTitle = "vivecraft.options.screen.stereorendering";
        VRSettings.VrOptions[] avrsettings$vroptions = new VRSettings.VrOptions[openVRDisplayOptions.length];
        System.arraycopy(openVRDisplayOptions, 0, avrsettings$vroptions, 0, openVRDisplayOptions.length);

        for (int i = 0; i < avrsettings$vroptions.length; ++i)
        {
            VRSettings.VrOptions vrsettings$vroptions = avrsettings$vroptions[i];

            if (vrsettings$vroptions == VRSettings.VrOptions.RELOAD_EXTERNAL_CAMERA && (!VRHotkeys.hasExternalCameraConfig() || this.minecraft.vrSettings.displayMirrorMode != 15 && this.minecraft.vrSettings.displayMirrorMode != 14))
            {
                avrsettings$vroptions[i] = VRSettings.VrOptions.DUMMY;
            }

            if (vrsettings$vroptions == VRSettings.VrOptions.MIRROR_EYE && this.minecraft.vrSettings.displayMirrorMode != 16 && this.minecraft.vrSettings.displayMirrorMode != 12)
            {
                avrsettings$vroptions[i] = VRSettings.VrOptions.DUMMY;
            }
        }

        super.init(avrsettings$vroptions, true);

        if (this.minecraft.vrSettings.displayMirrorMode == 15)
        {
            avrsettings$vroptions = new VRSettings.VrOptions[MROptions.length];
            System.arraycopy(MROptions, 0, avrsettings$vroptions, 0, MROptions.length);

            for (int j = 0; j < avrsettings$vroptions.length; ++j)
            {
                VRSettings.VrOptions vrsettings$vroptions1 = avrsettings$vroptions[j];

                if (vrsettings$vroptions1 == VRSettings.VrOptions.MONO_FOV && (!this.minecraft.vrSettings.mixedRealityMRPlusUndistorted || !this.minecraft.vrSettings.mixedRealityUnityLike))
                {
                    avrsettings$vroptions[j] = VRSettings.VrOptions.DUMMY;
                }

                if (vrsettings$vroptions1 == VRSettings.VrOptions.MIXED_REALITY_ALPHA_MASK && !this.minecraft.vrSettings.mixedRealityUnityLike)
                {
                    avrsettings$vroptions[j] = VRSettings.VrOptions.DUMMY;
                }

                if (vrsettings$vroptions1 == VRSettings.VrOptions.MIXED_REALITY_UNDISTORTED && !this.minecraft.vrSettings.mixedRealityUnityLike)
                {
                    avrsettings$vroptions[j] = VRSettings.VrOptions.DUMMY;
                }

                if (vrsettings$vroptions1 == VRSettings.VrOptions.MIXED_REALITY_KEY_COLOR && this.minecraft.vrSettings.mixedRealityAlphaMask && this.minecraft.vrSettings.mixedRealityUnityLike)
                {
                    avrsettings$vroptions[j] = VRSettings.VrOptions.DUMMY;
                }
            }

            super.init(avrsettings$vroptions, false);
        }
        else if (this.minecraft.vrSettings.displayMirrorMode == 13)
        {
            super.init(UDOptions, false);
        }
        else if (this.minecraft.vrSettings.displayMirrorMode == 14)
        {
            super.init(TUDOptions, false);
        }

        super.addDefaultButtons();
        this.buttons.stream().filter((w) ->
        {
            return w instanceof GuiVROptionButton;
        }).forEach((w) ->
        {
            GuiVROptionButton guivroptionbutton = (GuiVROptionButton)w;

            if (guivroptionbutton.getOption() == VRSettings.VrOptions.HANDHELD_CAMERA_RENDER_SCALE && Config.isShaders())
            {
                guivroptionbutton.active = false;
            }
        });
    }

    public void render(PoseStack p_96562_, int p_96563_, int p_96564_, float p_96565_)
    {
        super.render(p_96562_, p_96563_, p_96564_, p_96565_);
    }

    protected void loadDefaults()
    {
        this.settings.renderScaleFactor = 1.0F;
        this.settings.displayMirrorMode = 16;
        this.settings.displayMirrorLeftEye = false;
        this.settings.mixedRealityKeyColor = new Color(0, 0, 0);
        this.settings.mixedRealityRenderHands = false;
        this.settings.insideBlockSolidColor = false;
        this.settings.mixedRealityUnityLike = true;
        this.settings.mixedRealityMRPlusUndistorted = true;
        this.settings.mixedRealityAlphaMask = false;
        this.settings.mixedRealityFov = 40.0F;
        this.settings.mixedRealityRenderCameraModel = true;
        this.minecraft.options.fov = 70.0D;
        this.settings.handCameraFov = 70.0F;
        this.settings.handCameraResScale = 1.0F;
        this.settings.useFsaa = true;
        this.settings.vrUseStencil = true;
        this.minecraft.vrRenderer.reinitFrameBuffers("Defaults Loaded");
    }

    protected void actionPerformed(AbstractWidget widget)
    {
        if (widget instanceof GuiVROptionButton)
        {
            GuiVROptionButton guivroptionbutton = (GuiVROptionButton)widget;

            if (guivroptionbutton.id == VRSettings.VrOptions.MIRROR_DISPLAY.ordinal() || guivroptionbutton.id == VRSettings.VrOptions.FSAA.ordinal())
            {
                this.minecraft.vrRenderer.reinitFrameBuffers("Render Setting Changed");
            }
        }
    }

    public boolean mouseReleased(double p_94753_, double p_94754_, int p_94755_)
    {
        if (this.settings.renderScaleFactor != this.prevRenderScaleFactor || this.settings.handCameraResScale != this.prevHandCameraResScale)
        {
            this.prevRenderScaleFactor = this.settings.renderScaleFactor;
            this.prevHandCameraResScale = this.settings.handCameraResScale;
            this.minecraft.vrRenderer.reinitFrameBuffers("Render Setting Changed");
        }

        return super.mouseReleased(p_94753_, p_94754_, p_94755_);
    }
}
