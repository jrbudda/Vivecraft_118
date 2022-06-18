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
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.vivecraft.item.VivecraftItems;

public class VivecraftASMTransformer implements ITransformer<MethodNode>
{
    private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public @NotNull TransformerVoteResult castVote(ITransformerVotingContext arg0) {
        return TransformerVoteResult.YES;
	}

	@Override
	public @NotNull Set<Target> targets() {
        Set<Target> set = new HashSet<>();
		set.add(Target.targetMethod("net/minecraft/world/item/crafting/RecipeManager", "m_5787_", "(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V"));
		return set;
	}

	@Override
	public @NotNull MethodNode transform(MethodNode arg0, ITransformerVotingContext arg1) {
		AbstractInsnNode node = null;
		for (AbstractInsnNode v: arg0.instructions.toArray()) {
			if(v.getOpcode() == Opcodes.ASTORE && ((VarInsnNode)v).var == 4) 
				node = v;
		}
		if (node==null) {
			LOGGER.warn("Could not insert recipes");
		}		
		InsnList il = new InsnList();
		il.add(new VarInsnNode(Opcodes.ALOAD, 4));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/item/VivecraftItems", "injectItems", "(Ljava/util/Map;)V", false)); 
		arg0.instructions.insertBefore(node.getNext(), il);
		return arg0;
	}
}
