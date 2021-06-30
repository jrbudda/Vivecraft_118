package org.vivecraft.gui.physical.interactables;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.gui.physical.PhysicalInventory;
import org.vivecraft.utils.math.Quaternion;

public class CreativeTabButton extends Button
{
    public PhysicalInventory inventory;
    public CreativeModeTab tab;

    public CreativeTabButton(PhysicalInventory inventory, CreativeModeTab tab)
    {
        super(tab.getIconItem());
        this.inventory = inventory;
        this.tab = tab;
    }

    public Vec3 getAnchorPos(double partialTicks)
    {
        return this.inventory.getAnchorPos(partialTicks);
    }

    public Quaternion getAnchorRotation(double partialTicks)
    {
        return this.inventory.getAnchorRotation(partialTicks);
    }

    public void click(int button)
    {
        super.click(button);
        this.inventory.setSelectedTab(this.tab);
        this.inventory.refreshButtonStates();
    }
}
