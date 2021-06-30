package org.vivecraft.render;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class VRPlayerModel<T extends LivingEntity> extends PlayerModel<T>
{
    private final boolean smallArms;
    public ModelPart leftShoulder;
    public ModelPart rightShoulder;
    public ModelPart leftHand;
    public ModelPart rightHand;
    public ModelPart vrHMD;
    ResourceLocation DIAMOND_HMD = new ResourceLocation("vivecraft:textures/diamond_hmd.png");
    ResourceLocation GOLD_HMD = new ResourceLocation("vivecraft:textures/gold_hmd.png");
    ResourceLocation BLACK_HMD = new ResourceLocation("vivecraft:textures/black_hmd.png");
    PlayerModelController.RotInfo rotInfo;
    public boolean seated;
    private boolean laying;

    public VRPlayerModel(float p_i125_1_, boolean p_i125_2_, boolean seated)
    {
        super(p_i125_1_, p_i125_2_);
        this.smallArms = p_i125_2_;
        this.vrHMD = new ModelPart(this, 0, 0);
        this.vrHMD.setTexSize(16, 16);
        this.vrHMD.addBox(-3.5F, -6.0F, -7.5F, 7.0F, 4.0F, 5.0F, p_i125_1_);
        this.vrHMD.setTextureLocation(this.BLACK_HMD);
        this.seated = seated;

        if (!seated)
        {
            this.rightHand = new ModelPart(this, 40, 16);
            this.rightHand.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i125_1_);
            this.rightHand.setPos(-5.0F, 2.0F + p_i125_1_, 0.0F);
            this.leftHand = new ModelPart(this, 40, 16);
            this.leftHand.mirror = true;
            this.leftHand.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i125_1_);
            this.leftHand.setPos(5.0F, 2.0F + p_i125_1_, 0.0F);
            this.leftHand.texOffs(32, 56);
            this.rightHand.texOffs(40, 24);
            this.leftHand.cubes.clear();
            this.rightHand.cubes.clear();
            this.rightShoulder = new ModelPart(this, 40, 16);
            this.leftShoulder = new ModelPart(this, 32, 48);
            this.leftSleeve.cubes.clear();
            this.rightSleeve.cubes.clear();
            this.leftSleeve.texOffs(48, 56);
            this.rightSleeve.texOffs(40, 40);

            if (p_i125_2_)
            {
                this.leftHand.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, p_i125_1_);
                this.rightHand.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, p_i125_1_);
                this.leftSleeve.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, p_i125_1_ + 0.25F);
                this.rightSleeve.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, p_i125_1_ + 0.25F);
                this.leftShoulder.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, p_i125_1_);
                this.rightShoulder.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 4.0F, 4.0F, p_i125_1_);
            }
            else
            {
                this.leftHand.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, p_i125_1_);
                this.rightHand.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, p_i125_1_);
                this.leftSleeve.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, p_i125_1_ + 0.25F);
                this.rightSleeve.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, p_i125_1_ + 0.25F);
                this.leftShoulder.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, p_i125_1_);
                this.rightShoulder.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, p_i125_1_);
            }
        }
    }

    protected Iterable<ModelPart> bodyParts()
    {
        return this.seated ? super.bodyParts() : Iterables.concat(super.bodyParts(), ImmutableList.of(this.leftShoulder, this.rightShoulder));
    }

    public void setupAnim(T p_103395_, float p_103396_, float p_103397_, float p_103398_, float p_103399_, float p_103400_)
    {
        super.setupAnim(p_103395_, p_103396_, p_103397_, p_103398_, p_103399_, p_103400_);
        this.rotInfo = PlayerModelController.getInstance().getRotationsForPlayer(((Player)p_103395_).getUUID());
        PlayerModelController.RotInfo playermodelcontroller$rotinfo = PlayerModelController.getInstance().getRotationsForPlayer(((Player)p_103395_).getUUID());

        if (playermodelcontroller$rotinfo != null)
        {
            double d0 = (double)(-1.501F * playermodelcontroller$rotinfo.heightScale);
            float f = (float)Math.toRadians((double)p_103395_.yRot);
            float f1 = (float)Math.atan2(-playermodelcontroller$rotinfo.headRot.x, -playermodelcontroller$rotinfo.headRot.z);
            float f2 = (float)Math.asin(playermodelcontroller$rotinfo.headRot.y / playermodelcontroller$rotinfo.headRot.length());
            float f3 = (float)Math.atan2(-playermodelcontroller$rotinfo.leftArmRot.x, -playermodelcontroller$rotinfo.leftArmRot.z);
            float f4 = (float)Math.asin(playermodelcontroller$rotinfo.leftArmRot.y / playermodelcontroller$rotinfo.leftArmRot.length());
            float f5 = (float)Math.atan2(-playermodelcontroller$rotinfo.rightArmRot.x, -playermodelcontroller$rotinfo.rightArmRot.z);
            float f6 = (float)Math.asin(playermodelcontroller$rotinfo.rightArmRot.y / playermodelcontroller$rotinfo.rightArmRot.length());
            double d1 = playermodelcontroller$rotinfo.getBodyYawRadians();
            this.head.xRot = -f2;
            this.head.yRot = (float)(Math.PI - (double)f1 - d1);
            this.laying = this.swimAmount > 0.0F || p_103395_.isFallFlying() && !p_103395_.isAutoSpinAttack();

            if (!this.seated)
            {
                this.rightShoulder.visible = true;
                this.leftShoulder.visible = true;

                if (!playermodelcontroller$rotinfo.reverse)
                {
                    this.rightShoulder.setPos(-Mth.cos(this.body.yRot) * 5.0F, this.smallArms ? 2.5F : 2.0F, Mth.sin(this.body.yRot) * 5.0F);
                    this.leftShoulder.setPos(Mth.cos(this.body.yRot) * 5.0F, this.smallArms ? 2.5F : 2.0F, -Mth.sin(this.body.yRot) * 5.0F);
                }
                else
                {
                    this.leftShoulder.setPos(-Mth.cos(this.body.yRot) * 5.0F, this.smallArms ? 2.5F : 2.0F, Mth.sin(this.body.yRot) * 5.0F);
                    this.rightShoulder.setPos(Mth.cos(this.body.yRot) * 5.0F, this.smallArms ? 2.5F : 2.0F, -Mth.sin(this.body.yRot) * 5.0F);
                }

                if (this.crouching)
                {
                    this.rightShoulder.y += 3.2F;
                    this.leftShoulder.y += 3.2F;
                }

                Vec3 vec3 = playermodelcontroller$rotinfo.leftArmPos.add(0.0D, d0, 0.0D);
                vec3 = vec3.yRot((float)(-Math.PI + d1));
                vec3 = vec3.scale((double)(16.0F / playermodelcontroller$rotinfo.heightScale));
                this.leftHand.setPos((float)(-vec3.x), (float)(-vec3.y), (float)vec3.z);
                this.leftHand.xRot = (float)((double)(-f4) + (Math.PI * 1.5D));
                this.leftHand.yRot = (float)(Math.PI - (double)f3 - d1);
                this.leftHand.zRot = 0.0F;

                switch (this.leftArmPose)
                {
                    case THROW_SPEAR:
                        this.leftHand.xRot = (float)((double)this.leftHand.xRot - (Math.PI / 2D));

                    default:
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

                        Vec3 vec32 = playermodelcontroller$rotinfo.rightArmPos.add(0.0D, d0, 0.0D);
                        vec32 = vec32.yRot((float)(-Math.PI + d1));
                        vec32 = vec32.scale((double)(16.0F / playermodelcontroller$rotinfo.heightScale));
                        this.rightHand.setPos((float)(-vec32.x), -((float)vec32.y), (float)vec32.z);
                        this.rightHand.xRot = (float)((double)(-f6) + (Math.PI * 1.5D));
                        this.rightHand.yRot = (float)(Math.PI - (double)f5 - d1);
                        this.rightHand.zRot = 0.0F;

                        switch (this.rightArmPose)
                        {
                            case THROW_SPEAR:
                                this.rightHand.xRot = (float)((double)this.rightHand.xRot - (Math.PI / 2D));

                            default:
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

                                if (this.laying)
                                {
                                    this.head.z = 0.0F;
                                    this.head.x = 0.0F;
                                    this.head.y = -4.0F;
                                    this.head.xRot = (float)((double)this.head.xRot - (Math.PI / 2D));
                                    this.rightShoulder.xRot = (float)((double)this.rightShoulder.xRot - (Math.PI / 2D));
                                    this.leftShoulder.xRot = (float)((double)this.leftShoulder.xRot - (Math.PI / 2D));
                                }
                                else
                                {
                                    this.head.z = 0.0F;
                                    this.head.x = 0.0F;
                                    this.head.y = 0.0F;
                                }
                        }
                }
            }

            this.vrHMD.visible = true;

            switch (playermodelcontroller$rotinfo.hmd)
            {
                case 0:
                    this.vrHMD.visible = false;
                    break;

                case 1:
                    this.vrHMD.setTextureLocation(this.BLACK_HMD);
                    break;

                case 2:
                    this.vrHMD.setTextureLocation(this.GOLD_HMD);
                    break;

                case 3:
                    this.vrHMD.setTextureLocation(this.DIAMOND_HMD);
                    break;

                case 4:
                    this.vrHMD.setTextureLocation(this.DIAMOND_HMD);
            }

            this.vrHMD.copyFrom(this.head);
            this.hat.copyFrom(this.head);
            this.jacket.copyFrom(this.body);
            this.leftPants.copyFrom(this.leftLeg);
            this.rightPants.copyFrom(this.rightLeg);

            if (this.seated)
            {
                this.leftSleeve.copyFrom(this.leftArm);
                this.rightSleeve.copyFrom(this.rightArm);
            }
            else
            {
                this.leftSleeve.copyFrom(this.leftHand);
                this.rightSleeve.copyFrom(this.rightHand);
            }

            this.jacket.copyFrom(this.body);
        }
    }

    public void copyPropertiesTo(HumanoidModel<T> p_102873_)
    {
        super.copyPropertiesTo(p_102873_);
        p_102873_.leftArmPose = this.leftArmPose;
        p_102873_.rightArmPose = this.rightArmPose;
        p_102873_.crouching = this.crouching;
        p_102873_.head.copyFrom(this.head);
        p_102873_.hat.copyFrom(this.hat);
        p_102873_.body.copyFrom(this.body);

        if (this.rightShoulder != null)
        {
            p_102873_.rightArm.copyFrom(this.rightShoulder);
            p_102873_.leftArm.copyFrom(this.leftShoulder);
        }
        else
        {
            p_102873_.rightArm.copyFrom(this.rightArm);
            p_102873_.leftArm.copyFrom(this.leftArm);
        }

        p_102873_.rightLeg.copyFrom(this.rightLeg);
        p_102873_.leftLeg.copyFrom(this.leftLeg);
    }

    public void setAllVisible(boolean p_103419_)
    {
        super.setAllVisible(p_103419_);

        if (!this.seated)
        {
            this.rightShoulder.visible = p_103419_;
            this.leftShoulder.visible = p_103419_;
            this.rightHand.visible = p_103419_;
            this.leftHand.visible = p_103419_;
            this.leftArm.visible = false;
            this.rightArm.visible = false;
        }
    }

    protected ModelPart getArm(HumanoidArm p_102852_)
    {
        if (this.seated)
        {
            return p_102852_ == HumanoidArm.LEFT ? this.leftArm : this.rightArm;
        }
        else
        {
            return p_102852_ == HumanoidArm.LEFT ? this.leftHand : this.rightHand;
        }
    }

    public void translateToHand(HumanoidArm p_103392_, PoseStack p_103393_)
    {
        if (!this.seated)
        {
            ModelPart modelpart = this.getArm(p_103392_);

            if (this.laying)
            {
                p_103393_.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            }

            modelpart.translateAndRotate(p_103393_);
            p_103393_.mulPose(Vector3f.XP.rotation((float)Math.sin((double)this.attackTime * Math.PI)));
            p_103393_.translate(0.0D, -0.5D, 0.0D);
        }
        else
        {
            super.translateToHand(p_103392_, p_103393_);
        }
    }

    public void renderToBuffer(PoseStack p_103111_, VertexConsumer p_103112_, int p_103113_, int p_103114_, float p_103115_, float p_103116_, float p_103117_, float p_103118_)
    {
        this.body.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
        this.jacket.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
        this.leftLeg.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
        this.rightLeg.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
        this.leftPants.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
        this.rightPants.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
        p_103111_.pushPose();
        this.head.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
        this.hat.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
        this.vrHMD.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);

        if (this.seated)
        {
            this.leftArm.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
            this.rightArm.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
        }
        else
        {
            this.leftShoulder.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
            this.rightShoulder.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);

            if (this.laying)
            {
                p_103111_.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
            }

            this.rightHand.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
            this.leftHand.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
        }

        this.leftSleeve.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
        this.rightSleeve.render(p_103111_, p_103112_, p_103113_, p_103114_, p_103115_, p_103116_, p_103117_, p_103118_);
        p_103111_.popPose();
    }
}
