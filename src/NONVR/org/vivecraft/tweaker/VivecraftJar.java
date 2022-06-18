package org.vivecraft.tweaker;

import cpw.mods.jarhandling.impl.Jar;
import cpw.mods.jarhandling.impl.SimpleJarMetadata;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import optifine.OptiFineTransformationService;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

public class VivecraftJar extends Jar
{
    public VivecraftJar(Path... paths)
    {
        super(Manifest::new, (jar) ->
        {
            return new SimpleJarMetadata("org.vivecraft", (String)null, jar.getPackages(), new ArrayList<>());
        }, (s1, s2) ->
        {
            return true;
        }, paths);
    }
    
    @Override
    public Set<String> getPackages()
    {
        Set<String> set = new HashSet<>();
        Enumeration<? extends ZipEntry> enumeration;
		try {
			enumeration = LoaderUtils.getVivecraftZip().entries();
	 
			while (enumeration.hasMoreElements())
	        {
	            ZipEntry zipentry = enumeration.nextElement();
	            String s = zipentry.getName();

	            if (s.startsWith("vcsrg/") && s.endsWith(".class"))
	            {
	            	String pkg =s.substring(s.indexOf("/") + 1, s.lastIndexOf("/")).replace('/', '.');
	                set.add(pkg);
	            }
	        }
		} catch (Exception ex) {
            System.out.println("ERROR IN VIVECRAFTJAR " + ex.toString());
		}

        return set;
    }
    
   @Override
   public Optional<URI> findFile(String name)
    {
        return VivecraftTransformationService.getResourceUrl(name).map(LamdbaExceptionUtils.rethrowFunction(URL::toURI));
    }

}
