package org.vivecraft.gui.settings;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

public class GuiQuickCommandsList extends ObjectSelectionList<GuiQuickCommandsList.CommandEntry>
{
    private final GuiQuickCommandEditor parent;
    private final Minecraft mc;

    public GuiQuickCommandsList(GuiQuickCommandEditor parent, Minecraft mc)
    {
        super(mc, parent.width, parent.height, 32, parent.height - 32, 20);
        this.parent = parent;
        this.mc = mc;
        String[] astring = this.minecraft.vrSettings.vrQuickCommands;
        String s = null;
        int i = 0;

        for (String s1 : astring)
        {
            this.minecraft.font.width(s1);
            this.addEntry(new GuiQuickCommandsList.CommandEntry(s1, this));
        }
    }

    public class CommandEntry extends ObjectSelectionList.Entry<GuiQuickCommandsList.CommandEntry>
    {
        private final Button btnDelete;
        public final EditBox txt;

        private CommandEntry(String command, GuiQuickCommandsList parent)
        {
            this.txt = new EditBox(GuiQuickCommandsList.this.minecraft.font, parent.width / 2 - 100, 60, 200, 20, new TextComponent(""));
            this.txt.setValue(command);
            this.btnDelete = new Button(0, 0, 18, 18, "X", (p) ->
            {
                this.txt.setValue("");
                this.txt.changeFocus(true);
            });
        }

        public boolean mouseClicked(double p_94737_, double pMouseX, int p_94739_)
        {
            if (this.btnDelete.mouseClicked(p_94737_, pMouseX, p_94739_))
            {
                return true;
            }
            else
            {
                return this.txt.mouseClicked(p_94737_, pMouseX, p_94739_) ? true : super.mouseClicked(p_94737_, pMouseX, p_94739_);
            }
        }

        public boolean mouseDragged(double p_94740_, double pMouseX, int p_94742_, double pMouseY, double p_94744_)
        {
            if (this.btnDelete.mouseDragged(p_94740_, pMouseX, p_94742_, pMouseY, p_94744_))
            {
                return true;
            }
            else
            {
                return this.txt.mouseDragged(p_94740_, pMouseX, p_94742_, pMouseY, p_94744_) ? true : super.mouseDragged(p_94740_, pMouseX, p_94742_, pMouseY, p_94744_);
            }
        }

        public boolean mouseReleased(double p_94753_, double pMouseX, int p_94755_)
        {
            if (this.btnDelete.mouseReleased(p_94753_, pMouseX, p_94755_))
            {
                return true;
            }
            else
            {
                return this.txt.mouseReleased(p_94753_, pMouseX, p_94755_) ? true : super.mouseReleased(p_94753_, pMouseX, p_94755_);
            }
        }

        public boolean mouseScrolled(double p_94734_, double pMouseX, double p_94736_)
        {
            if (this.btnDelete.mouseScrolled(p_94734_, pMouseX, p_94736_))
            {
                return true;
            }
            else
            {
                return this.txt.mouseScrolled(p_94734_, pMouseX, p_94736_) ? true : super.mouseScrolled(p_94734_, pMouseX, p_94736_);
            }
        }

        public boolean charTyped(char p_94732_, int pCodePoint)
        {
            return this.txt.isFocused() ? this.txt.charTyped(p_94732_, pCodePoint) : super.charTyped(p_94732_, pCodePoint);
        }

        public boolean keyPressed(int p_94745_, int pKeyCode, int pScanCode)
        {
            return this.txt.isFocused() ? this.txt.keyPressed(p_94745_, pKeyCode, pScanCode) : super.keyPressed(p_94745_, pKeyCode, pScanCode);
        }

        public void render(PoseStack p_93523_, int pMatrixStack, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, boolean pMouseY, float pIsMouseOver)
        {
            this.txt.x = pTop;
            this.txt.y = pIndex;
            this.txt.render(p_93523_, pHeight, pMouseX, pIsMouseOver);
            this.btnDelete.x = this.txt.x + this.txt.getWidth() + 2;
            this.btnDelete.y = this.txt.y;
            this.btnDelete.visible = true;
            this.btnDelete.render(p_93523_, pHeight, pMouseX, pIsMouseOver);
        }

		@Override
		public Component getNarration() {
			// TODO Auto-generated method stub
			return null;
		}
    }
}
