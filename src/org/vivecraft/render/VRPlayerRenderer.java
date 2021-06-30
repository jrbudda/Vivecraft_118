package org.vivecraft.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class VRPlayerRenderer extends LivingEntityRenderer<AbstractClientPlayer, VRPlayerModel<AbstractClientPlayer>>
{
    public VRPlayerRenderer(EntityRenderDispatcher p_i1296_1_, boolean p_i1296_2_, boolean seated)
    {
        super(p_i1296_1_, new VRPlayerModel<>(0.0F, p_i1296_2_, seated), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel(0.5F), new HumanoidModel(1.0F)));
        this.addLayer(new ItemInHandLayer<>(this));
        this.addLayer(new ArrowLayer<>(this));
        this.addLayer(new VRCapeLayer(this));
        this.addLayer(new CustomHeadLayer<>(this));
        this.addLayer(new ElytraLayer<>(this));
        this.addLayer(new ParrotOnShoulderLayer<>(this));
        this.addLayer(new SpinAttackEffectLayer<>(this));
        this.addLayer(new BeeStingerLayer<>(this));
    }

    public void render(AbstractClientPlayer entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn)
    {
        if (Minecraft.getInstance().currentPass == RenderPass.GUI && entityIn.isLocalPlayer())
        {
            Matrix4f matrix4f = matrixStackIn.last().pose();
            double d0 = (new Vec3((double)matrix4f.m00, (double)matrix4f.m01, (double)matrix4f.m02)).length();
            matrixStackIn.last().pose().setIdentity();
            matrixStackIn.translate(0.0D, 0.0D, 1000.0D);
            matrixStackIn.scale((float)d0, (float)d0, (float)d0);
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F + Minecraft.getInstance().vrPlayer.vrdata_world_pre.getBodyYaw()));
        }

        PlayerModelController.RotInfo playermodelcontroller$rotinfo = PlayerModelController.getInstance().getRotationsForPlayer(entityIn.getUUID());

        if (playermodelcontroller$rotinfo != null)
        {
            matrixStackIn.scale(playermodelcontroller$rotinfo.heightScale, playermodelcontroller$rotinfo.heightScale, playermodelcontroller$rotinfo.heightScale);
            this.setModelVisibilities(entityIn);
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.scale(1.0F, 1.0F / playermodelcontroller$rotinfo.heightScale, 1.0F);
        }
    }

    public Vec3 getRenderOffset(AbstractClientPlayer entityIn, float partialTicks)
    {
        return entityIn.isVisuallySwimming() ? new Vec3(0.0D, -0.125D, 0.0D) : Vec3.ZERO;
    }

    private void setModelVisibilities(AbstractClientPlayer clientPlayer)
    {
        VRPlayerModel<AbstractClientPlayer> vrplayermodel = this.getModel();

        if (clientPlayer.isSpectator())
        {
            vrplayermodel.setAllVisible(false);
            vrplayermodel.head.visible = true;
            vrplayermodel.hat.visible = true;
        }
        else
        {
            vrplayermodel.setAllVisible(true);
            vrplayermodel.hat.visible = clientPlayer.isModelPartShown(PlayerModelPart.HAT);
            vrplayermodel.jacket.visible = clientPlayer.isModelPartShown(PlayerModelPart.JACKET);
            vrplayermodel.leftPants.visible = clientPlayer.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            vrplayermodel.rightPants.visible = clientPlayer.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            vrplayermodel.leftSleeve.visible = clientPlayer.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            vrplayermodel.rightSleeve.visible = clientPlayer.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            vrplayermodel.crouching = clientPlayer.isCrouching() && !clientPlayer.isVisuallySwimming();
            HumanoidModel.ArmPose humanoidmodel$armpose = func_241741_a_(clientPlayer, InteractionHand.MAIN_HAND);
            HumanoidModel.ArmPose humanoidmodel$armpose1 = func_241741_a_(clientPlayer, InteractionHand.OFF_HAND);

            if (humanoidmodel$armpose.isTwoHanded())
            {
                humanoidmodel$armpose1 = clientPlayer.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
            }

            if (clientPlayer.getMainArm() == HumanoidArm.RIGHT)
            {
                vrplayermodel.rightArmPose = humanoidmodel$armpose;
                vrplayermodel.leftArmPose = humanoidmodel$armpose1;
            }
            else
            {
                vrplayermodel.rightArmPose = humanoidmodel$armpose1;
                vrplayermodel.leftArmPose = humanoidmodel$armpose;
            }
        }
    }

    private static HumanoidModel.ArmPose func_241741_a_(AbstractClientPlayer p_241741_0_, InteractionHand p_241741_1_)
    {
        ItemStack itemstack = p_241741_0_.getItemInHand(p_241741_1_);

        if (itemstack.isEmpty())
        {
            return HumanoidModel.ArmPose.EMPTY;
        }
        else
        {
            if (p_241741_0_.getUsedItemHand() == p_241741_1_ && p_241741_0_.getUseItemRemainingTicks() > 0)
            {
                UseAnim useanim = itemstack.getUseAnimation();

                if (useanim == UseAnim.BLOCK)
                {
                    return HumanoidModel.ArmPose.BLOCK;
                }

                if (useanim == UseAnim.BOW)
                {
                    return HumanoidModel.ArmPose.BOW_AND_ARROW;
                }

                if (useanim == UseAnim.SPEAR)
                {
                    return HumanoidModel.ArmPose.THROW_SPEAR;
                }

                if (useanim == UseAnim.CROSSBOW && p_241741_1_ == p_241741_0_.getUsedItemHand())
                {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }
            }
            else if (!p_241741_0_.swinging && itemstack.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemstack))
            {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }

            return HumanoidModel.ArmPose.ITEM;
        }
    }

    public ResourceLocation getEntityTexture(AbstractClientPlayer entity)
    {
        return entity.getSkinTextureLocation();
    }

    protected void preRenderCallback(AbstractClientPlayer entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime)
    {
        float f = 0.9375F;
        matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
    }

    protected void renderName(AbstractClientPlayer entityIn, Component displayNameIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn)
    {
        double d0 = this.entityRenderDispatcher.distanceToSqr(entityIn);
        matrixStackIn.pushPose();

        if (d0 < 100.0D)
        {
            Scoreboard scoreboard = entityIn.getScoreboard();
            Objective objective = scoreboard.getDisplayObjective(2);

            if (objective != null)
            {
                Score score = scoreboard.getOrCreatePlayerScore(entityIn.getScoreboardName(), objective);
                super.renderNameTag(entityIn, (new TextComponent(Integer.toString(score.getScore()))).append(" ").append(objective.getDisplayName()), matrixStackIn, bufferIn, packedLightIn);
                matrixStackIn.translate(0.0D, (double)0.25875F, 0.0D);
            }
        }

        super.renderNameTag(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.popPose();
    }

    public void renderRightArm(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, AbstractClientPlayer playerIn)
    {
        this.renderItem(matrixStackIn, bufferIn, combinedLightIn, playerIn, (this.model).rightArm, (this.model).rightSleeve);
    }

    public void renderLeftArm(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, AbstractClientPlayer playerIn)
    {
        this.renderItem(matrixStackIn, bufferIn, combinedLightIn, playerIn, (this.model).leftArm, (this.model).leftSleeve);
    }

    private void renderItem(PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, AbstractClientPlayer playerIn, ModelPart rendererArmIn, ModelPart rendererArmwearIn)
    {
        VRPlayerModel<AbstractClientPlayer> vrplayermodel = this.getModel();
        this.setModelVisibilities(playerIn);
        vrplayermodel.attackTime = 0.0F;
        vrplayermodel.crouching = false;
        vrplayermodel.swimAmount = 0.0F;
        vrplayermodel.setupAnim(playerIn, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        rendererArmIn.xRot = 0.0F;
        rendererArmIn.render(matrixStackIn, bufferIn.getBuffer(RenderType.entitySolid(playerIn.getSkinTextureLocation())), combinedLightIn, OverlayTexture.NO_OVERLAY);
        rendererArmwearIn.xRot = 0.0F;
        rendererArmwearIn.render(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(playerIn.getSkinTextureLocation())), combinedLightIn, OverlayTexture.NO_OVERLAY);
    }

    protected void applyRotations(AbstractClientPlayer entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks)
    {
        if (this.getModel() instanceof VRPlayerModel && entityLiving instanceof Player)
        {
            UUID uuid = entityLiving.getUUID();
            VRPlayerModel vrplayermodel = this.getModel();
            double d4 = entityLiving.xOld + (entityLiving.getX() - entityLiving.xOld) * (double)partialTicks;
            d4 = entityLiving.yOld + (entityLiving.getY() - entityLiving.yOld) * (double)partialTicks;
            d4 = entityLiving.zOld + (entityLiving.getZ() - entityLiving.zOld) * (double)partialTicks;

            if (PlayerModelController.getInstance().isTracked(uuid))
            {
                PlayerModelController.RotInfo playermodelcontroller$rotinfo = PlayerModelController.getInstance().getRotationsForPlayer(uuid);
                rotationYaw = (float)Math.toDegrees(playermodelcontroller$rotinfo.getBodyYawRadians());
            }
        }

        float f2 = entityLiving.yRot;
        float f3 = entityLiving.getSwimAmount(partialTicks);

        if (entityLiving.isFallFlying())
        {
            super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
            float f = (float)entityLiving.getFallFlyingTicks() + partialTicks;
            float f1 = Mth.clamp(f * f / 100.0F, 0.0F, 1.0F);

            if (!entityLiving.isAutoSpinAttack())
            {
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(f1 * (-90.0F - entityLiving.xRot)));
            }

            Vec3 vec3 = entityLiving.getViewVector(partialTicks);
            Vec3 vec31 = entityLiving.getDeltaMovement();
            double d0 = Entity.getHorizontalDistanceSqr(vec31);
            double d3 = Entity.getHorizontalDistanceSqr(vec3);

            if (d0 > 0.0D && d3 > 0.0D)
            {
                double d1 = (vec31.x * vec3.x + vec31.z * vec3.z) / (Math.sqrt(d0) * Math.sqrt(d3));
                double d2 = vec31.x * vec3.z - vec31.z * vec3.x;
                matrixStackIn.mulPose(Vector3f.YP.rotation((float)(Math.signum(d2) * Math.acos(d1))));
            }
        }
        else if (f3 > 0.0F)
        {
            super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
            float f4 = entityLiving.isInWater() ? -90.0F - entityLiving.xRot : -90.0F;
            float f5 = Mth.lerp(f3, 0.0F, f4);
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(f5));

            if (entityLiving.isVisuallySwimming())
            {
                matrixStackIn.translate(0.0D, -1.0D, (double)0.3F);
            }
        }
        else
        {
            super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        }
    }
}
