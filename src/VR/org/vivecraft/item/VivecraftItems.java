package org.vivecraft.item;

import java.util.Map;

import com.google.common.collect.ImmutableMap.Builder;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.Blocks;

public class VivecraftItems {
	public static void injectItems(Map map) {
		//VIVECRAFT - This prolly cant stay here. Move to .json files someday.
		ItemStack is = new ItemStack(Items.LEATHER_BOOTS);
		is.setHoverName(new TranslatableComponent("vivecraft.item.jumpboots"));
		is.getOrCreateTag().putBoolean("Unbreakable", true);
		is.getOrCreateTag().putInt("HideFlags",4);

		ItemStack is2 = new ItemStack(Items.SHEARS);
		is2.setHoverName(new TranslatableComponent("vivecraft.item.climbclaws"));
		is2.getOrCreateTag().putBoolean("Unbreakable", true);
		is2.getOrCreateTag().putInt("HideFlags",4);

		ShapedRecipe boot = new ShapedRecipe(new ResourceLocation("jumpboots"),"Vivecraft", 1, 2, NonNullList.a(Ingredient.EMPTY,Ingredient.a(Items.LEATHER_BOOTS), Ingredient.a(new ItemStack(Blocks.SLIME_BLOCK))), is);
		ShapedRecipe claw = new ShapedRecipe(new ResourceLocation("climbclaws"),"Vivecraft", 3, 2, NonNullList.a(Ingredient.EMPTY,Ingredient.a(Items.SPIDER_EYE),Ingredient.EMPTY,Ingredient.a(Items.SPIDER_EYE),Ingredient.a(Items.SHEARS),Ingredient.EMPTY,Ingredient.a(Items.SHEARS)), is2);

		if (map.containsKey(boot.getType())) {
			((Map) map.get(boot.getType())).put(boot.getId(), boot);
			((Map) map.get(claw.getType())).put(claw.getId(), claw);
		}
	}
}
