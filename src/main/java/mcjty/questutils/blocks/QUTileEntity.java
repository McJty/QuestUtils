package mcjty.questutils.blocks;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mcjty.lib.bindings.DefaultValue;
import mcjty.lib.bindings.IValue;
import mcjty.lib.tileentity.GenericTileEntity;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.questutils.data.QUData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class QUTileEntity extends GenericTileEntity {

    private String identifier;

    public static final Key<String> VALUE_ID = new Key<>("id", Type.STRING);

    @Override
    public IValue<?>[] getValues() {
        return new IValue[] {
                new DefaultValue<>(VALUE_ID, this::getIdentifier, this::setIdentifier),
        };
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean hasIdentifier() {
        return identifier != null && !identifier.trim().isEmpty();
    }

    public void setIdentifier(String identifier) {
        if (!world.isRemote && hasIdentifier()) {
            QUData.getData().removeEntry(identifier);
            QUData.getData().removeEntry(world.provider.getDimension(), pos);
        }
        if (identifier == null || identifier.trim().isEmpty()) {
            this.identifier = null;
        } else {
            this.identifier = identifier;
        }
        if (!world.isRemote && hasIdentifier()) {
            QUData.getData().updateEntry(identifier, world.provider.getDimension(), pos);
        }
        markDirtyClient();
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!world.isRemote && hasIdentifier()) {
            QUData.getData().updateEntry(identifier, world.provider.getDimension(), pos);
        }
    }

    @Override
    public void onBlockBreak(World world, BlockPos pos, IBlockState state) {
        super.onBlockBreak(world, pos, state);
        if (!this.world.isRemote) {
            if (hasIdentifier()) {
                QUData.getData().removeEntry(identifier);
            }
            QUData.getData().removeEntry(this.world.provider.getDimension(), pos);
        }
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        if (tagCompound.hasKey("quid")) {
            identifier = tagCompound.getString("quid");
        } else {
            identifier = null;
        }
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        if (identifier != null) {
            tagCompound.setString("quid", identifier);
        }
    }

    public void writeToJson(JsonObject object) {
        if (hasIdentifier()) {
            // The identifier is put here as a reference but is not read in readFromJson
            object.add("id", new JsonPrimitive(identifier));
        }
    }

    public void readFromJson(JsonObject object) {
    }
}
