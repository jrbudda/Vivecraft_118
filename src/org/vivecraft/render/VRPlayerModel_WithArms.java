package org.vivecraft.render;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class VRPlayerModel_WithArms<T extends LivingEntity> extends VRPlayerModel<T>
{
	private final boolean slim;
	public ModelPart leftShoulder;
	public ModelPart rightShoulder;
	public ModelPart leftHand;
	public ModelPart rightHand;
	PlayerModelController.RotInfo rotInfo;
	private boolean laying;
	private final List<ModelPart> parts;

	public VRPlayerModel_WithArms(ModelPart p_170821_, boolean p_170822_)
	{
		super(p_170821_, p_170822_);
		this.slim = p_170822_;
		this.leftShoulder = p_170821_.getChilds("leftShoulder");
		this.rightShoulder = p_170821_.getChilds("rightShoulder");
		this.rightHand = p_170821_.getChilds("rightHand");
		this.leftHand = p_170821_.getChilds("leftHand");

		this.parts = p_170821_.getAllParts().filter((p_170824_) ->
		{
			return !p_170824_.isEmpty();
		}).collect(ImmutableList.toImmutableList());
	}

	public static MeshDefinition createMesh(CubeDeformation p_170826_, boolean p_170827_)
	{
		MeshDefinition meshdefinition = VRPlayerModel.createMesh(p_170826_, false);
		PartDefinition partdefinition = meshdefinition.getRoot();

		//its just a flesh wound
		partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
		partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);

		if(p_170827_) {
			partdefinition.addOrReplaceChild("leftHand", CubeListBuilder.create().texOffs(32, 56).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, p_170826_), PartPose.offset(5.0F, 2.5F, 0.0F));
			partdefinition.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(32, 56).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, p_170826_.extend(0.25f)), PartPose.offset(5.0F, 2.5F, 0.0F));
			partdefinition.addOrReplaceChild("rightHand", CubeListBuilder.create().texOffs(40, 24).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, p_170826_), PartPose.offset(-5.0F, 2.5F, 0.0F));
			partdefinition.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 24).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, p_170826_.extend(0.25f)), PartPose.offset(-5.0F, 2.5F, 0.0F));
			partdefinition.addOrReplaceChild("leftShoulder", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, p_170826_.extend(0.25F)), PartPose.offset(5.0F, 2.5F, 0.0F));
			partdefinition.addOrReplaceChild("rightShoulder", CubeListBuilder.create().texOffs(40, 32).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, p_170826_.extend(0.25F)), PartPose.offset(-5.0F, 2.5F, 0.0F));
		}else {
			partdefinition.addOrReplaceChild("leftHand", CubeListBuilder.create().texOffs(32, 56).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, p_170826_), PartPose.offset(5.0F, 2.5F, 0.0F));
			partdefinition.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(32, 56).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, p_170826_.extend(0.25f)), PartPose.offset(5.0F, 2.5F, 0.0F));
			partdefinition.addOrReplaceChild("rightHand", CubeListBuilder.create().texOffs(40, 24).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, p_170826_), PartPose.offset(-5.0F, 2.5F, 0.0F));
			partdefinition.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 24).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, p_170826_.extend(0.25f)), PartPose.offset(-5.0F, 2.5F, 0.0F));
			partdefinition.addOrReplaceChild("leftShoulder", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, p_170826_.extend(0.25F)), PartPose.offset(5.0F, 2.5F, 0.0F));
			partdefinition.addOrReplaceChild("rightShoulder", CubeListBuilder.create().texOffs(40, 32).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, p_170826_.extend(0.25F)), PartPose.offset(-5.0F, 2.5F, 0.0F));
		}
		return meshdefinition;
	}


	protected Iterable<ModelPart> bodyParts()
	{
        return ImmutableList.of(this.body, this.leftHand, this.rightHand,this.leftShoulder, this.rightShoulder, this.rightLeg, this.leftLeg, this.hat, this.leftPants, this.rightPants, this.leftSleeve, this.rightSleeve, this.jacket);
	}

	public void setupAnim(T p_103395_, float pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw)
	{
		super.setupAnim(p_103395_, pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw);
		this.rotInfo = PlayerModelController.getInstance().getRotationsForPlayer(((Player)p_103395_).getUUID());
		PlayerModelController.RotInfo rotinfo = PlayerModelController.getInstance().getRotationsForPlayer(((Player)p_103395_).getUUID());

		if (rotinfo == null) return;

		double d0 = (double)(-1.501F * rotinfo.heightScale);
		float f = (float)Math.toRadians((double)p_103395_.getYRot());
		float f1 = (float)Math.atan2(-rotinfo.headRot.x, -rotinfo.headRot.z);
		float f2 = (float)Math.asin(rotinfo.headRot.y / rotinfo.headRot.length());
		float f3 = (float)Math.atan2(-rotinfo.leftArmRot.x, -rotinfo.leftArmRot.z);
		float f4 = (float)Math.asin(rotinfo.leftArmRot.y / rotinfo.leftArmRot.length());
		float f5 = (float)Math.atan2(-rotinfo.rightArmRot.x, -rotinfo.rightArmRot.z);
		float f6 = (float)Math.asin(rotinfo.rightArmRot.y / rotinfo.rightArmRot.length());
		double d1 = rotinfo.getBodyYawRadians();

		this.laying = this.swimAmount > 0.0F || p_103395_.isFallFlying() && !p_103395_.isAutoSpinAttack();

		this.rightShoulder.visible = true;
		this.leftShoulder.visible = true;

		if (!rotinfo.reverse)
		{
			this.rightShoulder.setPos(-Mth.cos(this.body.yRot) * 5.0F, this.slim ? 2.5F : 2.0F, Mth.sin(this.body.yRot) * 5.0F);
			this.leftShoulder.setPos(Mth.cos(this.body.yRot) * 5.0F, this.slim ? 2.5F : 2.0F, -Mth.sin(this.body.yRot) * 5.0F);
		}
		else
		{
			this.leftShoulder.setPos(-Mth.cos(this.body.yRot) * 5.0F, this.slim ? 2.5F : 2.0F, Mth.sin(this.body.yRot) * 5.0F);
			this.rightShoulder.setPos(Mth.cos(this.body.yRot) * 5.0F, this.slim ? 2.5F : 2.0F, -Mth.sin(this.body.yRot) * 5.0F);
		}

		if (this.crouching)
		{
			this.rightShoulder.y += 3.2F;
			this.leftShoulder.y += 3.2F;
		}

		Vec3 vec3 = rotinfo.leftArmPos.add(0.0D, d0, 0.0D);
		vec3 = vec3.yRot((float)(-Math.PI + d1));
		vec3 = vec3.scale((double)(16.0F / rotinfo.heightScale));
		this.leftHand.setPos((float)(-vec3.x), (float)(-vec3.y), (float)vec3.z);
		this.leftHand.xRot = (float)((double)(-f4) + (Math.PI * 1.5D));
		this.leftHand.yRot = (float)(Math.PI - (double)f3 - d1);
		this.leftHand.zRot = 0.0F;


		Vec3 vec31 = new Vec3((double)this.leftShoulder.x + vec3.x, (double)this.leftShoulder.y + vec3.y, (double)this.leftShoulder.z - vec3.z);
		float f7 = (float)Math.atan2(vec31.x, vec31.z);
		float f8 = (float)((Math.PI * 1.5D) - Math.asin(vec31.y / vec31.length()));
		this.leftShoulder.zRot = 0.0F;
		this.leftShoulder.xRot = f8;
		this.leftShoulder.yRot = f7;

		if (this.leftShoulder.yRot > 0.0F)
		{
			this.leftShoulder.yRot = 0.0F;
		}

		switch (this.leftArmPose)
		{
		case THROW_SPEAR:
			this.leftHand.xRot = (float)((double)this.leftHand.xRot - (Math.PI / 2D));
		default:
		}

		Vec3 vec32 = rotinfo.rightArmPos.add(0.0D, d0, 0.0D);
		vec32 = vec32.yRot((float)(-Math.PI + d1));
		vec32 = vec32.scale((double)(16.0F / rotinfo.heightScale));
		this.rightHand.setPos((float)(-vec32.x), -((float)vec32.y), (float)vec32.z);
		this.rightHand.xRot = (float)((double)(-f6) + (Math.PI * 1.5D));
		this.rightHand.yRot = (float)(Math.PI - (double)f5 - d1);
		this.rightHand.zRot = 0.0F;

		Vec3 vec33 = new Vec3((double)this.rightShoulder.x + vec32.x, (double)this.rightShoulder.y + vec32.y, (double)this.rightShoulder.z - vec32.z);
		float f9 = (float)Math.atan2(vec33.x, vec33.z);
		float f10 = (float)((Math.PI * 1.5D) - Math.asin(vec33.y / vec33.length()));
		this.rightShoulder.zRot = 0.0F;
		this.rightShoulder.xRot = f10;
		this.rightShoulder.yRot = f9;

		if (this.rightShoulder.yRot < 0.0F)
		{
			this.rightShoulder.yRot = 0.0F;
		}

		switch (this.rightArmPose)
		{
		case THROW_SPEAR:
			this.rightHand.xRot = (float)((double)this.rightHand.xRot - (Math.PI / 2D));
		default:
		}

		if (this.laying)
		{
			this.rightShoulder.xRot = (float)((double)this.rightShoulder.xRot - (Math.PI / 2D));
			this.leftShoulder.xRot = (float)((double)this.leftShoulder.xRot - (Math.PI / 2D));
		}

		this.leftSleeve.copyFrom(this.leftHand);
		this.rightSleeve.copyFrom(this.rightHand);

	}

	public void setAllVisible(boolean p_103419_)
	{
		super.setAllVisible(p_103419_);

		this.rightShoulder.visible = p_103419_;
		this.leftShoulder.visible = p_103419_;
		this.rightHand.visible = p_103419_;
		this.leftHand.visible = p_103419_;
		this.leftArm.visible = false;
		this.rightArm.visible = false;

	}

	protected ModelPart getArm(HumanoidArm p_102852_)
	{
		return p_102852_ == HumanoidArm.LEFT ? this.leftHand : this.rightHand;
	}

	public void translateToHand(HumanoidArm p_103392_, PoseStack pSide)
	{
		ModelPart modelpart = this.getArm(p_103392_);

		if (this.laying)
		{
			pSide.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
		}

		modelpart.translateAndRotate(pSide);
		pSide.mulPose(Vector3f.XP.rotation((float)Math.sin((double)this.attackTime * Math.PI)));
		pSide.translate(0.0D, -0.5D, 0.0D);
	}

//	public void renderToBuffer(PoseStack p_103111_, VertexConsumer pMatrixStack, int pBuffer, int pPackedLight, float pPackedOverlay, float pRed, float pGreen, float pBlue)
//	{
//		this.body.render(p_103111_, pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue);
//		this.jacket.render(p_103111_, pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue);
//		this.leftLeg.render(p_103111_, pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue);
//		this.rightLeg.render(p_103111_, pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue);
//		this.leftPants.render(p_103111_, pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue);
//		this.rightPants.render(p_103111_, pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue);
//		p_103111_.pushPose();
//		this.head.render(p_103111_, pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue);
//		this.hat.render(p_103111_, pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue);
//		this.vrHMD.render(p_103111_, pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue);
//
//		if (this.seated)
//		{
//			this.leftArm.render(p_103111_, pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue);
//			this.rightArm.render(p_103111_, pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue);
//		}
//		else
//		{
//			this.leftShoulder.render(p_103111_, pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue);
//			this.rightShoulder.render(p_103111_, pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue);
//
//			if (this.laying)
//			{
//				p_103111_.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
//			}
//
//			this.rightHand.render(p_103111_, pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue);
//			this.leftHand.render(p_103111_, pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue);
//		}
//
//		this.leftSleeve.render(p_103111_, pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue);
//		this.rightSleeve.render(p_103111_, pMatrixStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue);
//		p_103111_.popPose();
//	}
}
