package org.vivecraft.gameplay;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SnowballItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.api.NetworkHelper;
import org.vivecraft.api.VRData;
import org.vivecraft.gameplay.screenhandlers.GuiHandler;
import org.vivecraft.gameplay.screenhandlers.KeyboardHandler;
import org.vivecraft.gameplay.screenhandlers.RadialHandler;
import org.vivecraft.gameplay.trackers.Tracker;
import org.vivecraft.gameplay.trackers.VehicleTracker;
import org.vivecraft.settings.VRSettings;

public class VRPlayer
{
    Minecraft mc = Minecraft.getInstance();
    public VRData vrdata_room_pre;
    public VRData vrdata_world_pre;
    public VRData vrdata_room_post;
    public VRData vrdata_world_post;
    public VRData vrdata_world_render;
    private long errorPrintTime = Util.getMillis();
    ArrayList<Tracker> trackers = new ArrayList<>();
    public float worldScale = Minecraft.getInstance().vrSettings.overrides.getSetting(VRSettings.VrOptions.WORLD_SCALE).getFloat();
    private boolean noTeleportClient = true;
    private boolean teleportOverride = false;
    public int teleportWarningTimer = -1;
    public Vec3 roomOrigin = new Vec3(0.0D, 0.0D, 0.0D);
    private boolean isFreeMoveCurrent = true;
    public double wfMode = 0.0D;
    public int wfCount = 0;
    public int roomScaleMovementDelay = 0;
    boolean initdone = false;
    public boolean onTick;

    public void registerTracker(Tracker tracker)
    {
        this.trackers.add(tracker);
    }

    public VRPlayer()
    {
        this.vrdata_room_pre = new VRData(new Vec3(0.0D, 0.0D, 0.0D), this.mc.vrSettings.walkMultiplier, 1.0F, 0.0F);
        this.vrdata_room_post = new VRData(new Vec3(0.0D, 0.0D, 0.0D), this.mc.vrSettings.walkMultiplier, 1.0F, 0.0F);
        this.vrdata_world_post = new VRData(new Vec3(0.0D, 0.0D, 0.0D), this.mc.vrSettings.walkMultiplier, 1.0F, 0.0F);
        this.vrdata_world_pre = new VRData(new Vec3(0.0D, 0.0D, 0.0D), this.mc.vrSettings.walkMultiplier, 1.0F, 0.0F);
    }

    public VRData getVRDataWorld()
    {
        return this.vrdata_world_render != null ? this.vrdata_world_render : this.vrdata_world_pre;
    }

    public static VRPlayer get()
    {
        return Minecraft.getInstance().vrPlayer;
    }

    public static Vec3 room_to_world_pos(Vec3 pos, VRData data)
    {
        Vec3 vec3 = new Vec3(pos.x * (double)data.worldScale, pos.y * (double)data.worldScale, pos.z * (double)data.worldScale);
        vec3 = vec3.yRot(data.rotation_radians);
        return vec3.add(data.origin.x, data.origin.y, data.origin.z);
    }

    public static Vec3 world_to_room_pos(Vec3 pos, VRData data)
    {
        Vec3 vec3 = pos.add(-data.origin.x, -data.origin.y, -data.origin.z);
        vec3 = new Vec3(vec3.x / (double)data.worldScale, vec3.y / (double)data.worldScale, vec3.z / (double)data.worldScale);
        return vec3.yRot(-data.rotation_radians);
    }

    public void postPoll()
    {
        this.vrdata_room_pre = new VRData(new Vec3(0.0D, 0.0D, 0.0D), this.mc.vrSettings.walkMultiplier, 1.0F, 0.0F);
        GuiHandler.processGui();
        KeyboardHandler.processGui();
        RadialHandler.processGui();
    }

    public void preTick()
    {
        this.onTick = true;
        this.vrdata_world_pre = new VRData(this.roomOrigin, this.mc.vrSettings.walkMultiplier, this.worldScale, (float)Math.toRadians((double)this.mc.vrSettings.worldRotation));
        float f = this.mc.vrSettings.overrides.getSetting(VRSettings.VrOptions.WORLD_SCALE).getFloat();

        if (this.mc.gameRenderer.isInMenuRoom())
        {
            this.worldScale = 1.0F;
        }
        else if (this.wfCount > 0 && !this.mc.isPaused())
        {
            if (this.wfCount < 40)
            {
                this.worldScale = (float)((double)this.worldScale - this.wfMode);

                if (this.wfMode > 0.0D)
                {
                    if (this.worldScale < f)
                    {
                        this.worldScale = f;
                    }
                }
                else if (this.wfMode < 0.0D && this.worldScale > f)
                {
                    this.worldScale = f;
                }
            }
            else
            {
                this.worldScale = (float)((double)this.worldScale + this.wfMode);

                if (this.wfMode > 0.0D)
                {
                    if (this.worldScale > 20.0F)
                    {
                        this.worldScale = 20.0F;
                    }
                }
                else if (this.wfMode < 0.0D && this.worldScale < 0.1F)
                {
                    this.worldScale = 0.1F;
                }
            }

            --this.wfCount;
        }
        else
        {
            this.worldScale = f;
        }

        if (this.mc.vrSettings.seated && !this.mc.gameRenderer.isInMenuRoom())
        {
            this.mc.vrSettings.worldRotation = this.mc.vr.seatedRot;
        }
    }

    public void postTick()
    {
        Minecraft minecraft = Minecraft.getInstance();
        VRData vrdata = new VRData(this.vrdata_world_pre.origin, minecraft.vrSettings.walkMultiplier, this.vrdata_world_pre.worldScale, this.vrdata_world_pre.rotation_radians);
        VRData vrdata1 = new VRData(this.vrdata_world_pre.origin, minecraft.vrSettings.walkMultiplier, this.worldScale, this.vrdata_world_pre.rotation_radians);
        Vec3 vec3 = vrdata1.hmd.getPosition().subtract(vrdata.hmd.getPosition());
        this.roomOrigin = this.roomOrigin.subtract(vec3);
        VRData vrdata2 = new VRData(this.roomOrigin, minecraft.vrSettings.walkMultiplier, this.worldScale, this.vrdata_world_pre.rotation_radians);
        float f = minecraft.vrSettings.worldRotation;
        float f1 = (float)Math.toDegrees((double)this.vrdata_world_pre.rotation_radians);
        this.rotateOriginAround(-f + f1, vrdata2.getHeadPivot());
        this.vrdata_room_post = new VRData(new Vec3(0.0D, 0.0D, 0.0D), minecraft.vrSettings.walkMultiplier, 1.0F, 0.0F);
        this.vrdata_world_post = new VRData(this.roomOrigin, minecraft.vrSettings.walkMultiplier, this.worldScale, (float)Math.toRadians((double)minecraft.vrSettings.worldRotation));
        this.doPermanantLookOverride(minecraft.player, this.vrdata_world_post);
        NetworkHelper.sendVRPlayerPositions(this);
        this.onTick = false;
    }

    public void preRender(float par1)
    {
        Minecraft minecraft = Minecraft.getInstance();
        float f = this.vrdata_world_post.worldScale * par1 + this.vrdata_world_pre.worldScale * (1.0F - par1);
        float f1 = this.vrdata_world_post.rotation_radians;
        float f2 = this.vrdata_world_pre.rotation_radians;
        float f3 = Math.abs(f1 - f2);

        if ((double)f3 > Math.PI)
        {
            if (f1 > f2)
            {
                f2 = (float)((double)f2 + (Math.PI * 2D));
            }
            else
            {
                f1 = (float)((double)f1 + (Math.PI * 2D));
            }
        }

        float f4 = f1 * par1 + f2 * (1.0F - par1);
        Vec3 vec3 = new Vec3(this.vrdata_world_pre.origin.x + (this.vrdata_world_post.origin.x - this.vrdata_world_pre.origin.x) * (double)par1, this.vrdata_world_pre.origin.y + (this.vrdata_world_post.origin.y - this.vrdata_world_pre.origin.y) * (double)par1, this.vrdata_world_pre.origin.z + (this.vrdata_world_post.origin.z - this.vrdata_world_pre.origin.z) * (double)par1);
        this.vrdata_world_render = new VRData(vec3, minecraft.vrSettings.walkMultiplier, f, f4);

        for (Tracker tracker : this.trackers)
        {
            if (tracker.getEntryPoint() == Tracker.EntryPoint.SPECIAL_ITEMS)
            {
                tracker.idleTick(minecraft.player);

                if (tracker.isActive(minecraft.player))
                {
                    tracker.doProcess(minecraft.player);
                }
                else
                {
                    tracker.reset(minecraft.player);
                }
            }
        }
    }

    public void postRender(float par1)
    {
    }

    public void setRoomOrigin(double x, double y, double z, boolean reset)
    {
        if (reset && this.vrdata_world_pre != null)
        {
            this.vrdata_world_pre.origin = new Vec3(x, y, z);
        }

        this.roomOrigin = new Vec3(x, y, z);
    }

    public void snapRoomOriginToPlayerEntity(LocalPlayer player, boolean reset, boolean instant)
    {
        if (!Thread.currentThread().getName().equals("Server thread"))
        {
            if (player != null && player.position() != Vec3.ZERO)
            {
                Minecraft minecraft = Minecraft.getInstance();

                if (minecraft.sneakTracker.sneakCounter <= 0)
                {
                    VRData vrdata = this.vrdata_world_pre;

                    if (instant)
                    {
                        vrdata = new VRData(this.roomOrigin, minecraft.vrSettings.walkMultiplier, this.worldScale, (float)Math.toRadians((double)minecraft.vrSettings.worldRotation));
                    }

                    Vec3 vec3 = vrdata.getHeadPivot().subtract(vrdata.origin);
                    double d0 = player.getX() - vec3.x;
                    double d2 = player.getZ() - vec3.z;
                    double d1 = player.getY() + player.getRoomYOffsetFromPose();
                    this.setRoomOrigin(d0, d1, d2, reset);
                }
            }
        }
    }

    public float rotDiff_Degrees(float start, float end)
    {
        double d0 = Math.toRadians((double)end);
        double d1 = Math.toRadians((double)start);
        return (float)Math.toDegrees(Math.atan2(Math.sin(d0 - d1), Math.cos(d0 - d1)));
    }

    public void rotateOriginAround(float degrees, Vec3 o)
    {
        Vec3 vec3 = this.roomOrigin;
        float f = (float)Math.toRadians((double)degrees);

        if (f != 0.0F)
        {
            this.setRoomOrigin(Math.cos((double)f) * (vec3.x - o.x) - Math.sin((double)f) * (vec3.z - o.z) + o.x, vec3.y, Math.sin((double)f) * (vec3.x - o.x) + Math.cos((double)f) * (vec3.z - o.z) + o.z, false);
        }
    }

    public void tick(LocalPlayer player, Minecraft mc, Random rand)
    {
        if (player.initFromServer)
        {
            if (!this.initdone)
            {
                System.out.println("<Debug info start>");
                System.out.println("Room object: " + this);
                System.out.println("Room origin: " + this.vrdata_world_pre.origin);
                System.out.println("Hmd position room: " + this.vrdata_room_pre.hmd.getPosition());
                System.out.println("Hmd position world: " + this.vrdata_world_pre.hmd.getPosition());
                System.out.println("Hmd Projection Left: " + mc.vrRenderer.eyeproj[0]);
                System.out.println("Hmd Projection Right: " + mc.vrRenderer.eyeproj[1]);
                System.out.println("<Debug info end>");
                this.initdone = true;
            }

            this.doPlayerMoveInRoom(player);

            for (Tracker tracker : this.trackers)
            {
                if (tracker.getEntryPoint() == Tracker.EntryPoint.LIVING_UPDATE)
                {
                    tracker.idleTick(mc.player);

                    if (tracker.isActive(mc.player))
                    {
                        tracker.doProcess(mc.player);
                    }
                    else
                    {
                        tracker.reset(mc.player);
                    }
                }
            }

            if (player.isPassenger())
            {
                Entity entity = mc.player.getVehicle();

                if (entity instanceof AbstractHorse)
                {
                    AbstractHorse abstracthorse = (AbstractHorse)entity;

                    if (abstracthorse.canBeControlledByRider() && abstracthorse.isSaddled() && !mc.horseTracker.isActive(mc.player))
                    {
                        abstracthorse.yBodyRot = this.vrdata_world_pre.getBodyYaw();
                        mc.vehicleTracker.rotationCooldown = 10;
                    }
                }
                else if (entity instanceof Mob)
                {
                    Mob mob = (Mob)entity;

                    if (mob.canBeControlledByRider())
                    {
                        mob.yBodyRot = this.vrdata_world_pre.getBodyYaw();
                        mc.vehicleTracker.rotationCooldown = 10;
                    }
                }
            }
        }
    }

    public void doPlayerMoveInRoom(LocalPlayer player)
    {
        if (this.roomScaleMovementDelay > 0)
        {
            --this.roomScaleMovementDelay;
        }
        else
        {
            Minecraft minecraft = Minecraft.getInstance();

            if (player != null)
            {
                if (!player.isShiftKeyDown())
                {
                    if (!player.isSleeping())
                    {
                        if (!minecraft.jumpTracker.isjumping())
                        {
                            if (!minecraft.climbTracker.isGrabbingLadder())
                            {
                                if (player.isAlive())
                                {
                                    VRData vrdata = new VRData(this.roomOrigin, minecraft.vrSettings.walkMultiplier, this.worldScale, this.vrdata_world_pre.rotation_radians);

                                    if (minecraft.vehicleTracker.canRoomscaleDismount(minecraft.player))
                                    {
                                        Vec3 vec35 = minecraft.player.getVehicle().position();
                                        Vec3 vec36 = vrdata.getHeadPivot();
                                        double d6 = Math.sqrt((vec36.x - vec35.x) * (vec36.x - vec35.x) + (vec36.z - vec35.z) * (vec36.z - vec35.z));

                                        if (d6 > 1.0D)
                                        {
                                            minecraft.sneakTracker.sneakCounter = 5;
                                        }
                                    }
                                    else
                                    {
                                        float f = player.getBbWidth() / 2.0F;
                                        float f1 = player.getBbHeight();
                                        Vec3 vec3 = vrdata.getHeadPivot();
                                        double d0 = vec3.x;
                                        double d1 = player.getY();
                                        double d2 = vec3.z;
                                        AABB aabb = new AABB(d0 - (double)f, d1, d2 - (double)f, d0 + (double)f, d1 + (double)f1, d2 + (double)f);
                                        Vec3 vec31 = null;
                                        float f2 = 0.0625F;
                                        boolean flag = minecraft.level.noCollision(player, aabb);

                                        if (flag)
                                        {
                                            player.setPosRaw(d0, !minecraft.vrSettings.simulateFalling ? d1 : player.getY(), d2);
                                            player.setBoundingBox(new AABB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.minY + (double)f1, aabb.maxZ));
                                            player.fallDistance = 0.0F;
                                            this.getEstimatedTorsoPosition(d0, d1, d2);
                                        }
                                        else if ((minecraft.vrSettings.walkUpBlocks && player.getMuhJumpFactor() == 1.0F || minecraft.climbTracker.isGrabbingLadder() && minecraft.vrSettings.realisticClimbEnabled) && player.fallDistance == 0.0F)
                                        {
                                            if (vec31 == null)
                                            {
                                                vec31 = this.getEstimatedTorsoPosition(d0, d1, d2);
                                            }

                                            float f3 = player.getDimensions(player.getPose()).width * 0.45F;
                                            double d3 = (double)(f - f3);
                                            AABB aabb1 = new AABB(vec31.x - d3, aabb.minY, vec31.z - d3, vec31.x + d3, aabb.maxY, vec31.z + d3);
                                            boolean flag1 = !minecraft.level.noCollision(player, aabb1);

                                            if (flag1)
                                            {
                                                double d4 = vec31.x - d0;
                                                double d5 = vec31.z - d2;
                                                aabb = aabb.move(d4, 0.0D, d5);
                                                int i = 0;

                                                if (player.onClimbable() && minecraft.vrSettings.realisticClimbEnabled)
                                                {
                                                    i = 6;
                                                }

                                                for (int j = 0; j <= 10 + i; ++j)
                                                {
                                                    aabb = aabb.move(0.0D, 0.1D, 0.0D);
                                                    flag = minecraft.level.noCollision(player, aabb);

                                                    if (flag)
                                                    {
                                                        d0 = d0 + d4;
                                                        d2 = d2 + d5;
                                                        d1 = d1 + (double)(0.1F * (float)j);
                                                        player.setPosRaw(d0, d1, d2);
                                                        player.setBoundingBox(new AABB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ));
                                                        Vec3 vec32 = this.roomOrigin.add(d4, (double)(0.1F * (float)j), d5);
                                                        this.setRoomOrigin(vec32.x, vec32.y, vec32.z, false);
                                                        Vec3 vec33 = player.getLookAngle();
                                                        Vec3 vec34 = (new Vec3(vec33.x, 0.0D, vec33.z)).normalize();
                                                        player.fallDistance = 0.0F;
                                                        minecraft.player.stepSound(new BlockPos(player.position()), player.position());
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public Vec3 getEstimatedTorsoPosition(double x, double y, double z)
    {
        Entity entity = Minecraft.getInstance().player;
        Vec3 vec3 = entity.getLookAngle();
        Vec3 vec31 = (new Vec3(vec3.x, 0.0D, vec3.z)).normalize();
        float f = (float)vec3.y * 0.25F;
        return new Vec3(x + vec31.x * (double)f, y + vec31.y * (double)f, z + vec31.z * (double)f);
    }

    public void blockDust(double x, double y, double z, int count, BlockPos bp, BlockState bs, float scale, float velscale)
    {
        new Random();
        Minecraft minecraft = Minecraft.getInstance();

        for (int i = 0; i < count; ++i)
        {
            TerrainParticle terrainparticle = new TerrainParticle(minecraft.level, x, y, z, 0.0D, 0.0D, 0.0D, bs);
            terrainparticle.setPower(velscale);
            //TODO: check
           // minecraft.particleEngine.add(terrainparticle.init(bp).scale(scale));
            minecraft.particleEngine.add(terrainparticle.scale(scale));
        }
    }

    public void updateFreeMove()
    {
        if (this.mc.teleportTracker.isAiming())
        {
            this.isFreeMoveCurrent = false;
        }

        if (this.mc.player.input.forwardImpulse != 0.0F || this.mc.player.input.leftImpulse != 0.0F)
        {
            this.isFreeMoveCurrent = true;
        }

        this.updateTeleportKeys();
    }

    public boolean getFreeMove()
    {
        if (this.mc.vrSettings.seated)
        {
            return this.mc.vrSettings.seatedFreeMove || !this.isTeleportEnabled();
        }
        else
        {
            return this.isFreeMoveCurrent || this.mc.vrSettings.forceStandingFreeMove;
        }
    }

    public String toString()
    {
        return "VRPlayer: \r\n \t origin: " + this.roomOrigin + "\r\n \t rotation: " + String.format("%.3f", Minecraft.getInstance().vrSettings.worldRotation) + "\r\n \t scale: " + String.format("%.3f", this.worldScale) + "\r\n \t room_pre " + this.vrdata_room_pre + "\r\n \t world_pre " + this.vrdata_world_pre + "\r\n \t world_post " + this.vrdata_world_post + "\r\n \t world_render " + this.vrdata_world_render;
    }

    public Vec3 getRightClickLookOverride(Player entity, int c)
    {
        Vec3 vec3 = entity.getLookAngle();

        if (this.mc.gameRenderer.crossVec != null)
        {
            vec3 = entity.getEyePosition(1.0F).subtract(this.mc.gameRenderer.crossVec).normalize().reverse();
        }

        ItemStack itemstack;
        label54:
        {
            itemstack = c == 0 ? entity.getMainHandItem() : entity.getOffhandItem();

            if (!(itemstack.getItem() instanceof SnowballItem) && !(itemstack.getItem() instanceof EggItem) && !(itemstack.getItem() instanceof SpawnEggItem) && !(itemstack.getItem() instanceof PotionItem) && !(itemstack.getItem() instanceof BowItem))
            {
                if (!(itemstack.getItem() instanceof CrossbowItem))
                {
                    break label54;
                }

                CrossbowItem crossbowitem = (CrossbowItem)itemstack.getItem();

                if (!CrossbowItem.isCharged(itemstack))
                {
                    break label54;
                }
            }

            VRData vrdata = this.mc.vrPlayer.vrdata_world_pre;
            vec3 = vrdata.getController(c).getDirection();
            Vec3 vec31 = this.mc.bowTracker.getAimVector();

            if (this.mc.bowTracker.isNotched() && vec31 != null && vec31.lengthSqr() > 0.0D)
            {
                vec3 = vec31.reverse();
            }

            return vec3;
        }

        if (itemstack.getItem() == Items.BUCKET && this.mc.interactTracker.bukkit[c])
        {
            vec3 = entity.getEyePosition(1.0F).subtract(this.mc.vrPlayer.vrdata_world_pre.getController(c).getPosition()).normalize().reverse();
        }

        return vec3;
    }

    public void doPermanantLookOverride(LocalPlayer entity, VRData data)
    {
    	if (entity == null)
    		return;


    	if (entity.isPassenger())
    	{
    		//Server-side movement
    		Vec3 vec3 = VehicleTracker.getSteeringDirection(entity);

    		if (vec3 != null)
    		{
    			entity.setXRot((float)Math.toDegrees(Math.asin(-vec3.y / vec3.length())));
    			entity.setYRot((float)Math.toDegrees(Math.atan2(-vec3.x, vec3.z)));
    			entity.setYHeadRot(entity.getYRot());
    		}
    	} else if(entity.isBlocking()) {
    		//block direction
    		if (entity.getUsedItemHand() == InteractionHand.MAIN_HAND) {
    			entity.setYRot(data.getController(0).getYaw());
    			entity.setYHeadRot(entity.getYRot());
    			entity.setXRot(-data.getController(0).getPitch());
    		} else {
    			entity.setYRot(data.getController(1).getYaw());
    			entity.setYHeadRot(entity.getYRot());
    			entity.setXRot(-data.getController(1).getPitch());
    		}
    	}
    	else  if (entity.isSprinting() && (entity.input.jumping || mc.options.keyJump.isDown()) || entity.isFallFlying() || entity.isSwimming() && entity.zza > 0.0F)
    	{
    		//Server-side movement
    		VRSettings vrsettings = this.mc.vrSettings;

    		if (this.mc.vrSettings.vrFreeMoveMode == VRSettings.FreeMove.CONTROLLER)
    		{
    			entity.setYRot(data.getController(1).getYaw());
    			entity.setYHeadRot(entity.getYRot());
    			entity.setXRot(-data.getController(1).getPitch());
    		}
    		else
    		{
    			entity.setYRot(data.hmd.getYaw());
    			entity.setYHeadRot(entity.getYRot());
    			entity.setXRot(-data.hmd.getPitch());
    		}
    	}
    	else if (mc.gameRenderer.crossVec != null){
    		//Look AT the crosshair by default, most compatible with mods.
    		Vec3 playerToCrosshair = entity.getEyePosition(1).subtract(mc.gameRenderer.crossVec); //backwards
    		double what = playerToCrosshair.y/playerToCrosshair.length();
    		if(what > 1) what = 1;
    		if(what < -1) what = -1;
    		float pitch = (float)Math.toDegrees(Math.asin(what));
    		float yaw = (float)Math.toDegrees(Math.atan2(playerToCrosshair.x, -playerToCrosshair.z));    
    		entity.setXRot(pitch);
    		entity.setYRot(yaw);
    		entity.setYHeadRot(yaw);
    	} else {
    		//use HMD only if no crosshair hit.
    		entity.setYRot(data.hmd.getYaw());
    		entity.setYHeadRot(entity.getYRot());
    		entity.setXRot(-data.hmd.getPitch());	
    	}
    }

    public Vec3 AimedPointAtDistance(VRData source, int controller, double distance)
    {
        Vec3 vec3 = source.getController(controller).getPosition();
        Vec3 vec31 = source.getController(controller).getDirection();
        return vec3.add(vec31.x * distance, vec31.y * distance, vec31.z * distance);
    }

    public HitResult rayTraceBlocksVR(VRData source, int controller, double blockReachDistance, boolean p_174822_4_)
    {
        Vec3 vec3 = source.getController(controller).getPosition();
        Vec3 vec31 = this.AimedPointAtDistance(source, controller, blockReachDistance);
        return this.mc.level.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, p_174822_4_ ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, this.mc.player));
    }

    public boolean isTeleportSupported()
    {
        return !this.noTeleportClient;
    }

    public boolean isTeleportOverridden()
    {
        return this.teleportOverride;
    }

    public boolean isTeleportEnabled()
    {
        boolean flag = !this.noTeleportClient || this.teleportOverride;

        if (this.mc.vrSettings.seated)
        {
            return flag;
        }
        else
        {
            return flag && !this.mc.vrSettings.forceStandingFreeMove;
        }
    }

    public void setTeleportSupported(boolean supported)
    {
        this.noTeleportClient = !supported;
        this.updateTeleportKeys();
    }

    public void setTeleportOverride(boolean override)
    {
        this.teleportOverride = override;
        this.updateTeleportKeys();
    }

    private void updateTeleportKeys()
    {
        this.mc.vr.getInputAction(this.mc.vr.keyTeleport).setEnabled(this.isTeleportEnabled());
        this.mc.vr.getInputAction(this.mc.vr.keyTeleportFallback).setEnabled(!this.isTeleportEnabled());
    }
}
