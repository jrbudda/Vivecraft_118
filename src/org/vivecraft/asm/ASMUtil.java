package org.vivecraft.asm;

import java.util.Iterator;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class ASMUtil
{
    private ASMUtil()
    {
    }

    public static MethodNode findMethod(ClassNode node, MethodTuple tuple)
    {
        for (MethodNode methodnode : node.methods)
        {
            if (methodnode.name.equals(tuple.methodName) && methodnode.desc.equals(tuple.methodDesc))
            {
                return methodnode;
            }
        }

        return null;
    }

    public static boolean deleteMethod(ClassNode node, MethodTuple tuple)
    {
        Iterator<MethodNode> iterator = node.methods.iterator();

        while (iterator.hasNext())
        {
            MethodNode methodnode = iterator.next();

            if (methodnode.name.equals(tuple.methodName) && methodnode.desc.equals(tuple.methodDesc))
            {
                iterator.remove();
                return true;
            }
        }

        return false;
    }

    public static void deleteInstructions(MethodNode node, int index, int count)
    {
        for (int i = 0; i < count; ++i)
        {
            node.instructions.remove(node.instructions.get(index));
        }
    }

    public static void insertInstructionsRelative(MethodNode node, AbstractInsnNode insn, int offset, InsnList list)
    {
        node.instructions.insert(node.instructions.get(node.instructions.indexOf(insn) + offset), list);
    }

    public static AbstractInsnNode findFirstOpcode(MethodNode node, int opcode)
    {
        return findNthOpcode(node, 0, opcode);
    }

    public static AbstractInsnNode findLastOpcode(MethodNode node, int opcode)
    {
        AbstractInsnNode abstractinsnnode = null;

        for (AbstractInsnNode abstractinsnnode1 : node.instructions)
        {
            if (abstractinsnnode1.getOpcode() == opcode)
            {
                abstractinsnnode = abstractinsnnode1;
            }
        }

        return abstractinsnnode;
    }

    public static AbstractInsnNode findNthOpcode(MethodNode node, int n, int opcode)
    {
        int i = 0;

        for (AbstractInsnNode abstractinsnnode : node.instructions)
        {
            if (abstractinsnnode.getOpcode() == opcode && i++ == n)
            {
                return abstractinsnnode;
            }
        }

        return null;
    }

    public static AbstractInsnNode findFirstInstruction(MethodNode node, int opcode, Object... args)
    {
        return findNthInstruction(node, 0, opcode, args);
    }

    public static AbstractInsnNode findLastInstruction(MethodNode node, int opcode, Object... args)
    {
        AbstractInsnNode abstractinsnnode = null;

        for (AbstractInsnNode abstractinsnnode1 : node.instructions)
        {
            if (matchInstruction(abstractinsnnode1, opcode, args))
            {
                abstractinsnnode = abstractinsnnode1;
            }
        }

        return abstractinsnnode;
    }

    public static AbstractInsnNode findNthInstruction(MethodNode node, int n, int opcode, Object... args)
    {
        int i = 0;

        for (AbstractInsnNode abstractinsnnode : node.instructions)
        {
            if (matchInstruction(abstractinsnnode, opcode, args) && i++ == n)
            {
                return abstractinsnnode;
            }
        }

        return null;
    }

    public static AbstractInsnNode findFirstInstructionPattern(MethodNode node, Object[]... instructions)
    {
        return findNthInstructionPattern(node, 0, instructions);
    }

    public static boolean addMethod(ClassNode node, MethodTuple tuple, MethodNode method)
    {
        Iterator<MethodNode> iterator = node.methods.iterator();

        while (iterator.hasNext())
        {
            if (method.name.equals(tuple.methodName) && method.desc.equals(tuple.methodDesc))
            {
                return false;
            }
        }

        node.methods.add(node);
        return true;
    }

    public static AbstractInsnNode findLastInstructionPattern(MethodNode node, int n, Object[]... instructions)
    {
        AbstractInsnNode abstractinsnnode = null;
        int i = 0;

        for (AbstractInsnNode abstractinsnnode1 : node.instructions)
        {
            Object[] aobject = new Object[instructions[i].length - 1];
            System.arraycopy(instructions[i], 1, aobject, 0, aobject.length);

            if (matchInstruction(abstractinsnnode1, instructions[i][0], aobject))
            {
                ++i;

                if (i == instructions.length)
                {
                    abstractinsnnode = abstractinsnnode1;
                    i = 0;
                }
            }
            else
            {
                i = 0;
            }
        }

        return abstractinsnnode;
    }

    public static AbstractInsnNode findNthInstructionPattern(MethodNode node, int n, Object[]... instructions)
    {
        int i = 0;
        int j = 0;

        for (AbstractInsnNode abstractinsnnode : node.instructions)
        {
            Object[] aobject = new Object[instructions[j].length - 1];
            System.arraycopy(instructions[j], 1, aobject, 0, aobject.length);

            if (matchInstruction(abstractinsnnode, instructions[j][0], aobject))
            {
                ++j;

                if (j == instructions.length)
                {
                    if (i++ == n)
                    {
                        return abstractinsnnode;
                    }

                    j = 0;
                }
            }
            else
            {
                j = 0;
            }
        }

        return null;
    }

    public static boolean matchInstruction(AbstractInsnNode ain, AbstractInsnNode ain2)
    {
        if (ain.getOpcode() != ain2.getOpcode())
        {
            return false;
        }
        else if (ain instanceof InsnNode)
        {
            return true;
        }
        else if (ain instanceof VarInsnNode)
        {
            return ((VarInsnNode)ain).var == ((VarInsnNode)ain2).var;
        }
        else if (ain instanceof LdcInsnNode)
        {
            return ((LdcInsnNode)ain).cst.equals(((LdcInsnNode)ain2).cst);
        }
        else if (ain instanceof IntInsnNode)
        {
            return ((IntInsnNode)ain).operand == ((IntInsnNode)ain2).operand;
        }
        else if (ain instanceof TypeInsnNode)
        {
            return ((TypeInsnNode)ain).desc.equals(((TypeInsnNode)ain2).desc);
        }
        else if (ain instanceof FieldInsnNode)
        {
            FieldInsnNode fieldinsnnode = (FieldInsnNode)ain;
            FieldInsnNode fieldinsnnode1 = (FieldInsnNode)ain2;
            return fieldinsnnode.owner.equals(fieldinsnnode1.owner) && fieldinsnnode.name.equals(fieldinsnnode1.name) && fieldinsnnode.desc.equals(fieldinsnnode1.desc);
        }
        else if (ain instanceof MethodInsnNode)
        {
            MethodInsnNode methodinsnnode = (MethodInsnNode)ain;
            MethodInsnNode methodinsnnode1 = (MethodInsnNode)ain2;
            return methodinsnnode.owner.equals(methodinsnnode1.owner) && methodinsnnode.name.equals(methodinsnnode1.name) && methodinsnnode.desc.equals(methodinsnnode1.desc) && methodinsnnode.itf == methodinsnnode1.itf;
        }
        else if (ain instanceof JumpInsnNode)
        {
            return ((JumpInsnNode)ain).label == ((JumpInsnNode)ain2).label;
        }
        else if (!(ain instanceof IincInsnNode))
        {
            return false;
        }
        else
        {
            IincInsnNode iincinsnnode = (IincInsnNode)ain;
            IincInsnNode iincinsnnode1 = (IincInsnNode)ain2;
            return iincinsnnode.var == iincinsnnode1.var && iincinsnnode.incr == iincinsnnode1.incr;
        }
    }

    public static boolean matchInstruction(AbstractInsnNode ain, int opcode, Object... args)
    {
        if (ain.getOpcode() != opcode)
        {
            return false;
        }
        else if (ain instanceof InsnNode)
        {
            return true;
        }
        else if (ain instanceof VarInsnNode)
        {
            return args[0] instanceof Integer && ((VarInsnNode)ain).var == args[0];
        }
        else if (ain instanceof LdcInsnNode)
        {
            return args[0].equals(((LdcInsnNode)ain).cst);
        }
        else if (ain instanceof IntInsnNode)
        {
            return args[0] instanceof Integer && ((IntInsnNode)ain).operand == args[0];
        }
        else if (ain instanceof TypeInsnNode)
        {
            return args[0] instanceof String && ((TypeInsnNode)ain).desc.equals(args[0]);
        }
        else if (ain instanceof FieldInsnNode)
        {
            if (args.length == 3 && args[0] instanceof String && args[1] instanceof String && args[2] instanceof String)
            {
                FieldInsnNode fieldinsnnode = (FieldInsnNode)ain;
                return fieldinsnnode.owner.equals(args[0]) && fieldinsnnode.name.equals(args[1]) && fieldinsnnode.desc.equals(args[2]);
            }
            else
            {
                return false;
            }
        }
        else if (ain instanceof MethodInsnNode)
        {
            if (args.length == 4 && args[0] instanceof String && args[1] instanceof String && args[2] instanceof String && args[3] instanceof Boolean)
            {
                MethodInsnNode methodinsnnode = (MethodInsnNode)ain;
                return methodinsnnode.owner.equals(args[0]) && methodinsnnode.name.equals(args[1]) && methodinsnnode.desc.equals(args[2]) && methodinsnnode.itf == args[3];
            }
            else
            {
                return false;
            }
        }
        else if (ain instanceof JumpInsnNode)
        {
            return args[0] instanceof LabelNode && ((JumpInsnNode)ain).label == args[0];
        }
        else if (!(ain instanceof IincInsnNode))
        {
            return false;
        }
        else if (args.length == 2 && args[0] instanceof Integer && args[1] instanceof Integer)
        {
            IincInsnNode iincinsnnode = (IincInsnNode)ain;
            return iincinsnnode.var == args[0] && iincinsnnode.incr == args[1];
        }
        else
        {
            return false;
        }
    }
}
