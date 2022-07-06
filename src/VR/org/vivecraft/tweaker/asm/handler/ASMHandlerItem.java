package org.vivecraft.tweaker.asm.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import org.vivecraft.tweaker.asm.ASMMethod;
import org.vivecraft.tweaker.asm.ASMUtil;

public class ASMHandlerItem
{
	private static final Logger LOGGER = LogManager.getLogger();

	@ASMMethod(className = "net/minecraft/world/item/Item", methodName = "m_41435_", methodDesc = "(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/ClipContext$Fluid;)Lnet/minecraft/world/phys/BlockHitResult;")
	public static void itemRayTrace(MethodNode methodNode)
	{
		AbstractInsnNode node = ASMUtil.findFirstInstruction(methodNode, Opcodes.ASTORE, 5);
		if (node == null) {
			LOGGER.warn("Failed to inject item ray trace patch");
			return;
		}

		InsnList il = new InsnList();
		il.add(new VarInsnNode(Opcodes.ALOAD, 1));
		il.add(new VarInsnNode(Opcodes.FLOAD, 3));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/utils/ASMInjections", "itemRayTracePitch", "(Lnet/minecraft/world/entity/player/Player;F)F", false));
		il.add(new VarInsnNode(Opcodes.FSTORE, 3));
		il.add(new VarInsnNode(Opcodes.ALOAD, 1));
		il.add(new VarInsnNode(Opcodes.FLOAD, 4));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/utils/ASMInjections", "itemRayTraceYaw", "(Lnet/minecraft/world/entity/player/Player;F)F", false));
		il.add(new VarInsnNode(Opcodes.FSTORE, 4));
		il.add(new VarInsnNode(Opcodes.ALOAD, 1));
		il.add(new VarInsnNode(Opcodes.ALOAD, 5));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/utils/ASMInjections", "itemRayTracePos", "(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;", false));
		il.add(new VarInsnNode(Opcodes.ASTORE, 5));
		methodNode.instructions.insert(node, il);
	}

	@ASMMethod(className = "net/minecraft/world/item/Item", methodName = "m_7203_", methodDesc = "(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;")
	public static void injectEatMe(MethodNode methodNode) {
		AbstractInsnNode node = ASMUtil.findFirstInstruction(methodNode, Opcodes.INVOKEVIRTUAL, "net/minecraft/world/entity/player/Player", "m_36391_", "(Z)Z", false);
		if (node == null) {
			LOGGER.warn("Failed to inject eat me");
			return;
		}

		InsnList il = new InsnList();
		il.add(new VarInsnNode(Opcodes.ALOAD, 4));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/utils/ASMInjections", "checkEatMe", "(ZLnet/minecraft/world/item/ItemStack;)Z", false));
		methodNode.instructions.insert(node, il);
	}
}
