package org.vivecraft.gui.physical.interactables;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.utils.Utils;
import org.vivecraft.utils.math.Quaternion;

public abstract class Button implements Interactable
{
    public boolean enabled = true;
    float scale = 0.015625F;
    ItemStack displayItem;
    Button.ModelButton model;
    public boolean isDown;
    public boolean isTouched;
    public boolean sticky = false;
    public boolean toggle = false;
    public Vec3 position = Vec3.ZERO;
    public Quaternion rotation = new Quaternion();

    public Button(ItemStack displayItem)
    {
        this.displayItem = displayItem;
        this.model = new Button.ModelButton(displayItem);
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public void render(double partialTicks, int renderLayer)
    {
        this.model.render();
    }

    public Vec3 getPosition(double partialTicks)
    {
        Vec3 vec3 = this.position;

        if (this.isDown)
        {
            vec3 = vec3.add(this.rotation.multiply(new Vec3(0.0D, -0.015D, 0.0D)));
        }
        else if (this.isTouched)
        {
            vec3 = vec3.add(this.rotation.multiply(new Vec3(0.0D, 0.01D, 0.0D)));
        }

        return vec3;
    }

    public Quaternion getRotation(double partialTicks)
    {
        return this.rotation;
    }

    public void touch()
    {
        this.isTouched = true;
    }

    public void untouch()
    {
        this.isTouched = false;
    }

    public void click(int button)
    {
        if (this.toggle)
        {
            this.isDown = !this.isDown;
        }
        else
        {
            this.isDown = true;
        }
    }

    public void unclick(int button)
    {
        if (!this.sticky && !this.toggle)
        {
            this.isDown = false;
        }
    }

    public AABB getBoundingBox()
    {
        return (new AABB((double)(-5.0F * this.scale), (double)(-3.0F * this.scale), (double)(0.0F * this.scale), (double)(5.0F * this.scale), (double)(3.0F * this.scale), (double)(4.0F * this.scale))).inflate(0.05D);
    }

    public class ModelButton extends EntityModel
    {
        public ResourceLocation TEXTURE = new ResourceLocation("vivecraft:textures/blocks/inv_button.png");
        ModelPart button;
        ItemStack displayItem;
        ItemRenderer renderItem = Minecraft.getInstance().getItemRenderer();

        public ModelButton(ItemStack displayItem)
        {
            this.displayItem = displayItem;
        }

        public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
        {
            GlStateManager._pushMatrix();
            float f = 0.0625F;
            GlStateManager._translatef(0.0F, scale * 4.0F, 0.0F);
            GlStateManager._scalef(f, f, f);
            Utils.glRotate(new Quaternion(90.0F, 0.0F, 0.0F));
            GlStateManager._popMatrix();
        }

        public void render()
        {
            Minecraft.getInstance().getTextureManager().bind(this.TEXTURE);
            this.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Button.this.scale);
        }

        public void setupAnim(Entity p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_)
        {
        }

        public void renderToBuffer(PoseStack p_103111_, VertexConsumer p_103112_, int p_103113_, int p_103114_, float p_103115_, float p_103116_, float p_103117_, float p_103118_)
        {
        }
    }
}
