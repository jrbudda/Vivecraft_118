package org.vivecraft.gui.physical.interactables;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.gui.physical.PhysicalItemSlotGui;
import org.vivecraft.utils.math.Quaternion;

public class PhysicalItemSlot implements Interactable
{
    public PhysicalItemSlotGui gui;
    public Minecraft mc;
    public boolean enabled = true;
    public boolean preview = true;
    public int slotId;
    public Vec3 position = new Vec3(0.0D, 0.0D, -0.5D);
    public double popOutScaleMult = 1.2D;
    public boolean popOut = false;
    public Quaternion rotation = new Quaternion();
    public Quaternion fullBlockRotation = new Quaternion(-90.0F, 0.0F, 0.0F);
    public Vec3 counterPos = new Vec3(0.0D, 0.0D, 0.0D);
    public Quaternion counterRot = new Quaternion();
    public Slot slot;
    public double scale = 0.2D;
    public double opacity = 1.0D;
    public double fullBlockScaleMult = 1.9D;
    public double counterScale = 0.1D;

    public PhysicalItemSlot(PhysicalItemSlotGui gui, int slotId)
    {
        this.slotId = slotId;
        this.gui = gui;
        this.mc = Minecraft.getInstance();
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public void render(double partialTicks, int renderLayer)
    {
    }

    public Vec3 getPosition(double partialTicks)
    {
        return this.position;
    }

    public Quaternion getRotation(double partialTicks)
    {
        return this.rotation;
    }

    public Quaternion getAnchorRotation(double partialTicks)
    {
        return this.gui.getAnchorRotation(partialTicks);
    }

    public Vec3 getAnchorPos(double partialTicks)
    {
        return this.gui.getAnchorPos(partialTicks);
    }

    public ItemStack getDisplayedItem()
    {
        if (this.preview && this.gui.touching == this)
        {
            ItemStack itemstack = this.mc.physicalGuiManager.getVirtualHeldItem();

            if (!itemstack.isEmpty())
            {
                return itemstack;
            }
        }

        return this.slot != null ? this.slot.getItem() : null;
    }

    public double getOpacity()
    {
        return this.gui.touching == this && this.preview && !this.mc.physicalGuiManager.getVirtualHeldItem().isEmpty() ? 0.5D : this.opacity;
    }

    public void touch()
    {
        int i = this.mc.options.mainHand == HumanoidArm.RIGHT ? 0 : 1;
        this.popOut = true;

        if (this.getDisplayedItem() != null && !this.getDisplayedItem().isEmpty())
        {
            this.mc.vr.triggerHapticPulse(i, 500);
        }

        if (this.preview && !this.mc.physicalGuiManager.getVirtualHeldItem().isEmpty())
        {
            this.mc.physicalGuiManager.setHideItemTouchingSlotOverride(ItemStack.EMPTY);
        }
        else
        {
            this.mc.physicalGuiManager.setHideItemTouchingSlotOverride((ItemStack)null);
        }
    }

    public void untouch()
    {
        this.popOut = false;
        this.mc.physicalGuiManager.setHideItemTouchingSlotOverride((ItemStack)null);
    }

    public void click(int button)
    {
        if (this.gui.isOpen())
        {
            this.opacity = 1.0D;
            this.mc.physicalGuiManager.clickSlot(this.slotId, button);
        }
    }

    public void unclick(int button)
    {
    }

    public AABB getBoundingBox()
    {
        return new AABB(-this.gui.touchDistance, -this.gui.touchDistance, -this.gui.touchDistance, this.gui.touchDistance, this.gui.touchDistance, this.gui.touchDistance);
    }
}
