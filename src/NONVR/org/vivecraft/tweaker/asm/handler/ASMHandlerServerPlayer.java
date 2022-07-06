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

public class ASMHandlerServerPlayer
{
	private static final Logger LOGGER = LogManager.getLogger();

	@ASMMethod(className = "net/minecraft/server/level/ServerPlayer", methodName = "m_143429_", methodDesc = "()V")
	public static void funItems(MethodNode methodNode)
	{
		InsnList il = new InsnList();
		il.add(new VarInsnNode(Opcodes.ALOAD, 0));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/utils/ASMInjections", "activateFun", "(Lnet/minecraft/server/level/ServerPlayer;)V", false));
		methodNode.instructions.insert(il);
	}

	@ASMMethod(className = "net/minecraft/server/level/ServerPlayer", methodName = "m_7197_", methodDesc = "(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;")
	public static void adjustItemThrow(MethodNode methodNode)
	{
		AbstractInsnNode node = ASMUtil.findFirstInstruction(methodNode, Opcodes.ASTORE, 4);
		if (node == null) {
			LOGGER.warn("Could not inject item throw");
			return;
		}

		InsnList il = new InsnList();
		il.add(new VarInsnNode(Opcodes.ALOAD, 0));
		il.add(new VarInsnNode(Opcodes.ALOAD, 4));
		il.add(new VarInsnNode(Opcodes.ILOAD, 2));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/utils/ASMInjections", "adjustItemThrow", "(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/item/ItemEntity;Z)V", false));
		methodNode.instructions.insert(node, il);
	}

	@ASMMethod(className = "net/minecraft/server/level/ServerPlayer", methodName = "m_9240_", methodDesc = "()V")
	public static void overridePose(MethodNode methodNode) {
		AbstractInsnNode node = ASMUtil.findFirstInstruction(methodNode, Opcodes.INVOKESPECIAL, "net/minecraft/world/entity/player/Player", "m_8119_", "()V", false);
		if (node == null) {
			LOGGER.warn("Could not inject pose override");
			return;
		}

		InsnList il = new InsnList();
		il.add(new VarInsnNode(Opcodes.ALOAD, 0));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/api/NetworkHelper", "overridePose", "(Lnet/minecraft/world/entity/player/Player;)V", false));
		methodNode.instructions.insert(node, il);
	}
}
