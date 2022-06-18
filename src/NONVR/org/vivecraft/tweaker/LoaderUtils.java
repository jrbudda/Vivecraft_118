package org.vivecraft.tweaker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipException;

public class LoaderUtils {
	private static URL ZipFileUrl;
    private static File vivecraftFile;
    
    public static void init() {
    	try {
    		ZipFileUrl = getVivecraftZipLocation().toURL();
    		vivecraftFile = toFile(ZipFileUrl.toURI());
    	} catch (Exception e) {
    		System.out.print("Error getting Vivecraft library: " + e.getLocalizedMessage());
		}
    }
    
    public static URI getVivecraftZipLocation() throws URISyntaxException
    {
        if (ZipFileUrl != null)
        {
            return ZipFileUrl.toURI();
        }
        else
        {
        	ZipFileUrl = LoaderUtils.class.getProtectionDomain().getCodeSource().getLocation();

            if (ZipFileUrl == null)
            {
                throw new RuntimeException("Could not find Vivecraft zip");
            }
            else
            {
                return ZipFileUrl.toURI();
            }
        }
    }

	public static java.util.zip.ZipFile getVivecraftZip() throws ZipException, URISyntaxException, IOException {
		if (vivecraftFile == null) {
			init();
		}
		return new java.util.zip.ZipFile(vivecraftFile);
	}
	
	public static URL getVivecraftURL() throws ZipException, URISyntaxException, IOException {
		if (ZipFileUrl == null) {
			init();
		}
		return ZipFileUrl;
	}
	
    public static File toFile(URI uri)
    {
        if (!"union".equals(uri.getScheme()))
        {
            return new File(uri);
        }
        else
        {
            try
            {
                String s = uri.getPath();

                if (s.contains("#"))
                {
                    s = s.substring(0, s.lastIndexOf("#"));
                }

                File file1 = new File(s);
                ZipFileUrl = file1.toURI().toURL();
                Map<String, String> map = new HashMap<>();
                map.put("create", "true");
                FileSystems.newFileSystem(URI.create("jar:" + ZipFileUrl + "!/"), map);
                return file1;
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
                return null;
            }
        }
    }
    public static String removePrefix(String str, String prefix)
    {
        if (str != null && prefix != null)
        {
            if (str.startsWith(prefix))
            {
                str = str.substring(prefix.length());
            }

            return str;
        }
        else
        {
            return str;
        }
    }

    public static String removePrefix(String str, String[] prefixes)
    {
        if (str != null && prefixes != null)
        {
            int i = str.length();

            for (int j = 0; j < prefixes.length; ++j)
            {
                String s = prefixes[j];
                str = removePrefix(str, s);

                if (str.length() != i)
                {
                    break;
                }
            }

            return str;
        }
        else
        {
            return str;
        }
    }

    public static String removeSuffix(String str, String suffix)
    {
        if (str != null && suffix != null)
        {
            if (str.endsWith(suffix))
            {
                str = str.substring(0, str.length() - suffix.length());
            }

            return str;
        }
        else
        {
            return str;
        }
    }

    public static String removeSuffix(String str, String[] suffixes)
    {
        if (str != null && suffixes != null)
        {
            int i = str.length();

            for (int j = 0; j < suffixes.length; ++j)
            {
                String s = suffixes[j];
                str = removeSuffix(str, s);

                if (str.length() != i)
                {
                    break;
                }
            }

            return str;
        }
        else
        {
            return str;
        }
    }
    
    public static String ensurePrefix(String str, String prefix)
    {
        if (str != null && prefix != null)
        {
            if (!str.startsWith(prefix))
            {
                str = prefix + str;
            }

            return str;
        }
        else
        {
            return str;
        }
    }
    public static byte[] readAll(InputStream is) throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        byte[] abyte = new byte[1024];

        while (true)
        {
            int i = is.read(abyte);

            if (i < 0)
            {
                is.close();
                return bytearrayoutputstream.toByteArray();
            }

            bytearrayoutputstream.write(abyte, 0, i);
        }
    }
}
