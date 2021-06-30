package org.vivecraft.gui.physical.interactables;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.gui.physical.PhysicalInventory;
import org.vivecraft.gui.physical.WindowCoordinator;
import org.vivecraft.utils.math.Quaternion;

public class MiniCrafting implements Interactable
{
    Minecraft mc;
    ModelResourceLocation craftingLoc;
    boolean extended;
    PhysicalInventory inventory;
    ArrayList<PhysicalItemSlot> craftingSlots = new ArrayList<>();
    public Vec3 position = Vec3.ZERO;
    public Quaternion rotation = new Quaternion();

    public MiniCrafting(PhysicalInventory inventory)
    {
        this.inventory = inventory;
        this.craftingLoc = new ModelResourceLocation("vivecraft:mini_crafting");
        this.mc = Minecraft.getInstance();
    }

    public void render(double partialTicks, int renderLayer)
    {
        if (renderLayer == 0)
        {
            GlStateManager._pushMatrix();
            GlStateManager._translated(-0.44D, 0.0D, -0.22D);
            PhysicalInventory.renderCustomModel(this.craftingLoc);
            GlStateManager._popMatrix();
        }
    }

    public void loadSlots()
    {
        this.craftingSlots.clear();

        if (this.inventory.metaData.hasExtra)
        {
            for (int i = 0; i < 2; ++i)
            {
                for (int j = 0; j < 2; ++j)
                {
                    int k = j * 2 + i + this.inventory.metaData.craftingOffset + 1;
                    PhysicalItemSlot physicalitemslot = new PhysicalItemSlot(this.inventory, k)
                    {
                        public Vec3 getAnchorPos(double partialTicks)
                        {
                            Vec3 vec31 = MiniCrafting.this.getAnchorPos(partialTicks);
                            return vec31.add(MiniCrafting.this.getAnchorRotation(partialTicks).multiply(MiniCrafting.this.getPosition(partialTicks)));
                        }
                        public Quaternion getAnchorRotation(double partialTicks)
                        {
                            return MiniCrafting.this.getAnchorRotation(partialTicks).multiply(MiniCrafting.this.getRotation(partialTicks));
                        }
                        public boolean isTouchable()
                        {
                            return !this.mc.physicalGuiManager.getVirtualHeldItem().isEmpty() || !this.slot.getItem().isEmpty();
                        }
                    };
                    physicalitemslot.slot = this.inventory.container.slots.get(k);
                    physicalitemslot.enabled = false;
                    Vec3 vec3 = new Vec3(-0.15D, 0.12D, 0.07D);
                    double d0 = 0.15D;
                    physicalitemslot.position = vec3.add(new Vec3((double)(-i) * d0, 0.0D, (double)(-j) * d0));
                    physicalitemslot.rotation = new Quaternion(90.0F, 0.0F, 0.0F);
                    physicalitemslot.scale = 0.15D;
                    this.craftingSlots.add(physicalitemslot);
                }
            }

            PhysicalItemSlot physicalitemslot1 = new PhysicalItemSlot(this.inventory, this.inventory.metaData.craftingOffset)
            {
                public Vec3 getAnchorPos(double partialTicks)
                {
                    Vec3 vec31 = MiniCrafting.this.getAnchorPos(partialTicks);
                    return vec31.add(MiniCrafting.this.getAnchorRotation(partialTicks).multiply(MiniCrafting.this.getPosition(partialTicks)));
                }
                public Quaternion getAnchorRotation(double partialTicks)
                {
                    return MiniCrafting.this.getAnchorRotation(partialTicks).multiply(MiniCrafting.this.getRotation(partialTicks));
                }
                public boolean isTouchable()
                {
                    return !this.slot.getItem().isEmpty();
                }
            };
            physicalitemslot1.slot = this.inventory.container.slots.get(physicalitemslot1.slotId);
            physicalitemslot1.enabled = false;
            physicalitemslot1.position = new Vec3(-0.2D, 0.4D, 0.0D);
            physicalitemslot1.fullBlockRotation = new Quaternion();
            physicalitemslot1.scale = 0.15D;
            physicalitemslot1.preview = false;
            this.craftingSlots.add(physicalitemslot1);
        }
    }

    public Vec3 getPosition(double partialTicks)
    {
        return this.position;
    }

    public Quaternion getRotation(double partialTicks)
    {
        return this.extended ? this.rotation : this.rotation.multiply(new Quaternion(0.0F, 0.0F, 90.0F));
    }

    public Vec3 getAnchorPos(double partialTicks)
    {
        return this.inventory.getAnchorPos(partialTicks);
    }

    public Quaternion getAnchorRotation(double partialTicks)
    {
        return this.inventory.getAnchorRotation(partialTicks);
    }

    public boolean isEnabled()
    {
        return true;
    }

    public void touch()
    {
    }

    public void untouch()
    {
    }

    public ArrayList<PhysicalItemSlot> getCraftingSlots()
    {
        return this.craftingSlots;
    }

    public void click(int button)
    {
        this.setExtended(!this.extended);
    }

    public void setExtended(boolean extended)
    {
        if (extended)
        {
            this.inventory.requestFatInventory();
        }
        else
        {
            this.returnItems();
        }

        this.extended = extended;

        for (PhysicalItemSlot physicalitemslot : this.craftingSlots)
        {
            physicalitemslot.enabled = extended;
        }
    }

    void returnItems()
    {
        ArrayList<PhysicalItemSlot> arraylist = new ArrayList<>();

        for (PhysicalItemSlot physicalitemslot : this.craftingSlots)
        {
            if (!physicalitemslot.slot.getItem().isEmpty())
            {
                arraylist.add(physicalitemslot);
            }
        }

        int[] aint = WindowCoordinator.getFreeSlotsInInventory(arraylist.size());

        for (int i = 0; i < aint.length; ++i)
        {
            if (aint[i] == -1)
            {
                aint[i] = -999;
            }

            this.mc.physicalGuiManager.windowCoordinator.enqueueOperation(new WindowCoordinator.ClickOperation(this.mc.physicalGuiManager, (arraylist.get(i)).slotId, 0));
            this.mc.physicalGuiManager.windowCoordinator.enqueueOperation(new WindowCoordinator.ClickOperation(this.mc.physicalGuiManager, aint[i], 0));
        }
    }

    public boolean isTouchable()
    {
        if (this.extended)
        {
            if (!this.mc.physicalGuiManager.getVirtualHeldItem().isEmpty())
            {
                return false;
            }

            for (PhysicalItemSlot physicalitemslot : this.craftingSlots)
            {
                if (!physicalitemslot.slot.getItem().isEmpty())
                {
                    return false;
                }
            }
        }

        return this.isEnabled();
    }

    public void unclick(int button)
    {
    }

    public AABB getBoundingBox()
    {
        return (new AABB(-0.44D, 0.0D, -0.22D, 0.0D, 0.12D, 0.22D)).inflate(0.05D);
    }
}
