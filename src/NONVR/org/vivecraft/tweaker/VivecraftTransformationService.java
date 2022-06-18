package org.vivecraft.tweaker;

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import cpw.mods.modlauncher.api.IModuleLayerManager.Layer;
import cpw.mods.modlauncher.api.ITransformationService.Resource;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VivecraftTransformationService implements ITransformationService
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static VivecraftTransformer transformer;

    @Override
    public String name()
    {
        return "Vivecraft";
    }
    
    @Override
    public void initialize(IEnvironment environment)
    {
        LOGGER.info("VivecraftTransformationService.initialize");
    }
    
    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) throws IncompatibleEnvironmentException
    {
    	LOGGER.info("VivecraftTransformationService.onLoad");
    	for(String o: otherServices) {
        	LOGGER.info("Other: " + o);
    	}
    	
//    	if(!otherServices.contains("OptiFine")) {
//    		throw new IncompatibleEnvironmentException("Vivecraft requires Optifine");
//   	}
//    	if(otherServices.contains("Vivecraft")) {
//    		throw new IncompatibleEnvironmentException("multiple Vivecraft mods found");
//    	}   	
    	try
    	{
        	transformer = new VivecraftTransformer();
    	}
    	catch (Exception exception)
    	{
    		LOGGER.error("Error loading ZIP file: ", (Throwable)exception);
    		throw new IncompatibleEnvironmentException("Error loading ZIP file");
    	}
    }
    
       
    @Override
    public List<Resource> completeScan(IModuleLayerManager layerManager)
    {
        List<Resource> list = new ArrayList<>();
        List<SecureJar> list1 = new ArrayList<>();
        try {
			try {
				list1.add(new VivecraftJar(LoaderUtils.toFile(LoaderUtils.getVivecraftURL().toURI()).toPath()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (URISyntaxException e) {
		}
        list.add(new Resource(Layer.GAME, list1));
        return list;
    }
    
    @Override
    public Entry<Set<String>, Supplier<Function<String, Optional<URL>>>> additionalResourcesLocator()
    {
        return ITransformationService.super.additionalResourcesLocator();
    }

    //This method apparently does nothing at all anymore.
    @Override
    public Entry<Set<String>, Supplier<Function<String, Optional<URL>>>> additionalClassesLocator()
    {
        Set<String> set = new HashSet<>();
        set.add("org.vivecraft.");
        Supplier<Function<String, Optional<URL>>> supplier = () ->
        {
            return VivecraftTransformationService::getResourceUrl;
        };
        Entry<Set<String>, Supplier<Function<String, Optional<URL>>>> entry = new SimpleEntry<>(set, supplier);
        LOGGER.info("additionalClassesLocator: " + set);
        return entry;
    }

    public static Optional<URL> getResourceUrl(String name)
    {
        if (name.endsWith(".class")) //&& !name.startsWith("org.vivecraft/"))
        {
        	if(name.contains("org/vivecraft"))
        		name = "vcsrg/" + name;
        	else
        		name = "vcsrg/" + name.replace(".class", ".clsrg");
        }

        if (transformer == null)
        {
            return Optional.empty();
        }
        else
        {
            ZipEntry zipentry;
			try {
				zipentry = LoaderUtils.getVivecraftZip().getEntry(name);
				

	            if (zipentry == null)
	            {
	                return Optional.empty();
	            }
	            else
	            {
	                try
	                {
	                    String s = LoaderUtils.getVivecraftURL().toExternalForm();
	                    URL url = new URL("jar:" + s + "!/" + name);
	                    return Optional.of(url);
	                }
	                catch (IOException ioexception1)
	                {
	                    LOGGER.error(ioexception1);
	                    return Optional.empty();
	                } catch (URISyntaxException e) {
						e.printStackTrace();
					}
	            }
	            
			} catch (URISyntaxException | IOException e1) {
				e1.printStackTrace();
			}

        }
        LOGGER.info("getResourceURL: " + name + "  Failed");

        return null;
    }

    @Override
    public List<ITransformer> transformers()
    {
        LOGGER.info("VivecraftTransformationService.transformers");
        List<ITransformer> list = new ArrayList<>();

        if (transformer != null)
        {
            list.add(transformer);
        }

        list.add(new VivecraftASM_RecipeManager());
        list.add(new VivecraftASM_Minecraft());
        return list;
    }

    

}
