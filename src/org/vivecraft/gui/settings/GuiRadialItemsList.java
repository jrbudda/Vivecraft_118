package org.vivecraft.gui.settings;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Arrays;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import org.apache.commons.lang3.ArrayUtils;

public class GuiRadialItemsList extends ObjectSelectionList
{
    private final GuiRadialConfiguration parent;
    private final Minecraft mc;
    private ObjectSelectionList.Entry[] listEntries;
    private int maxListLabelWidth = 0;

    public GuiRadialItemsList(GuiRadialConfiguration parent, Minecraft mc)
    {
        super(mc, parent.width, parent.height, 63, parent.height - 32, 20);
        this.parent = parent;
        this.mc = mc;
        this.maxListLabelWidth = 90;
        this.buildList();
    }

    public void buildList()
    {
        KeyMapping[] akeymapping = ArrayUtils.clone(this.mc.options.keyMappings);
        Arrays.sort((Object[])akeymapping);
        String s = null;

        for (KeyMapping keymapping : akeymapping)
        {
            String s1 = keymapping != null ? keymapping.getCategory() : null;

            if (s1 != null)
            {
                if (s1 != null && !s1.equals(s))
                {
                    s = s1;
                    this.addEntry(new GuiRadialItemsList.CategoryEntry(s1));
                }

                this.addEntry(new GuiRadialItemsList.MappingEntry(keymapping, this.parent));
            }
        }
    }

    public class CategoryEntry extends ObjectSelectionList.Entry
    {
        private final String labelText;
        private final int labelWidth;

        public CategoryEntry(String name)
        {
            this.labelText = I18n.m_118938_(name);
            this.labelWidth = GuiRadialItemsList.this.mc.font.width(this.labelText);
        }

        public void render(PoseStack p_93523_, int p_93524_, int p_93525_, int p_93526_, int p_93527_, int p_93528_, int p_93529_, int p_93530_, boolean p_93531_, float p_93532_)
        {
            GuiRadialItemsList.this.mc.font.draw(p_93523_, this.labelText, (float)(GuiRadialItemsList.this.mc.screen.width / 2 - this.labelWidth / 2), (float)(p_93525_ + p_93528_ - 9 - 1), 6777215);
        }

		@Override
		public Component getNarration() {
			// TODO Auto-generated method stub
			return null;
		}
    }

    public class MappingEntry extends ObjectSelectionList.Entry
    {
        private final KeyMapping myKey;
        private GuiRadialConfiguration parentScreen;

        private MappingEntry(KeyMapping key, GuiRadialConfiguration parent)
        {
            this.myKey = key;
            this.parentScreen = parent;
        }

        public void render(PoseStack p_93523_, int p_93524_, int p_93525_, int p_93526_, int p_93527_, int p_93528_, int p_93529_, int p_93530_, boolean p_93531_, float p_93532_)
        {
            ChatFormatting chatformatting = ChatFormatting.WHITE;

            if (p_93531_)
            {
                chatformatting = ChatFormatting.GREEN;
            }

            GuiRadialItemsList.this.mc.font.draw(p_93523_, chatformatting + I18n.m_118938_(this.myKey.getName()), (float)(GuiRadialItemsList.this.mc.screen.width / 2 - GuiRadialItemsList.this.maxListLabelWidth / 2), (float)(p_93525_ + p_93528_ / 2 - 9 / 2), 16777215);
        }

        public boolean mouseClicked(double p_94737_, double p_94738_, int p_94739_)
        {
            this.parentScreen.setKey(this.myKey);
            return true;
        }

		@Override
		public Component getNarration() {
			// TODO Auto-generated method stub
			return null;
		}
    }
}
