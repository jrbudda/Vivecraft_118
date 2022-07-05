package org.vivecraft.tweaker.asm.handler;

import org.vivecraft.tweaker.asm.ASMClass;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

public class ASMHandlerFluidState
{
    @ASMClass(className = "net/minecraft/world/level/material/FluidState")
    public static void extendableFluidState(ClassNode classNode) {
        classNode.access &= ~Opcodes.ACC_FINAL;
    }
}
