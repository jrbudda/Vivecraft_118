package org.vivecraft.gameplay.trackers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.world.entity.Pose;
import org.vivecraft.api.NetworkHelper;

public class CrawlTracker extends Tracker
{
    private boolean wasCrawling;
    public boolean crawling;
    public boolean crawlsteresis;

    public CrawlTracker(Minecraft mc)
    {
        super(mc);
    }

    public boolean isActive(LocalPlayer player)
    {
        if (this.mc.vrSettings.seated)
        {
            return false;
        }
        else if (!this.mc.vrSettings.allowCrawling)
        {
            return false;
        }
        else if (!NetworkHelper.serverAllowsCrawling)
        {
            return false;
        }
        else if (!player.isAlive())
        {
            return false;
        }
        else if (player.isSpectator())
        {
            return false;
        }
        else if (player.isSleeping())
        {
            return false;
        }
        else
        {
            return !player.isPassenger();
        }
    }

    public void reset(LocalPlayer player)
    {
        this.crawling = false;
        this.crawlsteresis = false;
        this.updateState(player);
    }

    public void doProcess(LocalPlayer player)
    {
        this.crawling = this.mc.vr.hmdPivotHistory.averagePosition((double)0.2F).y * (double)this.mc.vrPlayer.worldScale + (double)0.1F < (double)this.mc.vrSettings.crawlThreshold;
        this.updateState(player);
    }

    private void updateState(LocalPlayer player)
    {
        if (this.crawling != this.wasCrawling)
        {
            if (this.crawling)
            {
                player.setPose(Pose.SWIMMING);
                this.crawlsteresis = true;
            }

            if (NetworkHelper.serverAllowsCrawling)
            {
                ServerboundCustomPayloadPacket serverboundcustompayloadpacket = NetworkHelper.getVivecraftClientPacket(NetworkHelper.PacketDiscriminators.CRAWL, new byte[] {(byte)(this.crawling ? 1 : 0)});

                if (this.mc.getConnection() != null)
                {
                    this.mc.getConnection().send(serverboundcustompayloadpacket);
                }
            }

            this.wasCrawling = this.crawling;
        }

        if (!this.crawling && player.getPose() != Pose.SWIMMING)
        {
            this.crawlsteresis = false;
        }
    }
}
