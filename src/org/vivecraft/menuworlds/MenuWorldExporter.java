package org.vivecraft.menuworlds;

import com.google.common.io.Files;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import org.vivecraft.reflection.MCReflection;

public class MenuWorldExporter
{
    public static final int VERSION = 4;
    public static final int MIN_VERSION = 2;

    public static byte[] saveArea(Level world, int xMin, int zMin, int xSize, int zSize, int ground) throws IOException
    {
        MenuWorldExporter.BlockStateMapper menuworldexporter$blockstatemapper = new MenuWorldExporter.BlockStateMapper();
        int i = world.getMaxBuildHeight();
        int[] aint = new int[xSize * i * zSize];
        byte[] abyte = new byte[xSize * i * zSize];
        byte[] abyte1 = new byte[xSize * i * zSize];
        int[] aint1 = new int[xSize * i * zSize / 64];

        for (int j = xMin; j < xMin + xSize; ++j)
        {
            int k = j - xMin;

            for (int l = zMin; l < zMin + zSize; ++l)
            {
                int i1 = l - zMin;

                for (int j1 = 0; j1 < i; ++j1)
                {
                    int k1 = (j1 * zSize + i1) * xSize + k;
                    BlockPos blockpos = new BlockPos(j, j1, l);
                    BlockState blockstate = world.getBlockState(blockpos);
                    aint[k1] = menuworldexporter$blockstatemapper.getId(blockstate);
                    abyte[k1] = (byte)world.getBrightness(LightLayer.SKY, blockpos);
                    abyte1[k1] = (byte)world.getBrightness(LightLayer.BLOCK, blockpos);

                    if (j % 4 == 0 && j1 % 4 == 0 && l % 4 == 0)
                    {
                        int l1 = (j1 / 4 * (zSize / 4) + i1 / 4) * (xSize / 4) + k / 4;
                        aint1[l1] = BuiltinRegistries.BIOME.getId(world.getNoiseBiome(j / 4, j1 / 4, l / 4));
                    }
                }
            }
        }

        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);
        dataoutputstream.writeInt(xSize);
        dataoutputstream.writeInt(i);
        dataoutputstream.writeInt(zSize);
        dataoutputstream.writeInt(ground);
        dataoutputstream.writeUTF(world.dimensionType().effectsLocation().toString());

        if (world instanceof ServerLevel)
        {
            dataoutputstream.writeBoolean(((ServerLevel)world).isFlat());
        }
        else
        {
            dataoutputstream.writeBoolean((boolean) MCReflection.ClientWorldInfo_isFlat.get(world.getLevelData()));
        }

        dataoutputstream.writeBoolean(world.dimensionType().hasSkyLight());

        if (world instanceof ServerLevel)
        {
            dataoutputstream.writeLong(((ServerLevel)world).getSeed());
        }
        else
        {
            dataoutputstream.writeLong((long) MCReflection.BiomeManager_seed.get(world.getBiomeManager()));
        }

        menuworldexporter$blockstatemapper.writePalette(dataoutputstream);

        for (int i2 = 0; i2 < aint.length; ++i2)
        {
            dataoutputstream.writeInt(aint[i2]);
        }

        for (int j2 = 0; j2 < abyte.length; ++j2)
        {
            dataoutputstream.writeByte(abyte[j2] | abyte1[j2] << 4);
        }

        for (int k2 = 0; k2 < aint1.length; ++k2)
        {
            dataoutputstream.writeInt(aint1[k2]);
        }

        MenuWorldExporter.Header menuworldexporter$header = new MenuWorldExporter.Header();
        menuworldexporter$header.version = 4;
        menuworldexporter$header.uncompressedSize = bytearrayoutputstream.size();
        ByteArrayOutputStream bytearrayoutputstream1 = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream1 = new DataOutputStream(bytearrayoutputstream1);
        menuworldexporter$header.write(dataoutputstream1);
        Deflater deflater = new Deflater(9);
        deflater.setInput(bytearrayoutputstream.toByteArray());
        deflater.finish();
        byte[] abyte2 = new byte[1048576];

        while (!deflater.finished())
        {
            int l2 = deflater.deflate(abyte2);
            bytearrayoutputstream1.write(abyte2, 0, l2);
        }

        return bytearrayoutputstream1.toByteArray();
    }

    public static void saveAreaToFile(Level world, int xMin, int zMin, int xSize, int zSize, int ground, File file) throws IOException
    {
        byte[] abyte = saveArea(world, xMin, zMin, xSize, zSize, ground);
        Files.write(abyte, file);
    }

    public static FakeBlockAccess loadWorld(byte[] data) throws IOException, DataFormatException
    {
        MenuWorldExporter.Header menuworldexporter$header = new MenuWorldExporter.Header();

        try (DataInputStream datainputstream = new DataInputStream(new ByteArrayInputStream(data)))
        {
            menuworldexporter$header.read(datainputstream);
        }

        if (menuworldexporter$header.version <= 4 && menuworldexporter$header.version >= 2)
        {
            Inflater inflater = new Inflater();
            inflater.setInput(data, 8, data.length - 8);
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream(menuworldexporter$header.uncompressedSize);
            byte[] abyte = new byte[1048576];

            while (!inflater.finished())
            {
                int i = inflater.inflate(abyte);
                bytearrayoutputstream.write(abyte, 0, i);
            }

            DataInputStream datainputstream1 = new DataInputStream(new ByteArrayInputStream(bytearrayoutputstream.toByteArray()));
            int j = datainputstream1.readInt();
            int k = datainputstream1.readInt();
            int l = datainputstream1.readInt();
            int i1 = datainputstream1.readInt();
            ResourceLocation resourcelocation;

            if (menuworldexporter$header.version < 4)
            {
                int j1 = datainputstream1.readInt();

                switch (j1)
                {
                    case -1:
                        resourcelocation = new ResourceLocation("the_nether");
                        break;

                    case 1:
                        resourcelocation = new ResourceLocation("the_end");
                        break;

                    default:
                        resourcelocation = new ResourceLocation("overworld");
                }
            }
            else
            {
                resourcelocation = new ResourceLocation(datainputstream1.readUTF());
            }

            Registry<DimensionType> registry = RegistryAccess.builtin().registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
            ResourceKey<DimensionType> resourcekey = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, resourcelocation);
            DimensionType dimensiontype = registry.get(resourcekey);

            if (dimensiontype == null)
            {
                dimensiontype = registry.get(DimensionType.OVERWORLD_LOCATION);
            }

            boolean flag;

            if (menuworldexporter$header.version < 4)
            {
                flag = datainputstream1.readUTF().equals("flat");
            }
            else
            {
                flag = datainputstream1.readBoolean();
            }

            datainputstream1.readBoolean();
            long k1 = 0L;

            if (menuworldexporter$header.version >= 3)
            {
                k1 = datainputstream1.readLong();
            }

            MenuWorldExporter.BlockStateMapper menuworldexporter$blockstatemapper = new MenuWorldExporter.BlockStateMapper();
            menuworldexporter$blockstatemapper.readPalette(datainputstream1);
            BlockState[] ablockstate = new BlockState[j * k * l];

            for (int l1 = 0; l1 < ablockstate.length; ++l1)
            {
                ablockstate[l1] = menuworldexporter$blockstatemapper.getState(datainputstream1.readInt());
            }

            byte[] abyte2 = new byte[j * k * l];
            byte[] abyte1 = new byte[j * k * l];

            for (int i2 = 0; i2 < abyte2.length; ++i2)
            {
                int j2 = datainputstream1.readByte() & 255;
                abyte2[i2] = (byte)(j2 & 15);
                abyte1[i2] = (byte)(j2 >> 4);
            }

            Biome[] abiome = new Biome[j * k * l / 64];

            if (menuworldexporter$header.version == 2)
            {
                Biome[] abiome1 = new Biome[j * l];

                for (int k2 = 0; k2 < abiome1.length; ++k2)
                {
                    abiome1[k2] = getBiome(datainputstream1.readInt());
                }

                for (int j3 = 0; j3 < j / 4; ++j3)
                {
                    for (int l2 = 0; l2 < l / 4; ++l2)
                    {
                        abiome[l2 * (j / 4) + j3] = abiome1[l2 * 4 * j + j3 * 4];
                    }
                }

                int k3 = j / 4 * (l / 4);

                for (int l3 = 1; l3 < k / 4; ++l3)
                {
                    System.arraycopy(abiome, 0, abiome, k3 * l3, k3);
                }
            }
            else
            {
                for (int i3 = 0; i3 < abiome.length; ++i3)
                {
                    abiome[i3] = getBiome(datainputstream1.readInt());
                }
            }

            return new FakeBlockAccess(menuworldexporter$header.version, k1, ablockstate, abyte2, abyte1, abiome, j, k, l, i1, dimensiontype, flag);
        }
        else
        {
            throw new DataFormatException("Unsupported menu world version: " + menuworldexporter$header.version);
        }
    }

    public static FakeBlockAccess loadWorld(InputStream is) throws IOException, DataFormatException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        byte[] abyte = new byte[1048576];
        int i;

        while ((i = is.read(abyte)) != -1)
        {
            bytearrayoutputstream.write(abyte, 0, i);
        }

        return loadWorld(bytearrayoutputstream.toByteArray());
    }

    public static int readVersion(File file) throws IOException
    {
        int i;

        try (DataInputStream datainputstream = new DataInputStream(new FileInputStream(file)))
        {
            MenuWorldExporter.Header menuworldexporter$header = new MenuWorldExporter.Header();
            menuworldexporter$header.read(datainputstream);
            i = menuworldexporter$header.version;
        }

        return i;
    }

    private static Biome getBiome(int biomeId)
    {
        Biome biome = BuiltinRegistries.BIOME.byId(biomeId);
        return biome != null ? biome : BuiltinRegistries.BIOME.get(Biomes.PLAINS);
    }

    private static class BlockStateMapper
    {
        CrudeIncrementalIntIdentityHashBiMap<BlockState> paletteMap = new CrudeIncrementalIntIdentityHashBiMap<>(256);

        private BlockStateMapper()
        {
        }

        int getId(BlockState state)
        {
            int i = this.paletteMap.getId(state);
            return i == -1 ? this.paletteMap.add(state) : i;
        }

        BlockState getState(int id)
        {
            return this.paletteMap.byId(id);
        }

        void readPalette(DataInputStream dis) throws IOException
        {
            this.paletteMap.clear();
            int i = dis.readInt();

            for (int j = 0; j < i; ++j)
            {
                CompoundTag compoundtag = CompoundTag.TYPE.load(dis, 0, NbtAccounter.UNLIMITED);
                this.paletteMap.add(NbtUtils.readBlockState(compoundtag));
            }
        }

        void writePalette(DataOutputStream dos) throws IOException
        {
            dos.writeInt(this.paletteMap.size());

            for (int i = 0; i < this.paletteMap.size(); ++i)
            {
                CompoundTag compoundtag = NbtUtils.writeBlockState(this.paletteMap.byId(i));
                compoundtag.write(dos);
            }
        }
    }

    public static class Header
    {
        public static final int SIZE = 8;
        public int version;
        public int uncompressedSize;

        public void read(DataInputStream dis) throws IOException
        {
            this.version = dis.readInt();
            this.uncompressedSize = dis.readInt();
        }

        public void write(DataOutputStream dos) throws IOException
        {
            dos.writeInt(this.version);
            dos.writeInt(this.uncompressedSize);
        }
    }
}
