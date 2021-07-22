package org.vivecraft.gameplay.trackers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.api.VRData;
import org.vivecraft.render.RenderPass;

public class TelescopeTracker extends Tracker
{
    public static final ResourceLocation scopeResource = new ResourceLocation("vivecraft:trashbin");
    public static final ModelResourceLocation scopeModel = new ModelResourceLocation("vivecraft:trashbin");
    private static final double lensDistMax = 0.05D;
    private static final double lensDistMin = 0.185D;
    private static final double lensDotMax = 0.9D;
    private static final double lensDotMin = 0.75D;

    public TelescopeTracker(Minecraft mc)
    {
        super(mc);
    }

    public boolean isActive(LocalPlayer player)
    {
        return false;
    }

    public void doProcess(LocalPlayer player)
    {
    }

    public static boolean isTelescope(ItemStack i)
    {
        if (i.isEmpty())
        {
            return false;
        }
        else if (!i.hasCustomHoverName())
        {
            return false;
        }
        else if (i.getItem() != Items.ENDER_EYE)
        {
            return false;
        }
        else if (!i.getTag().getBoolean("Unbreakable"))
        {
            return false;
        }
        else
        {
            return i.getHoverName() instanceof TranslatableComponent && ((TranslatableComponent)i.getHoverName()).getKey().equals("vivecraft.item.telescope") || i.getHoverName().getString().equals("Eye of the Farseer");
        }
    }

    public static Vec3 getLensOrigin(int controller)
    {
        VRData.VRDevicePose vrdata$vrdevicepose = Minecraft.getInstance().vrPlayer.vrdata_world_pre.getController(controller);
        return vrdata$vrdevicepose.getPosition().add(getViewVector(controller).scale(-0.2D).add(vrdata$vrdevicepose.getDirection().scale((double)0.05F)));
    }

    public static Vec3 getViewVector(int controller)
    {
        return Minecraft.getInstance().vrPlayer.vrdata_world_pre.getController(controller).getCustomVector(new Vec3(0.0D, -1.0D, 0.0D));
    }

    public static boolean isViewing(int controller)
    {
        return viewPercent(controller) > 0.0F;
    }

    public static float viewPercent(int controller)
    {
        float f = 0.0F;

        for (int i = 0; i < 2; ++i)
        {
            float f1 = viewPercent(controller, i);

            if (f1 > f)
            {
                f = f1;
            }
        }

        return f;
    }

    private static float viewPercent(int controller, int e)
    {
        float f = 0.0F;

        if (e == -1)
        {
            return 0.0F;
        }
        else
        {
            VRData.VRDevicePose vrdata$vrdevicepose = Minecraft.getInstance().vrPlayer.vrdata_world_pre.getEye(RenderPass.values()[e]);
            double d0 = vrdata$vrdevicepose.getPosition().subtract(getLensOrigin(controller)).length();
            Vec3 vec3 = vrdata$vrdevicepose.getDirection();
            double d1 = Math.abs(vec3.dot(getViewVector(controller)));
            double d2 = 0.0D;
            double d3 = 0.0D;

            if (d1 > 0.75D)
            {
                if (d1 > 0.9D)
                {
                    d2 = 1.0D;
                }
                else
                {
                    d2 = (d1 - 0.75D) / 0.15000000000000002D;
                }
            }

            if (d0 < 0.185D)
            {
                if (d0 < 0.05D)
                {
                    d3 = 1.0D;
                }
                else
                {
                    d3 = 1.0D - (d0 - 0.05D) / 0.135D;
                }
            }

            return (float)Math.min(d2, d3);
        }
    }
}
