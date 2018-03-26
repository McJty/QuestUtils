package mcjty.questutils.blocks;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.network.Argument;
import mcjty.questutils.data.QUData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;

public class QUTileEntity extends GenericTileEntity {

    public static final String CMD_SETID = "cmdSetId";

    private String identifier;

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
    public void onBlockBreak(World workd, BlockPos pos, IBlockState state) {
        super.onBlockBreak(workd, pos, state);
        if (!world.isRemote) {
            if (hasIdentifier()) {
                QUData.getData().removeEntry(identifier);
            }
            QUData.getData().removeEntry(world.provider.getDimension(), pos);
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

    @Override
    public boolean execute(EntityPlayerMP playerMP, String command, Map<String, Argument> args) {
        boolean rc = super.execute(playerMP, command, args);
        if (rc) {
            return true;
        }
        if (CMD_SETID.equals(command)) {
            String id = args.get("id").getString();
            setIdentifier(id);
            return true;
        }

        return false;
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
