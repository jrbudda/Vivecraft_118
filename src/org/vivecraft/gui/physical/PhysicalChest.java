package org.vivecraft.gui.physical;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.gui.physical.interactables.Interactable;
import org.vivecraft.gui.physical.interactables.PhysicalItemSlot;
import org.vivecraft.utils.math.Quaternion;
import org.vivecraft.utils.math.Vector3;

public class PhysicalChest extends PhysicalItemSlotGui
{
    boolean wasOpen = false;
    boolean loadedDouble;
    double lidAngle = 0.0D;
    double lastLidAngle = 0.0D;
    int handOnLid = -1;
    int layer = 2;

    public PhysicalChest(BlockPos pos)
    {
        super(pos);
        this.openDistance = 0.0D;
    }

    public boolean isDouble()
    {
        Vec3 vec3 = this.getAnchorPos(0.0D);
        Vec3 vec31 = new Vec3(-1.0D, 0.0D, 0.0D);
        Quaternion quaternion = this.getAnchorRotation();
        BlockPos blockpos = new BlockPos(vec3.add(quaternion.multiply(vec31)));
        BlockState blockstate = this.mc.level.getBlockState(blockpos);
        return blockstate.getBlock().equals(Blocks.CHEST) && this.blockState.getBlock().equals(Blocks.CHEST) || blockstate.getBlock().equals(Blocks.TRAPPED_CHEST) && this.blockState.getBlock().equals(Blocks.TRAPPED_CHEST);
    }

    public double getLidHoldAngle(BlockPos pos, double partialTicks)
    {
        BlockState blockstate = this.mc.level.getBlockState(pos);
        boolean flag = false;

        if ((!this.blockState.getBlock().equals(Blocks.CHEST) || !blockstate.getBlock().equals(Blocks.CHEST)) && this.blockState.getBlock().equals(Blocks.TRAPPED_CHEST) && blockstate.getBlock().equals(Blocks.TRAPPED_CHEST))
        {
        }

        return !this.isFullyClosed() && (pos.equals(this.blockPos) || flag) ? this.lastLidAngle + (this.lidAngle - this.lastLidAngle) * partialTicks : -1.0D;
    }

    public boolean isAlive()
    {
        if (!this.mc.level.getBlockState(this.blockPos).getBlock().equals(this.blockState.getBlock()))
        {
            return false;
        }
        else
        {
            return this.loadedDouble == this.isDouble();
        }
    }

    public void onUpdate()
    {
        super.onUpdate();
        double[] adouble = new double[2];
        Vec3 vec3 = this.getAnchorPos();
        Quaternion quaternion = this.getAnchorRotation().multiply(new Quaternion(0.0F, 180.0F, 0.0F));
        vec3 = vec3.add(quaternion.multiply(new Vec3(0.0D, 0.1D, -0.4D)));

        if (this.isDouble())
        {
            vec3 = vec3.add(quaternion.multiply(new Vec3(0.5D, 0.0D, 0.0D)));
        }

        Quaternion quaternion1 = new Quaternion((float)(-this.lidAngle), 0.0F, 0.0F);
        Vec3 vec31 = vec3.add(quaternion.multiply(quaternion1.multiply(new Vec3(0.0D, 0.0D, 0.4D))));

        for (int i = 0; i < 2; ++i)
        {
            Vec3 vec32 = this.mc.vrPlayer.vrdata_world_pre.getController(i).getPosition();
            Vec3 vec33 = quaternion1.inverse().multiply(quaternion.inverse().multiply(vec32.subtract(vec31)));
            double d0 = this.isDouble() ? 1.0D : 0.5D;
            double d2 = 0.5D;

            if (!(Math.abs(vec33.x) < d0) || !(Math.abs(vec33.z) < d2) || !(Math.abs(vec33.y - 0.05D) < 0.1D) && (this.handOnLid != i || !(Math.abs(vec33.y) < 0.4D)))
            {
                adouble[i] = Double.MAX_VALUE;
            }
            else
            {
                adouble[i] = -vec33.y;
            }
        }

        int k = adouble[0] < adouble[1] ? 0 : 1;

        if (adouble[k] < 0.5D)
        {
            if (!this.isOpen && !this.mc.physicalGuiManager.isIntercepting())
            {
                this.tryOpenWindow();
            }

            this.handOnLid = k;
            Vec3 vec34 = this.mc.vrPlayer.vrdata_world_pre.getController(k).getPosition();
            Vec3 vec35 = vec34.subtract(vec3).normalize();
            vec35 = quaternion.inverse().multiply(vec35);
            vec35 = new Vec3(0.0D, vec35.y, vec35.z);
            double d4 = (double)Quaternion.createFromToVector(new Vector3(0.0F, 0.0F, -1.0F), new Vector3(vec35)).toEuler().getPitch();
            d4 = Math.max(Math.min(d4, 90.0D), 0.0D);
            this.lastLidAngle = this.lidAngle;
            this.lidAngle = d4;
        }
        else
        {
            this.handOnLid = -1;
            double d3 = this.lidAngle - this.lastLidAngle;
            double d5 = 1.0D * Math.abs((this.lidAngle - 45.0D) / 45.0D) + 0.5D;

            if (this.isOpen && this.lidAngle > 45.0D)
            {
                d3 = d3 + d5;
            }
            else
            {
                d3 = d3 - d5;
            }

            this.lastLidAngle = this.lidAngle;
            this.lidAngle = Math.min(Math.max(0.0D, this.lidAngle + d3), 90.0D);

            if (this.isOpen && this.lidAngle == 0.0D)
            {
                this.close();
            }
        }

        if (this.isOpen)
        {
            if (this.isInRange())
            {
                int l = this.mc.options.mainHand == HumanoidArm.RIGHT ? 0 : 1;
                Vec3 vec36 = this.mc.vrPlayer.vrdata_world_pre.getController(l).getPosition();
                Vec3 vec37 = this.getAnchorRotation().inverse().multiply(vec36.subtract(this.getAnchorPos()));
                double d1 = vec37.y + 0.3D;
                int j = (int)(d1 / (double)0.4F * 2.0D);
                j = Math.min(Math.max(0, j), 2);
                this.switchLayer(j);
            }
            else
            {
                this.switchLayer(2);
            }
        }
    }

    boolean isInRange()
    {
        for (int i = 0; i < 2; ++i)
        {
            Vec3 vec3 = this.mc.vrPlayer.vrdata_world_pre.getController(i).getPosition();
            Vec3 vec31 = this.getAnchorRotation().inverse().multiply(vec3.subtract(this.getAnchorPos()));

            if (this.isDouble())
            {
                vec31 = vec31.add(new Vec3(0.5D, 0.0D, 0.0D));
            }

            double d0 = this.isDouble() ? 1.0D : 0.5D;
            double d1 = 0.5D;

            if (Math.abs(vec31.x) < d0 && Math.abs(vec31.z) < d1)
            {
                return true;
            }
        }

        return false;
    }

    int getSlotCount()
    {
        if (this.container == null)
        {
            return this.isDouble() ? 54 : 27;
        }
        else
        {
            return (this.container.slots.get(0)).container.getContainerSize();
        }
    }

    public void render(double partialTicks)
    {
        getBlockOrientation(this.blockPos);
        super.render(partialTicks);
    }

    void loadSlots()
    {
        this.loadedDouble = this.isDouble();
        boolean flag = this.container == null;
        int i = this.getSlotCount();
        int j = i / 9;
        this.interactables.clear();
        Vec3 vec3 = new Vec3(0.2D, -0.375D, 0.2D);
        double d0 = this.isDouble() ? 0.8D : 0.6D;
        double d1 = 0.6D;
        double d2 = 0.6D;

        for (int k = 0; k < j; ++k)
        {
            int l = k / (j / 3);
            int i1 = k % (j / 3);

            for (int j1 = 0; j1 < 3; ++j1)
            {
                for (int k1 = 0; k1 < 3; ++k1)
                {
                    double d3 = d0 / 3.0D;
                    double d4 = d1 / 3.0D;
                    double d5 = d2 / 3.0D;
                    int l1 = k * 9 + j1 * 3 + k1;
                    PhysicalItemSlot physicalitemslot = new PhysicalItemSlot(this, l1);

                    if (!flag)
                    {
                        physicalitemslot.slot = this.container.slots.get(l1);
                    }

                    physicalitemslot.position = vec3.add(new Vec3(-d3 * (double)k1, d4 * (double)l, -d5 * (double)j1)).add(new Vec3((double)(-i1) * (d0 + 0.07D), 0.0D, 0.0D));
                    physicalitemslot.rotation = new Quaternion(90.0F, 0.0F, 0.0F);
                    physicalitemslot.fullBlockScaleMult = 1.9D;
                    physicalitemslot.scale = 0.19D;
                    physicalitemslot.opacity = 1.0D;
                    this.interactables.add(physicalitemslot);
                }
            }
        }
    }

    void switchLayer(int layer)
    {
        this.layer = layer;
        int i = this.getSlotCount();

        for (Interactable interactable : this.interactables)
        {
            if (interactable instanceof PhysicalItemSlot)
            {
                PhysicalItemSlot physicalitemslot = (PhysicalItemSlot)interactable;
                int j = physicalitemslot.slotId / 9 / (i / 9 / 3);

                if (j > layer)
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

    public boolean isFullyClosed()
    {
        return super.isFullyClosed();
    }

    public void open(Object payload)
    {
        if (payload instanceof Container)
        {
            Container container = (Container)payload;
            this.wasOpen = true;
            super.open((Object)null);
        }
    }
}
