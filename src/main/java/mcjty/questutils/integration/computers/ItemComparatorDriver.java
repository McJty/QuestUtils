package mcjty.questutils.integration.computers;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import mcjty.lib.integration.computers.AbstractOCDriver;
import mcjty.questutils.blocks.itemcomparator.ItemComparatorTE;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class ItemComparatorDriver {
    public static class OCDriver extends AbstractOCDriver {
        public OCDriver() {
            super("questutils_item_comparator", ItemComparatorTE.class);
        }

        public static class InternalManagedEnvironment extends AbstractOCDriver.InternalManagedEnvironment<ItemComparatorTE> {
            public InternalManagedEnvironment(ItemComparatorTE tile) {
                super(tile, "questutils_item_comparator");
            }

            @Callback(doc = "function():string; Return the identifier of this block")
            public Object[] getIdentifier(Context c, Arguments a) {
                return new Object[]{tile.getIdentifier()};
            }

            @Callback(doc = "function():boolean; Return true if the buffer matches the filter")
            public Object[] matches(Context c, Arguments a) {
                return new Object[]{tile.isPowered()};
            }

            @Callback(doc = "function(); Dump the buffer to an inventory on top")
            public Object[] dump(Context c, Arguments a) {
                tile.dump();
                return new Object[]{true};
            }
            
            @Override
            public int priority() {
                return 4;
            }
        }

        @Override
        public AbstractManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side, TileEntity tile) {
            return new InternalManagedEnvironment((ItemComparatorTE) tile);
        }
    }
}
