package org.vivecraft.gui.framework;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.phys.Vec2;
import net.optifine.Lang;
import org.vivecraft.settings.VRSettings;

public abstract class GuiVROptionsBase extends Screen
{
    public static final int DONE_BUTTON = 200;
    public static final int DEFAULTS_BUTTON = 201;
    protected final Screen lastScreen;
    protected final VRSettings settings;
    private VRTooltipManager tooltipManager = new VRTooltipManager(this, new TooltipProviderVROptions());
    protected boolean reinit;
    protected boolean drawDefaultButtons = true;
    protected ObjectSelectionList visibleList = null;
    private int nextButtonIndex = 0;
    public String vrTitle = "Title";
    private Button btnDone;
    private Button btnDefaults;

    public GuiVROptionsBase(Screen lastScreen)
    {
        super(new TextComponent(""));
        this.lastScreen = lastScreen;
        this.settings = Minecraft.getInstance().vrSettings;
    }

    protected void addDefaultButtons()
    {
        this.addRenderableWidget(this.btnDone = new Button(this.width / 2 + 5, this.height - 30, 150, 20, new TranslatableComponent("gui.back"), (p) ->
        {
            if (!this.onDoneClicked())
            {
                this.minecraft.vrSettings.saveOptions();
                this.minecraft.setScreen(this.lastScreen);
            }
        }));
        this.addRenderableWidget(this.btnDefaults = new Button(this.width / 2 - 155, this.height - 30, 150, 20, new TranslatableComponent("vivecraft.gui.loaddefaults"), (p) ->
        {
            this.loadDefaults();
            this.minecraft.vrSettings.saveOptions();
            this.reinit = true;
        }));
    }

    protected boolean onDoneClicked()
    {
        return false;
    }

    protected void init(VROptionLayout[] settings, boolean clear)
    {
        if (clear)
        {
        	this.clearWidgets();
        }

        int i = 0;

        for (final VROptionLayout vroptionlayout : settings)
        {
            if (vroptionlayout.getOption() != null && vroptionlayout.getOption().getEnumFloat())
            {
                this.addRenderableWidget(new GuiVROptionSlider(vroptionlayout.getOrdinal(), vroptionlayout.getX(this.width), vroptionlayout.getY(this.height), vroptionlayout.getOption(), (double)vroptionlayout.getOption().getValueMin(), (double)vroptionlayout.getOption().getValueMax())
                {
                    public void onClick(double p_93634_, double pMouseX)
                    {
                        if (vroptionlayout.getCustomHandler() == null || !vroptionlayout.getCustomHandler().apply(this, new Vec2((float)p_93634_, (float)pMouseX)))
                        {
                            super.onClick(p_93634_, pMouseX);
                        }
                    }
                });
            }
            else if (vroptionlayout.getOption() != null)
            {
                this.addRenderableWidget(new GuiVROptionButton(vroptionlayout.getOrdinal(), vroptionlayout.getX(this.width), vroptionlayout.getY(this.height), vroptionlayout.getOption(), vroptionlayout.getButtonText(), (p) ->
                {
                    if (vroptionlayout.getCustomHandler() == null || !vroptionlayout.getCustomHandler().apply((GuiVROptionButton)p, new Vec2(0.0F, 0.0F)))
                    {
                        this.settings.setOptionValue(((GuiVROptionButton)p).getOption());
                        p.setMessage(new TextComponent(vroptionlayout.getButtonText()));
                    }
                }));
            }
            else if (vroptionlayout.getScreen() != null)
            {
                this.addRenderableWidget(new GuiVROptionButton(vroptionlayout.getOrdinal(), vroptionlayout.getX(this.width), vroptionlayout.getY(this.height), vroptionlayout.getButtonText(), (p) ->
                {
                    try {
                        if (vroptionlayout.getCustomHandler() != null && vroptionlayout.getCustomHandler().apply((GuiVROptionButton)p, new Vec2(0.0F, 0.0F)))
                        {
                            return;
                        }

                        this.settings.saveOptions();
                        this.minecraft.setScreen(vroptionlayout.getScreen().getConstructor(Screen.class).newInstance(this));
                    }
                    catch (ReflectiveOperationException reflectiveoperationexception)
                    {
                        reflectiveoperationexception.printStackTrace();
                    }
                }));
            }
            else if (vroptionlayout.getCustomHandler() != null)
            {
                this.addRenderableWidget(new GuiVROptionButton(vroptionlayout.getOrdinal(), vroptionlayout.getX(this.width), vroptionlayout.getY(this.height), vroptionlayout.getButtonText(), (p) ->
                {
                    vroptionlayout.getCustomHandler().apply((GuiVROptionButton)p, new Vec2(0.0F, 0.0F));
                }));
            }
            else
            {
                this.addRenderableWidget(new GuiVROptionButton(vroptionlayout.getOrdinal(), vroptionlayout.getX(this.width), vroptionlayout.getY(this.height), vroptionlayout.getButtonText(), (p) ->
                {
                }));
            }
        }

        ++i;
    }

    protected void loadDefaults()
    {
    }

    protected void init(VROptionEntry[] settings, boolean clear)
    {
        if (clear)
        {
        	this.clearWidgets();
            this.nextButtonIndex = 0;
        }

        ArrayList<VROptionLayout> arraylist = new ArrayList<>();

        if (this.nextButtonIndex < this.children().size())
        {
            this.nextButtonIndex = this.children().size();
        }

        int i = this.nextButtonIndex;

        for (int j = 0; j < settings.length; ++j)
        {
            VROptionLayout.Position vroptionlayout$position = settings[j].center ? VROptionLayout.Position.POS_CENTER : (i % 2 == 0 ? VROptionLayout.Position.POS_LEFT : VROptionLayout.Position.POS_RIGHT);

            if (settings[j].center && i % 2 != 0)
            {
                ++i;
            }

            if (settings[j].option != null)
            {
                if (settings[j].option != VRSettings.VrOptions.DUMMY)
                {
                    arraylist.add(new VROptionLayout(settings[j].option, settings[j].customHandler, vroptionlayout$position, (float)Math.floor((double)((float)i / 2.0F)), true, settings[j].title));
                }
            }
            else if (settings[j].customHandler != null)
            {
                arraylist.add(new VROptionLayout(settings[j].customHandler, vroptionlayout$position, (float)Math.floor((double)((float)i / 2.0F)), true, settings[j].title));
            }

            if (settings[j].center)
            {
                ++i;
            }

            ++i;
        }

        this.nextButtonIndex = i;
        this.init(arraylist.toArray(new VROptionLayout[0]), false);
    }

    protected void init(VRSettings.VrOptions[] settings, boolean clear)
    {
        VROptionEntry[] avroptionentry = new VROptionEntry[settings.length];

        for (int i = 0; i < settings.length; ++i)
        {
            avroptionentry[i] = new VROptionEntry(settings[i]);
        }

        this.init(avroptionentry, clear);
    }

    public void render(PoseStack p_96562_, int pMatrixStack, int pMouseX, float pMouseY)
    {
        if (this.reinit)
        {
            this.reinit = false;
            this.init();
        }

        this.renderBackground(p_96562_);

        if (this.visibleList != null)
        {
            this.visibleList.render(p_96562_, pMatrixStack, pMouseX, pMouseY);
        }

        drawCenteredString(p_96562_, this.font, Lang.get(this.vrTitle), this.width / 2, 15, 16777215);

        if (this.btnDefaults != null)
        {
            this.btnDefaults.visible = this.drawDefaultButtons;
        }

        if (this.btnDone != null)
        {
            this.btnDone.visible = this.drawDefaultButtons;
        }

        super.render(p_96562_, pMatrixStack, pMouseX, pMouseY);
        this.tooltipManager.drawTooltips(p_96562_, pMatrixStack, pMouseX, this.getButtonList());
    }

    protected void actionPerformed(AbstractWidget button)
    {
    }

    protected void actionPerformedRightClick(AbstractWidget button)
    {
    }

    public boolean mouseClicked(double p_94737_, double pMouseX, int p_94739_)
    {
        boolean flag = super.mouseClicked(p_94737_, pMouseX, p_94739_);
        AbstractWidget abstractwidget = getSelectedButton((int)p_94737_, (int)pMouseX, this.getButtonList());

        if (abstractwidget != null)
        {
            if (!(abstractwidget instanceof GuiVROptionSlider))
            {
                abstractwidget.playDownSound(this.minecraft.getSoundManager());
            }

            if (p_94739_ == 0)
            {
                this.actionPerformed(abstractwidget);
            }
            else if (p_94739_ == 1)
            {
                this.actionPerformedRightClick(abstractwidget);
            }
        }
        else if (this.visibleList != null)
        {
            return this.visibleList.mouseClicked(p_94737_, pMouseX, p_94739_);
        }

        return flag;
    }

    public boolean mouseReleased(double p_94753_, double pMouseX, int p_94755_)
    {
        return this.visibleList != null ? this.visibleList.mouseReleased(p_94753_, pMouseX, p_94755_) : super.mouseReleased(p_94753_, pMouseX, p_94755_);
    }

    public boolean mouseDragged(double p_94740_, double pMouseX, int p_94742_, double pMouseY, double p_94744_)
    {
        return this.visibleList != null ? this.visibleList.mouseDragged(p_94740_, pMouseX, p_94742_, pMouseY, p_94744_) : super.mouseDragged(p_94740_, pMouseX, p_94742_, pMouseY, p_94744_);
    }

    public boolean mouseScrolled(double p_94734_, double pMouseX, double p_94736_)
    {
        if (this.visibleList != null)
        {
            this.visibleList.mouseScrolled(p_94734_, pMouseX, p_94736_);
        }

        return super.mouseScrolled(p_94734_, pMouseX, p_94736_);
    }

    public boolean keyPressed(int p_94745_, int pKeyCode, int pScanCode)
    {
        if (p_94745_ == 256)
        {
            if (!this.onDoneClicked())
            {
                this.minecraft.vrSettings.saveOptions();
                this.minecraft.setScreen(this.lastScreen);
            }

            return true;
        }
        else
        {
            return this.visibleList != null && this.visibleList.keyPressed(p_94745_, pKeyCode, pScanCode) ? true : super.keyPressed(p_94745_, pKeyCode, pScanCode);
        }
    }

    public boolean charTyped(char p_94732_, int pCodePoint)
    {
        return this.visibleList != null && this.visibleList.charTyped(p_94732_, pCodePoint) ? true : super.charTyped(p_94732_, pCodePoint);
    }
}
