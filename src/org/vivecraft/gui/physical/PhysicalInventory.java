package org.vivecraft.gui.physical;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.api.VRData;
import org.vivecraft.gui.physical.interactables.ArmorDisplay;
import org.vivecraft.gui.physical.interactables.Button;
import org.vivecraft.gui.physical.interactables.CreativeItemSlot;
import org.vivecraft.gui.physical.interactables.CreativeTabButton;
import org.vivecraft.gui.physical.interactables.HotBarItemSlot;
import org.vivecraft.gui.physical.interactables.Interactable;
import org.vivecraft.gui.physical.interactables.MiniCrafting;
import org.vivecraft.gui.physical.interactables.PhysicalItemSlot;
import org.vivecraft.gui.physical.interactables.Slider;
import org.vivecraft.gui.physical.interactables.Trashbin;
import org.vivecraft.utils.Utils;
import org.vivecraft.utils.math.Quaternion;

public class PhysicalInventory extends PhysicalItemSlotGui
{
    public PhysicalInventory.Hotbar hotbar;
    LocalPlayer player;
    int layer;
    ModelResourceLocation bagLoc;
    ModelResourceLocation bagLocGold;
    ModelResourceLocation bagLocClosed;
    ModelResourceLocation bagLocGoldClosed;
    double slotOffset;
    boolean fullyClosed = true;
    int heldItemSlot = -1;
    ArrayList<Interactable> miscInteractables = new ArrayList<>();
    CreativeModeTab selectedTab = CreativeModeTab.TAB_INVENTORY;
    ArrayList<CreativeTabButton> creativeTabButtons = new ArrayList<>();
    PhysicalItemSlot offhand = null;
    MiniCrafting miniCrafting;
    ArmorDisplay armorDisplay;
    HashMap<CreativeModeTab, List<PhysicalItemSlot>> tabs = new HashMap<>();
    double scrollPos = 0.0D;
    CreativeModeTab bkgTab;
    double bkgScroll;
    boolean overridingTab = false;
    long transitionTimeStamp = -1L;
    int putDownItemSlot = -1;

    public PhysicalInventory(LocalPlayer player)
    {
        super(player);
        this.player = player;
        this.bagLoc = new ModelResourceLocation("vivecraft:bag", "type=normal");
        this.bagLocGold = new ModelResourceLocation("vivecraft:bag", "type=gold");
        this.bagLocClosed = new ModelResourceLocation("vivecraft:bag", "type=closed");
        this.bagLocGoldClosed = new ModelResourceLocation("vivecraft:bag", "type=gold_closed");
        this.hotbar = new PhysicalInventory.Hotbar(this);
        this.miniCrafting = new MiniCrafting(this);
        this.armorDisplay = new ArmorDisplay(this);
        this.container = player.inventoryMenu;
        this.loadSlots();
        this.hotbar.open((Object)null);
    }

    public boolean isFullyClosed()
    {
        return this.fullyClosed;
    }

    public void open(Object payload)
    {
        if (!this.player.isCreative())
        {
            this.selectedTab = CreativeModeTab.TAB_INVENTORY;
        }

        this.isOpen = true;
        this.fullyClosed = false;
        this.loadSlots();
        this.hotbar.open((Object)null);
        this.armorDisplay.open((Object)null);
        this.miniCrafting.setExtended(false);
        this.mc.physicalGuiManager.onGuiOpened();
    }

    public void close()
    {
        this.isOpen = false;
        this.armorDisplay.close();
        this.mc.physicalGuiManager.onGuiClosed();
    }

    public void hideBag()
    {
        if (this.isOpen)
        {
            this.close();
        }

        this.fullyClosed = true;
    }

    public void showBag()
    {
        this.fullyClosed = false;
    }

    void loadSlots()
    {
        if (this.container != null)
        {
            this.interactables.clear();
            this.miscInteractables.clear();
            this.creativeTabButtons.clear();
            this.metaData = analyseInventory(this.container);
            this.populateTabs();

            if (this.metaData != null && this.metaData.inventoryOffset != -1)
            {
                Vec3 vec3 = new Vec3(-0.25D, -0.65D, 0.2D);
                double d0 = 0.5D;
                double d1 = 0.4D;
                double d2 = 0.5D;

                for (Entry<CreativeModeTab, List<PhysicalItemSlot>> entry : this.tabs.entrySet())
                {
                    for (int i = 0; i < entry.getValue().size(); ++i)
                    {
                        PhysicalItemSlot physicalitemslot = entry.getValue().get(i);
                        double d3 = d0 / 3.0D;
                        double d4 = d1 / 3.0D;
                        double d5 = d2 / 3.0D;
                        int j;

                        if (physicalitemslot instanceof CreativeItemSlot)
                        {
                            j = i;
                        }
                        else
                        {
                            j = physicalitemslot.slotId - this.metaData.inventoryOffset;
                            physicalitemslot.slot = this.container.slots.get(j + this.metaData.inventoryOffset);
                        }

                        int k = j / 9;
                        int l = j % 9 / 3;
                        int i1 = j % 9 % 3;
                        physicalitemslot.position = vec3.add(new Vec3(-d3 * (double)i1, d4 * (double)k, -d5 * (double)l));
                        physicalitemslot.rotation = new Quaternion(90.0F, 0.0F, 0.0F);
                        physicalitemslot.fullBlockScaleMult = 1.8D;
                        physicalitemslot.scale = 0.15D;
                        physicalitemslot.opacity = 1.0D;

                        if (this.selectedTab.equals(entry.getKey()))
                        {
                            this.interactables.add(physicalitemslot);
                        }
                    }
                }

                if (this.player.isCreative())
                {
                    Slider slider = new Slider()
                    {
                        public Quaternion getAnchorRotation(double partialTicks)
                        {
                            return PhysicalInventory.this.getAnchorRotation(partialTicks);
                        }
                        public Vec3 getAnchorPos(double partialTicks)
                        {
                            return PhysicalInventory.this.getAnchorPos(partialTicks);
                        }
                    };
                    slider.position = new Vec3(-0.712D, 0.02D, 0.0D);
                    slider.registerScrollListener(new Slider.ScrollListener()
                    {
                        public void onScroll(double perc)
                        {
                            PhysicalInventory.this.scrollTo(perc);
                        }
                    });
                    this.miscInteractables.add(slider);
                    Trashbin trashbin = new Trashbin(this);
                    trashbin.position = new Vec3(-0.1D, 0.03D, 0.3D);
                    trashbin.rotation = new Quaternion(90.0F, 0.0F, 0.0F);
                    trashbin.scale = 0.1D;
                    this.miscInteractables.add(trashbin);
                }

                Button button = new Button(new ItemStack(Items.CHAINMAIL_CHESTPLATE))
                {
                    public Vec3 getAnchorPos(double partialTicks)
                    {
                        return PhysicalInventory.this.getAnchorPos(partialTicks);
                    }
                    public Quaternion getAnchorRotation(double partialTicks)
                    {
                        return PhysicalInventory.this.getAnchorRotation(partialTicks);
                    }
                    public void click(int button)
                    {
                        super.click(button);

                        if (this.isDown)
                        {
                            PhysicalInventory.this.requestFatInventory();
                            PhysicalInventory.this.armorDisplay.setArmorMode(true);
                        }
                        else
                        {
                            PhysicalInventory.this.armorDisplay.setArmorMode(false);
                        }
                    }
                };
                button.toggle = true;
                button.rotation = new Quaternion(-90.0F, -90.0F, 0.0F);
                button.position = new Vec3(-0.07D, -0.5D, 0.0D);
                this.miscInteractables.add(button);
                this.loadTabButtons();
                this.loadFatInvSlots();
                this.interactables.addAll(this.miscInteractables);
                this.scrollTo(0.0D);
            }
        }
    }

    void loadFatInvSlots()
    {
        if (this.metaData.hasExtra)
        {
            this.offhand = new PhysicalItemSlot(this, this.metaData.hotbarOffset + 9)
            {
                public void click(int button)
                {
                    PhysicalInventory.this.requestFatInventory();
                    super.click(button);
                }
                public AABB getBoundingBox()
                {
                    return super.getBoundingBox().deflate(0.1D);
                }
                public void render(double partialTicks, int renderLayer)
                {
                }
            };
            this.offhand.slot = this.container.getSlot(this.offhand.slotId);
            this.offhand.position = new Vec3(0.0D, 0.0D, 0.1D);
            this.interactables.add(this.offhand);
        }

        this.miniCrafting.position = new Vec3(-0.75D, -0.1D, 0.0D);
        this.miscInteractables.add(this.miniCrafting);
        this.miniCrafting.loadSlots();
        this.miscInteractables.addAll(this.miniCrafting.getCraftingSlots());
    }

    public void requestFatInventory()
    {
        PhysicalGui physicalgui = this.mc.physicalGuiManager.activeGui;

        if (physicalgui != null)
        {
            physicalgui.close();
        }
    }

    void loadTabButtons()
    {
        if (this.player.isCreative())
        {
            ArrayList<CreativeModeTab> arraylist = new ArrayList<>(Arrays.asList(CreativeModeTab.TABS));
            arraylist.removeIf((it) ->
            {
                return !this.tabs.containsKey(it);
            });
            int i = arraylist.size();
            double d0 = 0.5D;
            double d1 = 0.5D;
            int j = (int)Math.ceil(Math.sqrt((double)i));
            int k = (int)((double)i / Math.sqrt((double)i));
            int l = i % k;
            Vec3 vec3 = new Vec3(-0.4D, -0.9D, -0.3D);

            for (int i1 = 0; i1 < arraylist.size(); ++i1)
            {
                CreativeModeTab creativemodetab = arraylist.get(i1);
                CreativeTabButton creativetabbutton = new CreativeTabButton(this, creativemodetab);
                int j1 = i1 / k;
                int k1 = i1 % k;
                double d2 = ((double)j1 + 0.5D) / (double)j * d1 + d1 / 2.0D;
                int l1;

                if (j1 == j - 1 && l != 0)
                {
                    l1 = l;
                }
                else
                {
                    l1 = k;
                }

                double d3 = (double)l1 / (double)k * d0;
                double d4 = ((double)k1 + 0.5D) / (double)l1 * d3 - d3 / 2.0D;
                creativetabbutton.position = vec3.add(new Vec3(d4, d2, 0.0D));
                creativetabbutton.rotation = new Quaternion(-90.0F, 0.0F, 0.0F);
                creativetabbutton.sticky = true;
                this.creativeTabButtons.add(creativetabbutton);
                this.refreshButtonStates();
            }

            this.miscInteractables.addAll(this.creativeTabButtons);
        }
    }

    public void refreshButtonStates()
    {
        for (CreativeTabButton creativetabbutton : this.creativeTabButtons)
        {
            creativetabbutton.isDown = creativetabbutton.tab.equals(this.selectedTab);
        }
    }

    public void scrollTo(double perc)
    {
        this.scrollPos = perc;
        List<PhysicalItemSlot> list = this.tabs.get(this.selectedTab);
        int i = (int)Math.ceil((double)list.size() / 9.0D);
        this.slotOffset = -perc * (double)(i - 3) * 0.13333333333333333D;
        int j = (int)((double)(i - 3) * perc);

        for (int k = 0; k < list.size(); ++k)
        {
            int l = k / 9;
            boolean flag = l >= j && l <= j + 2;
            (list.get(k)).enabled = flag;
        }
    }

    void populateTabs()
    {
        this.tabs.clear();

        for (CreativeModeTab creativemodetab : CreativeModeTab.TABS)
        {
            if (!creativemodetab.equals(CreativeModeTab.TAB_HOTBAR))
            {
                ArrayList<PhysicalItemSlot> arraylist = new ArrayList<>();

                if (creativemodetab.equals(CreativeModeTab.TAB_INVENTORY))
                {
                    for (int i = this.metaData.inventoryOffset; i < this.metaData.inventoryOffset + 27; ++i)
                    {
                        arraylist.add(new PhysicalItemSlot(this, i)
                        {
                            public Vec3 getPosition(double partialTicks)
                            {
                                return super.getPosition(partialTicks).add(new Vec3(0.0D, PhysicalInventory.this.slotOffset, 0.0D));
                            }
                            public void click(int button)
                            {
                                super.click(button);
                                PhysicalInventory.this.setTabOverride(false);
                            }
                        });
                    }
                }
                else
                {
                    if (!this.player.isCreative())
                    {
                        continue;
                    }

                    NonNullList<ItemStack> nonnulllist = NonNullList.create();
                    creativemodetab.fillItemList(nonnulllist);

                    for (int j = 0; j < nonnulllist.size(); ++j)
                    {
                        CreativeItemSlot creativeitemslot = new CreativeItemSlot(this, nonnulllist.get(j), j + this.metaData.inventoryOffset)
                        {
                            public Vec3 getPosition(double partialTicks)
                            {
                                return super.getPosition(partialTicks).add(new Vec3(0.0D, PhysicalInventory.this.slotOffset, 0.0D));
                            }
                        };
                        arraylist.add(creativeitemslot);
                    }
                }

                this.tabs.put(creativemodetab, arraylist);
            }
        }
    }

    public void setSelectedTab(CreativeModeTab selectedTab)
    {
        this.setSelectedTab(selectedTab, 0.0D);
    }

    public void setSelectedTab(CreativeModeTab selectedTab, double scroll)
    {
        this.selectedTab = selectedTab;
        this.interactables.clear();
        this.interactables.addAll(this.miscInteractables);
        this.interactables.addAll(this.tabs.get(selectedTab));
        this.scrollTo(scroll);
        this.overridingTab = false;
    }

    public void setTabOverride(boolean doOverride)
    {
        if (!this.overridingTab && doOverride)
        {
            this.bkgTab = this.selectedTab;
            this.bkgScroll = this.scrollPos;
            this.setSelectedTab(CreativeModeTab.TAB_INVENTORY);
            this.overridingTab = true;
        }
        else if (this.overridingTab && !doOverride)
        {
            this.setSelectedTab(this.bkgTab, this.bkgScroll);
        }
    }

    public void tryOpenWindow()
    {
    }

    void switchLayer(int layer)
    {
        this.layer = layer;

        for (Interactable interactable : this.interactables)
        {
            if (interactable instanceof PhysicalItemSlot)
            {
                PhysicalItemSlot physicalitemslot = (PhysicalItemSlot)interactable;
                int i = physicalitemslot.slotId - this.metaData.inventoryOffset;
                int j = i / 9;

                if (layer != -1 && j > layer)
                {
                    physicalitemslot.opacity = 0.1D;
                }
                else
                {
                    physicalitemslot.opacity = 1.0D;
                }
            }
        }
    }

    public void onUpdate()
    {
        super.onUpdate();
        this.hotbar.onUpdate();
        this.armorDisplay.onUpdate();

        for (Interactable interactable : this.interactables)
        {
            interactable.update();
        }

        int k = this.mc.options.mainHand == HumanoidArm.RIGHT ? 1 : 0;
        Vec3 vec3 = this.mc.vrPlayer.vrdata_world_pre.getController(k).getCustomVector(new Vec3(0.0D, 1.0D, 0.0D));

        if (vec3.y < 0.2D && this.isOpen)
        {
            this.close();
        }

        if (vec3.y > 0.3D && !this.isOpen && !this.fullyClosed)
        {
            this.open((Object)null);
        }

        if (this.transitionTimeStamp != -1L && Utils.milliTime() - this.transitionTimeStamp > 5000L)
        {
            this.mc.physicalGuiManager.guiTransitionOverride = null;
            this.transitionTimeStamp = -1L;
        }

        if (this.isOpen)
        {
            if (this.touching != null && this.touching instanceof PhysicalItemSlot)
            {
                PhysicalItemSlot physicalitemslot = (PhysicalItemSlot)this.touching;
                int i = physicalitemslot.slotId - this.metaData.inventoryOffset;
                int j = i / 9;
                this.switchLayer(j);
            }
            else
            {
                this.switchLayer(-1);
            }
        }
    }

    public void preGuiChange(PhysicalGui gui)
    {
        this.transitionTimeStamp = Utils.milliTime();
        this.mc.physicalGuiManager.guiTransitionOverride = this.mc.physicalGuiManager.getVirtualHeldItem();
        this.metaData = analyseInventory(this.container);
        this.putDownHeldItem();
        this.miniCrafting.setExtended(false);
    }

    public void postGuiChange(PhysicalGui gui)
    {
        this.transitionTimeStamp = -1L;

        if (gui != null && gui.container != null)
        {
            this.container = gui.container;
        }
        else
        {
            this.container = this.player.inventoryMenu;
        }

        this.hotbar.open((Object)null);
        this.loadSlots();
        this.pickItemBackUp();
        this.mc.physicalGuiManager.guiTransitionOverride = null;
    }

    public Quaternion getAnchorRotation(double partialTicks)
    {
        int i = this.mc.options.mainHand == HumanoidArm.RIGHT ? 1 : 0;
        VRData vrdata = this.mc.vrPlayer.vrdata_world_render;

        if (vrdata == null)
        {
            vrdata = this.mc.vrPlayer.vrdata_room_pre;
        }

        Quaternion quaternion = new Quaternion(vrdata.getController(i).getMatrix());
        return quaternion.multiply(new Quaternion(0.0F, 180.0F, 0.0F));
    }

    public Vec3 getAnchorPos(double partialTicks)
    {
        int i = this.mc.options.mainHand == HumanoidArm.RIGHT ? 1 : 0;
        VRData vrdata = this.mc.vrPlayer.vrdata_world_render;

        if (vrdata == null)
        {
            vrdata = this.mc.vrPlayer.vrdata_world_pre;
        }

        return vrdata.getController(i).getPosition();
    }

    void putDownHeldItem()
    {
        Logger logger = Logger.getLogger("inv");
        this.putDownItemSlot = -1;
        this.metaData = analyseInventory(this.mc.player.containerMenu);

        if (!this.mc.physicalGuiManager.getRawHeldItem().isEmpty())
        {
            int i = this.metaData.inventoryOffset;
            logger.info("Putting down held item: " + this.mc.physicalGuiManager.getRawHeldItem());
            logger.fine("New GUI has inventory offset " + i);

            for (int j = 0; j < 36; ++j)
            {
                ItemStack itemstack = this.container.slots.get(j + i).getItem();

                if (itemstack.isEmpty())
                {
                    this.putDownItemSlot = j;
                    logger.fine("Found free slot " + this.putDownItemSlot + ". Putting down item.");
                    this.mc.physicalGuiManager.clickSlot(i + j, 0);
                    return;
                }
            }

            this.putDownItemSlot = Math.min(Math.max(0, this.heldItemSlot - i), 35);
            logger.warning("Full inventory! Performing item override into slot " + this.putDownItemSlot);
            this.mc.physicalGuiManager.clickSlot(this.heldItemSlot, 0);
            this.mc.physicalGuiManager.clickSlot(-1, 0);
        }
    }

    void pickItemBackUp()
    {
        this.metaData = analyseInventory(this.mc.player.containerMenu);

        if (this.putDownItemSlot != -1)
        {
            int i = this.metaData.inventoryOffset;
            Logger logger = Logger.getLogger("inv");
            logger.fine("New inventory offset is " + i);

            if (i != -1)
            {
                logger.info("Picking item " + this.mc.player.containerMenu.getSlot(i + this.putDownItemSlot).getItem().getHoverName() + " from slot " + this.putDownItemSlot + " back up.");
                this.mc.physicalGuiManager.clickSlot(i + this.putDownItemSlot, 0);
                this.putDownItemSlot = -1;
            }
        }
    }

    public void render(double partialTicks)
    {
        GlStateManager._pushMatrix();
        GlStateManager._enableLighting();
        Player player = Minecraft.getInstance().player;
        Vec3 vec3 = new Vec3(player.xOld + (player.getX() - player.xOld) * partialTicks, player.yOld + (player.getY() - player.yOld) * partialTicks, player.zOld + (player.getZ() - player.zOld) * partialTicks);
        Vec3 vec31 = this.getAnchorPos(partialTicks);
        Quaternion quaternion = this.getAnchorRotation(partialTicks);
        vec31 = vec31.subtract(vec3);
        float f = 1.0F;
        ModelResourceLocation modelresourcelocation;

        if (this.isOpen())
        {
            modelresourcelocation = player.isCreative() ? this.bagLocGold : this.bagLoc;
        }
        else
        {
            modelresourcelocation = player.isCreative() ? this.bagLocGoldClosed : this.bagLocClosed;
        }

        GlStateManager._colorMask(false, false, false, false);

        for (int i = 0; i < 2; ++i)
        {
            GlStateManager._pushMatrix();

            if (i == 0)
            {
                GlStateManager._colorMask(false, false, false, false);
                GlStateManager._depthFunc(518);
            }
            else
            {
                GlStateManager._colorMask(true, true, true, true);
                GlStateManager._depthFunc(515);
            }

            GlStateManager._translated(vec31.x, vec31.y, vec31.z);
            Utils.glRotate(quaternion);
            GlStateManager._translated(-0.9D, -0.7D, -0.5D);
            GlStateManager._scalef(f, f, f);
            renderCustomModel(modelresourcelocation);
            GlStateManager._popMatrix();
            GlStateManager._pushMatrix();

            if (this.isOpen())
            {
                super.render(partialTicks);
            }

            this.armorDisplay.render(partialTicks);
            GlStateManager._popMatrix();
        }

        GlStateManager._depthFunc(515);
        GlStateManager._popMatrix();
    }

    public static void renderCustomModel(ModelResourceLocation model)
    {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
    }

    public class Hotbar extends PhysicalItemSlotGui
    {
        public PhysicalInventory parent;

        public Hotbar(PhysicalInventory inventory)
        {
            super(inventory.entity);
            this.parent = inventory;
        }

        public void open(Object payload)
        {
            this.isOpen = true;
            this.container = PhysicalInventory.this.container;
            this.loadSlots();
        }

        public void tryOpenWindow()
        {
        }

        void loadSlots()
        {
            if (this.container != null)
            {
                this.metaData = analyseInventory(this.container);
                int i = this.metaData.hotbarOffset;
                Vec3 vec3 = new Vec3(0.0D, -0.6D, -0.1D);
                this.interactables.clear();

                for (int j = 0; j < 9; ++j)
                {
                    PhysicalItemSlot physicalitemslot;

                    if (j != 0 && j != 8)
                    {
                        if (j == 4)
                        {
                            physicalitemslot = new HotBarItemSlot(this, i + j)
                            {
                                public ItemStack getDisplayedItem()
                                {
                                    return PhysicalInventory.this.isOpen() ? super.getDisplayedItem() : this.slot.getItem();
                                }
                            };
                            physicalitemslot.scale = 0.1D;
                            physicalitemslot.rotation = new Quaternion(90.0F, 0.0F, 0.0F);
                            physicalitemslot.position = vec3.add(new Vec3(0.0D, 0.0D, 0.1D));
                        }
                        else
                        {
                            physicalitemslot = new HotBarItemSlot(this, i + j)
                            {
                                public Vec3 getAnchorPos(double partialTicks)
                                {
                                    int j1 = this.mc.options.mainHand == HumanoidArm.RIGHT ? 1 : 0;
                                    VRData vrdata = this.mc.vrPlayer.vrdata_world_render;

                                    if (vrdata == null)
                                    {
                                        vrdata = this.mc.vrPlayer.vrdata_world_pre;
                                    }

                                    return vrdata.getController(j1).getPosition();
                                }
                                public Quaternion getAnchorRotation(double partialTicks)
                                {
                                    int j1 = this.mc.options.mainHand == HumanoidArm.RIGHT ? 1 : 0;
                                    VRData vrdata = this.mc.vrPlayer.vrdata_world_render;

                                    if (vrdata == null)
                                    {
                                        vrdata = this.mc.vrPlayer.vrdata_world_pre;
                                    }

                                    return new Quaternion(vrdata.getController(j1).getMatrix());
                                }
                                public ItemStack getDisplayedItem()
                                {
                                    return PhysicalInventory.this.isOpen() ? super.getDisplayedItem() : this.slot.getItem();
                                }
                            };
                            physicalitemslot.scale = 0.1D;
                            physicalitemslot.rotation = new Quaternion(0.0F, 90.0F, 180.0F);
                            int i1 = j < 4 ? 1 : -1;
                            int l = j < 4 ? j : j - 5;
                            physicalitemslot.position = new Vec3(-0.1D, 0.06D * (double)i1, -0.05D + 0.1D * (double)l);
                        }
                    }
                    else
                    {
                        int k = j == 0 ? 1 : -1;
                        physicalitemslot = new HotBarItemSlot(this, i + j)
                        {
                            public ItemStack getDisplayedItem()
                            {
                                return PhysicalInventory.this.isOpen() ? super.getDisplayedItem() : this.slot.getItem();
                            }
                        };
                        physicalitemslot.position = vec3.add(new Vec3((double)k * 0.2D, 0.0D, 0.0D));
                        physicalitemslot.rotation = new Quaternion(180.0F, (float)(-90 * k), 0.0F);
                        physicalitemslot.scale = 0.4D;
                        physicalitemslot.counterRot = new Quaternion(-90.0F, (float)(k * -90), 0.0F);
                        physicalitemslot.fullBlockScaleMult = 0.5D;
                    }

                    physicalitemslot.slot = this.container.slots.get(i + j);

                    if (this.mc.physicalGuiManager.isHoldingHotbarSlot && this.mc.player.inventory.selected == j)
                    {
                        physicalitemslot.opacity = 0.1D;
                    }

                    this.interactables.add(physicalitemslot);
                }
            }
        }

        boolean isInRange()
        {
            return this.touching != null;
        }

        public Quaternion getAnchorRotation(double partialTicks)
        {
            VRData vrdata = this.mc.vrPlayer.vrdata_world_render;

            if (vrdata == null)
            {
                vrdata = this.mc.vrPlayer.vrdata_world_pre;
            }

            return new Quaternion(0.0F, vrdata.getFacingYaw(), 0.0F);
        }

        public Vec3 getAnchorPos(double partialTicks)
        {
            VRData vrdata = this.mc.vrPlayer.vrdata_world_render;

            if (vrdata == null)
            {
                vrdata = this.mc.vrPlayer.vrdata_world_pre;
            }

            return vrdata.hmd.getPosition();
        }

        public void onUpdate()
        {
            super.onUpdate();
        }

        public void close()
        {
            this.isOpen = false;
        }
    }
}
