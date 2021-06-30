package org.vivecraft.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.vivecraft.gameplay.screenhandlers.GuiHandler;
import org.vivecraft.gameplay.screenhandlers.KeyboardHandler;
import org.vivecraft.provider.ControllerType;
import org.vivecraft.provider.InputSimulator;
import org.vivecraft.provider.MCVR;
import org.vivecraft.utils.Utils;
import org.vivecraft.utils.lwjgl.Matrix4f;
import org.vivecraft.utils.lwjgl.Vector3f;

public class PhysicalKeyboard
{
    private final Minecraft mc = Minecraft.getInstance();
    private boolean reinit;
    private boolean shift;
    private boolean shiftSticky;
    private List<PhysicalKeyboard.KeyButton> keys;
    private static final int ROWS = 4;
    private static final int COLUMNS = 13;
    private static final float SPACING = 0.0064F;
    private static final float KEY_WIDTH = 0.04F;
    private static final float KEY_HEIGHT = 0.04F;
    private static final float KEY_WIDTH_SPECIAL = 0.086399995F;
    private int rows;
    private int columns;
    private float spacing;
    private float keyWidth;
    private float keyHeight;
    private float keyWidthSpecial;
    private float scale = 1.0F;
    private PhysicalKeyboard.KeyButton[] pressedKey = new PhysicalKeyboard.KeyButton[2];
    private long[] pressTime = new long[2];
    private long[] pressRepeatTime = new long[2];
    private long shiftPressTime;
    private boolean lastPressedShift;
    private String easterEggText = new String(new byte[] {114, 111, 121, 97, 108, 32, 114, 97, 105, 110, 98, 111, 119}, StandardCharsets.UTF_8);
    private int easterEggIndex = 0;
    private boolean easterEggActive;

    public PhysicalKeyboard()
    {
        this.keys = new ArrayList<>();
    }

    public void init()
    {
        this.keys.clear();
        this.rows = 4;
        this.columns = 13;
        this.spacing = 0.0064F * this.scale;
        this.keyWidth = 0.04F * this.scale;
        this.keyHeight = 0.04F * this.scale;
        this.keyWidthSpecial = 0.086399995F * this.scale;
        String s = this.mc.vrSettings.keyboardKeys;

        if (this.shift)
        {
            s = this.mc.vrSettings.keyboardKeysShift;
        }

        float f = (float)s.length() / (float)this.columns;

        if (Math.abs((float)this.rows - f) > 0.01F)
        {
            this.rows = Mth.ceil(f);
        }

        for (int i = 0; i < this.rows; ++i)
        {
            for (int j = 0; j < this.columns; ++j)
            {
                int k = i * this.columns + j;
                char c0 = ' ';

                if (k < s.length())
                {
                    c0 = s.charAt(k);
                }

                final char c1 = c0;
                this.addKey(new PhysicalKeyboard.KeyButton(k, String.valueOf(c0), this.keyWidthSpecial + this.spacing + (float)j * (this.keyWidth + this.spacing), (float)i * (this.keyHeight + this.spacing), this.keyWidth, this.keyHeight)
                {
                    public void onPressed()
                    {
                        InputSimulator.typeChar(c1);

                        if (!PhysicalKeyboard.this.shiftSticky)
                        {
                            PhysicalKeyboard.this.setShift(false, false);
                        }

                        if (c1 == '/' && PhysicalKeyboard.this.mc.screen == null)
                        {
                            InputSimulator.pressKey(47);
                            InputSimulator.releaseKey(47);
                        }
                    }
                });
            }
        }

        for (int l = 0; l < 2; ++l)
        {
            PhysicalKeyboard.KeyButton physicalkeyboard$keybutton = this.addKey(new PhysicalKeyboard.KeyButton(1000 + l, "Shift", l == 1 ? this.keyWidthSpecial + this.spacing + (float)this.columns * (this.keyWidth + this.spacing) : 0.0F, 3.0F * (this.keyHeight + this.spacing), this.keyWidthSpecial, this.keyHeight)
            {
                public void onPressed()
                {
                    if (PhysicalKeyboard.this.shift && !PhysicalKeyboard.this.shiftSticky && Utils.milliTime() - PhysicalKeyboard.this.shiftPressTime < 400L)
                    {
                        PhysicalKeyboard.this.setShift(true, true);
                    }
                    else
                    {
                        PhysicalKeyboard.this.setShift(!PhysicalKeyboard.this.shift, false);
                    }

                    PhysicalKeyboard.this.shiftPressTime = Utils.milliTime();
                }
            });

            if (this.shift)
            {
                if (!this.shiftSticky)
                {
                    physicalkeyboard$keybutton.color.r = 0.0F;
                }

                physicalkeyboard$keybutton.color.b = 0.0F;
            }
        }

        this.addKey(new PhysicalKeyboard.KeyButton(1002, " ", this.keyWidthSpecial + this.spacing + (float)(this.columns - 5) / 2.0F * (this.keyWidth + this.spacing), (float)this.rows * (this.keyHeight + this.spacing), 5.0F * (this.keyWidth + this.spacing) - this.spacing, this.keyHeight)
        {
            public void onPressed()
            {
                InputSimulator.typeChar(' ');
            }
        });
        this.addKey(new PhysicalKeyboard.KeyButton(1003, "Tab", 0.0F, this.keyHeight + this.spacing, this.keyWidthSpecial, this.keyHeight)
        {
            public void onPressed()
            {
                InputSimulator.pressKey(258);
                InputSimulator.releaseKey(258);
            }
        });
        this.addKey(new PhysicalKeyboard.KeyButton(1004, "Esc", 0.0F, 0.0F, this.keyWidthSpecial, this.keyHeight)
        {
            public void onPressed()
            {
                InputSimulator.pressKey(256);
                InputSimulator.releaseKey(256);
            }
        });
        this.addKey(new PhysicalKeyboard.KeyButton(1005, "Bksp", this.keyWidthSpecial + this.spacing + (float)this.columns * (this.keyWidth + this.spacing), 0.0F, this.keyWidthSpecial, this.keyHeight)
        {
            public void onPressed()
            {
                InputSimulator.pressKey(259);
                InputSimulator.releaseKey(259);
            }
        });
        this.addKey(new PhysicalKeyboard.KeyButton(1006, "Enter", this.keyWidthSpecial + this.spacing + (float)this.columns * (this.keyWidth + this.spacing), 2.0F * (this.keyHeight + this.spacing), this.keyWidthSpecial, this.keyHeight)
        {
            public void onPressed()
            {
                InputSimulator.pressKey(257);
                InputSimulator.releaseKey(257);
            }
        });
        this.addKey(new PhysicalKeyboard.KeyButton(1007, "\u2191", this.keyWidthSpecial + this.spacing + (float)(this.columns + 1) * (this.keyWidth + this.spacing), 4.0F * (this.keyHeight + this.spacing), this.keyWidth, this.keyHeight)
        {
            public void onPressed()
            {
                InputSimulator.pressKey(265);
                InputSimulator.releaseKey(265);
            }
        });
        this.addKey(new PhysicalKeyboard.KeyButton(1008, "\u2193", this.keyWidthSpecial + this.spacing + (float)(this.columns + 1) * (this.keyWidth + this.spacing), 5.0F * (this.keyHeight + this.spacing), this.keyWidth, this.keyHeight)
        {
            public void onPressed()
            {
                InputSimulator.pressKey(264);
                InputSimulator.releaseKey(264);
            }
        });
        this.addKey(new PhysicalKeyboard.KeyButton(1009, "\u2190", this.keyWidthSpecial + this.spacing + (float)this.columns * (this.keyWidth + this.spacing), 5.0F * (this.keyHeight + this.spacing), this.keyWidth, this.keyHeight)
        {
            public void onPressed()
            {
                InputSimulator.pressKey(263);
                InputSimulator.releaseKey(263);
            }
        });
        this.addKey(new PhysicalKeyboard.KeyButton(1010, "\u2192", this.keyWidthSpecial + this.spacing + (float)(this.columns + 2) * (this.keyWidth + this.spacing), 5.0F * (this.keyHeight + this.spacing), this.keyWidth, this.keyHeight)
        {
            public void onPressed()
            {
                InputSimulator.pressKey(262);
                InputSimulator.releaseKey(262);
            }
        });
        this.addKey(new PhysicalKeyboard.KeyButton(1011, "Cut", 1.0F * (this.keyWidthSpecial + this.spacing), -1.0F * (this.keyHeight + this.spacing), this.keyWidthSpecial, this.keyHeight)
        {
            public void onPressed()
            {
                InputSimulator.pressKey(341);
                InputSimulator.pressKey(88);
                InputSimulator.releaseKey(88);
                InputSimulator.releaseKey(341);
            }
        });
        this.addKey(new PhysicalKeyboard.KeyButton(1012, "Copy", 2.0F * (this.keyWidthSpecial + this.spacing), -1.0F * (this.keyHeight + this.spacing), this.keyWidthSpecial, this.keyHeight)
        {
            public void onPressed()
            {
                InputSimulator.pressKey(341);
                InputSimulator.pressKey(67);
                InputSimulator.releaseKey(67);
                InputSimulator.releaseKey(341);
            }
        });
        this.addKey(new PhysicalKeyboard.KeyButton(1013, "Paste", 3.0F * (this.keyWidthSpecial + this.spacing), -1.0F * (this.keyHeight + this.spacing), this.keyWidthSpecial, this.keyHeight)
        {
            public void onPressed()
            {
                InputSimulator.pressKey(341);
                InputSimulator.pressKey(86);
                InputSimulator.releaseKey(86);
                InputSimulator.releaseKey(341);
            }
        });

        for (int i1 = 0; i1 < 2; ++i1)
        {
            if (this.pressedKey[i1] != null)
            {
                for (PhysicalKeyboard.KeyButton physicalkeyboard$keybutton1 : this.keys)
                {
                    if (physicalkeyboard$keybutton1.id == this.pressedKey[i1].id)
                    {
                        this.pressedKey[i1] = physicalkeyboard$keybutton1;
                        physicalkeyboard$keybutton1.pressed = true;
                        break;
                    }
                }
            }
        }

        this.reinit = false;
    }

    public void process()
    {
        if (this.reinit)
        {
            this.init();
        }

        for (int i = 0; i < 2; ++i)
        {
            ControllerType controllertype = ControllerType.values()[i];
            PhysicalKeyboard.KeyButton physicalkeyboard$keybutton = this.findTouchedKey(controllertype);

            if (physicalkeyboard$keybutton != null)
            {
                if (physicalkeyboard$keybutton != this.pressedKey[i] && Utils.milliTime() - this.pressTime[i] >= 150L)
                {
                    if (this.pressedKey[i] != null)
                    {
                        this.pressedKey[i].unpress(controllertype);
                        this.pressedKey[i] = null;
                    }

                    physicalkeyboard$keybutton.press(controllertype, false);
                    this.pressedKey[i] = physicalkeyboard$keybutton;
                    this.pressTime[i] = Utils.milliTime();
                    this.pressRepeatTime[i] = Utils.milliTime();
                }
                else if (physicalkeyboard$keybutton == this.pressedKey[i] && Utils.milliTime() - this.pressTime[i] >= 500L && Utils.milliTime() - this.pressRepeatTime[i] >= 100L)
                {
                    physicalkeyboard$keybutton.press(controllertype, true);
                    this.pressRepeatTime[i] = Utils.milliTime();
                }
            }
            else if (this.pressedKey[i] != null)
            {
                this.pressedKey[i].unpress(controllertype);
                this.pressedKey[i] = null;
                this.pressTime[i] = Utils.milliTime();
            }
        }
    }

    public void processBindings()
    {
        if (GuiHandler.keyKeyboardShift.consumeClick())
        {
            this.setShift(true, true);
            this.lastPressedShift = true;
        }

        if (!GuiHandler.keyKeyboardShift.isDown() && this.lastPressedShift)
        {
            this.setShift(false, false);
            this.lastPressedShift = false;
        }
    }

    private Vector3f getCenterPos()
    {
        return new Vector3f(((this.keyWidth + this.spacing) * ((float)this.columns + (float)this.columns % 2.0F / 2.0F) + (this.keyWidthSpecial + this.spacing) * 2.0F) / 2.0F, (this.keyHeight + this.spacing) * (float)(this.rows + 1), 0.0F);
    }

    private PhysicalKeyboard.KeyButton findTouchedKey(ControllerType controller)
    {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.translate(this.getCenterPos());
        Matrix4f.mul(matrix4f, (Matrix4f)Utils.convertOVRMatrix(KeyboardHandler.Rotation_room).invert(), matrix4f);
        matrix4f.translate((Vector3f)Utils.convertToVector3f(KeyboardHandler.Pos_room).negate());
        Vec3 vec3 = Utils.convertToVector3d(Utils.transformVector(matrix4f, Utils.convertToVector3f(this.mc.vrPlayer.vrdata_room_pre.getController(controller.ordinal()).getPosition()), true));

        for (PhysicalKeyboard.KeyButton physicalkeyboard$keybutton : this.keys)
        {
            if (physicalkeyboard$keybutton.getCollisionBoundingBox().contains(vec3))
            {
                return physicalkeyboard$keybutton;
            }
        }

        return null;
    }

    private void updateEasterEgg(String label)
    {
        if (this.easterEggIndex < this.easterEggText.length())
        {
            if (label.toLowerCase().equals(String.valueOf(this.easterEggText.charAt(this.easterEggIndex))))
            {
                ++this.easterEggIndex;
            }
            else
            {
                this.easterEggIndex = 0;
            }
        }
        else if (label.equals("Enter"))
        {
            this.easterEggActive = !this.easterEggActive;
        }
        else
        {
            this.easterEggIndex = 0;
        }
    }

    private void drawBox(BufferBuilder buf, AABB box, GlStateManager.Color color)
    {
        buf.vertex(box.minX, box.minY, box.minZ).color(color.r, color.g, color.b, color.a).normal(0.0F, 0.0F, -1.0F).endVertex();
        buf.vertex(box.minX, box.maxY, box.minZ).color(color.r, color.g, color.b, color.a).normal(0.0F, 0.0F, -1.0F).endVertex();
        buf.vertex(box.maxX, box.maxY, box.minZ).color(color.r, color.g, color.b, color.a).normal(0.0F, 0.0F, -1.0F).endVertex();
        buf.vertex(box.maxX, box.minY, box.minZ).color(color.r, color.g, color.b, color.a).normal(0.0F, 0.0F, -1.0F).endVertex();
        buf.vertex(box.minX, box.minY, box.minZ).color(color.r, color.g, color.b, color.a).normal(0.0F, -1.0F, 0.0F).endVertex();
        buf.vertex(box.maxX, box.minY, box.minZ).color(color.r, color.g, color.b, color.a).normal(0.0F, -1.0F, 0.0F).endVertex();
        buf.vertex(box.maxX, box.minY, box.maxZ).color(color.r, color.g, color.b, color.a).normal(0.0F, -1.0F, 0.0F).endVertex();
        buf.vertex(box.minX, box.minY, box.maxZ).color(color.r, color.g, color.b, color.a).normal(0.0F, -1.0F, 0.0F).endVertex();
        buf.vertex(box.minX, box.minY, box.minZ).color(color.r, color.g, color.b, color.a).normal(-1.0F, 0.0F, 0.0F).endVertex();
        buf.vertex(box.minX, box.minY, box.maxZ).color(color.r, color.g, color.b, color.a).normal(-1.0F, 0.0F, 0.0F).endVertex();
        buf.vertex(box.minX, box.maxY, box.maxZ).color(color.r, color.g, color.b, color.a).normal(-1.0F, 0.0F, 0.0F).endVertex();
        buf.vertex(box.minX, box.maxY, box.minZ).color(color.r, color.g, color.b, color.a).normal(-1.0F, 0.0F, 0.0F).endVertex();
        buf.vertex(box.maxX, box.maxY, box.maxZ).color(color.r, color.g, color.b, color.a).normal(0.0F, 0.0F, 1.0F).endVertex();
        buf.vertex(box.minX, box.maxY, box.maxZ).color(color.r, color.g, color.b, color.a).normal(0.0F, 0.0F, 1.0F).endVertex();
        buf.vertex(box.minX, box.minY, box.maxZ).color(color.r, color.g, color.b, color.a).normal(0.0F, 0.0F, 1.0F).endVertex();
        buf.vertex(box.maxX, box.minY, box.maxZ).color(color.r, color.g, color.b, color.a).normal(0.0F, 0.0F, 1.0F).endVertex();
        buf.vertex(box.maxX, box.maxY, box.maxZ).color(color.r, color.g, color.b, color.a).normal(0.0F, 1.0F, 0.0F).endVertex();
        buf.vertex(box.maxX, box.maxY, box.minZ).color(color.r, color.g, color.b, color.a).normal(0.0F, 1.0F, 0.0F).endVertex();
        buf.vertex(box.minX, box.maxY, box.minZ).color(color.r, color.g, color.b, color.a).normal(0.0F, 1.0F, 0.0F).endVertex();
        buf.vertex(box.minX, box.maxY, box.maxZ).color(color.r, color.g, color.b, color.a).normal(0.0F, 1.0F, 0.0F).endVertex();
        buf.vertex(box.maxX, box.maxY, box.maxZ).color(color.r, color.g, color.b, color.a).normal(1.0F, 0.0F, 0.0F).endVertex();
        buf.vertex(box.maxX, box.minY, box.maxZ).color(color.r, color.g, color.b, color.a).normal(1.0F, 0.0F, 0.0F).endVertex();
        buf.vertex(box.maxX, box.minY, box.minZ).color(color.r, color.g, color.b, color.a).normal(1.0F, 0.0F, 0.0F).endVertex();
        buf.vertex(box.maxX, box.maxY, box.minZ).color(color.r, color.g, color.b, color.a).normal(1.0F, 0.0F, 0.0F).endVertex();
    }

    public void render()
    {
        Vector3f vector3f = this.getCenterPos();
        GlStateManager._translatef(-vector3f.x, -vector3f.y, -vector3f.z);
        GlStateManager._disableTexture();
        GlStateManager._disableCull();
        GlStateManager._enableAlphaTest();
        GlStateManager._alphaFunc(516, 0.0F);
        GlStateManager._enableBlend();

        if (this.easterEggActive)
        {
            for (PhysicalKeyboard.KeyButton physicalkeyboard$keybutton : this.keys)
            {
                GlStateManager.Color glstatemanager$color = Utils.colorFromHSB(((float)this.mc.tickCounter + this.mc.getFrameTime()) / 100.0F + (float)(physicalkeyboard$keybutton.boundingBox.minX + (physicalkeyboard$keybutton.boundingBox.maxX - physicalkeyboard$keybutton.boundingBox.minX) / 2.0D) / 2.0F, 1.0F, 1.0F);
                physicalkeyboard$keybutton.color.r = glstatemanager$color.r;
                physicalkeyboard$keybutton.color.g = glstatemanager$color.g;
                physicalkeyboard$keybutton.color.b = glstatemanager$color.b;
            }
        }

        this.mc.getTextureManager().bind(new ResourceLocation("vivecraft:textures/white.png"));
        GlStateManager._depthFunc(519);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Font font = this.mc.getEntityRenderDispatcher().getFont();
        ArrayList<Tuple<String, Vector3f>> arraylist = new ArrayList<>();
        float f5 = 0.002F * this.scale;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(7, DefaultVertexFormat.POSITION_COLOR_NORMAL);

        for (PhysicalKeyboard.KeyButton physicalkeyboard$keybutton1 : this.keys)
        {
            AABB aabb = physicalkeyboard$keybutton1.getRenderBoundingBox();
            GlStateManager.Color glstatemanager$color1 = physicalkeyboard$keybutton1.getRenderColor();
            this.drawBox(bufferbuilder, aabb, glstatemanager$color1);
            float f = (float)font.width(physicalkeyboard$keybutton1.label) * f5;
            float f1 = 9.0F * f5;
            float f2 = (float)aabb.minX + ((float)aabb.maxX - (float)aabb.minX) / 2.0F - f / 2.0F;
            float f3 = (float)aabb.minY + ((float)aabb.maxY - (float)aabb.minY) / 2.0F - f1 / 2.0F;
            float f4 = (float)aabb.minZ + ((float)aabb.maxZ - (float)aabb.minZ) / 2.0F;
            arraylist.add(new Tuple<>(physicalkeyboard$keybutton1.label, new Vector3f(f2, f3, f4)));
        }

        tesselator.end();
        GlStateManager._depthFunc(515);
        GlStateManager._enableTexture();
        GlStateManager._disableLighting();
        MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(tesselator.getBuilder());
        PoseStack posestack = new PoseStack();

        for (Tuple<String, Vector3f> tuple : arraylist)
        {
            posestack.pushPose();
            posestack.translate((double)(tuple.getB()).x, (double)(tuple.getB()).y, (double)(tuple.getB()).z);
            posestack.scale(f5, f5, 1.0F);
            font.drawInBatch(tuple.getA(), 0.0F, 0.0F, -1, false, posestack.last().pose(), multibuffersource$buffersource, false, 0, 15728880, font.isBidirectional());
            posestack.popPose();
        }

        multibuffersource$buffersource.endBatch();
        GlStateManager._enableLighting();
        GlStateManager._enableBlend();
        GlStateManager._enableDepthTest();
        GlStateManager._enableTexture();
        GlStateManager._enableCull();
        RenderSystem.defaultBlendFunc();
    }

    public void show()
    {
        if (!this.shiftSticky)
        {
            this.shift = false;
        }

        this.scale = this.mc.vrSettings.physicalKeyboardScale;
        this.reinit = true;
    }

    private PhysicalKeyboard.KeyButton addKey(PhysicalKeyboard.KeyButton key)
    {
        this.keys.add(key);
        return key;
    }

    public boolean isShift()
    {
        return this.shift;
    }

    public boolean isShiftSticky()
    {
        return this.shiftSticky;
    }

    public void setShift(boolean shift, boolean sticky)
    {
        if (shift != this.shift || sticky != this.shiftSticky)
        {
            this.shift = shift;
            this.shiftSticky = shift && sticky;
            this.reinit = true;
        }
    }

    public float getScale()
    {
        return this.scale;
    }

    public void setScale(float scale)
    {
        this.scale = scale;
        this.reinit = true;
    }

    private abstract class KeyButton
    {
        public final int id;
        public final String label;
        public final AABB boundingBox;
        public GlStateManager.Color color = new GlStateManager.Color(1.0F, 1.0F, 1.0F, 0.5F);
        public boolean pressed;

        public KeyButton(int id, String label, float x, float y, float width, float height)
        {
            this.id = id;
            this.label = label;
            this.boundingBox = new AABB((double)x, (double)y, 0.0D, (double)(x + width), (double)(y + height), 0.028D * (double)PhysicalKeyboard.this.scale);
        }

        public AABB getRenderBoundingBox()
        {
            return this.pressed ? this.boundingBox.move(0.0D, 0.0D, 0.012D * (double)PhysicalKeyboard.this.scale) : this.boundingBox;
        }

        public AABB getCollisionBoundingBox()
        {
            return this.pressed ? this.boundingBox.expandTowards(0.0D, 0.0D, 0.08D) : this.boundingBox;
        }

        public GlStateManager.Color getRenderColor()
        {
            GlStateManager.Color glstatemanager$color = new GlStateManager.Color(this.color.r, this.color.g, this.color.b, this.color.a);

            if (!this.pressed)
            {
                glstatemanager$color.r *= 0.5F;
                glstatemanager$color.g *= 0.5F;
                glstatemanager$color.b *= 0.5F;
            }

            return glstatemanager$color;
        }

        public final void press(ControllerType controller, boolean isRepeat)
        {
            if (!isRepeat)
            {
                PhysicalKeyboard.this.mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }

            MCVR.get().triggerHapticPulse(controller, isRepeat ? 300 : 600);
            this.pressed = true;
            this.onPressed();
            PhysicalKeyboard.this.updateEasterEgg(this.label);
        }

        public final void unpress(ControllerType controller)
        {
            this.pressed = false;
        }

        public abstract void onPressed();
    }
}
