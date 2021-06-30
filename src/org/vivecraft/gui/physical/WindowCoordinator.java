package org.vivecraft.gui.physical;

import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.vivecraft.physicalinventory.Semaphore;

public class WindowCoordinator extends Thread implements PhysicalGuiManager.WindowReceivedListener
{
    PhysicalGuiManager guiManager;
    Minecraft mc;
    Queue<WindowCoordinator.WindowOperation> operationQueue = new LinkedTransferQueue<>();
    private final Object populatedSemaphore = new Object();
    private final Object emptySemaphore = new Object();

    public WindowCoordinator(PhysicalGuiManager mgr)
    {
        this.guiManager = mgr;
        this.mc = mgr.mc;
    }

    public void run()
    {
        while (true)
        {
            try
            {
                this.waitForQueuePopulated();
                WindowCoordinator.WindowOperation windowcoordinator$windowoperation = this.operationQueue.peek();
                windowcoordinator$windowoperation.execute();
                this.operationQueue.poll();

                synchronized (this.emptySemaphore)
                {
                    if (this.operationQueue.isEmpty())
                    {
                        this.emptySemaphore.notifyAll();
                    }
                }
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
    }

    public void enqueueOperation(WindowCoordinator.WindowOperation operation)
    {
        this.operationQueue.add(operation);

        synchronized (this.populatedSemaphore)
        {
            this.populatedSemaphore.notifyAll();
        }
    }

    public void waitForQueuePopulated()
    {
        if (this.operationQueue.isEmpty())
        {
            synchronized (this.populatedSemaphore)
            {
                try
                {
                    this.populatedSemaphore.wait();
                }
                catch (InterruptedException interruptedexception)
                {
                    interruptedexception.printStackTrace();
                }
            }
        }
    }

    public void waitForQueueEmpty()
    {
        if (!this.operationQueue.isEmpty())
        {
            synchronized (this.emptySemaphore)
            {
                try
                {
                    this.emptySemaphore.wait();
                }
                catch (InterruptedException interruptedexception)
                {
                    interruptedexception.printStackTrace();
                }
            }
        }
    }

    public void onWindowReceived()
    {
        WindowCoordinator.WindowOperation windowcoordinator$windowoperation = this.operationQueue.peek();

        if (windowcoordinator$windowoperation != null && windowcoordinator$windowoperation instanceof PhysicalGuiManager.WindowReceivedListener)
        {
            ((PhysicalGuiManager.WindowReceivedListener)windowcoordinator$windowoperation).onWindowReceived();
        }
    }

    public static int[] getFreeSlotsInInventory(int count)
    {
        if (count == 0)
        {
            return new int[0];
        }
        else
        {
            AbstractContainerMenu abstractcontainermenu = Minecraft.getInstance().player.containerMenu;
            PhysicalGui.InventoryMetaData physicalgui$inventorymetadata = PhysicalGui.analyseInventory(abstractcontainermenu);
            int i = physicalgui$inventorymetadata.inventoryOffset;
            int[] aint = new int[count];

            for (int j = 0; j < aint.length; ++j)
            {
                aint[j] = -1;
            }

            int l = 0;

            for (int k = 0; k < 36; ++k)
            {
                ItemStack itemstack = abstractcontainermenu.slots.get(k + i).getItem();

                if (itemstack.isEmpty())
                {
                    if (l >= count)
                    {
                        break;
                    }

                    aint[l] = k + i;
                    ++l;
                }
            }

            return aint;
        }
    }

    public static class ClearInventoryOperation extends WindowCoordinator.WindowOperation
    {
        public void execute()
        {
            for (int i = 1; i <= 45; ++i)
            {
                this.mc.player.connection.send(new ServerboundSetCreativeModeSlotPacket(i, ItemStack.EMPTY));
            }
        }
    }

    public static class ClickOperation extends WindowCoordinator.WindowOperation
    {
        int slotId;
        int mouseButton;
        PhysicalGuiManager guiManager;
        ClickType type = ClickType.PICKUP;
        boolean raw = false;

        public ClickOperation(PhysicalGuiManager guiManager, int slotId, ClickType type, boolean raw, int mouseButton)
        {
            this(guiManager, slotId, mouseButton);
            this.type = type;
            this.raw = raw;
        }

        public ClickOperation(PhysicalGuiManager guiManager, int slotId, int mouseButton)
        {
            this.slotId = slotId;
            this.mouseButton = mouseButton;
            this.guiManager = guiManager;
        }

        public String toString()
        {
            return "ClickOperation{slotId=" + this.slotId + ", mouseButton=" + this.mouseButton + ", guiManager=" + this.guiManager + ", type=" + this.type + ", raw=" + this.raw + '}';
        }

        public void execute()
        {
            AbstractContainerMenu abstractcontainermenu = this.mc.player.containerMenu;
            int i = this.mc.player.inventory.selected;
            PhysicalGui.InventoryMetaData physicalgui$inventorymetadata = PhysicalGui.analyseInventory(abstractcontainermenu);
            int j = physicalgui$inventorymetadata.hotbarOffset;

            if (!this.raw && this.guiManager.isHoldingHotbarSlot && !this.mc.player.inventory.getSelected().isEmpty())
            {
                if (this.slotId == j + i)
                {
                    this.guiManager.isHoldingHotbarSlot = false;
                    return;
                }

                this.mc.player.windowClickSynced(abstractcontainermenu.containerId, i + j, 0, ClickType.PICKUP, 1000L);
                this.mc.player.windowClickSynced(abstractcontainermenu.containerId, this.slotId, this.mouseButton, this.type, 1000L);
                this.guiManager.isHoldingHotbarSlot = false;
            }
            else
            {
                this.mc.player.windowClickSynced(abstractcontainermenu.containerId, this.slotId, this.mouseButton, this.type, 1000L);
            }
        }
    }

    public static class CloseWindowOperation extends WindowCoordinator.WindowOperation
    {
        public void execute()
        {
            this.mc.player.connection.send(new ServerboundContainerClosePacket(this.mc.player.containerMenu.containerId));
            this.mc.player.inventory.setCarried(ItemStack.EMPTY);
            this.mc.player.containerMenu = this.mc.player.inventoryMenu;

            try
            {
                Thread.sleep(10L);
            }
            catch (InterruptedException interruptedexception)
            {
                interruptedexception.printStackTrace();
            }
        }
    }

    public static class FabricateItemOperation extends WindowCoordinator.WindowOperation
    {
        ItemStack item;

        public FabricateItemOperation(ItemStack item)
        {
            this.item = item;
        }

        public void execute()
        {
            PhysicalGui.InventoryMetaData physicalgui$inventorymetadata = PhysicalGui.analyseInventory(this.mc.player.containerMenu);

            if (this.mc.physicalGuiManager.isHoldingHotbarSlot && !this.mc.player.inventory.getSelected().isEmpty())
            {
                this.mc.physicalGuiManager.isHoldingHotbarSlot = false;
                this.mc.player.connection.send(new ServerboundSetCreativeModeSlotPacket(36 + this.mc.player.inventory.selected, ItemStack.EMPTY));
            }
            else
            {
                ItemStack itemstack;

                if (!this.mc.player.inventory.getCarried().isEmpty())
                {
                    itemstack = ItemStack.EMPTY;
                }
                else
                {
                    itemstack = this.item;
                }

                this.mc.physicalGuiManager.isHoldingHotbarSlot = false;
                ItemStack itemstack1 = this.mc.player.containerMenu.getSlot(physicalgui$inventorymetadata.inventoryOffset).getItem().copy();
                this.mc.player.connection.send(new ServerboundSetCreativeModeSlotPacket(9, itemstack));
                this.mc.player.windowClickSynced(this.mc.player.containerMenu.containerId, physicalgui$inventorymetadata.inventoryOffset, 0, ClickType.PICKUP, 1000L);
                this.mc.player.connection.send(new ServerboundSetCreativeModeSlotPacket(9, itemstack1));
            }
        }
    }

    public static class FakeHoldOperation extends WindowCoordinator.WindowOperation
    {
        boolean holding;

        public FakeHoldOperation(boolean holding)
        {
            this.holding = holding;
        }

        public void execute()
        {
            this.mc.physicalGuiManager.isHoldingHotbarSlot = this.holding;
        }
    }

    public static class OpenWindowOperation extends WindowCoordinator.WindowOperation implements PhysicalGuiManager.WindowReceivedListener
    {
        PhysicalGui gui;
        PhysicalGuiManager manager;
        final Semaphore windowRecievedSemaphore = new Semaphore(5000L);

        public OpenWindowOperation(PhysicalGuiManager manager, PhysicalGui gui)
        {
            this.gui = gui;
            this.manager = manager;
        }

        public void onWindowReceived()
        {
            this.windowRecievedSemaphore.wakeUp();
        }

        public void execute()
        {
            this.windowRecievedSemaphore.reactivate();
            boolean flag = this.gui.requestOpen();

            if (flag)
            {
                this.manager.activeGui = this.gui;
                this.manager.requestWindowIntercept();
                this.windowRecievedSemaphore.waitFor();
            }
        }
    }

    public abstract static class WindowOperation
    {
        Minecraft mc = Minecraft.getInstance();

        public abstract void execute();
    }
}
