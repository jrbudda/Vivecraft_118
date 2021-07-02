package org.vivecraft.render;

import java.util.UUID;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5EarsLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ParrotOnShoulderLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class VRPlayerRenderer extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>>
{
    public VRPlayerRenderer(EntityRendererProvider.Context p_174557_, boolean p_174558_, boolean seated)
    {
        super(p_174557_, seated ? 
        		new VRPlayerModel<AbstractClientPlayer>(p_174557_.bakeLayer(p_174558_ ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), p_174558_) : 
        			new VRPlayerModel_WithArms<AbstractClientPlayer>(p_174557_.bakeLayer(p_174558_ ? ModelLayers.PLAYER_SLIM : ModelLayers.PLAYER), p_174558_), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel(p_174557_.bakeLayer(p_174558_ ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidModel(p_174557_.bakeLayer(p_174558_ ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR))));
        this.addLayer(new PlayerItemInHandLayer<>(this));
        this.addLayer(new ArrowLayer<>(p_174557_, this));
        this.addLayer(new Deadmau5EarsLayer(this));
        this.addLayer(new CapeLayer(this));
        this.addLayer(new CustomHeadLayer<>(this, p_174557_.getModelSet()));
        this.addLayer(new ElytraLayer<>(this, p_174557_.getModelSet()));
        this.addLayer(new ParrotOnShoulderLayer<>(this, p_174557_.getModelSet()));
        this.addLayer(new SpinAttackEffectLayer<>(this, p_174557_.getModelSet()));
        this.addLayer(new BeeStingerLayer<>(this));
    }

    public void render(AbstractClientPlayer entityIn, float pEntity, float pEntityYaw, PoseStack matrixStackIn, MultiBufferSource pMatrixStack, int pBuffer)
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
            this.setModelProperties(entityIn);
            super.render(entityIn, pEntity, pEntityYaw, matrixStackIn, pMatrixStack, pBuffer);
            matrixStackIn.scale(1.0F, 1.0F / playermodelcontroller$rotinfo.heightScale, 1.0F);
        }
    }

    public Vec3 getRenderOffset(AbstractClientPlayer p_117785_, float pEntity)
    {
    	//idk why we do this anymore
        return p_117785_.isVisuallySwimming() ? new Vec3(0.0D, -0.125D, 0.0D) : Vec3.ZERO;
       // return p_117785_.isCrouching() ? new Vec3(0.0D, -0.125D, 0.0D) : super.getRenderOffset(p_117785_, pEntity);
    }

    private void setModelProperties(AbstractClientPlayer p_117819_)
    {
        VRPlayerModel<AbstractClientPlayer> playermodel = (VRPlayerModel<AbstractClientPlayer>) this.getModel();

        if (p_117819_.isSpectator())
        {
            playermodel.setAllVisible(false);
            playermodel.head.visible = true;
            playermodel.hat.visible = true;
        }
        else
        {
            playermodel.setAllVisible(true);
            playermodel.hat.visible = p_117819_.isModelPartShown(PlayerModelPart.HAT);
            playermodel.jacket.visible = p_117819_.isModelPartShown(PlayerModelPart.JACKET);
            playermodel.leftPants.visible = p_117819_.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
            playermodel.rightPants.visible = p_117819_.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
            playermodel.leftSleeve.visible = p_117819_.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
            playermodel.rightSleeve.visible = p_117819_.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
            playermodel.crouching = p_117819_.isCrouching() && !p_117819_.isVisuallySwimming();
            HumanoidModel.ArmPose humanoidmodel$armpose = getArmPose(p_117819_, InteractionHand.MAIN_HAND);
            HumanoidModel.ArmPose humanoidmodel$armpose1 = getArmPose(p_117819_, InteractionHand.OFF_HAND);

            if (humanoidmodel$armpose.isTwoHanded())
            {
                humanoidmodel$armpose1 = p_117819_.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
            }

            if (p_117819_.getMainArm() == HumanoidArm.RIGHT)
            {
                playermodel.rightArmPose = humanoidmodel$armpose;
                playermodel.leftArmPose = humanoidmodel$armpose1;
            }
            else
            {
                playermodel.rightArmPose = humanoidmodel$armpose1;
                playermodel.leftArmPose = humanoidmodel$armpose;
            }
        }
    }

    private static HumanoidModel.ArmPose getArmPose(AbstractClientPlayer p_117795_, InteractionHand p_117796_)
    {
        ItemStack itemstack = p_117795_.getItemInHand(p_117796_);

        if (itemstack.isEmpty())
        {
            return HumanoidModel.ArmPose.EMPTY;
        }
        else
        {
            if (p_117795_.getUsedItemHand() == p_117796_ && p_117795_.getUseItemRemainingTicks() > 0)
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

                if (useanim == UseAnim.CROSSBOW && p_117796_ == p_117795_.getUsedItemHand())
                {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }

                if (useanim == UseAnim.SPYGLASS)
                {
                    return HumanoidModel.ArmPose.SPYGLASS;
                }
            }
            else if (!p_117795_.swinging && itemstack.is(Items.CROSSBOW) && CrossbowItem.isCharged(itemstack))
            {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }

            return HumanoidModel.ArmPose.ITEM;
        }
    }

    public ResourceLocation getTextureLocation(AbstractClientPlayer p_117783_)
    {
        return p_117783_.getSkinTextureLocation();
    }

    protected void scale(AbstractClientPlayer p_117798_, PoseStack pLivingEntity, float pMatrixStack)
    {
        float f = 0.9375F;
        pLivingEntity.scale(0.9375F, 0.9375F, 0.9375F);
    }

    protected void renderNameTag(AbstractClientPlayer p_117808_, Component pEntity, PoseStack pDisplayName, MultiBufferSource pMatrixStack, int pBuffer)
    {
        double d0 = this.entityRenderDispatcher.distanceToSqr(p_117808_);
        pDisplayName.pushPose();

        if (d0 < 100.0D)
        {
            Scoreboard scoreboard = p_117808_.getScoreboard();
            Objective objective = scoreboard.getDisplayObjective(2);

            if (objective != null)
            {
                Score score = scoreboard.getOrCreatePlayerScore(p_117808_.getScoreboardName(), objective);
                super.renderNameTag(p_117808_, (new TextComponent(Integer.toString(score.getScore()))).append(" ").append(objective.getDisplayName()), pDisplayName, pMatrixStack, pBuffer);
                pDisplayName.translate(0.0D, (double)(9.0F * 1.15F * 0.025F), 0.0D);
            }
        }

        super.renderNameTag(p_117808_, pEntity, pDisplayName, pMatrixStack, pBuffer);
        pDisplayName.popPose();
    }

    public void renderRightHand(PoseStack p_117771_, MultiBufferSource pMatrixStack, int pBuffer, AbstractClientPlayer pCombinedLight)
    {
        this.renderHand(p_117771_, pMatrixStack, pBuffer, pCombinedLight, (this.model).rightArm, (this.model).rightSleeve);
    }

    public void renderLeftHand(PoseStack p_117814_, MultiBufferSource pMatrixStack, int pBuffer, AbstractClientPlayer pCombinedLight)
    {
        this.renderHand(p_117814_, pMatrixStack, pBuffer, pCombinedLight, (this.model).leftArm, (this.model).leftSleeve);
    }

    private void renderHand(PoseStack p_117776_, MultiBufferSource pMatrixStack, int pBuffer, AbstractClientPlayer pCombinedLight, ModelPart pPlayer, ModelPart pRendererArm)
    {
        VRPlayerModel<AbstractClientPlayer> playermodel = (VRPlayerModel<AbstractClientPlayer>) this.getModel();
        this.setModelProperties(pCombinedLight);
        playermodel.attackTime = 0.0F;
        playermodel.crouching = false;
        playermodel.swimAmount = 0.0F;
        playermodel.setupAnim(pCombinedLight, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        pPlayer.xRot = 0.0F;
        pPlayer.render(p_117776_, pMatrixStack.getBuffer(RenderType.entitySolid(pCombinedLight.getSkinTextureLocation())), pBuffer, OverlayTexture.NO_OVERLAY);
        pRendererArm.xRot = 0.0F;
        pRendererArm.render(p_117776_, pMatrixStack.getBuffer(RenderType.entityTranslucent(pCombinedLight.getSkinTextureLocation())), pBuffer, OverlayTexture.NO_OVERLAY);
    }

    protected void setupRotations(AbstractClientPlayer p_117802_, PoseStack pEntityLiving, float pMatrixStack, float pAgeInTicks, float pRotationYaw)
    {
    	VRPlayerModel vrplayermodel = (VRPlayerModel) this.getModel();
    	double d4 = p_117802_.xOld + (p_117802_.getX() - p_117802_.xOld) * (double)pRotationYaw;
    	d4 = p_117802_.yOld + (p_117802_.getY() - p_117802_.yOld) * (double)pRotationYaw;
    	d4 = p_117802_.zOld + (p_117802_.getZ() - p_117802_.zOld) * (double)pRotationYaw;
    	
    	UUID uuid = p_117802_.getUUID();
    	if (PlayerModelController.getInstance().isTracked(uuid))
    	{
    		PlayerModelController.RotInfo playermodelcontroller$rotinfo = PlayerModelController.getInstance().getRotationsForPlayer(uuid);
    		pAgeInTicks = (float)Math.toDegrees(playermodelcontroller$rotinfo.getBodyYawRadians());
    	}
        float wasyaw = p_117802_.getYRot();

        //vanilla below here
        float f = p_117802_.getSwimAmount(pRotationYaw);

        if (p_117802_.isFallFlying())
        {
            super.setupRotations(p_117802_, pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw);
            float f1 = (float)p_117802_.getFallFlyingTicks() + pRotationYaw;
            float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);

            if (!p_117802_.isAutoSpinAttack())
            {
                pEntityLiving.mulPose(Vector3f.XP.rotationDegrees(f2 * (-90.0F - p_117802_.getXRot())));
            }

            Vec3 vec3 = p_117802_.getViewVector(pRotationYaw);
            Vec3 vec31 = p_117802_.getDeltaMovement();
            double d0 = vec31.horizontalDistanceSqr();
            double d1 = vec3.horizontalDistanceSqr();

            if (d0 > 0.0D && d1 > 0.0D)
            {
                double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
                double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
                pEntityLiving.mulPose(Vector3f.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
            }
        }
        else if (f > 0.0F)
        {
            super.setupRotations(p_117802_, pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw);
            float f3 = p_117802_.isInWater() ? -90.0F - p_117802_.getXRot() : -90.0F;
            float f4 = Mth.lerp(f, 0.0F, f3);
            pEntityLiving.mulPose(Vector3f.XP.rotationDegrees(f4));

            if (p_117802_.isVisuallySwimming())
            {
                pEntityLiving.translate(0.0D, -1.0D, (double)0.3F);
            }
        }
        else
        {
            super.setupRotations(p_117802_, pEntityLiving, pMatrixStack, pAgeInTicks, pRotationYaw);
        }
    }
}
