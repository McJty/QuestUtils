package mcjty.questutils.api;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public interface IScreen {

    /**
     * Return the ID of this screen
     */
    String getID();

    /**
     * Return the position in the world for the top-left block of this creen
     */
    BlockPos getPosition();

    /**
     * Return the world of this screen
     */
    World getScreenWorld();

    /**
     * Get the IItemHandler that you can use to access the items in the screen.
     */
    IItemHandler getItemHandler();

    /**
     * Set an icon to display (instead of displaying items).
     * @param icon the resource location
     * @param filename if this is not null and the icon cannot be found it will try to load the texture and associate
     *                 it with the given icon. This only happens the first time
     */
    void setIcon(ResourceLocation icon, @Nullable String filename);
    ResourceLocation getIcon();
    String getFilename();

    /**
     * Set the title to display at the top of the screen.
     */
    void setTitle(String title, TextAlignment alignment, int color);
    void setTitle(FormattedString title);
    FormattedString getTitle();

    /**
     * Set one of the three status messages
     * @param idx 0, 1, or 2
     * @param status the message to display
     * @param alignment
     * @param color
     */
    void setStatus(int idx, String status, TextAlignment alignment, int color);
    void setStatus(int idx, FormattedString status);
    FormattedString[] getStatus();

    /**
     * Set the color of the border for the items
     */
    void setBorderColor(int borderColor);
    int getBorderColor();

    /**
     * Set the background color of the screen
     */
    void setBackgroundColor(int color);
    int getBackgroundColor();

    /**
     * Set the size of the screen
     */
    void setSize(ScreenSize size);
    ScreenSize getSize();

    /**
     * Enable transparency for the background
     */
    void setTransparent(boolean transparent);
    boolean isTransparent();
}
