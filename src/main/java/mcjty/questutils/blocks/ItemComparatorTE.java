package mcjty.questutils.blocks;

import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.entity.GenericTileEntity;
import mcjty.lib.network.Argument;
import mcjty.lib.varia.RedstoneMode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import java.util.Map;

public class ItemComparatorTE extends GenericTileEntity implements DefaultSidedInventory {

    public static final String CMD_RSMODE = "rsMode";

    private static int[] slots = null;
    private boolean inAlarm = false;

    private InventoryHelper inventoryHelper = new InventoryHelper(this, ItemComparatorContainer.factory, 32);

    @Override
    protected boolean needsRedstoneMode() {
        return true;
    }

    @Override
    protected boolean needsCustomInvWrapper() {
        return true;
    }

    @Override
    public InventoryHelper getInventoryHelper() {
        return inventoryHelper;
    }

    private void detect() {
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
                    toFind -= Math.max(toFind, amounts[i]);
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
                    toFind -= Math.max(toFind, amounts[i]);
                    amounts[i] -= toFind;
                    if (toFind <= 0) {
                        return;
                    }
                }
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
}
