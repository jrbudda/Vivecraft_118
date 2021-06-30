package org.vivecraft.gui.physical.interactables;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.api.VRData;
import org.vivecraft.gameplay.VRPlayer;
import org.vivecraft.utils.math.Quaternion;

public class Slider implements Interactable
{
    public boolean enabled = true;
    Slider.ModelSlider slider = new Slider.ModelSlider(80);
    public Vec3 position = Vec3.ZERO;
    public Quaternion rotation = new Quaternion();
    Vec3 anchorPos = Vec3.ZERO;
    Quaternion anchorRotation = new Quaternion();
    Minecraft mc = Minecraft.getInstance();
    public float scale = 0.005F;
    boolean holding;
    double slideOffset;
    ArrayList<Slider.ScrollListener> listeners = new ArrayList<>();

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public void render(double partialTicks, int renderLayer)
    {
        this.slider.render(this.scale);

        if (this.holding)
        {
            double d0 = this.getTargetSlidePos(partialTicks) + this.slideOffset;
            this.slider.setSliderPos(Math.min(Math.max(0.0D, d0), 1.0D));
        }
    }

    double getTargetSlidePos(double partialTicks)
    {
        VRData vrdata = VRPlayer.get().vrdata_world_render;

        if (vrdata == null)
        {
            vrdata = VRPlayer.get().vrdata_world_pre;
        }

        int i = this.mc.options.mainHand == HumanoidArm.RIGHT ? 0 : 1;
        Vec3 vec3 = vrdata.getController(i).getPosition();
        Vec3 vec31 = this.getAnchorPos(partialTicks).add(this.getAnchorRotation(partialTicks).multiply(this.getPosition(partialTicks)));
        Vec3 vec32 = this.getAnchorRotation(partialTicks).multiply(this.getRotation(partialTicks).multiply(new Vec3(0.0D, 0.0D, -1.0D)));
        double d0 = vec31.subtract(vec3).dot(vec32);
        return d0 / (double)((float)(this.slider.length - 15) * this.scale) + 0.5D;
    }

    public Vec3 getPosition(double partialTicks)
    {
        return this.position;
    }

    public Quaternion getRotation(double partialTicks)
    {
        return this.rotation;
    }

    public Vec3 getAnchorPos(double partialTicks)
    {
        return this.anchorPos;
    }

    public Quaternion getAnchorRotation(double partialTicks)
    {
        return this.anchorRotation;
    }

    public void touch()
    {
    }

    public void untouch()
    {
    }

    public void registerScrollListener(Slider.ScrollListener listener)
    {
        this.listeners.add(listener);
    }

    public void unregisterScrollListener(Slider.ScrollListener listener)
    {
        this.listeners.remove(listener);
    }

    public void notifyScroll(double perc)
    {
        for (Slider.ScrollListener slider$scrolllistener : this.listeners)
        {
            slider$scrolllistener.onScroll(perc);
        }
    }

    public void update()
    {
        if (this.holding)
        {
            this.notifyScroll(this.getSlidePercent());
        }
    }

    public AABB getBoundingBox()
    {
        ModelPart.Cube modelpart$cube = this.slider.knob.cubes.get(0);
        AABB aabb = new AABB((double)(this.scale * modelpart$cube.minX), (double)(this.scale * modelpart$cube.minY), (double)(this.scale * modelpart$cube.minZ), (double)(this.scale * modelpart$cube.maxX), (double)(this.scale * modelpart$cube.maxY), (double)(this.scale * modelpart$cube.maxZ));
        aabb = aabb.move(0.0D, 0.0D, (double)this.scale * (this.slider.sliderPos * (double)(this.slider.length - 15) - (double)((float)this.slider.length / 2.0F) + 15.0D));
        return aabb.inflate(0.1D);
    }

    public void click(int button)
    {
        if (!this.holding)
        {
            this.holding = true;
            this.slideOffset = this.slider.getSliderPos() - this.getTargetSlidePos(0.0D);
        }
    }

    public void unclick(int button)
    {
        this.holding = false;
    }

    public double getSlidePercent()
    {
        return 1.0D - this.slider.getSliderPos();
    }

    class ModelSlider extends EntityModel
    {
        public ResourceLocation TEXTURE = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
        ModelPart rail;
        ModelPart knob;
        int length;
        private double sliderPos = 1.0D;

        public ModelSlider(int length)
        {
            this.length = length;
            this.rail = (new ModelPart(this, 0, 0)).setTexSize(256, 256);
            int[][] aint = new int[][] {{141, 11, 167, 12}, {143, 11, 144, 12}, {166, 11, 167, 12}, {141, 11, 142, 12}, {141, 11, 142, 12}, {166, 11, 167, 12}};
            this.knob = (new ModelPart(this, 0, 0)).setTexSize(256, 256);
            int[][] aint1 = new int[][] {{232, 0, 244, 15}, {233, 14, 234, 15}, {233, 14, 234, 15}, {232, 0, 233, 1}, {232, 0, 233, 1}, {233, 14, 234, 15}};
            this.setSliderPos(this.sliderPos);
        }

        public double getSliderPos()
        {
            return this.sliderPos;
        }

        public void setSliderPos(double sliderPos)
        {
            this.sliderPos = sliderPos;
            this.knob.z = (float)(sliderPos * (double)(this.length - 15)) - (float)this.length / 2.0F;
        }

        void render(float scale)
        {
            Minecraft.getInstance().getTextureManager().bind(this.TEXTURE);
        }

        public void setupAnim(Entity p_102618_, float p_102619_, float p_102620_, float p_102621_, float p_102622_, float p_102623_)
        {
        }

        public void renderToBuffer(PoseStack p_103111_, VertexConsumer p_103112_, int p_103113_, int p_103114_, float p_103115_, float p_103116_, float p_103117_, float p_103118_)
        {
        }
    }

    public interface ScrollListener
    {
        void onScroll(double var1);
    }
}
