package org.vivecraft.gui.physical.interactables;

import java.util.ArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.api.VRData;
import org.vivecraft.gui.physical.PhysicalInventory;
import org.vivecraft.gui.physical.PhysicalItemSlotGui;
import org.vivecraft.utils.math.Quaternion;

public class ArmorDisplay extends PhysicalItemSlotGui
{
    PhysicalInventory inventory;
    boolean armorMode;
    ArrayList<ArmorDisplay.ArmorItemSlot> armorSlots = new ArrayList<>();

    public ArmorDisplay(PhysicalInventory inventory)
    {
        super(inventory.entity);
        this.inventory = inventory;
    }

    public void open(Object payload)
    {
        this.container = this.inventory.container;
        this.loadSlots();
        this.isOpen = true;
    }

    public void close()
    {
        this.isOpen = false;
        this.setArmorMode(false);
    }

    public void setArmorMode(boolean armorMode)
    {
        this.armorMode = armorMode;

        for (ArmorDisplay.ArmorItemSlot armordisplay$armoritemslot : this.armorSlots)
        {
            armordisplay$armoritemslot.enabled = armorMode;
        }

        this.mc.vrSettings.tmpRenderSelf = armorMode;
    }

    public void tryOpenWindow()
    {
    }

    public void render(double partialTicks)
    {
        super.render(partialTicks);
    }

    public void loadSlots()
    {
        this.armorSlots.clear();
        this.interactables.clear();

        if (this.inventory.metaData.hasExtra && this.inventory.container != null)
        {
            int i = this.inventory.metaData.armorOffset;
            ArmorDisplay.ArmorItemSlot armordisplay$armoritemslot = new ArmorDisplay.ArmorItemSlot(this, i);
            armordisplay$armoritemslot.feetBound = false;
            armordisplay$armoritemslot.position = new Vec3(0.0D, 0.0D, 0.0D);
            armordisplay$armoritemslot.slot = this.inventory.container.getSlot(armordisplay$armoritemslot.slotId);
            this.armorSlots.add(armordisplay$armoritemslot);
            ArmorDisplay.ArmorItemSlot armordisplay$armoritemslot1 = new ArmorDisplay.ArmorItemSlot(this, i + 1);
            armordisplay$armoritemslot1.position = new Vec3(0.0D, -0.4D, 0.0D);
            armordisplay$armoritemslot1.feetBound = false;
            armordisplay$armoritemslot1.slot = this.inventory.container.getSlot(armordisplay$armoritemslot1.slotId);
            this.armorSlots.add(armordisplay$armoritemslot1);
            ArmorDisplay.ArmorItemSlot armordisplay$armoritemslot2 = new ArmorDisplay.ArmorItemSlot(this, i + 2);
            armordisplay$armoritemslot2.slot = this.inventory.container.getSlot(armordisplay$armoritemslot2.slotId);
            armordisplay$armoritemslot2.feetBound = true;
            armordisplay$armoritemslot2.position = new Vec3(0.0D, 0.7D, 0.0D);
            this.armorSlots.add(armordisplay$armoritemslot2);
            ArmorDisplay.ArmorItemSlot armordisplay$armoritemslot3 = new ArmorDisplay.ArmorItemSlot(this, i + 3);
            armordisplay$armoritemslot3.position = new Vec3(0.0D, 0.1D, 0.0D);
            armordisplay$armoritemslot3.feetBound = true;
            armordisplay$armoritemslot3.slot = this.inventory.container.getSlot(armordisplay$armoritemslot3.slotId);
            this.armorSlots.add(armordisplay$armoritemslot3);

            for (ArmorDisplay.ArmorItemSlot armordisplay$armoritemslot4 : this.armorSlots)
            {
                armordisplay$armoritemslot4.enabled = this.armorMode;
            }

            this.interactables.addAll(this.armorSlots);
        }
    }

    public Vec3 getAnchorPos(double partialTicks)
    {
        return new Vec3(this.mc.gameRenderer.rveX, this.mc.gameRenderer.rveY, this.mc.gameRenderer.rveZ);
    }

    public Quaternion getAnchorRotation(double partialTicks)
    {
        return new Quaternion(0.0F, -this.entity.yRot, 0.0F);
    }

    class ArmorItemSlot extends PhysicalItemSlot
    {
        public AABB boundingBox = super.getBoundingBox();
        boolean placed;
        public boolean feetBound;

        public ArmorItemSlot(PhysicalItemSlotGui gui, int slotId)
        {
            super(gui, slotId);
            this.preview = false;
        }

        public ItemStack getDisplayedItem()
        {
            return ItemStack.EMPTY;
        }

        public void touch()
        {
            if (!this.mc.physicalGuiManager.getVirtualHeldItem().isEmpty())
            {
                super.click(0);
                this.placed = false;
            }
            else
            {
                this.placed = true;
            }
        }

        public void untouch()
        {
            if (!this.placed && !this.slot.getItem().isEmpty())
            {
                super.click(0);
            }
        }

        public void click(int button)
        {
            if (this.placed)
            {
                super.click(0);
            }

            this.placed = true;
        }

        public boolean isEnabled()
        {
            return !this.mc.physicalGuiManager.getVirtualHeldItem().isEmpty() && !this.slot.mayPlace(this.mc.physicalGuiManager.getVirtualHeldItem()) ? false : super.isEnabled();
        }

        public AABB getBoundingBox()
        {
            return this.boundingBox;
        }

        public Vec3 getAnchorPos(double partialTicks)
        {
            if (this.feetBound)
            {
                return ArmorDisplay.this.getAnchorPos(partialTicks);
            }
            else
            {
                VRData vrdata = this.mc.vrPlayer.getVRDataWorld();
                return vrdata.hmd.getPosition();
            }
        }
    }
}
