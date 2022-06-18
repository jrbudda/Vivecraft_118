package org.vivecraft.tweaker;

//This is borrowed directly from optifine but has to live in this package cause classloaders.

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterators;
import java.util.stream.StreamSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class AccessFixer
{
    private static final Logger LOGGER = LogManager.getLogger();

    public static void fixMemberAccess(ClassNode classOld, ClassNode classNew)
    {
        List<FieldNode> list = classOld.fields;
        List<FieldNode> list1 = classNew.fields;
        Map<String, FieldNode> map = getMapFields(list);

        for (FieldNode fieldnode : list1)
        {
            String s = fieldnode.name;
            FieldNode fieldnode1 = map.get(s);

            if (fieldnode1 != null && fieldnode.access != fieldnode1.access)
            {
                fieldnode.access = combineAccess(fieldnode.access, fieldnode1.access);
            }
        }

        List<MethodNode> list2 = classOld.methods;
        List<MethodNode> list3 = classNew.methods;
        Map<String, MethodNode> map1 = getMapMethods(list2);
        Set<String> set = new HashSet<>();

        for (MethodNode methodnode : list3)
        {
            String s1 = methodnode.name + methodnode.desc;
            MethodNode methodnode1 = map1.get(s1);

            if (methodnode1 != null && methodnode.access != methodnode1.access)
            {
                int i = methodnode.access;
                methodnode.access = combineAccess(methodnode.access, methodnode1.access);

                if (isSet(i, 2) && !isSet(methodnode.access, 2) && !isSet(methodnode.access, 8) && !methodnode.name.equals("<init>"))
                {
                    set.add(methodnode.name + methodnode.desc);
                }
            }
        }

        if (!set.isEmpty())
        {
            List<MethodInsnNode> list4 = new ArrayList<>();
            List<MethodNode> list5 = classNew.methods;
            list5.forEach((mn) ->
            {
                StreamSupport.stream(Spliterators.spliteratorUnknownSize(mn.instructions.iterator(), 16), false).filter((i) -> {
                    return ((AbstractInsnNode) i).getOpcode() == 183;
                }).map(MethodInsnNode.class::cast).filter((m) -> {
                	MethodInsnNode min = (MethodInsnNode) m;
                    return set.contains(min.name + min.desc);
                }).forEach((m) -> {
                    ((MethodInsnNode) m).setOpcode(182);
                    list4.add((MethodInsnNode) m);
                });
            });
        }

        List<InnerClassNode> list5 = classOld.innerClasses;
        List<InnerClassNode> list6 = classNew.innerClasses;
        Map<String, InnerClassNode> map2 = getMapInnerClasses(list5);

        for (InnerClassNode innerclassnode1 : list6)
        {
            String s2 = innerclassnode1.name;
            InnerClassNode innerclassnode = map2.get(s2);

            if (innerclassnode != null && innerclassnode1.access != innerclassnode.access)
            {
                int j = combineAccess(innerclassnode1.access, innerclassnode.access);
                innerclassnode1.access = j;
            }
        }

        if (classNew.access != classOld.access)
        {
            int k = combineAccess(classNew.access, classOld.access);
            classNew.access = k;
        }
    }

    private static int combineAccess(int access, int access2)
    {
        if (access == access2)
        {
            return access;
        }
        else
        {
            int i = 7;
            int j = access & ~i;

            if (!isSet(access, 16) || !isSet(access2, 16))
            {
                j &= -17;
            }

            if (!isSet(access, 1) && !isSet(access2, 1))
            {
                if (!isSet(access, 4) && !isSet(access2, 4))
                {
                    if (isSet(access, i) && isSet(access2, i))
                    {
                        return !isSet(access, 2) && !isSet(access2, 2) ? j : j | 2;
                    }
                    else
                    {
                        return j;
                    }
                }
                else
                {
                    return j | 4;
                }
            }
            else
            {
                return j | 1;
            }
        }
    }

    private static boolean isSet(int access, int flag)
    {
        return (access & flag) != 0;
    }

    public static Map<String, FieldNode> getMapFields(List<FieldNode> fields)
    {
        Map<String, FieldNode> map = new LinkedHashMap<>();

        for (FieldNode fieldnode : fields)
        {
            String s = fieldnode.name;
            map.put(s, fieldnode);
        }

        return map;
    }

    public static Map<String, MethodNode> getMapMethods(List<MethodNode> methods)
    {
        Map<String, MethodNode> map = new LinkedHashMap<>();

        for (MethodNode methodnode : methods)
        {
            String s = methodnode.name + methodnode.desc;
            map.put(s, methodnode);
        }

        return map;
    }

    public static Map<String, InnerClassNode> getMapInnerClasses(List<InnerClassNode> innerClasses)
    {
        Map<String, InnerClassNode> map = new LinkedHashMap<>();

        for (InnerClassNode innerclassnode : innerClasses)
        {
            String s = innerclassnode.name;
            map.put(s, innerclassnode);
        }

        return map;
    }

    private static String toString(int access)
    {
        StringBuffer stringbuffer = new StringBuffer();

        if (isSet(access, 1))
        {
            addToBuffer(stringbuffer, "public", " ");
        }

        if (isSet(access, 4))
        {
            addToBuffer(stringbuffer, "protected", " ");
        }

        if (isSet(access, 2))
        {
            addToBuffer(stringbuffer, "private", " ");
        }

        if (isSet(access, 16))
        {
            addToBuffer(stringbuffer, "final", " ");
        }

        return stringbuffer.toString();
    }

    private static void addToBuffer(StringBuffer sb, String val, String separator)
    {
        if (sb.length() > 0)
        {
            sb.append(separator);
        }

        sb.append(val);
    }
}
