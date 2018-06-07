package mcjty.questutils.integration.computers;

import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import mcjty.lib.integration.computers.AbstractOCDriver;
import mcjty.questutils.api.TextAlignment;
import mcjty.questutils.blocks.screen.ScreenContainer;
import mcjty.questutils.blocks.screen.ScreenTE;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
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

            @Callback(doc = "function():string; Return the identifier of this block")
            public Object[] getIdentifier(Context c, Arguments a) {
                return new Object[]{tile.getIdentifier()};
            }

            @Callback(doc = "function(string,integer,integer); Set the title (text, alignment, color")
            public Object[] setTitle(Context c, Arguments a) {
                String newVal = a.checkString(0);
                int align = a.checkInteger(1);
                int color = a.checkInteger(2);
                tile.setTitle(newVal, TextAlignment.values()[align], color);
                return new Object[]{true};
            }

            @Callback(doc = "function(integer,string,integer,integer); Set a status line (index, text, alignment, color)")
            public Object[] setStatus(Context c, Arguments a) {
                Integer index = a.checkInteger(0);
                String newVal = a.checkString(1);
                int align = a.checkInteger(2);
                int color = a.checkInteger(3);
                tile.setStatus(index, newVal, TextAlignment.values()[align], color);
                return new Object[]{true};
            }

            @Callback(doc = "function(integer); Set the border color (for the objective item/icon)")
            public Object[] setBorderColor(Context c, Arguments a) {
                Integer color = a.checkInteger(0);
                tile.setBorderColor(color);
                return new Object[]{true};
            }

            @Callback(doc = "function(integer); Set the screen color")
            public Object[] setColor(Context c, Arguments a) {
                Integer color = a.checkInteger(0);
                tile.setBackgroundColor(color);
                return new Object[]{true};
            }

            @Callback(doc = "function(boolean); Set the screen transparency")
            public Object[] setTransparency(Context c, Arguments a) {
                Boolean trans = a.checkBoolean(0);
                tile.setTransparent(trans);
                return new Object[]{true};
            }

            @Callback(doc = "function(integer,itemstack); Set the objective item to show on the screen")
            public Object[] setObjectiveItem(Context c, Arguments a) {
                Integer index = a.checkInteger(0);
                ItemStack newVal = a.checkItemStack(1);
                tile.setInventorySlotContents(ScreenContainer.SLOT_ITEM + index, newVal);
                return new Object[]{true};
            }

            @Callback(doc = "function(string,string); Set the objective icon to show on the screen")
            public Object[] setObjectiveIcon(Context c, Arguments a) {
                String icon = a.checkString(0);
                String filename = a.checkString(1);
                tile.setIcon(icon == null || icon.trim().isEmpty() ? null : new ResourceLocation(icon), filename);
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
