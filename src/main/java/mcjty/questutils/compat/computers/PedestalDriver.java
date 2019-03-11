package mcjty.questutils.compat.computers;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import mcjty.lib.integration.computers.AbstractOCDriver;
import mcjty.questutils.blocks.pedestal.PedestalMode;
import mcjty.questutils.blocks.pedestal.PedestalTE;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class PedestalDriver {
    public static class OCDriver extends AbstractOCDriver {
        public OCDriver() {
            super("questutils_pedestal", PedestalTE.class);
        }

        public static class InternalManagedEnvironment extends AbstractOCDriver.InternalManagedEnvironment<PedestalTE> {
            public InternalManagedEnvironment(PedestalTE tile) {
                super(tile, "questutils_pedestal");
            }

            @Callback(doc = "function():string; Return the identifier of this block")
            public Object[] getIdentifier(Context c, Arguments a) {
                return new Object[]{tile.getIdentifier()};
            }

            @Callback(doc = "function(string); Set the mode for this pedestal ('display', 'interact', 'take', or 'place')")
            public Object[] setMode(Context c, Arguments a) {
                String mode = a.checkString(0);
                tile.setMode(PedestalMode.getModeByName(mode));
                return new Object[]{true};
            }

            @Override
            public int priority() {
                return 4;
            }
        }

        @Override
        public AbstractManagedEnvironment createEnvironment(World world, BlockPos pos, EnumFacing side, TileEntity tile) {
            return new InternalManagedEnvironment((PedestalTE) tile);
        }
    }
}
