package mcjty.questutils.blocks.itemcomparator;

import mcjty.lib.container.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ItemComparatorContainer extends GenericContainer {
    public static final String CONTAINER_INVENTORY = "container";

    public static final int SLOT_MATCHER = 0;
    public static final int SLOT_INPUT = 4*4;

    public static final ContainerFactory factory = new ContainerFactory() {
        @Override
        protected void setup() {
            addSlotBox(new SlotDefinition(SlotType.SLOT_CONTAINER), CONTAINER_INVENTORY, SLOT_MATCHER, 12, 37, 4, 18, 4, 18);
            addSlotBox(new SlotDefinition(SlotType.SLOT_CONTAINER), CONTAINER_INVENTORY, SLOT_INPUT, 102, 37, 4, 18, 4, 18);
            layoutPlayerInventorySlots(12, 142);
        }
    };

    public ItemComparatorContainer(EntityPlayer player, IInventory containerInventory) {
        super(factory);
        addInventory(CONTAINER_INVENTORY, containerInventory);
        addInventory(ContainerFactory.CONTAINER_PLAYER, player.inventory);
        generateSlots();
    }

    @Override
    protected Slot createSlot(SlotFactory slotFactory, IInventory inventory, int index, int x, int y, SlotType slotType) {
        if (index >= 16 && index < 16+16) {
            return new BaseSlot(inventory, index, x, y) {
                @Override
                public boolean isItemValid(ItemStack stack) {
                    if (!inventory.isItemValidForSlot(getSlotIndex(), stack)) {
                        return false;
                    }
                    return super.isItemValid(stack);
                }
            };
        } else {
            return super.createSlot(slotFactory, inventory, index, x, y, slotType);
        }
    }

    @Override
    public ItemStack slotClick(int index, int button, ClickType mode, EntityPlayer player) {
        ItemStack stack = super.slotClick(index, button, mode, player);
        ((ItemComparatorTE)getInventory(CONTAINER_INVENTORY)).detect();
        return stack;
    }
}
