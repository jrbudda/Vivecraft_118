package org.vivecraft.tweaker;

import cpw.mods.modlauncher.TransformerHolder;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerActivity;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import cpw.mods.modlauncher.api.ITransformer.Target;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class VivecraftTransformer implements ITransformer<ClassNode>
{
    private static final Logger LOGGER = LogManager.getLogger();
    public List<ITransformer> undeadClassTransformers = new ArrayList<>();
    public List<ITransformer> lostMethodTransformers = new ArrayList<>();
    public List<ITransformer> fieldTransformersOftheDamned = new ArrayList<>();
    public Set<Target> ofTargets = null;
    private List<String> exclusions = Arrays.asList(
    		"net/minecraft/server/packs/repository/Pack",
    		"net/minecraft/server/packs/repository/PackRepository",
    		"net/minecraft/server/packs/repository/Pack$PackConstructor",
    		"net/minecraft/server/packs/repository/Pack$Position",
    		"net/minecraft/item/Item", 
    		"net/minecraft/item/Item$Properties", 
    		"net/minecraft/client/gui/screen/inventory/ContainerScreen", 
    		"net/minecraft/client/gui/screen/inventory/CreativeScreen",
    		"net/minecraft/fluid/FluidState",
    		"net/minecraft/world/item/crafting/RecipeManager",
    		"net/minecraft/client/Minecraft",
    		"net/minecraft/client/Minecraft$ExperimentalDialogType",
            "net/minecraft/server/level/ServerPlayer"
    );
    
    public TransformerVoteResult castVote(ITransformerVotingContext context)
    {
        return TransformerVoteResult.YES;
    }

    public Set<Target> targets()
    {
        Set<Target> set = new HashSet<>();
        String[] astring = this.getNamesMatching("vcsrg", ".clsrg");

        for (int i = 0; i < astring.length; ++i)
        {
            String s = astring[i];
            s = LoaderUtils.removePrefix(s, new String[] {"vcsrg/"});
            s = LoaderUtils.removeSuffix(s, new String[] {".clsrg"});
            Target target = Target.targetPreClass(s);

            if (!this.exclusions.contains(s) && !s.contains("minecraftforge"))
            {
                set.add(target);
            }
        }

        LOGGER.info("Targets: " + set.size());
        return set;
    }

    public ClassNode transform(ClassNode input, ITransformerVotingContext context)
    {
        ClassNode classnode = input;
        String s = context.getClassName();
        String s1 = s.replace('.', '/');
        byte[] abyte = this.getResourceBytes(s1 + ".clsrg");

        if (abyte != null)
        {
            LOGGER.info("Class Debug " + s + " History ... ");

            for (ITransformerActivity itransformeractivity : context.getAuditActivities())
            {
            	LOGGER.info("... " + itransformeractivity.getActivityString());
            }

            InputStream inputstream = new ByteArrayInputStream(abyte);
            ClassNode classnode1 = this.loadClass(inputstream);
            LOGGER.info("Vivecraft Replacing " + s);

            if (classnode1 != null)
            {
                this.debugClass(classnode1);
                AccessFixer.fixMemberAccess(input, classnode1);
                classnode = classnode1;
            }

            for (ITransformer<ClassNode> itransformer : this.undeadClassTransformers)
            {
                TransformerHolder<ClassNode> transformerholder = (TransformerHolder)itransformer;

                if (!transformerholder.owner().name().contains("OptiFine") && !transformerholder.owner().name().contains("Vivecraft"))
                {
                    for (Target target : transformerholder.targets())
                    {
                        if (target.getClassName().equals(context.getClassName()))
                        {
                            classnode = transformerholder.transform(classnode, context);
                            LOGGER.info("ARISE! " + transformerholder.owner().name() + " " + target.getClassName() + " " + target.getElementName() + " " + target.getElementDescriptor());
                        }
                    }
                }
            }
        } else {
            LOGGER.info("Class Debug " + s + " not found ");
        }

        List<MethodNode> list = new ArrayList<>();

        for (MethodNode methodnode : (List<MethodNode>)classnode.methods)
        {
            for (ITransformer<MethodNode> itransformer1 : this.lostMethodTransformers)
            {
                for (Target target1 : itransformer1.targets())
                {
                    if (target1.getClassName().equals(context.getClassName()) && target1.getElementName().equals(methodnode.name) && target1.getElementDescriptor().equals(methodnode.desc))
                    {
                    	LOGGER.info("ARISE! " + target1.getClassName() + " " + target1.getElementName() + " " + target1.getElementDescriptor());
                        methodnode = itransformer1.transform(methodnode, context);
                    }
                }
            }

            list.add(methodnode);
        }

        classnode.methods = list;
        List<FieldNode> list1 = new ArrayList<>();

        for (FieldNode fieldnode : (List<FieldNode>)classnode.fields)
        {
            for (ITransformer<FieldNode> itransformer2 : this.fieldTransformersOftheDamned)
            {
                for (Target target2 : itransformer2.targets())
                {
                    if (target2.getClassName().equals(context.getClassName()) && target2.getElementName().equals(fieldnode.name))
                    {
                    	LOGGER.info("ARISE! " + target2.getClassName() + " " + target2.getElementName() + " " + target2.getElementDescriptor());
                        fieldnode = itransformer2.transform(fieldnode, context);
                    }
                }
            }

            list1.add(fieldnode);
        }

        classnode.fields = list1;
        return classnode;
    }

    private void debugClass(ClassNode classNode)
    {
    }

    private ClassNode loadClass(InputStream in)
    {
        try
        {
            ClassReader classreader = new ClassReader(in);
            ClassNode classnode = new ClassNode(393216);
            classreader.accept(classnode, 0);
            return classnode;
        }
        catch (IOException ioexception1)
        {
            ioexception1.printStackTrace();
            return null;
        }
    }

    private String[] getNamesMatching(String prefix, String suffix)
    {
        List<String> list = new ArrayList<>();
        Enumeration<? extends ZipEntry> enumeration;
		try {
			enumeration = LoaderUtils.getVivecraftZip().entries();
	        while (enumeration.hasMoreElements())
	        {
	            ZipEntry zipentry = enumeration.nextElement();
	            String s = zipentry.getName();

	            if (s.startsWith(prefix) && s.endsWith(suffix))
	            {
	                list.add(s);
	            }
	        }

	        String[] astring = list.toArray(new String[list.size()]);
	        return astring;
			
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}

		return null;
    }

    private byte[] getResourceBytes(String name)
    {
        try
        {
            name = LoaderUtils.ensurePrefix(name, "/vcsrg/");
            InputStream inputstream = this.getClass().getResourceAsStream(name);

            if (inputstream == null)
            {
                return null;
            }
            else
            {
                byte[] abyte = LoaderUtils.readAll(inputstream);
                inputstream.close();
                return abyte;
            }
        }
        catch (IOException ioexception1)
        {
            ioexception1.printStackTrace();
            return null;
        }
    }
}
