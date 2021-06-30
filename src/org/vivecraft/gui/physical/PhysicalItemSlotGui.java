package org.vivecraft.gui.physical;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;
import org.vivecraft.gui.physical.interactables.Interactable;
import org.vivecraft.gui.physical.interactables.PhysicalItemSlot;
import org.vivecraft.utils.Utils;
import org.vivecraft.utils.math.Quaternion;

public class PhysicalItemSlotGui extends PhysicalGui
{
    protected boolean isOpen = false;
    boolean isBlock;
    public Entity entity;
    public BlockPos blockPos;
    public BlockState blockState;
    public PhysicalGui.InventoryMetaData metaData = null;
    public ArrayList<Interactable> interactables = new ArrayList<>();
    double shortestDist = -1.0D;
    public double touchDistance = 0.25D;
    double openDistance = 0.4D;

    public PhysicalItemSlotGui(BlockPos pos)
    {
        this.isBlock = true;
        this.blockPos = pos;
        this.blockState = this.mc.level.getBlockState(pos);
        this.init();
    }

    public PhysicalItemSlotGui(Entity entity)
    {
        this.isBlock = false;
        this.entity = entity;
        this.init();
    }

    void init()
    {
        this.loadSlots();
    }

    boolean isInRange()
    {
        return this.shortestDist != -1.0D && this.shortestDist < 0.5D;
    }

    public void render(double partialTicks)
    {
        if (!this.isFullyClosed())
        {
            Player player = Minecraft.getInstance().player;
            Vec3 vec3 = new Vec3(player.xOld + (player.getX() - player.xOld) * partialTicks, player.yOld + (player.getY() - player.yOld) * partialTicks, player.zOld + (player.getZ() - player.zOld) * partialTicks);
            int i = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);

            for (int j = 0; j <= 1; ++j)
            {
                for (Interactable interactable : this.interactables)
                {
                    if (interactable.isEnabled())
                    {
                        GlStateManager._pushMatrix();
                        GlStateManager._matrixMode(5888);
                        GlStateManager._depthFunc(i);
                        Vec3 vec31 = interactable.getAnchorPos(partialTicks);
                        Quaternion quaternion = interactable.getAnchorRotation(partialTicks);
                        vec31 = vec31.subtract(vec3);
                        GlStateManager._translated(vec31.x, vec31.y, vec31.z);
                        Utils.glRotate(quaternion);
                        Vec3 vec32 = interactable.getPosition(partialTicks);
                        GlStateManager._translated(vec32.x, vec32.y, vec32.z);
                        Utils.glRotate(interactable.getRotation(partialTicks));
                        interactable.render(partialTicks, j);
                        GlStateManager._popMatrix();
                    }
                }
            }
        }
    }

    public void close()
    {
        if (this.isOpen)
        {
            this.isOpen = false;
            this.touching = null;
            this.shortestDist = -1.0D;

            if (this.mc.physicalGuiManager.activeGui != null && this.mc.physicalGuiManager.activeGui.equals(this))
            {
                this.mc.physicalGuiManager.activeGui = null;
            }

            this.mc.physicalGuiManager.requestGuiSwitch((PhysicalGui)null);
            this.mc.physicalGuiManager.onGuiClosed();
        }
    }

    public boolean isFullyClosed()
    {
        return !this.isOpen;
    }

    public void open(Object payload)
    {
        this.mc.player.containerMenu = this.container;
        this.loadSlots();
        this.metaData = analyseInventory(this.container);
        this.isOpen = true;
        this.mc.physicalGuiManager.onGuiOpened();
    }

    public Vec3 getAnchorPos(double partialTicks)
    {
        if (this.isBlock)
        {
            return new Vec3((double)this.blockPos.getX() + 0.5D, (double)this.blockPos.getY() + 0.5D, (double)this.blockPos.getZ() + 0.5D);
        }
        else
        {
            Vec3 vec3 = new Vec3(this.entity.xo, this.entity.yo, this.entity.zo);
            return vec3.add(this.entity.position().subtract(vec3).scale(partialTicks));
        }
    }

    public Quaternion getAnchorRotation(double partialTicks)
    {
        return this.isBlock ? getBlockOrientation(this.blockPos) : new Quaternion(0.0F, (float)(-((double)this.entity.yRotO + partialTicks * (double)(this.entity.yRot - this.entity.yRotO))), 0.0F);
    }

    public boolean requestOpen()
    {
        boolean flag;

        if (this.isBlock)
        {
            flag = this.mc.gameMode.useItemOn(this.mc.player, this.mc.level, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.ZERO, Direction.UP, this.blockPos, false)) == InteractionResult.SUCCESS;
        }
        else
        {
            flag = this.mc.gameMode.interact(this.mc.player, this.entity, InteractionHand.MAIN_HAND) == InteractionResult.SUCCESS;
        }

        return flag;
    }

    public void tryOpenWindow()
    {
        if (!this.mc.player.isShiftKeyDown())
        {
            this.mc.physicalGuiManager.requestGuiSwitch(this);
        }
    }

    void loadSlots()
    {
        if (this.blockState != null)
        {
            String s = getBlockId(this.blockState.getBlock());
            this.interactables.clear();

            if (s.equals("minecraft:crafting_table"))
            {
                for (int i = 0; i < 3; ++i)
                {
                    for (int j = 1; j <= 3; ++j)
                    {
                        int k = i * 3 + j;
                        PhysicalItemSlot physicalitemslot = new PhysicalItemSlot(this, k);
                        physicalitemslot.position = new Vec3((double)(2 - j) * 0.2D, 0.5D, (double)(1 - i) * 0.2D);
                        physicalitemslot.rotation = new Quaternion(90.0F, 0.0F, 0.0F);

                        if (this.container != null)
                        {
                            physicalitemslot.slot = this.container.slots.get(k);
                        }

                        this.interactables.add(physicalitemslot);
                    }
                }

                PhysicalItemSlot physicalitemslot1 = new PhysicalItemSlot(this, 0);
                physicalitemslot1.position = new Vec3(0.0D, 1.0D, 0.0D);
                physicalitemslot1.fullBlockRotation = new Quaternion();
                physicalitemslot1.preview = false;

                if (this.container != null)
                {
                    physicalitemslot1.slot = this.container.slots.get(0);
                }

                this.interactables.add(physicalitemslot1);
            }
        }
    }

    void reloadSlots()
    {
        HashMap<Integer, Slot> hashmap = new HashMap<>();

        for (Interactable interactable : this.interactables)
        {
            if (interactable instanceof PhysicalItemSlot)
            {
                PhysicalItemSlot physicalitemslot = (PhysicalItemSlot)interactable;
                hashmap.put(physicalitemslot.slotId, physicalitemslot.slot);
            }
        }

        this.loadSlots();

        for (Interactable interactable1 : this.interactables)
        {
            if (interactable1 instanceof PhysicalItemSlot)
            {
                PhysicalItemSlot physicalitemslot1 = (PhysicalItemSlot)interactable1;
                Slot slot = hashmap.get(physicalitemslot1.slotId);

                if (slot != null)
                {
                    physicalitemslot1.slot = slot;
                }
            }
        }
    }

    public void onMayClose()
    {
        if (this.blockState != null)
        {
            String s = getBlockId(this.blockState.getBlock());

            if (s.equals("minecraft:crafting_table"))
            {
                this.close();
            }
        }
    }

    public boolean isOpen()
    {
        return this.isOpen;
    }

    public boolean isAlive()
    {
        return this.isBlock ? this.mc.level.getBlockState(this.blockPos).getBlock().equals(this.blockState.getBlock()) : this.entity.isAlive();
    }

    public void onUpdate()
    {
        int i = this.mc.options.mainHand == HumanoidArm.RIGHT ? 0 : 1;
        Vec3 vec3 = this.mc.vrPlayer.vrdata_world_pre.getController(i).getPosition();
        vec3 = vec3.add(this.mc.vrPlayer.vrdata_world_pre.getController(i).getDirection().scale(0.1D));

        if (this.touching != null && !this.interactables.contains(this.touching))
        {
            this.touching.untouch();
            this.touching = null;
        }

        ArrayList<Interactable> arraylist = new ArrayList<>();

        for (Interactable interactable : this.interactables)
        {
            if (interactable.isTouchable())
            {
                Vec3 vec31 = interactable.getAnchorRotation(0.0D).inverse().multiply(vec3.subtract(interactable.getAnchorPos(0.0D)));
                vec31 = interactable.getRotation(0.0D).inverse().multiply(vec31.subtract(interactable.getPosition(0.0D)));

                if (interactable.getBoundingBox().contains(vec31))
                {
                    arraylist.add(interactable);
                }
            }
        }

        this.shortestDist = -1.0D;
        Interactable interactable2 = null;
        double d1 = -1.0D;

        for (Interactable interactable1 : arraylist)
        {
            Vec3 vec32 = interactable1.getAnchorPos(0.0D);
            Quaternion quaternion = interactable1.getAnchorRotation(0.0D);
            Vec3 vec33 = vec32.add(quaternion.multiply(interactable1.getPosition(0.0D)));
            double d0 = vec33.subtract(vec3).length();

            if (this.shortestDist == -1.0D || this.shortestDist > d0)
            {
                this.shortestDist = d0;
                interactable2 = interactable1;
            }

            if (interactable1.equals(this.touching))
            {
                d1 = d0;
            }
        }

        if (this.isOpen() && this.touching != null && (d1 == -1.0D || d1 - this.shortestDist > 0.01D))
        {
            this.touching.untouch();
            this.touching = null;
        }

        if (!this.isOpen && !this.mc.physicalGuiManager.isIntercepting() && interactable2 != null && this.shortestDist < this.openDistance)
        {
            this.tryOpenWindow();
        }
        else if (this.isOpen && (interactable2 == null || this.shortestDist > this.openDistance + 0.05D))
        {
            boolean flag = true;

            for (Interactable interactable3 : this.interactables)
            {
                if (interactable3 instanceof PhysicalItemSlot)
                {
                    PhysicalItemSlot physicalitemslot = (PhysicalItemSlot)interactable3;

                    if (!physicalitemslot.getDisplayedItem().isEmpty())
                    {
                        flag = false;
                    }
                }
            }

            if (flag)
            {
                this.onMayClose();
            }
        }

        if (this.isOpen() && interactable2 != null && this.shortestDist != -1.0D && this.touching == null)
        {
            this.touching = interactable2;
            interactable2.touch();
        }
    }
}
