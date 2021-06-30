package org.vivecraft.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.optifine.Config;

public class VRCapeLayer extends RenderLayer<AbstractClientPlayer, VRPlayerModel<AbstractClientPlayer>>
{
    public VRCapeLayer(RenderLayerParent<AbstractClientPlayer, VRPlayerModel<AbstractClientPlayer>> p_117346_)
    {
        super(p_117346_);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if (entitylivingbaseIn.isCapeLoaded() && !entitylivingbaseIn.isInvisible() && entitylivingbaseIn.isModelPartShown(PlayerModelPart.CAPE) && entitylivingbaseIn.getCloakTextureLocation() != null)
        {
            ItemStack itemstack = entitylivingbaseIn.getItemBySlot(EquipmentSlot.CHEST);

            if (itemstack.getItem() != Items.ELYTRA)
            {
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.0D, 0.0D, 0.125D);
                double d0 = Mth.lerp((double)partialTicks, entitylivingbaseIn.xCloakO, entitylivingbaseIn.xCloak) - Mth.lerp((double)partialTicks, entitylivingbaseIn.xo, entitylivingbaseIn.getX());
                double d1 = Mth.lerp((double)partialTicks, entitylivingbaseIn.yCloakO, entitylivingbaseIn.yCloak) - Mth.lerp((double)partialTicks, entitylivingbaseIn.yo, entitylivingbaseIn.getY());
                double d2 = Mth.lerp((double)partialTicks, entitylivingbaseIn.zCloakO, entitylivingbaseIn.zCloak) - Mth.lerp((double)partialTicks, entitylivingbaseIn.zo, entitylivingbaseIn.getZ());
                float f = entitylivingbaseIn.yBodyRotO + (entitylivingbaseIn.yBodyRot - entitylivingbaseIn.yBodyRotO);
                double d3 = (double)Mth.sin(f * ((float)Math.PI / 180F));
                double d4 = (double)(-Mth.cos(f * ((float)Math.PI / 180F)));
                float f1 = (float)d1 * 10.0F;
                f1 = Mth.clamp(f1, -6.0F, 32.0F);
                float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
                f2 = Mth.clamp(f2, 0.0F, 150.0F);
                float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
                f3 = Mth.clamp(f3, -20.0F, 20.0F);

                if (f2 < 0.0F)
                {
                    f2 = 0.0F;
                }

                if (f2 > 165.0F)
                {
                    f2 = 165.0F;
                }

                if (f1 < -5.0F)
                {
                    f1 = -5.0F;
                }

                float f4 = Mth.lerp(partialTicks, entitylivingbaseIn.oBob, entitylivingbaseIn.bob);
                f1 = f1 + Mth.sin(Mth.lerp(partialTicks, entitylivingbaseIn.walkDistO, entitylivingbaseIn.walkDist) * 6.0F) * 32.0F * f4;

                if (entitylivingbaseIn.isCrouching())
                {
                    f1 += 25.0F;
                }

                float f5 = Config.getAverageFrameTimeSec() * 20.0F;
                f5 = Config.limit(f5, 0.02F, 1.0F);
                entitylivingbaseIn.capeRotateX = Mth.lerp(f5, entitylivingbaseIn.capeRotateX, 6.0F + f2 / 2.0F + f1);
                entitylivingbaseIn.capeRotateZ = Mth.lerp(f5, entitylivingbaseIn.capeRotateZ, f3 / 2.0F);
                entitylivingbaseIn.capeRotateY = Mth.lerp(f5, entitylivingbaseIn.capeRotateY, 180.0F - f3 / 2.0F);
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(entitylivingbaseIn.capeRotateX));
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(entitylivingbaseIn.capeRotateZ));
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(entitylivingbaseIn.capeRotateY));
                VertexConsumer vertexconsumer = bufferIn.getBuffer(RenderType.entitySolid(entitylivingbaseIn.getCloakTextureLocation()));
                this.getParentModel().renderCloak(matrixStackIn, vertexconsumer, packedLightIn, OverlayTexture.NO_OVERLAY);
                matrixStackIn.popPose();
            }
        }
    }
}
