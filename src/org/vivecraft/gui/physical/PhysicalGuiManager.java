package org.vivecraft.gui.physical;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.vivecraft.gameplay.trackers.Tracker;
import org.vivecraft.utils.Utils;

public class PhysicalGuiManager extends Tracker
{
    public ArrayList<PhysicalGui> guisInRange = new ArrayList<>();
    public PhysicalGui activeGui = null;
    public PhysicalInventory playerInventory = null;
    public WindowCoordinator windowCoordinator;
    boolean isGuiOpen;
    ItemStack guiTransitionOverride = null;
    public boolean isHoldingHotbarSlot = true;
    long interceptRequestTime = -1L;
    int interceptTimeout = 1000;
    ArrayList<PhysicalGuiManager.WindowReceivedListener> windowReceivedListeners = new ArrayList<>();
    boolean inTransition;
    private ItemStack hideItemTouchingSlotOverride;

    public void setHoldingHotbarSlotSafe(boolean holdingHotbarSlot)
    {
        this.windowCoordinator.enqueueOperation(new WindowCoordinator.FakeHoldOperation(holdingHotbarSlot));
    }

    public ItemStack getVirtualHeldItem()
    {
        ItemStack itemstack = this.mc.player.inventory.getCarried();

        if (!itemstack.isEmpty())
        {
            return itemstack;
        }
        else if (this.isHoldingHotbarSlot)
        {
            int i = this.mc.player.inventory.selected;
            PhysicalGui.InventoryMetaData physicalgui$inventorymetadata = PhysicalGui.analyseInventory(this.mc.player.containerMenu);
            int j = physicalgui$inventorymetadata.hotbarOffset;
            return this.mc.player.containerMenu.slots.get(j + i).getItem();
        }
        else
        {
            return ItemStack.EMPTY;
        }
    }

    public ItemStack getRawHeldItem()
    {
        return this.mc.player.inventory.getCarried();
    }

    public PhysicalGuiManager(Minecraft mc)
    {
        super(mc);
        this.windowCoordinator = new WindowCoordinator(this);
        this.registerWindowReceivedListener(this.windowCoordinator);
        this.windowCoordinator.start();
    }

    public boolean isActive(LocalPlayer player)
    {
        return false;
    }

    public void init(LocalPlayer player)
    {
        if (this.playerInventory != null)
        {
            this.reset(player);
        }

        this.playerInventory = new PhysicalInventory(player);
    }

    public void reset(LocalPlayer player)
    {
        this.guisInRange.clear();

        if (this.playerInventory != null)
        {
            this.playerInventory.close();
        }

        this.playerInventory = null;
    }

    public void preClickAction()
    {
        if (this.mc.vrSettings.physicalGuiEnabled)
        {
            if (this.activeGui != null || this.playerInventory.isOpen())
            {
                this.equipHeldItem();
            }
        }
    }

    public void doProcess(LocalPlayer player)
    {
        if (this.playerInventory == null)
        {
            this.init(player);
        }

        BlockPos blockpos = player.blockPosition();
        Iterator<PhysicalGui> iterator = this.guisInRange.iterator();

        while (iterator.hasNext())
        {
            PhysicalGui physicalgui = iterator.next();
            BlockPos blockpos1 = new BlockPos(physicalgui.getAnchorPos());
            boolean flag = Math.abs(blockpos.getX() - blockpos1.getX()) < 3 && Math.abs(blockpos.getY() - blockpos1.getY()) < 3 && Math.abs(blockpos.getZ() - blockpos1.getZ()) < 3;

            if ((!flag || !physicalgui.isAlive()) && physicalgui.isOpen())
            {
                physicalgui.close();
            }

            if (!flag && physicalgui.isFullyClosed() || !physicalgui.isAlive())
            {
                iterator.remove();
            }
        }

        for (int i = -2; i < 3; ++i)
        {
            for (int j = -2; j < 3; ++j)
            {
                label77:

                for (int k = -2; k < 3; ++k)
                {
                    BlockPos blockpos2 = new BlockPos(i + blockpos.getX(), j + blockpos.getY(), k + blockpos.getZ());
                    BlockState blockstate = player.level.getBlockState(blockpos2);
                    Block block = blockstate.getBlock();

                    if (PhysicalGui.isImplemented(block))
                    {
                        for (PhysicalGui physicalgui1 : this.guisInRange)
                        {
                            BlockPos blockpos3 = new BlockPos(physicalgui1.getAnchorPos());

                            if (blockpos3.equals(blockpos2))
                            {
                                continue label77;
                            }
                        }

                        if (PhysicalGui.isMainPart(player, blockstate, blockpos2))
                        {
                            PhysicalGui physicalgui3 = PhysicalGui.createFromBlock(player, block, blockpos2);

                            if (physicalgui3 != null)
                            {
                                this.guisInRange.add(physicalgui3);
                            }
                        }
                    }
                }
            }
        }

        for (PhysicalGui physicalgui2 : this.guisInRange)
        {
            physicalgui2.onUpdate();
        }

        if (this.playerInventory != null)
        {
            this.playerInventory.onUpdate();
        }
    }

    public void requestWindowIntercept()
    {
        this.interceptRequestTime = Utils.milliTime();
    }

    public boolean isIntercepting()
    {
        return Utils.milliTime() - this.interceptRequestTime < (long)this.interceptTimeout;
    }

    public void registerWindowReceivedListener(PhysicalGuiManager.WindowReceivedListener listener)
    {
        this.windowReceivedListeners.add(listener);
    }

    public void handleWindow(Object payload)
    {
        this.interceptRequestTime = -1L;

        if (this.activeGui != null)
        {
            this.activeGui.open(payload);
        }

        for (PhysicalGuiManager.WindowReceivedListener physicalguimanager$windowreceivedlistener : this.windowReceivedListeners)
        {
            physicalguimanager$windowreceivedlistener.onWindowReceived();
        }
    }

    public void doRender(double partialTicks)
    {
        GlStateManager._enableLighting();

        for (PhysicalGui physicalgui : this.guisInRange)
        {
            if (!physicalgui.isFullyClosed())
            {
                physicalgui.render(partialTicks);
            }
        }

        if (this.playerInventory != null)
        {
            if (!this.playerInventory.isFullyClosed())
            {
                this.playerInventory.render(partialTicks);
            }

            this.playerInventory.hotbar.render(partialTicks);
        }
    }

    public boolean requestGuiSwitch(final PhysicalGui gui)
    {
        if (!this.inTransition && !this.isIntercepting() && (this.activeGui == null || !this.activeGui.equals(gui)))
        {
            this.inTransition = true;
            (new Thread(new Runnable()
            {
                public void run()
                {
                    Logger logger = Logger.getLogger("inv");
                    logger.info("Switching from gui " + PhysicalGuiManager.this.activeGui + " to " + gui);
                    PhysicalGuiManager.this.playerInventory.preGuiChange(gui);
                    PhysicalGuiManager.this.windowCoordinator.waitForQueueEmpty();

                    if (PhysicalGuiManager.this.activeGui != null)
                    {
                        PhysicalGuiManager.this.activeGui.close();
                        PhysicalGuiManager.this.windowCoordinator.waitForQueueEmpty();
                    }

                    if (gui != null)
                    {
                        logger.info("Opening new window");
                        PhysicalGuiManager.this.windowCoordinator.enqueueOperation(new WindowCoordinator.OpenWindowOperation(PhysicalGuiManager.this, gui));
                    }
                    else
                    {
                        logger.info("Closing window");
                        PhysicalGuiManager.this.mc.physicalGuiManager.windowCoordinator.enqueueOperation(new WindowCoordinator.CloseWindowOperation());
                    }

                    PhysicalGuiManager.this.windowCoordinator.waitForQueueEmpty();
                    PhysicalGuiManager.this.playerInventory.postGuiChange(gui);
                    PhysicalGuiManager.this.windowCoordinator.waitForQueueEmpty();
                    PhysicalGuiManager.this.inTransition = false;
                    logger.info("Gui switch completed");
                }
            })).start();
            return true;
        }
        else
        {
            return false;
        }
    }

    public ItemStack getHeldItemOverride()
    {
        if (this.hideItemTouchingSlotOverride != null)
        {
            return this.hideItemTouchingSlotOverride;
        }
        else if (this.guiTransitionOverride != null)
        {
            return this.guiTransitionOverride;
        }
        else if (this.isGuiOpen)
        {
            return this.getVirtualHeldItem();
        }
        else
        {
            return !this.isHoldingHotbarSlot ? ItemStack.EMPTY : null;
        }
    }

    public ItemStack getOffhandOverride()
    {
        return this.playerInventory != null && this.playerInventory.isOpen() ? this.playerInventory.offhand.getDisplayedItem() : null;
    }

    public void setHideItemTouchingSlotOverride(ItemStack hideItemTouchingSlotOverride)
    {
        this.hideItemTouchingSlotOverride = hideItemTouchingSlotOverride;
    }

    public void clickSlot(int slotId, int mouseButton, boolean raw, ClickType clickType)
    {
        this.windowCoordinator.enqueueOperation(new WindowCoordinator.ClickOperation(this, slotId, clickType, raw, mouseButton));
    }

    public void clickSlot(int slotId, int mouseButton)
    {
        this.clickSlot(slotId, mouseButton, false, ClickType.PICKUP);
    }

    void equipHeldItem()
    {
        ItemStack itemstack = this.getRawHeldItem();

        if (!itemstack.isEmpty())
        {
            int i = 4;
            PhysicalGui.InventoryMetaData physicalgui$inventorymetadata = PhysicalGui.analyseInventory(this.mc.player.containerMenu);

            if (physicalgui$inventorymetadata == null)
            {
                return;
            }

            ItemStack itemstack1 = this.mc.player.containerMenu.getSlot(physicalgui$inventorymetadata.hotbarOffset + i).getItem();

            if (itemstack1.isEmpty())
            {
                this.mc.physicalGuiManager.clickSlot(physicalgui$inventorymetadata.hotbarOffset + i, 0, true, ClickType.PICKUP);
                this.mc.player.inventory.selected = i;
                this.setHoldingHotbarSlotSafe(true);
                return;
            }

            for (int j = 0; j < 9; ++j)
            {
                ItemStack itemstack2 = this.mc.player.containerMenu.getSlot(physicalgui$inventorymetadata.hotbarOffset + j).getItem();

                if (itemstack2.isEmpty())
                {
                    this.mc.physicalGuiManager.clickSlot(physicalgui$inventorymetadata.hotbarOffset + j, 0, true, ClickType.PICKUP);
                    this.mc.player.inventory.selected = j;
                    this.setHoldingHotbarSlotSafe(true);
                    return;
                }
            }

            this.mc.physicalGuiManager.clickSlot(physicalgui$inventorymetadata.hotbarOffset + i, 0, true, ClickType.PICKUP);
            this.mc.player.inventory.selected = i;
            this.setHoldingHotbarSlotSafe(true);
            int[] aint = WindowCoordinator.getFreeSlotsInInventory(1);

            if (aint[0] != -1)
            {
                this.mc.physicalGuiManager.clickSlot(aint[0], 0, true, ClickType.PICKUP);
            }
            else
            {
                this.mc.physicalGuiManager.clickSlot(-999, 0, true, ClickType.PICKUP);
            }
        }
    }

    void onGuiOpened()
    {
        int i = 0;

        for (PhysicalGui physicalgui : this.guisInRange)
        {
            if (physicalgui.isOpen())
            {
                ++i;
            }
        }

        if (this.playerInventory.isOpen())
        {
            ++i;
        }

        if (i == 1)
        {
            this.isGuiOpen = true;
        }
    }

    void onGuiClosed()
    {
        int i = 0;

        for (PhysicalGui physicalgui : this.guisInRange)
        {
            if (physicalgui.isOpen())
            {
                ++i;
            }
        }

        if (this.playerInventory.isOpen())
        {
            ++i;
        }

        if (i == 0)
        {
            this.equipHeldItem();
            this.isGuiOpen = false;
        }
    }

    public void toggleInventoryBag()
    {
        if (this.playerInventory.isFullyClosed())
        {
            this.playerInventory.showBag();
        }
        else
        {
            this.playerInventory.hideBag();
        }
    }

    public interface WindowReceivedListener
    {
        void onWindowReceived();
    }
}
