package org.vivecraft.tweaker.asm.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import org.vivecraft.tweaker.asm.ASMMethod;
import org.vivecraft.tweaker.asm.ASMUtil;

public class ASMHandlerContainerScreen
{
	private static final Logger LOGGER = LogManager.getLogger();

	@ASMMethod(className = "net/minecraft/client/gui/screens/inventory/AbstractContainerScreen", methodName = "m_7979_", methodDesc = "(DDIDD)Z")
	public static void injectShift(MethodNode methodNode)
	{
		AbstractInsnNode node = ASMUtil.findFirstInstruction(methodNode, Opcodes.GETFIELD, "net/minecraft/client/gui/screens/inventory/AbstractContainerScreen", "f_97738_", "Z");
		if (node == null) {
			LOGGER.warn("Could not inject hasShiftDown");
			return;
		}

		LabelNode label = ((JumpInsnNode)methodNode.instructions.get(methodNode.instructions.indexOf(node) + 1)).label;

		InsnList il = new InsnList();
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/client/gui/screens/Screen", "m_96638_", "()Z", false));
		il.add(new JumpInsnNode(Opcodes.IFNE, label));
		ASMUtil.insertInstructionsRelative(methodNode, node, -2, il);
	}
}
