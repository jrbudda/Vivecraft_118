package org.vivecraft.tweaker.asm.handler;

import org.vivecraft.tweaker.asm.ASMMethod;
import org.vivecraft.tweaker.asm.ASMUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ASMHandlerScreen
{
	private static final Logger LOGGER = LogManager.getLogger();

	@ASMMethod(className = "net/minecraft/client/gui/screens/Screen", methodName = "m_96558_", methodDesc = "(Lcom/mojang/blaze3d/vertex/PoseStack;I)V")
	public static void overrideMenuBG(MethodNode methodNode)
	{
		AbstractInsnNode nodeFrom = ASMUtil.findFirstInstruction(methodNode, Opcodes.LDC, -1072689136);
		AbstractInsnNode nodeTo = ASMUtil.findFirstInstruction(methodNode, Opcodes.LDC, -804253680);
		methodNode.instructions.set(nodeFrom, new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/utils/ScreenUtils", "getBGFrom", "()I", false));
		methodNode.instructions.set(nodeTo, new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/utils/ScreenUtils", "getBGTo", "()I", false));
	}
}
