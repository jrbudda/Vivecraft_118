package org.vivecraft.gui.physical.interactables;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.gui.physical.PhysicalInventory;
import org.vivecraft.gui.physical.PhysicalItemSlotGui;
import org.vivecraft.gui.physical.WindowCoordinator;
import org.vivecraft.utils.Utils;
import org.vivecraft.utils.math.Quaternion;

public class Trashbin extends CreativeItemSlot
{
    ModelResourceLocation binLoc;
    boolean charging = false;
    double charge = 0.0D;
    double lastCharge = 0.0D;
    double chargePerTick = 0.01D;
    double chargeMultPerTick = 1.03D;

    public Trashbin(PhysicalItemSlotGui gui)
    {
        super(gui, ItemStack.EMPTY, -1);
        this.binLoc = new ModelResourceLocation("vivecraft:trashbin");
    }

    public void render(double partialTicks, int renderLayer)
    {
        GlStateManager._pushMatrix();
        double d0 = 0.4D;
        Vec3 vec3 = new Vec3(-0.21D, -0.03D, -0.21D);
        double d1 = (this.lastCharge + (this.charge - this.lastCharge) * partialTicks) * 5.0D * 360.0D % 360.0D;
        Utils.glRotate(new Quaternion(-90.0F, 0.0F, 0.0F));
        Utils.glRotate(new Quaternion(0.0F, (float)d1, 0.0F));
        GlStateManager._translated(vec3.x, vec3.y, vec3.z);
        GlStateManager._scalef((float)d0, (float)d0, (float)d0);
        PhysicalInventory.renderCustomModel(this.binLoc);
        GlStateManager._popMatrix();
        super.render(partialTicks, renderLayer);
    }

    public void update()
    {
        this.lastCharge = this.charge;

        if (this.charging)
        {
            this.charge += this.chargePerTick;
            this.charge *= this.chargeMultPerTick;

            if (this.charge >= 1.0D)
            {
                this.charging = false;
                Vec3 vec3 = new Vec3(-0.3D, -0.3D, 0.0D);
                Vec3 vec31 = this.getAnchorPos(0.0D).add(this.getAnchorRotation(0.0D).multiply(vec3));
                Utils.spawnParticles(ParticleTypes.EXPLOSION, 100, vec31, new Vec3(0.1D, 0.1D, 0.1D), 0.0D);
                this.mc.physicalGuiManager.windowCoordinator.enqueueOperation(new WindowCoordinator.ClearInventoryOperation());
            }
        }
        else
        {
            this.charge = 0.0D;
        }
    }

    public void click(int button)
    {
        if (this.getDisplayedItem() != null && !this.getDisplayedItem().isEmpty())
        {
            super.click(button);
        }
        else
        {
            this.charging = true;
        }
    }

    public void unclick(int button)
    {
        this.charging = false;
    }

    public ItemStack getDisplayedItem()
    {
        if (this.gui.touching == this)
        {
            ItemStack itemstack = this.mc.physicalGuiManager.getVirtualHeldItem();

            if (!itemstack.isEmpty())
            {
                return itemstack;
            }
        }

        return ItemStack.EMPTY;
    }

    public AABB getBoundingBox()
    {
        return super.getBoundingBox().deflate(0.13D);
    }
}
