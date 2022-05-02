package org.vivecraft.tweaker;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class MinecriftVanillaTweaker implements ITweaker
{
    private static final Logger LOGGER = LogManager.getLogger();
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile)
    {
        dbg("MinecriftVanillaTweaker: acceptOptions");
    }

    public void injectIntoClassLoader(LaunchClassLoader classLoader)
    {
        dbg("MinecriftVanillaTweaker: injectIntoClassLoader");
        classLoader.addTransformerExclusion("org.vivecraft.asm.");
        classLoader.registerTransformer("org.vivecraft.tweaker.MinecriftClassTransformer");
    }

    public String getLaunchTarget()
    {
        dbg("MinecriftVanillaTweaker: getLaunchTarget");
        return "net.minecraft.client.main.Main";
    }

    public String[] getLaunchArguments()
    {
        dbg("MinecriftVanillaTweaker: getLaunchArguments");
        return new String[0];
    }

    private static void dbg(String str)
    {
    	LOGGER.info(str);
    }
}
