package mcjty.questutils.blocks.itemcomparator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.network.Argument;
import mcjty.lib.varia.RedstoneMode;
import mcjty.questutils.blocks.QUTileEntity;
import mcjty.questutils.json.JsonTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.util.Map;

public class ItemComparatorTE extends QUTileEntity implements DefaultSidedInventory {

    public static final String CMD_RSMODE = "rsMode";

    private static int[] slots = null;
    private boolean inAlarm = false;

    private InventoryHelper inventoryHelper = new InventoryHelper(this, ItemComparatorContainer.factory, 32);

    @Override
    protected boolean needsCustomInvWrapper() {
        return true;
    }

    @Override
    public InventoryHelper getInventoryHelper() {
        return inventoryHelper;
    }

    public void detect() {
        boolean ok = true;

        int[] amounts = new int[16];
        for (int i = 0 ; i < 16 ; i++) {
            amounts[i] = getStackInSlot(i+16).getCount();
        }

        for (int i = 0 ; i < 16 ; i++) {
            ItemStack item = getStackInSlot(i);
            if (!item.isEmpty()) {
                if (countMissing(item, amounts) == 0) {
                    consume(item, amounts);
                } else {
                    ok = false;
                    break;
                }
            }
        }
        setAlarm(ok);
    }

    private int countMissing(ItemStack matcher, int[] amounts) {
        int toFind = matcher.getCount();
        for (int i = 0 ; i < 16 ; i++) {
            ItemStack item = getStackInSlot(i+16);
            if (!item.isEmpty()) {
                if (ItemStack.areItemsEqual(matcher, item) && ItemStack.areItemStackTagsEqual(matcher, item)) {
                    toFind -= Math.min(toFind, amounts[i]);
                    if (toFind <= 0) {
                        return 0;      // We found enough
                    }
                }
            }
        }
        return toFind;
    }

    private void consume(ItemStack matcher, int[] amounts) {
        int toFind = matcher.getCount();
        for (int i = 0 ; i < 16 ; i++) {
            ItemStack item = getStackInSlot(i+16);
            if (!item.isEmpty()) {
                if (ItemStack.areItemsEqual(matcher, item) && ItemStack.areItemStackTagsEqual(matcher, item)) {
                    int consume = Math.min(toFind, amounts[i]);
                    toFind -= consume;
                    amounts[i] -= consume;
                    if (toFind <= 0) {
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void setPowerInput(int powered) {
        if (powered != powerLevel) {
            super.setPowerInput(powered);
            if (powered > 0) {
                dump();
            }
        }
    }

    public void dump() {
        for (int i = 0 ; i < 16 ; i++) {
            ItemStack stack = getStackInSlot(i+16);
            if (!stack.isEmpty()) {
                stack = InventoryHelper.insertItem(world, pos, EnumFacing.UP, stack);
                setInventorySlotContents(i+16, stack);
            }
        }
    }

    public boolean isPowered() {
        return inAlarm;
    }

    public void setAlarm(boolean alarm) {
        if (alarm != inAlarm) {
            inAlarm = alarm;
            getWorld().notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
            markDirty();
        }
    }


    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (slots == null) {
            slots = new int[16];
            for (int i = 0 ; i < 16 ; i++) {
                slots[i] = i+16;
            }
        }
        return slots;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return index >= 16 && index < 16+16;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index >= 16 && index < 16+16;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        getInventoryHelper().setInventorySlotContents(getInventoryStackLimit(), index, stack);
        detect();
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = getInventoryHelper().decrStackSize(index, count);
        detect();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = getInventoryHelper().removeStackFromSlot(index);
        detect();
        return stack;
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
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        readBufferFromNBT(tagCompound, inventoryHelper);
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        writeBufferToNBT(tagCompound, inventoryHelper);
    }

    @Override
    public boolean execute(EntityPlayerMP playerMP, String command, Map<String, Argument> args) {
        boolean rc = super.execute(playerMP, command, args);
        if (rc) {
            return true;
        }
        if (CMD_RSMODE.equals(command)) {
            String m = args.get("rs").getString();
            setRSMode(RedstoneMode.getMode(m));
            return true;
        }

        return false;
    }

    @Override
    public void writeToJson(JsonObject object) {
        super.writeToJson(object);
        if (hasIdentifier()) {
            JsonArray array = new JsonArray();
            for (int i = 0 ; i < 16 ; i++) {
                ItemStack stack = getStackInSlot(i);
                if (!stack.isEmpty()) {
                    JsonObject itemJson = JsonTools.itemStackToJson(stack);
                    array.add(itemJson);
                }
            }
            object.add("filter", array);
        }
    }

    @Override
    public void readFromJson(JsonObject object) {
        super.readFromJson(object);
        if (object.has("filter")) {
            for (int i = 0 ; i < 16 ; i++) {
                setInventorySlotContents(i, ItemStack.EMPTY);
            }
            int idx = 0;
            JsonArray array = object.getAsJsonArray("filter");
            for (JsonElement element : array) {
                setInventorySlotContents(idx++, JsonTools.jsonToItemStack(element.getAsJsonObject()));
            }
            detect();
        }
    }
}
