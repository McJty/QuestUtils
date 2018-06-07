package mcjty.questutils.api;

import javax.annotation.Nullable;

/**
 * Main interface for this mod.
 * Get a reference to an implementation of this interface by calling:
 *         FMLInterModComms.sendFunctionMessage("questutils", "getQuestUtils", "<whatever>.YourClass$GetQuestUtils");
 */
public interface IQuestUtils {

    /**
     * Find a screen given an id. If the id does not exist or is not associated with a screen it will return null.
     * Note that this must be called server-side!
     * @param id
     */
    @Nullable
    IScreen findScreen(String id);
}
