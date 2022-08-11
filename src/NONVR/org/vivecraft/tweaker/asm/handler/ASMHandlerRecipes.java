package org.vivecraft.tweaker.asm.handler;

import org.vivecraft.tweaker.asm.ASMMethod;
import org.vivecraft.tweaker.asm.ASMUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ASMHandlerRecipes
{
	private static final Logger LOGGER = LogManager.getLogger();

	@ASMMethod(className = "net/minecraft/world/item/crafting/RecipeManager", methodName = "m_5787_", methodDesc = "(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V")
	public static void injectRecipes(MethodNode methodNode)
	{
		AbstractInsnNode node = ASMUtil.findFirstInstruction(methodNode, Opcodes.INVOKEINTERFACE, "java/util/Set", "stream", "()Ljava/util/stream/Stream;", true);
		if (node == null) {
			LOGGER.warn("Could not inject recipes");
			return;
		}

		InsnList il = new InsnList();
		il.add(new VarInsnNode(Opcodes.ALOAD, 4));
		il.add(new VarInsnNode(Opcodes.ALOAD, 5));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/utils/ASMInjections", "injectItems", "(Ljava/util/Map;Lcom/google/common/collect/ImmutableMap$Builder;)V", false));
		ASMUtil.insertInstructionsRelative(methodNode, node, -4, il);
	}
}
