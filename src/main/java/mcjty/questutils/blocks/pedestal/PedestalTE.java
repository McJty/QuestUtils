package mcjty.questutils.blocks.pedestal;

import com.google.gson.JsonObject;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.questutils.blocks.QUTileEntity;
import mcjty.questutils.json.JsonTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class PedestalTE extends QUTileEntity implements DefaultSidedInventory {

    private InventoryHelper inventoryHelper = new InventoryHelper(this, PedestalContainer.factory, 1);
    private static int[] slots = null;

    @Override
    protected boolean needsCustomInvWrapper() {
        return true;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (slots == null) {
            slots = new int[] { PedestalContainer.SLOT_ITEM };
        }
        return slots;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return true;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }


    @Override
    public InventoryHelper getInventoryHelper() {
        return inventoryHelper;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return canPlayerAccess(player);
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound compound) {
        super.readRestorableFromNBT(compound);
        readBufferFromNBT(compound, inventoryHelper);
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound compound) {
        super.writeRestorableToNBT(compound);
        writeBufferToNBT(compound, inventoryHelper);
    }

    @Override
    public void writeToJson(JsonObject object) {
        super.writeToJson(object);
        if (hasIdentifier()) {
            object.add("filter", JsonTools.writeItemsToJson(getInventoryHelper(), 0, 1));
        }
    }

    @Override
    public void readFromJson(JsonObject object) {
        super.readFromJson(object);
        if (object.has("filter")) {
            JsonTools.readItemsFromJson(object.getAsJsonArray("filter"), getInventoryHelper(), 0, 1);
        }
    }
}
