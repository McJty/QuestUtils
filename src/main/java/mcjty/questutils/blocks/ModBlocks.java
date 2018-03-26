package mcjty.questutils.blocks;

import mcjty.questutils.blocks.itemcomparator.ItemComparatorBlock;
import mcjty.questutils.blocks.screen.ScreenBlock;
import mcjty.questutils.blocks.screen.ScreenHitBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModBlocks {

    public static ItemComparatorBlock itemComparatorBlock;
    public static ScreenBlock screenBlock;
    public static ScreenHitBlock screenHitBlock;

    public static void init() {
        itemComparatorBlock = new ItemComparatorBlock();
        screenBlock = new ScreenBlock();
        screenHitBlock = new ScreenHitBlock();
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        itemComparatorBlock.initModel();
        screenBlock.initModel();
        screenHitBlock.initModel();
    }
}
