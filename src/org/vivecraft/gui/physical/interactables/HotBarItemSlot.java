package org.vivecraft.gui.physical.interactables;

import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.gui.physical.PhysicalInventory;
import org.vivecraft.gui.physical.WindowCoordinator;
import org.vivecraft.utils.math.Quaternion;

public class HotBarItemSlot extends PhysicalItemSlot
{
    PhysicalInventory.Hotbar gui;

    public HotBarItemSlot(PhysicalInventory.Hotbar gui, int slotId)
    {
        super(gui, slotId);
        this.gui = gui;
    }

    public void touch()
    {
        if (this.gui.parent.isOpen())
        {
            super.touch();
        }
        else
        {
            this.popOut = true;
        }
    }

    public Vec3 getAnchorPos(double partialTicks)
    {
        return this.gui.getAnchorPos(partialTicks);
    }

    public Quaternion getAnchorRotation(double partialTicks)
    {
        return this.gui.getAnchorRotation(partialTicks);
    }

    public void click(int button)
    {
        int i = this.gui.parent.metaData.hotbarOffset;

        if (this.gui.parent.isOpen())
        {
            super.click(button);
        }
        else
        {
            for (Interactable interactable : this.gui.interactables)
            {
                if (interactable instanceof PhysicalItemSlot)
                {
                    PhysicalItemSlot physicalitemslot = (PhysicalItemSlot)interactable;
                    physicalitemslot.opacity = 1.0D;
                }
            }

            if (this.mc.player.inventory.selected == this.slotId - i)
            {
                if (this.mc.physicalGuiManager.isHoldingHotbarSlot)
                {
                    this.mc.physicalGuiManager.isHoldingHotbarSlot = false;
                }
                else
                {
                    this.mc.physicalGuiManager.isHoldingHotbarSlot = true;
                    this.opacity = 0.1D;
                }
            }
            else
            {
                this.mc.physicalGuiManager.isHoldingHotbarSlot = true;
                this.opacity = 0.1D;
            }

            this.mc.player.inventory.selected = this.slotId - i;
        }
    }

    public void onDragDrop(Interactable source)
    {
        if (source instanceof HotBarItemSlot)
        {
            HotBarItemSlot hotbaritemslot = (HotBarItemSlot)source;
            int i = this.slotId - this.gui.metaData.hotbarOffset;
            this.mc.physicalGuiManager.windowCoordinator.enqueueOperation(new WindowCoordinator.ClickOperation(this.mc.physicalGuiManager, hotbaritemslot.slotId, ClickType.SWAP, true, i));
        }
    }
}
