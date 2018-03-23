package mcjty.questutils.blocks;

import mcjty.questutils.blocks.itemcomparator.ItemComparatorBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static ItemComparatorBlock itemComparatorBlock;

    public static void init() {
        itemComparatorBlock = new ItemComparatorBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        itemComparatorBlock.initModel();
    }
}
