package org.vivecraft.gui.physical;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.CraftingTableBlock;
import net.minecraft.world.level.block.EnderChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.gui.physical.interactables.Interactable;
import org.vivecraft.utils.math.Quaternion;

public abstract class PhysicalGui
{
    public AbstractContainerMenu container = null;
    public Minecraft mc = Minecraft.getInstance();
    public Interactable touching;
    public Interactable clicked;

    public abstract void render(double var1);

    public abstract void close();

    public abstract void open(Object var1);

    public abstract boolean requestOpen();

    public abstract Vec3 getAnchorPos(double var1);

    public abstract Quaternion getAnchorRotation(double var1);

    public final Vec3 getAnchorPos()
    {
        return this.getAnchorPos((double)Minecraft.getInstance().getFrameTime());
    }

    public final Quaternion getAnchorRotation()
    {
        return this.getAnchorRotation((double)Minecraft.getInstance().getFrameTime());
    }

    public abstract boolean isOpen();

    public abstract boolean isFullyClosed();

    public abstract boolean isAlive();

    public abstract void onUpdate();

    static boolean isImplemented(Block block)
    {
        if (!(block instanceof ChestBlock) && !(block instanceof EnderChestBlock))
        {
            return block instanceof CraftingTableBlock;
        }
        else
        {
            return true;
        }
    }

    static String getBlockId(Block block)
    {
        return Registry.BLOCK.getKey(block).toString();
    }

    static PhysicalGui createFromBlock(Player player, Block block, BlockPos pos)
    {
        if (block instanceof CraftingTableBlock)
        {
            return new PhysicalItemSlotGui(pos);
        }
        else
        {
            return !(block instanceof ChestBlock) && !(block instanceof EnderChestBlock) ? null : new PhysicalChest(pos);
        }
    }

    static boolean isMainPart(Player player, BlockState blockState, BlockPos pos)
    {
        return true;
    }

    public static Quaternion getBlockOrientation(BlockPos pos)
    {
        return null;
    }

    public static PhysicalGui.InventoryMetaData analyseInventory(AbstractContainerMenu container)
    {
        if (container == null)
        {
            return null;
        }
        else
        {
            int i = -1;
            int j = -1;
            int k = -1;
            int l = 0;

            for (int i1 = 0; i1 < container.slots.size(); ++i1)
            {
                Slot slot = container.slots.get(i1);

                if (slot instanceof ResultSlot && k == -1)
                {
                    k = i1;
                }

                if (slot.container instanceof Inventory)
                {
                    if (j == -1)
                    {
                        j = i1;
                    }

                    ++l;
                }
            }

            if (l == 41)
            {
                i = j;
                j += 4;
            }
            else if (l != 36)
            {
                j = -1;
            }

            PhysicalGui.InventoryMetaData physicalgui$inventorymetadata = new PhysicalGui.InventoryMetaData();
            physicalgui$inventorymetadata.armorOffset = i;
            physicalgui$inventorymetadata.inventoryOffset = j;
            physicalgui$inventorymetadata.craftingOffset = k;
            physicalgui$inventorymetadata.hotbarOffset = j + 27;
            physicalgui$inventorymetadata.hasExtra = i != -1;
            return physicalgui$inventorymetadata;
        }
    }

    public static class InventoryMetaData
    {
        public int inventoryOffset;
        public int armorOffset;
        public int hotbarOffset;
        public int craftingOffset;
        public boolean hasExtra;
    }
}
