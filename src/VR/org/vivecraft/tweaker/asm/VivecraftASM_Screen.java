package org.vivecraft.tweaker.asm;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;

public class VivecraftASM_Screen implements ITransformer<MethodNode>
{
    private static final Logger LOGGER = LogManager.getLogger();

    @Nonnull
    public Set<Target> targets()
    {
    	Set<Target> targets = new HashSet<>();
    	targets.add(Target.targetMethod("net/minecraft/client/gui/screens/Screen", "m_96558_", "(Lcom/mojang/blaze3d/vertex/PoseStack;I)V"));
    	return targets;
    }
    
    public MethodNode transform(MethodNode input, ITransformerVotingContext context)
    {
		LOGGER.info("Vivecraft Modifying " + input.name);
		AbstractInsnNode nodeFrom = ASMUtil.findFirstInstruction(input, Opcodes.LDC, -1072689136);
		AbstractInsnNode nodeTo = ASMUtil.findFirstInstruction(input, Opcodes.LDC, -804253680);
		if(nodeFrom == null || nodeTo == null) {
			LOGGER.warn("Could not inject Screen BG");
			return input;
		}
		input.instructions.set(nodeFrom, new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/utils/ScreenUtils", "getBGFrom", "()I", false));
		input.instructions.set(nodeTo, new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/utils/ScreenUtils", "getBGTo", "()I", false));
		return input;
    }

    @Nonnull
    public TransformerVoteResult castVote(ITransformerVotingContext iTransformerVotingContext)
    {
        return TransformerVoteResult.YES;
    }
}
