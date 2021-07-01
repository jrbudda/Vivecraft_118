package org.vivecraft.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;

import org.lwjgl.opengl.GL43;
import org.vivecraft.gameplay.trackers.SwingTracker;
import org.vivecraft.provider.ControllerType;
import org.vivecraft.reflection.MCReflection;

public class VRArmRenderer extends PlayerRenderer
{
    public VRArmRenderer(EntityRendererProvider.Context p_117733_, boolean p_117734_)
    {
        super(p_117733_, p_117734_);
    }

    public void renderRightHand(PoseStack p_117771_, MultiBufferSource p_117772_, int p_117773_, AbstractClientPlayer p_117774_)
    {
        this.renderItem(ControllerType.RIGHT, p_117771_, p_117772_, p_117773_, p_117774_, (this.model).rightArm, (this.model).rightSleeve);
    }

    public void renderLeftHand(PoseStack p_117814_, MultiBufferSource p_117815_, int p_117816_, AbstractClientPlayer p_117817_)
    {
        this.renderItem(ControllerType.LEFT, p_117814_, p_117815_, p_117816_, p_117817_, (this.model).leftArm, (this.model).leftSleeve);
    }

    private void renderItem(ControllerType side, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, AbstractClientPlayer playerIn, ModelPart rendererArmIn, ModelPart rendererArmwearIn)
    {
        PlayerModel<AbstractClientPlayer> playermodel = this.getModel();
        MCReflection.RenderPlayer_setModelVisibilities.invoke(this, playerIn);
        GlStateManager._enableBlend();
        GlStateManager._enableCull();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        playermodel.attackTime = 0.0F;
        playermodel.crouching = false;
        playermodel.swimAmount = 0.0F;
        rendererArmIn.xRot = 0.0F;
        playermodel.leftSleeve.copyFrom(playermodel.leftArm);
        playermodel.rightSleeve.copyFrom(playermodel.rightArm);
        float f = SwingTracker.getItemFade((LocalPlayer)playerIn, ItemStack.EMPTY);
        rendererArmIn.render(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(playerIn.getSkinTextureLocation())), combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, f);
        rendererArmwearIn.xRot = 0.0F;
        rendererArmwearIn.render(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(playerIn.getSkinTextureLocation())), combinedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, f);
        GL43.glTexEnvi(8960, 8704, 8448);
        GL43.glTexEnvi(8960, 34163, 1);
        GlStateManager._disableBlend();
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
