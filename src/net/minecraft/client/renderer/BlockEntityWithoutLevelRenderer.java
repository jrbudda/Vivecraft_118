package net.minecraft.client.renderer;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.model.ShieldModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.AbstractSkullBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.optifine.EmissiveTextures;
import org.apache.commons.lang3.StringUtils;

public class BlockEntityWithoutLevelRenderer implements ResourceManagerReloadListener
{
    private static final ShulkerBoxBlockEntity[] SHULKER_BOXES = Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map((dyeColorIn) ->
    {
        return new ShulkerBoxBlockEntity(dyeColorIn, BlockPos.ZERO, Blocks.SHULKER_BOX.defaultBlockState());
    }).toArray((sizeIn) ->
    {
        return new ShulkerBoxBlockEntity[sizeIn];
    });
    private static final ShulkerBoxBlockEntity DEFAULT_SHULKER_BOX = new ShulkerBoxBlockEntity(BlockPos.ZERO, Blocks.SHULKER_BOX.defaultBlockState());
    private final ChestBlockEntity chest = new ChestBlockEntity(BlockPos.ZERO, Blocks.CHEST.defaultBlockState());
    private final ChestBlockEntity trappedChest = new TrappedChestBlockEntity(BlockPos.ZERO, Blocks.TRAPPED_CHEST.defaultBlockState());
    private final EnderChestBlockEntity enderChest = new EnderChestBlockEntity(BlockPos.ZERO, Blocks.ENDER_CHEST.defaultBlockState());
    private final BannerBlockEntity banner = new BannerBlockEntity(BlockPos.ZERO, Blocks.WHITE_BANNER.defaultBlockState());
    private final BedBlockEntity bed = new BedBlockEntity(BlockPos.ZERO, Blocks.RED_BED.defaultBlockState());
    private final ConduitBlockEntity conduit = new ConduitBlockEntity(BlockPos.ZERO, Blocks.CONDUIT.defaultBlockState());
    private ShieldModel shieldModel;
    public TridentModel tridentModel;
    private Map<SkullBlock.Type, SkullModelBase> skullModels;
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    private final EntityModelSet entityModelSet;

    public BlockEntityWithoutLevelRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_)
    {
        this.blockEntityRenderDispatcher = p_172550_;
        this.entityModelSet = p_172551_;
    }

    public void onResourceManagerReload(ResourceManager p_172555_)
    {
        this.shieldModel = new ShieldModel(this.entityModelSet.bakeLayer(ModelLayers.SHIELD));
        this.tridentModel = new TridentModel(this.entityModelSet.bakeLayer(ModelLayers.TRIDENT));
        this.skullModels = SkullBlockRenderer.createSkullRenderers(this.entityModelSet);
    }

    public void renderByItem(ItemStack p_108830_, ItemTransforms.TransformType p_108831_, PoseStack p_108832_, MultiBufferSource p_108833_, int p_108834_, int p_108835_)
    {
        if (EmissiveTextures.isActive())
        {
            EmissiveTextures.beginRender();
        }

        this.renderRaw(p_108830_, p_108832_, p_108833_, p_108834_, p_108835_);

        if (EmissiveTextures.isActive())
        {
            if (EmissiveTextures.hasEmissive())
            {
                EmissiveTextures.beginRenderEmissive();
                this.renderRaw(p_108830_, p_108832_, p_108833_, LightTexture.MAX_BRIGHTNESS, p_108835_);
                EmissiveTextures.endRenderEmissive();
            }

            EmissiveTextures.endRender();
        }
    }

    public void renderRaw(ItemStack itemStackIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn)
    {
        Item item = itemStackIn.getItem();

        if (item instanceof BlockItem)
        {
            Block block = ((BlockItem)item).getBlock();

            if (block instanceof AbstractSkullBlock)
            {
                GameProfile gameprofile = null;

                if (itemStackIn.hasTag())
                {
                    CompoundTag compoundtag = itemStackIn.getTag();

                    if (compoundtag.contains("SkullOwner", 10))
                    {
                        gameprofile = NbtUtils.readGameProfile(compoundtag.getCompound("SkullOwner"));
                    }
                    else if (compoundtag.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundtag.getString("SkullOwner")))
                    {
                        gameprofile = new GameProfile((UUID)null, compoundtag.getString("SkullOwner"));
                        compoundtag.remove("SkullOwner");
                        SkullBlockEntity.updateGameprofile(gameprofile, (p_315852_1_) ->
                        {
                            compoundtag.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), p_315852_1_));
                        });
                    }
                }

                SkullBlock.Type skullblock$type = ((AbstractSkullBlock)block).getType();
                SkullModelBase skullmodelbase = this.skullModels.get(skullblock$type);
                RenderType rendertype = SkullBlockRenderer.getRenderType(skullblock$type, gameprofile);
                SkullBlockRenderer.renderSkull((Direction)null, 180.0F, 0.0F, matrixStackIn, bufferIn, combinedLightIn, skullmodelbase, rendertype);
            }
            else
            {
                BlockState blockstate = block.defaultBlockState();
                BlockEntity blockentity;

                if (block instanceof AbstractBannerBlock)
                {
                    this.banner.fromItem(itemStackIn, ((AbstractBannerBlock)block).getColor());
                    blockentity = this.banner;
                }
                else if (block instanceof BedBlock)
                {
                    this.bed.setColor(((BedBlock)block).getColor());
                    blockentity = this.bed;
                }
                else if (blockstate.is(Blocks.CONDUIT))
                {
                    blockentity = this.conduit;
                }
                else if (blockstate.is(Blocks.CHEST))
                {
                    blockentity = this.chest;
                }
                else if (blockstate.is(Blocks.ENDER_CHEST))
                {
                    blockentity = this.enderChest;
                }
                else if (blockstate.is(Blocks.TRAPPED_CHEST))
                {
                    blockentity = this.trappedChest;
                }
                else
                {
                    if (!(block instanceof ShulkerBoxBlock))
                    {
                        return;
                    }

                    DyeColor dyecolor = ShulkerBoxBlock.getColorFromItem(item);

                    if (dyecolor == null)
                    {
                        blockentity = DEFAULT_SHULKER_BOX;
                    }
                    else
                    {
                        blockentity = SHULKER_BOXES[dyecolor.getId()];
                    }
                }

                this.blockEntityRenderDispatcher.renderItem(blockentity, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
            }
        }
        else if (itemStackIn.is(Items.SHIELD))
        {
            boolean flag = itemStackIn.getTagElement("BlockEntityTag") != null;
            matrixStackIn.pushPose();
            matrixStackIn.scale(1.0F, -1.0F, -1.0F);
            Material material = flag ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;
            VertexConsumer vertexconsumer1 = material.sprite().wrap(ItemRenderer.getFoilBufferDirect(bufferIn, this.shieldModel.renderType(material.atlasLocation()), true, itemStackIn.hasFoil()));
            this.shieldModel.handle().render(matrixStackIn, vertexconsumer1, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);

            if (flag)
            {
                List<Pair<BannerPattern, DyeColor>> list = BannerBlockEntity.createPatterns(ShieldItem.getColor(itemStackIn), BannerBlockEntity.getItemPatterns(itemStackIn));
                BannerRenderer.renderPatterns(matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn, this.shieldModel.plate(), material, false, list, itemStackIn.hasFoil());
            }
            else
            {
                this.shieldModel.plate().render(matrixStackIn, vertexconsumer1, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            matrixStackIn.popPose();
        }
        else if (itemStackIn.is(Items.TRIDENT))
        {
            matrixStackIn.pushPose();
            matrixStackIn.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer vertexconsumer = ItemRenderer.getFoilBufferDirect(bufferIn, this.tridentModel.renderType(TridentModel.TEXTURE), false, itemStackIn.hasFoil());
            this.tridentModel.renderToBuffer(matrixStackIn, vertexconsumer, combinedLightIn, combinedOverlayIn, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStackIn.popPose();
        }
    }
}
