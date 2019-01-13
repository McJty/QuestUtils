package mcjty.questutils.blocks.itemcomparator;

import com.google.gson.JsonObject;
import mcjty.lib.bindings.DefaultAction;
import mcjty.lib.bindings.DefaultValue;
import mcjty.lib.bindings.IAction;
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
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;

public class ItemComparatorTE extends QUTileEntity implements DefaultSidedInventory {

    private static int[] slots = null;
    private boolean inAlarm = false;
    private boolean ignoreMeta = false;
    private boolean ignoreNBT = false;

    private InventoryHelper inventoryHelper = new InventoryHelper(this, ItemComparatorContainer.factory, 32);
    private InventoryHelper ghostSlots = new InventoryHelper(this, ItemComparatorContainer.factory, 16);

    public static final String ACTION_REMEMBER = "remember";
    public static final String ACTION_FORGET = "forget";

    public static final Key<Boolean> VALUE_IGNORE_NBT = new Key<>("ignoreNBT", Type.BOOLEAN);
    public static final Key<Boolean> VALUE_IGNORE_META = new Key<>("ignoreMeta", Type.BOOLEAN);

    @Override
    public IValue<?>[] getValues() {
        return new IValue[] {
                new DefaultValue<>(VALUE_ID, this::getIdentifier, this::setIdentifier),
                new DefaultValue<>(VALUE_IGNORE_META, this::isIgnoreMeta, this::setIgnoreMeta),
                new DefaultValue<>(VALUE_IGNORE_NBT, this::isIgnoreNBT, this::setIgnoreNBT)
        };
    }

    public boolean isIgnoreMeta() {
        return ignoreMeta;
    }

    public void setIgnoreMeta(boolean ignoreMeta) {
        this.ignoreMeta = ignoreMeta;
        markDirtyClient();
        detect();
    }

    public boolean isIgnoreNBT() {
        return ignoreNBT;
    }

    public void setIgnoreNBT(boolean ignoreNBT) {
        this.ignoreNBT = ignoreNBT;
        markDirtyClient();
        detect();
    }

    @Override
    protected boolean needsCustomInvWrapper() {
        return true;
    }

    @Override
    public IAction[] getActions() {
        return new IAction[] {
                new DefaultAction(ACTION_REMEMBER, this::rememberItems),
                new DefaultAction(ACTION_FORGET, this::forgetItems)
        };
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

    private boolean match(ItemStack matcher, ItemStack item) {
        if (ignoreMeta) {
            if (matcher.getItem() != item.getItem()) {
                return false;
            }
        } else {
            if (!ItemStack.areItemsEqual(matcher, item)) {
                return false;
            }
        }
        if (ignoreNBT) {
            return true;
        } else {
            return ItemStack.areItemStackTagsEqual(matcher, item);
        }
    }

    private int countMissing(ItemStack matcher, int[] amounts) {
        int toFind = matcher.getCount();
        for (int i = 0 ; i < 16 ; i++) {
            ItemStack item = getStackInSlot(i+16);
            if (!item.isEmpty()) {
                if (match(matcher, item)) {
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
                if (match(matcher, item)) {
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

    public InventoryHelper getGhostSlots() {
        return ghostSlots;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        if (!isItemValidForSlot(index, itemStackIn)) {
            return false;
        }
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
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index >= 16 && index < 16+16) {
            ItemStack ghostSlot = ghostSlots.getStackInSlot(index - 16);
            if (!ghostSlot.isEmpty()) {
                if (!ghostSlot.isItemEqual(stack)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void rememberItems() {
        for (int i = 0 ; i < ghostSlots.getCount() ; i++) {
            int slotIdx = i + 16;
            if (inventoryHelper.containsItem(slotIdx)) {
                ItemStack stack = inventoryHelper.getStackInSlot(slotIdx).copy();
                stack.setCount(1);
                ghostSlots.setStackInSlot(i, stack);
            }
        }
        markDirtyClient();
    }

    private void forgetItems() {
        for (int i = 0 ; i < ghostSlots.getCount() ; i++) {
            ghostSlots.setStackInSlot(i, ItemStack.EMPTY);
        }
        markDirtyClient();
    }


    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        readBufferFromNBT(tagCompound, inventoryHelper);
        readGhostBufferFromNBT(tagCompound);
        ignoreMeta = tagCompound.getBoolean("ignoreMeta");
        ignoreNBT = tagCompound.getBoolean("ignoreNBT");
    }

    private void readGhostBufferFromNBT(NBTTagCompound tagCompound) {
        NBTTagList bufferTagList = tagCompound.getTagList("GItems", Constants.NBT.TAG_COMPOUND);
        for (int i = 0 ; i < bufferTagList.tagCount() ; i++) {
            NBTTagCompound nbtTagCompound = bufferTagList.getCompoundTagAt(i);
            ghostSlots.setStackInSlot(i, new ItemStack(nbtTagCompound));
        }
    }


    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        writeBufferToNBT(tagCompound, inventoryHelper);
        writeGhostBufferToNBT(tagCompound);
        tagCompound.setBoolean("ignoreMeta", ignoreMeta);
        tagCompound.setBoolean("ignoreNBT", ignoreNBT);
    }

    private void writeGhostBufferToNBT(NBTTagCompound tagCompound) {
        NBTTagList bufferTagList = new NBTTagList();
        for (int i = 0 ; i < ghostSlots.getCount() ; i++) {
            ItemStack stack = ghostSlots.getStackInSlot(i);
            NBTTagCompound nbtTagCompound = new NBTTagCompound();
            if (!stack.isEmpty()) {
                stack.writeToNBT(nbtTagCompound);
            }
            bufferTagList.appendTag(nbtTagCompound);
        }
        tagCompound.setTag("GItems", bufferTagList);
    }


    @Override
    public void writeToJson(JsonObject object) {
        super.writeToJson(object);
        if (hasIdentifier()) {
            object.add("filter", JsonTools.writeItemsToJson(getInventoryHelper(), 0, 16));
            object.add("ghost", JsonTools.writeItemsToJson(ghostSlots, 0, 16));
        }
    }

    @Override
    public void readFromJson(JsonObject object) {
        super.readFromJson(object);
        if (object.has("filter")) {
            JsonTools.readItemsFromJson(object.getAsJsonArray("filter"), getInventoryHelper(), 0, 16);
            detect();
        }
        if (object.has("ghost")) {
            JsonTools.readItemsFromJson(object.getAsJsonArray("ghost"), ghostSlots, 0, 16);
        }
    }
}
