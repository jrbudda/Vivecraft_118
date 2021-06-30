package org.vivecraft.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.optifine.model.QuadBounds;
import org.vivecraft.gameplay.trackers.CameraTracker;
import org.vivecraft.settings.VRHotkeys;
import org.vivecraft.utils.Utils;

public class VRWidgetHelper
{
    private static final Random random = new Random();
    public static boolean debug = false;

    public static void renderVRThirdPersonCamWidget()
    {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.vrSettings.mixedRealityRenderCameraModel)
        {
            if ((minecraft.currentPass == RenderPass.LEFT || minecraft.currentPass == RenderPass.RIGHT) && (minecraft.vrSettings.displayMirrorMode == 15 || minecraft.vrSettings.displayMirrorMode == 14))
            {
                float f = 0.35F;

                if (minecraft.interactTracker.isInCamera() && !VRHotkeys.isMovingThirdPersonCam())
                {
                    f *= 1.03F;
                }

                renderVRCameraWidget(-0.748F, -0.438F, -0.06F, f, RenderPass.THIRD, GameRenderer.thirdPersonCameraModel, GameRenderer.thirdPersonCameraDisplayModel, () ->
                {
                    minecraft.vrRenderer.framebufferMR.bindRead();
                }, (face) ->
                {
                    if (face == Direction.NORTH)
                    {
                        return VRWidgetHelper.DisplayFace.MIRROR;
                    }
                    else {
                        return face == Direction.SOUTH ? VRWidgetHelper.DisplayFace.NORMAL : VRWidgetHelper.DisplayFace.NONE;
                    }
                });
            }
        }
    }

    public static void renderVRHandheldCameraWidget()
    {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.currentPass != RenderPass.CAMERA && minecraft.cameraTracker.isVisible())
        {
            float f = 0.25F;

            if (minecraft.interactTracker.isInHandheldCamera() && !minecraft.cameraTracker.isMoving())
            {
                f *= 1.03F;
            }

            renderVRCameraWidget(-0.5F, -0.25F, -0.22F, f, RenderPass.CAMERA, CameraTracker.cameraModel, CameraTracker.cameraDisplayModel, () ->
            {
                if (minecraft.getItemInHandRenderer().getNearOpaqueBlock(minecraft.vrPlayer.vrdata_world_render.getEye(RenderPass.CAMERA).getPosition(), (double)minecraft.gameRenderer.minClipDistance) == null)
                {
                    minecraft.vrRenderer.cameraFramebuffer.bindRead();
                }
                else {
                    minecraft.getTextureManager().bind(new ResourceLocation("vivecraft:textures/black.png"));
                }
            }, (face) ->
            {
                return face == Direction.SOUTH ? VRWidgetHelper.DisplayFace.NORMAL : VRWidgetHelper.DisplayFace.NONE;
            });
        }
    }

    public static void renderVRCameraWidget(float offsetX, float offsetY, float offsetZ, float scale, RenderPass renderPass, ModelResourceLocation model, ModelResourceLocation displayModel, Runnable displayBindFunc, Function<Direction, VRWidgetHelper.DisplayFace> displayFaceFunc)
    {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.pushMatrix();
        minecraft.gameRenderer.applyVRModelViewLegacy(minecraft.currentPass);
        Vec3 vec3 = minecraft.vrPlayer.vrdata_world_render.getEye(renderPass).getPosition();
        Vec3 vec31 = minecraft.vrPlayer.vrdata_world_render.getEye(minecraft.currentPass).getPosition();
        Vec3 vec32 = vec3.subtract(vec31);
        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.translated(vec32.x, vec32.y, vec32.z);
        RenderSystem.multMatrix(minecraft.vrPlayer.vrdata_world_render.getEye(renderPass).getMatrix().toMCMatrix());
        scale = scale * minecraft.vrPlayer.vrdata_world_render.worldScale;
        RenderSystem.scalef(scale, scale, scale);

        if (debug)
        {
            RenderSystem.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
            minecraft.gameRenderer.renderDebugAxes(0, 0, 0, 0.08F);
            RenderSystem.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
        }

        RenderSystem.translatef(offsetX, offsetY, offsetZ);
        minecraft.getTextureManager().bind(InventoryMenu.BLOCK_ATLAS);
        BlockPos blockpos = new BlockPos(minecraft.vrPlayer.vrdata_world_render.getEye(renderPass).getPosition());
        int i = Utils.getCombinedLightWithMin(minecraft.level, blockpos, 0);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(7, DefaultVertexFormat.BLOCK);
        minecraft.getBlockRenderer().getModelRenderer().renderModel((new PoseStack()).last(), bufferbuilder, (BlockState)null, minecraft.getModelManager().getModel(model), 1.0F, 1.0F, 1.0F, i, OverlayTexture.NO_OVERLAY);
        tesselator.end();
        RenderSystem.disableBlend();
        RenderSystem.alphaFunc(519, 0.0F);
        displayBindFunc.run();
        BufferBuilder bufferbuilder1 = tesselator.getBuilder();
        bufferbuilder1.begin(7, DefaultVertexFormat.POSITION_TEX_LMAP_COLOR_NORMAL);

        for (BakedQuad bakedquad : minecraft.getModelManager().getModel(displayModel).getQuads((BlockState)null, (Direction)null, random))
        {
            if (displayFaceFunc.apply(bakedquad.getDirection()) != VRWidgetHelper.DisplayFace.NONE && bakedquad.a().getName().equals(new ResourceLocation("vivecraft:transparent")))
            {
                QuadBounds quadbounds = bakedquad.getQuadBounds();
                boolean flag = displayFaceFunc.apply(bakedquad.getDirection()) == VRWidgetHelper.DisplayFace.MIRROR;
                int j = LightTexture.pack(15, 15);
                bufferbuilder1.vertex(flag ? (double)quadbounds.getMaxX() : (double)quadbounds.getMinX(), (double)quadbounds.getMinY(), (double)quadbounds.getMinZ()).uv(flag ? 1.0F : 0.0F, 0.0F).uv2(j).color(1.0F, 1.0F, 1.0F, 1.0F).normal(0.0F, 0.0F, flag ? -1.0F : 1.0F).endVertex();
                bufferbuilder1.vertex(flag ? (double)quadbounds.getMinX() : (double)quadbounds.getMaxX(), (double)quadbounds.getMinY(), (double)quadbounds.getMinZ()).uv(flag ? 0.0F : 1.0F, 0.0F).uv2(j).color(1.0F, 1.0F, 1.0F, 1.0F).normal(0.0F, 0.0F, flag ? -1.0F : 1.0F).endVertex();
                bufferbuilder1.vertex(flag ? (double)quadbounds.getMinX() : (double)quadbounds.getMaxX(), (double)quadbounds.getMaxY(), (double)quadbounds.getMinZ()).uv(flag ? 0.0F : 1.0F, 1.0F).uv2(j).color(1.0F, 1.0F, 1.0F, 1.0F).normal(0.0F, 0.0F, flag ? -1.0F : 1.0F).endVertex();
                bufferbuilder1.vertex(flag ? (double)quadbounds.getMaxX() : (double)quadbounds.getMinX(), (double)quadbounds.getMaxY(), (double)quadbounds.getMinZ()).uv(flag ? 1.0F : 0.0F, 1.0F).uv2(j).color(1.0F, 1.0F, 1.0F, 1.0F).normal(0.0F, 0.0F, flag ? -1.0F : 1.0F).endVertex();
            }
        }

        tesselator.end();
        RenderSystem.enableBlend();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.popMatrix();
    }

    public static enum DisplayFace
    {
        NONE,
        NORMAL,
        MIRROR;
    }
}
