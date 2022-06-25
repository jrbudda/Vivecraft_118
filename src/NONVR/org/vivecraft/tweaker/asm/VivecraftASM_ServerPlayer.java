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

public class VivecraftASM_ServerPlayer implements ITransformer<MethodNode>
{
    private static final Logger LOGGER = LogManager.getLogger();

    private String initInventoryMenu = "m_143429_";
    private String drop = "m_7197_";
    private String doTick = "m_9240_";

    @Nonnull
    public Set<Target> targets()
    {
    	Set<Target> targets = new HashSet<>();
    	targets.add(Target.targetMethod("net/minecraft/server/level/ServerPlayer", initInventoryMenu, "()V"));
    	targets.add(Target.targetMethod("net/minecraft/server/level/ServerPlayer", drop, "(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;"));
    	targets.add(Target.targetMethod("net/minecraft/server/level/ServerPlayer", doTick, "()V"));
    	return targets;
    }
    
    public MethodNode transform(MethodNode input, ITransformerVotingContext context)
    {
		LOGGER.info("Vivecraft Modifying " + input.name);
    	if(input.name.equalsIgnoreCase(drop)) {
    		AbstractInsnNode node = ASMUtil.findFirstInstruction(input, Opcodes.ASTORE, 4);
    		
    		if (node == null) {
    			LOGGER.warn("Could not inject item throw");
    			return input;
    		}
    		
    		InsnList il = new InsnList();
    		il.add(new VarInsnNode(Opcodes.ALOAD, 0));
    		il.add(new VarInsnNode(Opcodes.ALOAD, 4));
    		il.add(new VarInsnNode(Opcodes.ILOAD, 2));
    		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/utils/ASMInjections", "adjustItemThrow", "(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/item/ItemEntity;Z)V", false));
    		input.instructions.insert(node, il);
    		
    	} else if(input.name.equalsIgnoreCase(initInventoryMenu)) {
    		InsnList il = new InsnList();
    		il.add(new VarInsnNode(Opcodes.ALOAD, 0));
    		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/utils/ASMInjections", "activateFun", "(Lnet/minecraft/server/level/ServerPlayer;)V", false));
    		input.instructions.insert(il);
    	} else if(input.name.equalsIgnoreCase(doTick)) {
			AbstractInsnNode node = null;
			for (AbstractInsnNode v: input.instructions.toArray()) {
				if(v.getOpcode() == Opcodes.INVOKESPECIAL) 
					node = v;
			}
			if(node == null) {
    			LOGGER.warn("Could not inject pose override");
    			return input;
			}
    		InsnList il = new InsnList();
    		il.add(new VarInsnNode(Opcodes.ALOAD, 0));
    		il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "org/vivecraft/api/NetworkHelper", "overridePose", "(Lnet/minecraft/server/level/ServerPlayer;)V", false));
    		input.instructions.insert(node, il);
    	}

    	return input;
    }

    @Nonnull
    public TransformerVoteResult castVote(ITransformerVotingContext iTransformerVotingContext)
    {
        return TransformerVoteResult.YES;
    }
}
