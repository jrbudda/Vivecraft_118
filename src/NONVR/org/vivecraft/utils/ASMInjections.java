package org.vivecraft.utils;

import java.util.Map;
import java.util.Random;

import org.vivecraft.api.NetworkHelper;
import org.vivecraft.api.ServerVivePlayer;

import com.google.common.collect.ImmutableMap.Builder;

import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class ASMInjections
{
    private static Random random = new Random();

    public static boolean containerCreativeMouseDown(int eatTheStack)
    {
        return false;
    }

    public static void addCreativeItems(CreativeModeTab tab, NonNullList<ItemStack> list)
    {
        if (tab == CreativeModeTab.TAB_FOOD || tab == null)
        {
            ItemStack itemstack = (new ItemStack(Items.PUMPKIN_PIE)).setHoverName(new TextComponent("EAT ME"));
            ItemStack itemstack1 = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER).setHoverName(new TextComponent("DRINK ME"));
            itemstack1.getTag().putInt("HideFlags", 32);
            list.add(itemstack);
            list.add(itemstack1);
        }

        if (tab == CreativeModeTab.TAB_TOOLS || tab == null)
        {
            ItemStack itemstack3 = (new ItemStack(Items.LEATHER_BOOTS)).setHoverName(new TranslatableComponent("vivecraft.item.jumpboots"));
            itemstack3.getTag().putBoolean("Unbreakable", true);
            itemstack3.getTag().putInt("HideFlags", 4);
            ItemStack itemstack4 = (new ItemStack(Items.SHEARS)).setHoverName(new TranslatableComponent("vivecraft.item.climbclaws"));
            itemstack4.getTag().putBoolean("Unbreakable", true);
            itemstack4.getTag().putInt("HideFlags", 4);
            list.add(itemstack3);
            list.add(itemstack4);
        }
    }

    public static void addCreativeSearch(String query, NonNullList<ItemStack> list)
    {
        NonNullList<ItemStack> nonnulllist = NonNullList.create();
        addCreativeItems((CreativeModeTab)null, nonnulllist);

        for (ItemStack itemstack : nonnulllist)
        {
            if (query.isEmpty() || itemstack.getHoverName().toString().toLowerCase().contains(query.toLowerCase()))
            {
                list.add(itemstack);
            }
        }
    }

    public static void activateFun(ServerPlayer serverPlayer) {
        ServerVivePlayer serverVivePlayer = NetworkHelper.vivePlayers.get(serverPlayer.getUUID());

        if (/*!Minecraft.getInstance().vrSettings.disableFun &&*/ serverVivePlayer != null && serverVivePlayer.isVR() && random.nextInt(40) == 3)
        {
            ItemStack itemstack;

            if (random.nextInt(2) == 1)
            {
                itemstack = (new ItemStack(Items.PUMPKIN_PIE)).setHoverName(new TextComponent("EAT ME"));
            }
            else
            {
                itemstack = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER).setHoverName(new TextComponent("DRINK ME"));
            }

            itemstack.getTag().putInt("HideFlags", 32);

            if (serverPlayer.getInventory().add(itemstack))
            {
                serverPlayer.inventoryMenu.broadcastChanges();
            }
        }
    }

    public static void adjustItemThrow(ServerPlayer serverPlayer, ItemEntity itemEntity, boolean pDropAround) {
        ServerVivePlayer serverVivePlayer = NetworkHelper.vivePlayers.get(serverPlayer.getUUID());

        if (serverVivePlayer != null && serverVivePlayer.isVR() && !pDropAround && itemEntity != null)
        {
            Vec3 vec3 = serverVivePlayer.getControllerPos(0, serverPlayer);
            Vec3 vec31 = serverVivePlayer.getControllerDir(0);
            float f = 0.3F;
            itemEntity.setDeltaMovement(vec31.x * (double)f, vec31.y * (double)f, vec31.z * (double)f);
            itemEntity.setPos(vec3.x() + itemEntity.getDeltaMovement().x(), vec3.y() + itemEntity.getDeltaMovement().y(), vec3.z() + itemEntity.getDeltaMovement().z());
        }
    }

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
        	Map <RecipeType<?>, Builder <ResourceLocation, Recipe<?>>> map1 = map;
        	(map1.get(boot.getType())).put(boot.getId(), boot);
        	(map1.get(claw.getType())).put(claw.getId(), claw);
        }
    }

    public static void dummy(float f)
    {
    }
}
