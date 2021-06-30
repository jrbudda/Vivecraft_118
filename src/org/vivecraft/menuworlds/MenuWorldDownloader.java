package org.vivecraft.menuworlds;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import org.vivecraft.settings.VRSettings;
import org.vivecraft.utils.Utils;

public class MenuWorldDownloader
{
    private static final String baseUrl = "https://cache.techjargaming.com/vivecraft/115/";
    private static boolean init;
    private static Random rand;

    public static void init()
    {
        if (!init)
        {
            rand = new Random();
            rand.nextInt();
            init = true;
        }
    }

    public static void downloadWorld(String path) throws IOException, NoSuchAlgorithmException
    {
        File file1 = new File(path);
        file1.getParentFile().mkdirs();

        if (file1.exists())
        {
            String s = Utils.getFileChecksum(file1, "SHA-1");
            String s1 = Utils.httpReadLine("https://cache.techjargaming.com/vivecraft/115/checksum.php?file=" + path);

            if (s.equals(s1))
            {
                System.out.println("SHA-1 matches for " + path);
                return;
            }
        }

        System.out.println("Downloading world " + path);
        Utils.httpReadToFile("https://cache.techjargaming.com/vivecraft/115/" + path, file1, true);
    }

    public static InputStream getRandomWorld() throws IOException, NoSuchAlgorithmException
    {
        init();
        VRSettings vrsettings = Minecraft.getInstance().vrSettings;

        try
        {
            List<MenuWorldDownloader.MenuWorldItem> list = new ArrayList<>();

            if (vrsettings.menuWorldSelection == 0 || vrsettings.menuWorldSelection == 1)
            {
                list.addAll(getCustomWorlds());
            }

            if (vrsettings.menuWorldSelection == 0 || vrsettings.menuWorldSelection == 2 || list.size() == 0)
            {
                list.addAll(getOfficialWorlds());
            }

            if (list.size() == 0)
            {
                return getRandomWorldFallback();
            }
            else
            {
                MenuWorldDownloader.MenuWorldItem menuworlddownloader$menuworlditem = getRandomWorldFromList(list);
                return getStreamForWorld(menuworlddownloader$menuworlditem);
            }
        }
        catch (IOException ioexception)
        {
            ioexception.printStackTrace();
            return getRandomWorldFallback();
        }
    }

    private static InputStream getStreamForWorld(MenuWorldDownloader.MenuWorldItem world) throws IOException, NoSuchAlgorithmException
    {
        if (world.file != null)
        {
            System.out.println("Using world " + world.file.getName());
            return new FileInputStream(world.file);
        }
        else if (world.path != null)
        {
            downloadWorld(world.path);
            System.out.println("Using official world " + world.path);
            return new FileInputStream(world.path);
        }
        else
        {
            throw new IllegalArgumentException("File or path must be assigned");
        }
    }

    private static List<MenuWorldDownloader.MenuWorldItem> getCustomWorlds() throws IOException
    {
        File file1 = new File("menuworlds/custom_114");
        return (List<MenuWorldDownloader.MenuWorldItem>)(file1.exists() ? getWorldsInDirectory(file1) : new ArrayList<>());
    }

    private static List<MenuWorldDownloader.MenuWorldItem> getOfficialWorlds() throws IOException
    {
        List<MenuWorldDownloader.MenuWorldItem> list = new ArrayList<>();

        for (String s : Utils.httpReadAllLines("https://cache.techjargaming.com/vivecraft/115/menuworlds_list.php?minver=2&maxver=4&mcver=1.16.5"))
        {
            list.add(new MenuWorldDownloader.MenuWorldItem("menuworlds/" + s, (File)null));
        }

        return list;
    }

    private static InputStream getRandomWorldFallback() throws IOException, NoSuchAlgorithmException
    {
        System.out.println("Couldn't find a world, trying random file from directory");
        File file1 = new File("menuworlds");

        if (file1.exists())
        {
            MenuWorldDownloader.MenuWorldItem menuworlddownloader$menuworlditem = getRandomWorldFromList(getWorldsInDirectory(file1));

            if (menuworlddownloader$menuworlditem != null)
            {
                return getStreamForWorld(menuworlddownloader$menuworlditem);
            }
        }

        return null;
    }

    private static List<MenuWorldDownloader.MenuWorldItem> getWorldsInDirectory(File dir) throws IOException
    {
        List<MenuWorldDownloader.MenuWorldItem> list = new ArrayList<>();
        List<File> list1 = Arrays.asList(dir.listFiles((filex) ->
        {
            return filex.isFile() && filex.getName().toLowerCase().endsWith(".mmw");
        }));

        if (list1.size() > 0)
        {
            for (File file1 : list1)
            {
                int i = MenuWorldExporter.readVersion(file1);

                if (i >= 2 && i <= 4)
                {
                    list.add(new MenuWorldDownloader.MenuWorldItem((String)null, file1));
                }
            }
        }

        return list;
    }

    private static MenuWorldDownloader.MenuWorldItem getRandomWorldFromList(List<MenuWorldDownloader.MenuWorldItem> list)
    {
        return list.size() > 0 ? list.get(rand.nextInt(list.size())) : null;
    }

    private static class MenuWorldItem
    {
        final File file;
        final String path;

        public MenuWorldItem(String path, File file)
        {
            this.file = file;
            this.path = path;
        }
    }
}
