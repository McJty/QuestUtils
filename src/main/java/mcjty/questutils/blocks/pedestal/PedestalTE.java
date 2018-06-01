package mcjty.questutils.blocks.pedestal;

import com.google.gson.JsonObject;
import mcjty.lib.bindings.DefaultValue;
import mcjty.lib.bindings.IValue;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.questutils.blocks.QUTileEntity;
import mcjty.questutils.json.JsonTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

public class PedestalTE extends QUTileEntity implements DefaultSidedInventory {

    private InventoryHelper inventoryHelper = new InventoryHelper(this, PedestalContainer.factory, 1);
    private static int[] slots = null;
    private boolean inAlarm = false;

    private PedestalMode mode = PedestalMode.MODE_DISPLAY;

    public static final Key<Integer> VALUE_MODE = new Key<>("mode", Type.INTEGER);

    @Override
    public IValue<?>[] getValues() {
        return new IValue[] {
                new DefaultValue<>(VALUE_MODE, () -> this.getMode().ordinal(), (v) -> this.setMode(PedestalMode.values()[v])),
        };
    }

    public void interactItem(EntityPlayer player, EnumHand hand) {
        if (player.getHeldItem(hand).isEmpty()) {
            takeItem(player, hand);
        } else {
            placeItem(player, hand);
        }
    }

    public void placeItem(EntityPlayer player, EnumHand hand) {
        if (player.getHeldItem(hand).isEmpty()) {
            return;
        }
        ItemStack remaining = InventoryHelper.insertItem(world, pos, null, player.getHeldItem(hand));
        player.setHeldItem(hand, remaining);
    }

    public void takeItem(EntityPlayer player, EnumHand hand) {
        ItemStack stack = getStackInSlot(PedestalContainer.SLOT_ITEM);
        if (stack.isEmpty()) {
            return;
        }
        if (player.getHeldItem(hand).isEmpty()) {
            player.setHeldItem(hand, stack);
            setInventorySlotContents(PedestalContainer.SLOT_ITEM, ItemStack.EMPTY);
        } else if (player.inventory.addItemStackToInventory(stack)) {
            setInventorySlotContents(PedestalContainer.SLOT_ITEM, ItemStack.EMPTY);
        }
    }

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

    public void detect() {
        setAlarm(!getStackInSlot(PedestalContainer.SLOT_ITEM).isEmpty());
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

    public PedestalMode getMode() {
        return mode;
    }

    public void setMode(PedestalMode mode) {
        this.mode = mode;
        markDirtyClient();
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound compound) {
        super.readRestorableFromNBT(compound);
        readBufferFromNBT(compound, inventoryHelper);
        mode = PedestalMode.values()[compound.getInteger("mode")];
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound compound) {
        super.writeRestorableToNBT(compound);
        writeBufferToNBT(compound, inventoryHelper);
        compound.setInteger("mode", mode.ordinal());
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
            detect();
        }
    }
}
