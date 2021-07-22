package org.vivecraft.gameplay.trackers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public abstract class Tracker
{
    public Minecraft mc;

    public Tracker(Minecraft mc)
    {
        this.mc = mc;
    }

    public abstract boolean isActive(LocalPlayer var1);

    public abstract void doProcess(LocalPlayer var1);

    public void reset(LocalPlayer player)
    {
    }

    public void idleTick(LocalPlayer player)
    {
    }

    public Tracker.EntryPoint getEntryPoint()
    {
        return Tracker.EntryPoint.LIVING_UPDATE;
    }

    public static enum EntryPoint
    {
        LIVING_UPDATE,
        SPECIAL_ITEMS;
    }
}
