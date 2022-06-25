package org.vivecraft.tweaker.asm;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;

public class VivecraftASM_Minecraft implements ITransformer<MethodNode>
{
    private static final Logger LOGGER = LogManager.getLogger();

    @Nonnull
    public Set<Target> targets()
    {
    	Set<Target> targets = new HashSet<>();
    	targets.add(Target.targetMethod("net/minecraft/client/Minecraft", "m_91398_", "()V"));
    	return targets;
    }
    
    public MethodNode transform(MethodNode input, ITransformerVotingContext context)
    {
		LOGGER.info("Vivecraft Modifying " + input.name);
		InsnList il = new InsnList();
		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/render/PlayerModelController", "getInstance", "()Lorg/vivecraft/render/PlayerModelController;", false));
		il.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "org/vivecraft/render/PlayerModelController", "tick", "()V", false));
		input.instructions.insert(il);
        return input;
    }

    @Nonnull
    public TransformerVoteResult castVote(ITransformerVotingContext iTransformerVotingContext)
    {
        return TransformerVoteResult.YES;
    }
}
