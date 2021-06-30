package org.vivecraft.gui.physical.interactables;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.vivecraft.gui.physical.PhysicalItemSlotGui;
import org.vivecraft.gui.physical.WindowCoordinator;

public class CreativeItemSlot extends PhysicalItemSlot
{
    ItemStack prefabItem;

    public CreativeItemSlot(PhysicalItemSlotGui gui, ItemStack item, int fakeSlotnum)
    {
        super(gui, fakeSlotnum);
        this.prefabItem = item;
    }

    public ItemStack getDisplayedItem()
    {
        return this.prefabItem;
    }

    public void click(int button)
    {
        ItemStack itemstack = this.prefabItem.copy();
        itemstack.setCount(button == 0 ? itemstack.getMaxStackSize() : 1);

        if (this.mc.physicalGuiManager.getVirtualHeldItem().isEmpty())
        {
            this.mc.physicalGuiManager.playerInventory.setSelectedTab(CreativeModeTab.TAB_INVENTORY);
            this.mc.physicalGuiManager.playerInventory.refreshButtonStates();
        }

        this.mc.physicalGuiManager.windowCoordinator.enqueueOperation(new WindowCoordinator.FabricateItemOperation(itemstack));
    }
}
