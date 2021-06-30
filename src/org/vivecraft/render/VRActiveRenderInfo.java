package org.vivecraft.render;

import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import net.optifine.shaders.Shaders;
import org.vivecraft.api.VRData;

public class VRActiveRenderInfo extends Camera
{
    public void setup(BlockGetter p_90576_, Entity p_90577_, boolean p_90578_, boolean p_90579_, float p_90580_)
    {
        this.initialized = true;
        this.level = p_90576_;
        this.entity = p_90577_;
        Minecraft minecraft = Minecraft.getInstance();
        RenderPass renderpass = minecraft.currentPass;

        if (Shaders.isShadowPass && renderpass != RenderPass.THIRD && renderpass != RenderPass.CAMERA)
        {
            renderpass = RenderPass.CENTER;
        }

        VRData.VRDevicePose vrdata$vrdevicepose = minecraft.vrPlayer.vrdata_world_render.getEye(renderpass);
        this.setPosition(vrdata$vrdevicepose.getPosition());
        this.xRot = -vrdata$vrdevicepose.getPitch();
        this.yRot = vrdata$vrdevicepose.getYaw();
        this.forwards.set((float)vrdata$vrdevicepose.getDirection().x, (float)vrdata$vrdevicepose.getDirection().y, (float)vrdata$vrdevicepose.getDirection().z);
        Vec3 vec3 = vrdata$vrdevicepose.getCustomVector(new Vec3(0.0D, 1.0D, 0.0D));
        this.up.set((float)vec3.x, (float)vec3.y, (float)vec3.z);
        vrdata$vrdevicepose.getCustomVector(new Vec3(1.0D, 0.0D, 0.0D));
        this.left.set((float)vec3.x, (float)vec3.y, (float)vec3.z);
        this.rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
        this.rotation.mul(Vector3f.YP.rotationDegrees(-this.yRot));
        this.rotation.mul(Vector3f.XP.rotationDegrees(this.xRot));
    }

    public void tick()
    {
    }

    public boolean isDetached()
    {
        return false;
    }
}
