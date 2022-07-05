package org.vivecraft.tweaker.asm;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import org.vivecraft.tweaker.asm.handler.*;

public class VivecraftASMTransformer implements ITransformer<ClassNode>
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final List<Class<?>> asmHandlers = new ArrayList<>();

    public VivecraftASMTransformer()
    {
        // TODO: these haven't been tested yet, but they *should* work
        asmHandlers.add(ASMHandlerContainerScreen.class);
        asmHandlers.add(ASMHandlerCreativeScreen.class);
        asmHandlers.add(ASMHandlerFluidState.class);
        asmHandlers.add(ASMHandlerItem.class);

        //asmHandlers.add(ASMHandlerMinecraft.class); // not needed for VR
        asmHandlers.add(ASMHandlerRecipes.class);
        asmHandlers.add(ASMHandlerScreen.class);
        asmHandlers.add(ASMHandlerServerPlayer.class);
    }

    public ClassNode transform(ClassNode input, ITransformerVotingContext context)
    {
        BiConsumer<Method, Object> invoke = (method, param) -> {
            try {
                method.invoke(null, param);
            } catch (Exception e) {
                LOGGER.error("Patching failed", e);
            }
        };

        asmHandlers.stream().flatMap(h -> Arrays.stream(h.getMethods())).forEach(method -> {
            Optional.ofNullable(method.getAnnotation(ASMClass.class)).ifPresent(ann -> {
                if (input.name.equals(ann.className())) {
                    LOGGER.info("Applying patch " + method.getDeclaringClass().getSimpleName() + "." + method.getName() + " to class " + input.name);
                    invoke.accept(method, input);
                }
            });
            Optional.ofNullable(method.getAnnotation(ASMField.class)).ifPresent(ann -> {
                for (FieldNode node : input.fields) {
                    if (input.name.equals(ann.className()) && node.name.equals(ann.fieldName())) {
                        LOGGER.info("Applying patch " + method.getDeclaringClass().getSimpleName() + "." + method.getName() + " to field " + input.name + " " + node.name);
                        invoke.accept(method, node);
                    }
                }
            });
            Optional.ofNullable(method.getAnnotation(ASMMethod.class)).ifPresent(ann -> {
                for (MethodNode node : input.methods) {
                    if (input.name.equals(ann.className()) && node.name.equals(ann.methodName()) && node.desc.equals(ann.methodDesc())) {
                        LOGGER.info("Applying patch " + method.getDeclaringClass().getSimpleName() + "." + method.getName() + " to method " + input.name + " " + node.name + node.desc);
                        invoke.accept(method, node);
                    }
                }
            });
        });

        return input;
    }

    @Nonnull
    public TransformerVoteResult castVote(ITransformerVotingContext iTransformerVotingContext)
    {
        return TransformerVoteResult.YES;
    }

    @Nonnull
    public Set<Target> targets()
    {
        HashSet<String> set = new HashSet<>();

        asmHandlers.stream().flatMap(h -> Arrays.stream(h.getMethods())).forEach(method -> {
            Optional.ofNullable(method.getAnnotation(ASMClass.class)).ifPresent(ann -> set.add(ann.className()));
            Optional.ofNullable(method.getAnnotation(ASMField.class)).ifPresent(ann -> set.add(ann.className()));
            Optional.ofNullable(method.getAnnotation(ASMMethod.class)).ifPresent(ann -> set.add(ann.className()));
        });

        return set.stream().map(Target::targetClass).collect(Collectors.toSet());
    }
}
