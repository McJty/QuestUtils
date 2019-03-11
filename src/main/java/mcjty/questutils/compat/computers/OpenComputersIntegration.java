package mcjty.questutils.compat.computers;

import li.cil.oc.api.Driver;
import net.minecraftforge.fml.common.Optional;

public class OpenComputersIntegration {
    @Optional.Method(modid="opencomputers")
    public static void init() {
        Driver.add(new ItemComparatorDriver.OCDriver());
        Driver.add(new ScreenDriver.OCDriver());
        Driver.add(new PedestalDriver.OCDriver());
    }
}
