package org.vivecraft.tweaker.asm.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import org.vivecraft.tweaker.asm.ASMMethod;
import org.vivecraft.tweaker.asm.ASMUtil;

public class ASMHandlerCreativeScreen
{
	private static final Logger LOGGER = LogManager.getLogger();

	@ASMMethod(className = "net/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen", methodName = "m_98630_", methodDesc = "()V")
	public static void injectSearch(MethodNode methodNode)
	{
		AbstractInsnNode node = ASMUtil.findFirstInstruction(methodNode, Opcodes.PUTFIELD, "net/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen", "f_98508_", "F");
		if (node == null) {
			LOGGER.warn("Could not inject creative search items");
			return;
		}

		InsnList il = new InsnList();
		il.add(new VarInsnNode(Opcodes.ALOAD, 0));
		il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen", "f_98510_", "Lnet/minecraft/client/gui/components/EditBox;"));
		il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/gui/components/EditBox", "m_94155_", "()Ljava/lang/String;", false));
		il.add(new VarInsnNode(Opcodes.ALOAD, 0));
		il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen", "f_97732_", "Lnet/minecraft/world/inventory/AbstractContainerMenu;"));
		il.add(new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu"));
		il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu", "f_98639_", "Lnet/minecraft/core/NonNullList;"));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/utils/ASMInjections", "addCreativeSearch", "(Ljava/lang/String;Lnet/minecraft/core/NonNullList;)V", false));
		ASMUtil.insertInstructionsRelative(methodNode, node, -3, il);
	}

	@ASMMethod(className = "net/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen", methodName = "m_98560_", methodDesc = "(Lnet/minecraft/world/item/CreativeModeTab;)V")
	public static void injectTabs(MethodNode methodNode)
	{
		AbstractInsnNode node = ASMUtil.findFirstInstruction(methodNode, Opcodes.INVOKEVIRTUAL, "net/minecraft/world/item/CreativeModeTab", "m_6151_", "(Lnet/minecraft/core/NonNullList;)V", false);
		if (node == null) {
			LOGGER.warn("Could not inject creative tab items");
			return;
		}

		InsnList il = new InsnList();
		il.add(new VarInsnNode(Opcodes.ALOAD, 1));
		il.add(new VarInsnNode(Opcodes.ALOAD, 0));
		il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen", "f_97732_", "Lnet/minecraft/world/inventory/AbstractContainerMenu;"));
		il.add(new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu"));
		il.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu", "f_98639_", "Lnet/minecraft/core/NonNullList;"));
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/utils/ASMInjections", "addCreativeItems", "(Lnet/minecraft/world/item/CreativeModeTab;Lnet/minecraft/core/NonNullList;)V", false));
		methodNode.instructions.insert(node, il);
	}
}
