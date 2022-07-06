package org.vivecraft.tweaker.asm.handler;

import org.vivecraft.tweaker.asm.ASMMethod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ASMHandlerMinecraft
{
	private static final Logger LOGGER = LogManager.getLogger();

	@ASMMethod(className = "net/minecraft/client/Minecraft", methodName = "m_91398_", methodDesc = "()V")
	public static void injectPMCTick(MethodNode methodNode)
	{
		InsnList il = new InsnList();
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/render/PlayerModelController", "getInstance", "()Lorg/vivecraft/render/PlayerModelController;", false));
		il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "org/vivecraft/render/PlayerModelController", "tick", "()V", false));
		methodNode.instructions.insert(il);
	}
}
