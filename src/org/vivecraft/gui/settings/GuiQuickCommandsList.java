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

        public boolean mouseClicked(double p_94737_, double p_94738_, int p_94739_)
        {
            if (this.btnDelete.mouseClicked(p_94737_, p_94738_, p_94739_))
            {
                return true;
            }
            else
            {
                return this.txt.mouseClicked(p_94737_, p_94738_, p_94739_) ? true : super.mouseClicked(p_94737_, p_94738_, p_94739_);
            }
        }

        public boolean mouseDragged(double p_94740_, double p_94741_, int p_94742_, double p_94743_, double p_94744_)
        {
            if (this.btnDelete.mouseDragged(p_94740_, p_94741_, p_94742_, p_94743_, p_94744_))
            {
                return true;
            }
            else
            {
                return this.txt.mouseDragged(p_94740_, p_94741_, p_94742_, p_94743_, p_94744_) ? true : super.mouseDragged(p_94740_, p_94741_, p_94742_, p_94743_, p_94744_);
            }
        }

        public boolean mouseReleased(double p_94753_, double p_94754_, int p_94755_)
        {
            if (this.btnDelete.mouseReleased(p_94753_, p_94754_, p_94755_))
            {
                return true;
            }
            else
            {
                return this.txt.mouseReleased(p_94753_, p_94754_, p_94755_) ? true : super.mouseReleased(p_94753_, p_94754_, p_94755_);
            }
        }

        public boolean mouseScrolled(double p_94734_, double p_94735_, double p_94736_)
        {
            if (this.btnDelete.mouseScrolled(p_94734_, p_94735_, p_94736_))
            {
                return true;
            }
            else
            {
                return this.txt.mouseScrolled(p_94734_, p_94735_, p_94736_) ? true : super.mouseScrolled(p_94734_, p_94735_, p_94736_);
            }
        }

        public boolean charTyped(char p_94732_, int p_94733_)
        {
            return this.txt.isFocused() ? this.txt.charTyped(p_94732_, p_94733_) : super.charTyped(p_94732_, p_94733_);
        }

        public boolean keyPressed(int p_94745_, int p_94746_, int p_94747_)
        {
            return this.txt.isFocused() ? this.txt.keyPressed(p_94745_, p_94746_, p_94747_) : super.keyPressed(p_94745_, p_94746_, p_94747_);
        }

        public void render(PoseStack p_93523_, int p_93524_, int p_93525_, int p_93526_, int p_93527_, int p_93528_, int p_93529_, int p_93530_, boolean p_93531_, float p_93532_)
        {
            this.txt.x = p_93526_;
            this.txt.y = p_93525_;
            this.txt.render(p_93523_, p_93529_, p_93530_, p_93532_);
            this.btnDelete.x = this.txt.x + this.txt.getWidth() + 2;
            this.btnDelete.y = this.txt.y;
            this.btnDelete.visible = true;
            this.btnDelete.render(p_93523_, p_93529_, p_93530_, p_93532_);
        }

		@Override
		public Component getNarration() {
			// TODO Auto-generated method stub
			return null;
		}
    }
}
