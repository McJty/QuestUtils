package mcjty.questutils.integration.computers;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import mcjty.lib.integration.computers.AbstractOCDriver;
import mcjty.questutils.blocks.screen.ScreenContainer;
import mcjty.questutils.blocks.screen.ScreenTE;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class ScreenDriver {
    public static class OCDriver extends AbstractOCDriver {
        public OCDriver() {
            super("questutils_screen", ScreenTE.class);
        }

        public static class InternalManagedEnvironment extends AbstractOCDriver.InternalManagedEnvironment<ScreenTE> {
            public InternalManagedEnvironment(ScreenTE tile) {
                super(tile, "questutils_screen");
            }

            @Callback(doc = "function(string); Set the title")
            public Object[] setTitle(Context c, Arguments a) {
                String newVal = a.checkString(0);
                tile.setTitle(newVal);
                return new Object[]{true};
            }

            @Callback(doc = "function(integer,string); Set a status line")
            public Object[] setStatus(Context c, Arguments a) {
                Integer index = a.checkInteger(0);
                String newVal = a.checkString(1);
                tile.setStatus(index, newVal);
                return new Object[]{true};
            }

            @Callback(doc = "function(itemstack); Set the objective item to show on the screen")
            public Object[] setObjectiveItem(Context c, Arguments a) {
                ItemStack newVal = a.checkItemStack(0);
                tile.setInventorySlotContents(ScreenContainer.SLOT_ITEM, newVal);
                return new Object[]{true};
            }

            @Override
            public int priority() {
                return 4;
            }
        }

        @Override
        public AbstractManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side, TileEntity tile) {
            return new InternalManagedEnvironment((ScreenTE) tile);
        }
    }
}
