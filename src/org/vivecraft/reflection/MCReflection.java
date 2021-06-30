package org.vivecraft.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.state.StateHolder;
import org.vivecraft.asm.ObfNames;

public class MCReflection
{
    public static final MCReflection.ReflectionField SoundHandler_sndManager = new MCReflection.ReflectionField(SoundManager.class, "field_147694_f");
    public static final MCReflection.ReflectionMethod SoundEngine_reload = new MCReflection.ReflectionMethod(SoundEngine.class, "func_148596_a");
    public static final String BlockState_OnBlockActivated = "func_225533_a_";
    public static final MCReflection.ReflectionField PlayerController_blockHitDelay = new MCReflection.ReflectionField(MultiPlayerGameMode.class, "field_78781_i");
    public static final MCReflection.ReflectionField PlayerController_isHittingBlock = new MCReflection.ReflectionField(MultiPlayerGameMode.class, "field_78778_j");
    public static final MCReflection.ReflectionField PlayerController_blocknoise = new MCReflection.ReflectionField(MultiPlayerGameMode.class, "field_78780_h");
    public static final MCReflection.ReflectionField KeyBinding_pressed = new MCReflection.ReflectionField(KeyMapping.class, "field_74513_e");
    public static final MCReflection.ReflectionField KeyBinding_pressTime = new MCReflection.ReflectionField(KeyMapping.class, "field_151474_i");
    public static final MCReflection.ReflectionMethod KeyBinding_unpressKey = new MCReflection.ReflectionMethod(KeyMapping.class, "func_74505_d");
    public static final MCReflection.ReflectionField KeyBinding_keyCode = new MCReflection.ReflectionField(KeyMapping.class, "field_74512_d");
    public static final MCReflection.ReflectionField KeyBinding_CATEGORY_ORDER = new MCReflection.ReflectionField(KeyMapping.class, "field_193627_d");
    public static final MCReflection.ReflectionField Entity_eyeHeight = new MCReflection.ReflectionField(Entity.class, "field_213326_aJ");
    public static final MCReflection.ReflectionMethod RenderPlayer_setModelVisibilities = new MCReflection.ReflectionMethod(PlayerRenderer.class, "func_177137_d", AbstractClientPlayer.class);
    public static final MCReflection.ReflectionField CCustomPayloadPacket_channel = new MCReflection.ReflectionField(ServerboundCustomPayloadPacket.class, "field_149562_a");
    public static final MCReflection.ReflectionField CCustomPayloadPacket_data = new MCReflection.ReflectionField(ServerboundCustomPayloadPacket.class, "field_149561_c");
    public static final MCReflection.ReflectionField StateHolder_mapCodec = new MCReflection.ReflectionField(StateHolder.class, "field_235893_d_");
    public static final MCReflection.ReflectionField ClientWorldInfo_isFlat = new MCReflection.ReflectionField(ClientLevel.ClientLevelData.class, "field_239146_c_");
    public static final MCReflection.ReflectionField BiomeManager_seed = new MCReflection.ReflectionField(BiomeManager.class, "field_226833_b_");
    public static final MCReflection.ReflectionField NetworkManager_channel = new MCReflection.ReflectionField(Connection.class, "field_150746_k");

    public static class ReflectionConstructor
    {
        private final Class<?> clazz;
        private final Class<?>[] params;
        private Constructor constructor;

        public ReflectionConstructor(Class<?> clazz, Class<?>... params)
        {
            this.clazz = clazz;
            this.params = params;
            this.reflect();
        }

        public Object newInstance(Object... args)
        {
            try
            {
                return this.constructor.newInstance(args);
            }
            catch (ReflectiveOperationException reflectiveoperationexception)
            {
                throw new RuntimeException(reflectiveoperationexception);
            }
        }

        private void reflect()
        {
            try
            {
                this.constructor = this.clazz.getDeclaredConstructor(this.params);
            }
            catch (NoSuchMethodException nosuchmethodexception)
            {
                StringBuilder stringbuilder = new StringBuilder();

                if (this.params.length > 0)
                {
                    stringbuilder.append(" with params ");
                    stringbuilder.append(Arrays.stream(this.params).map(Class::getName).collect(Collectors.joining(",")));
                }

                throw new RuntimeException("reflecting constructor " + stringbuilder.toString() + " in " + this.clazz.toString(), nosuchmethodexception);
            }

            this.constructor.setAccessible(true);
        }
    }

    public static class ReflectionField
    {
        private final Class<?> clazz;
        private final String srgName;
        private Field field;

        public ReflectionField(Class<?> clazz, String srgName)
        {
            this.clazz = clazz;
            this.srgName = srgName;
            this.reflect();
        }

        public Object get(Object obj)
        {
            try
            {
                return this.field.get(obj);
            }
            catch (ReflectiveOperationException reflectiveoperationexception)
            {
                throw new RuntimeException(reflectiveoperationexception);
            }
        }

        public void set(Object obj, Object value)
        {
            try
            {
                this.field.set(obj, value);
            }
            catch (ReflectiveOperationException reflectiveoperationexception)
            {
                throw new RuntimeException(reflectiveoperationexception);
            }
        }

        private void reflect()
        {
            try
            {
                this.field = this.clazz.getDeclaredField(this.srgName);
            }
            catch (NoSuchFieldException nosuchfieldexception2)
            {
                try
                {
                    this.field = this.clazz.getDeclaredField(ObfNames.resolveField(this.srgName, true));
                }
                catch (NoSuchFieldException nosuchfieldexception1)
                {
                    try
                    {
                        this.field = this.clazz.getDeclaredField(ObfNames.getDevMapping(this.srgName));
                    }
                    catch (NoSuchFieldException nosuchfieldexception)
                    {
                        StringBuilder stringbuilder = new StringBuilder(this.srgName);

                        if (!this.srgName.equals(ObfNames.resolveField(this.srgName, true)))
                        {
                            stringbuilder.append(',').append(ObfNames.resolveField(this.srgName, true));
                        }

                        if (!this.srgName.equals(ObfNames.getDevMapping(this.srgName)))
                        {
                            stringbuilder.append(',').append(ObfNames.getDevMapping(this.srgName));
                        }

                        throw new RuntimeException("reflecting field " + stringbuilder.toString() + " in " + this.clazz.toString(), nosuchfieldexception2);
                    }
                }
            }

            this.field.setAccessible(true);
        }
    }

    public static class ReflectionMethod
    {
        private final Class<?> clazz;
        private final String srgName;
        private final Class<?>[] params;
        private Method method;

        public ReflectionMethod(Class<?> clazz, String srgName, Class<?>... params)
        {
            this.clazz = clazz;
            this.srgName = srgName;
            this.params = params;
            this.reflect();
        }

        public Method getMethod()
        {
            return this.method;
        }

        public Object invoke(Object obj, Object... args)
        {
            try
            {
                return this.method.invoke(obj, args);
            }
            catch (ReflectiveOperationException reflectiveoperationexception)
            {
                throw new RuntimeException(reflectiveoperationexception);
            }
        }

        private void reflect()
        {
            try
            {
                this.method = this.clazz.getDeclaredMethod(this.srgName, this.params);
            }
            catch (NoSuchMethodException nosuchmethodexception2)
            {
                try
                {
                    this.method = this.clazz.getDeclaredMethod(ObfNames.resolveMethod(this.srgName, true), this.params);
                }
                catch (NoSuchMethodException nosuchmethodexception1)
                {
                    try
                    {
                        this.method = this.clazz.getDeclaredMethod(ObfNames.getDevMapping(this.srgName), this.params);
                    }
                    catch (NoSuchMethodException nosuchmethodexception)
                    {
                        StringBuilder stringbuilder = new StringBuilder(this.srgName);

                        if (!this.srgName.equals(ObfNames.resolveMethod(this.srgName, true)))
                        {
                            stringbuilder.append(',').append(ObfNames.resolveMethod(this.srgName, true));
                        }

                        if (!this.srgName.equals(ObfNames.getDevMapping(this.srgName)))
                        {
                            stringbuilder.append(',').append(ObfNames.getDevMapping(this.srgName));
                        }

                        if (this.params.length > 0)
                        {
                            stringbuilder.append(" with params ");
                            stringbuilder.append(Arrays.stream(this.params).map(Class::getName).collect(Collectors.joining(",")));
                        }

                        throw new RuntimeException("reflecting method " + stringbuilder.toString() + " in " + this.clazz.toString(), nosuchmethodexception2);
                    }
                }
            }

            this.method.setAccessible(true);
        }
    }
}
