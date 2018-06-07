package mcjty.questutils.blocks.screen;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mcjty.lib.bindings.DefaultAction;
import mcjty.lib.bindings.IAction;
import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.lib.typed.TypedMap;
import mcjty.questutils.api.FormattedString;
import mcjty.questutils.api.IScreen;
import mcjty.questutils.api.ScreenSize;
import mcjty.questutils.api.TextAlignment;
import mcjty.questutils.blocks.QUTileEntity;
import mcjty.questutils.json.JsonTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ScreenTE extends QUTileEntity implements DefaultSidedInventory, IScreen {

    public static final String ACTION_CLICK = "click";
    public static final String ACTION_HOVER = "hover";

    public static final String CMD_UPDATE = "screen.cmdUpdate";
    public static final Key<Integer> PARAM_COLOR = new Key<>("color", Type.INTEGER);
    public static final Key<Integer> PARAM_SCREEN = new Key<>("screen", Type.INTEGER);
    public static final Key<Integer> PARAM_SIZE = new Key<>("size", Type.INTEGER);
    public static final Key<String> PARAM_ICON = new Key<>("icon", Type.STRING);
    public static final Key<String> PARAM_FILE = new Key<>("file", Type.STRING);
    public static final Key<Boolean> PARAM_TRANSP = new Key<>("transp", Type.BOOLEAN);

    public static final String CMD_UPDATE_STRING = "screen.cmdUpdateString";
    public static final Key<String> PARAM_TITLE = new Key<>("title", Type.STRING);
    public static final Key<Integer> PARAM_TITLE_A = new Key<>("titleA", Type.INTEGER);
    public static final Key<Integer> PARAM_TITLE_C = new Key<>("titleC", Type.INTEGER);
    public static final Key<String> PARAM_STATUS0 = new Key<>("status0", Type.STRING);
    public static final Key<Integer> PARAM_STATUS0_A = new Key<>("status0A", Type.INTEGER);
    public static final Key<Integer> PARAM_STATUS0_C = new Key<>("status0C", Type.INTEGER);
    public static final Key<String> PARAM_STATUS1 = new Key<>("status1", Type.STRING);
    public static final Key<Integer> PARAM_STATUS1_A = new Key<>("status1A", Type.INTEGER);
    public static final Key<Integer> PARAM_STATUS1_C = new Key<>("status1C", Type.INTEGER);
    public static final Key<String> PARAM_STATUS2 = new Key<>("status2", Type.STRING);
    public static final Key<Integer> PARAM_STATUS2_A = new Key<>("status2A", Type.INTEGER);
    public static final Key<Integer> PARAM_STATUS2_C = new Key<>("status2C", Type.INTEGER);

    @Override
    public IAction[] getActions() {
        return new IAction[] {
                new DefaultAction(ACTION_CLICK, () -> {}),
                new DefaultAction(ACTION_HOVER, () -> {}),
        };
    }

    private InventoryHelper inventoryHelper = new InventoryHelper(this, ScreenContainer.factory, 9);
    private FormattedString title;
    private FormattedString[] status = new FormattedString[3];
    private ResourceLocation icon;
    private String filename;

    private ScreenSize size = ScreenSize.NORMAL;
    private boolean transparent = false;    // Transparent screen.
    private int color = 0;                  // Color of the screen.
    private int borderColor = EnumDyeColor.BROWN.getColorValue();

    private int trueTypeMode = 0;           // 0 is default, -1 is disabled, 1 is truetype

    public ScreenTE() {
    }

    @Override
    protected boolean needsCustomInvWrapper() {
        return true;
    }

    @Override
    public String getID() {
        return getIdentifier();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        int xCoord = getPos().getX();
        int yCoord = getPos().getY();
        int zCoord = getPos().getZ();
        int s = size.ordinal();
        return new AxisAlignedBB(xCoord - s - 1, yCoord - s - 1, zCoord - s - 1, xCoord + s + 1, yCoord + s + 1, zCoord + s + 1); // TODO see if we can shrink this
    }

    @Override
    public ResourceLocation getIcon() {
        return icon;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public void setIcon(ResourceLocation icon, String filename) {
        this.icon = icon;
        this.filename = ((filename == null) || filename.trim().isEmpty()) ? null : filename;
        markDirtyClient();
    }

    @Override
    public void setTitle(String title, TextAlignment alignment, int color) {
        this.title = ((title == null) || title.trim().isEmpty()) ? null : new FormattedString(title, alignment, color);
        markDirtyClient();
    }

    @Override
    public void setTitle(FormattedString title) {
        this.title = title;
        markDirtyClient();
    }

    @Override
    public void setStatus(int idx, String status, TextAlignment alignment, int color) {
        this.status[idx] = ((status == null) || status.trim().isEmpty()) ? null : new FormattedString(status, alignment, color);
        markDirtyClient();
    }

    @Override
    public void setStatus(int idx, FormattedString status) {
        this.status[idx] = status;
        markDirtyClient();
    }

    @Override
    public FormattedString getTitle() {
        return title;
    }

    @Override
    public FormattedString[] getStatus() {
        return status;
    }

    private void checkStateClient() {
    }

    private void checkStateServer() {
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return ScreenContainer.factory.getAccessibleSlots();
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return ScreenContainer.factory.isOutputSlot(index);
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return ScreenContainer.factory.isInputSlot(index);
    }

    @Override
    public int getSizeInventory() {
        return inventoryHelper.getCount();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventoryHelper.getStackInSlot(index);
    }

    @Override
    public InventoryHelper getInventoryHelper() {
        return inventoryHelper;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return canPlayerAccess(player);
    }

    @Override
    public IItemHandler getItemHandler() {
        return getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        readBufferFromNBT(tagCompound, inventoryHelper);
        size = ScreenSize.values()[tagCompound.getInteger("size")];
        transparent = tagCompound.getBoolean("transparent");
        color = tagCompound.getInteger("color");
        borderColor = tagCompound.getInteger("borderColor");
        trueTypeMode = tagCompound.getInteger("truetype");
        title = getFormattedStringFromNBT(tagCompound, "title");
        status[0] = getFormattedStringFromNBT(tagCompound, "stat0");
        status[1] = getFormattedStringFromNBT(tagCompound, "stat1");
        status[2] = getFormattedStringFromNBT(tagCompound, "stat2");
        String iconString = tagCompound.getString("icon");
        if (iconString.trim().isEmpty()) {
            icon = null;
        } else {
            icon = new ResourceLocation(iconString);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        return tagCompound;
    }

    @Override
    public void writeRestorableToNBT(NBTTagCompound tagCompound) {
        super.writeRestorableToNBT(tagCompound);
        writeBufferToNBT(tagCompound, inventoryHelper);
        tagCompound.setInteger("size", size.ordinal());
        tagCompound.setBoolean("transparent", transparent);
        tagCompound.setInteger("color", color);
        tagCompound.setInteger("borderColor", borderColor);
        tagCompound.setInteger("truetype", trueTypeMode);
        writeFormattedStringToNBT(tagCompound, "title", title);
        writeFormattedStringToNBT(tagCompound, "stat0", status[0]);
        writeFormattedStringToNBT(tagCompound, "stat1", status[1]);
        writeFormattedStringToNBT(tagCompound, "stat2", status[2]);
        if (icon != null) {
            tagCompound.setString("icon", icon.toString());
        }
    }

    private FormattedString getFormattedStringFromNBT(NBTTagCompound tagCompound, String prefix) {
        if (tagCompound.hasKey(prefix)) {
            return new FormattedString(tagCompound.getString(prefix),
                    TextAlignment.values()[tagCompound.getByte(prefix + "A")],
                    tagCompound.getInteger(prefix + "C"));
        } else {
            return null;
        }
    }

    private void writeFormattedStringToNBT(NBTTagCompound tagCompound, String prefix, FormattedString string) {
        if (string != null) {
            tagCompound.setString(prefix, string.getText());
            tagCompound.setByte(prefix+"A", (byte) string.getAlignment().ordinal());
            tagCompound.setInteger(prefix+"C", string.getColor());
        }
    }

    @Override
    public int getBorderColor() {
        return borderColor;
    }

    @Override
    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        markDirtyClient();
    }

    @Override
    public int getBackgroundColor() {
        return color;
    }

    @Override
    public void setBackgroundColor(int color) {
        this.color = color;
        markDirtyClient();
    }

    @Override
    public void setSize(ScreenSize size) {
        this.size = size;
        markDirtyClient();
    }

    public int getTrueTypeMode() {
        return trueTypeMode;
    }

    public void setTrueTypeMode(int trueTypeMode) {
        this.trueTypeMode = trueTypeMode;
        markDirtyClient();
    }

    @Override
    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
        markDirtyClient();
    }

    @Override
    public ScreenSize getSize() {
        return size;
    }

    @Override
    public boolean isTransparent() {
        return transparent;
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public World getScreenWorld() {
        return world;
    }

    @Override
    public boolean execute(EntityPlayerMP playerMP, String command, TypedMap params) {
        boolean rc = super.execute(playerMP, command, params);
        if (rc) {
            return true;
        }
        if (CMD_UPDATE.equals(command)) {
            Integer color = params.get(PARAM_COLOR);
            setBorderColor(color);
            Integer screen = params.get(PARAM_SCREEN);
            setBackgroundColor(screen);
            String iconString = params.get(PARAM_ICON);
            String fileName = params.get(PARAM_FILE);
            boolean transp = params.get(PARAM_TRANSP);
            setTransparent(transp);
            int s = params.get(PARAM_SIZE);
            setSize(ScreenSize.values()[s]);
            setIcon(iconString.trim().isEmpty() ? null : new ResourceLocation(iconString),
                    fileName.trim().isEmpty() ? null : fileName);
            markDirtyClient();
            return true;
        } else if (CMD_UPDATE_STRING.equals(command)) {
            if (params.get(PARAM_TITLE) != null) {
                setTitle(params.get(PARAM_TITLE), TextAlignment.values()[params.get(PARAM_TITLE_A)], params.get(PARAM_TITLE_C));
            }
            if (params.get(PARAM_STATUS0) != null) {
                setStatus(0, params.get(PARAM_STATUS0), TextAlignment.values()[params.get(PARAM_STATUS0_A)], params.get(PARAM_STATUS0_C));
            }
            if (params.get(PARAM_STATUS1) != null) {
                setStatus(1, params.get(PARAM_STATUS1), TextAlignment.values()[params.get(PARAM_STATUS1_A)], params.get(PARAM_STATUS1_C));
            }
            if (params.get(PARAM_STATUS2) != null) {
                setStatus(2, params.get(PARAM_STATUS2), TextAlignment.values()[params.get(PARAM_STATUS2_A)], params.get(PARAM_STATUS2_C));
            }
            markDirtyClient();
            return true;
        }
        return false;
    }

    @Override
    public void writeToJson(JsonObject object) {
        super.writeToJson(object);
        if (hasIdentifier()) {
            object.add("color", new JsonPrimitive(color));
            object.add("borderColor", new JsonPrimitive(borderColor));
            if (title != null) {
                object.add("title", writeFormattedStringToJson(title));
            }
            if (status[0] != null) {
                object.add("status0", writeFormattedStringToJson(status[0]));
            }
            if (status[1] != null) {
                object.add("status1", writeFormattedStringToJson(status[1]));
            }
            if (status[2] != null) {
                object.add("status2", writeFormattedStringToJson(status[2]));
            }
            if (icon != null) {
                object.add("icon", new JsonPrimitive(icon.toString()));
            }
            object.add("items", JsonTools.writeItemsToJson(getInventoryHelper(), ScreenContainer.SLOT_ITEM, ScreenContainer.SLOT_ITEM+9));
        }
    }

    @Override
    public void readFromJson(JsonObject object) {
        super.readFromJson(object);
        if (object.has("color")) {
            color = object.get("color").getAsInt();
        }
        if (object.has("borderColor")) {
            borderColor = object.get("borderColor").getAsInt();
        }
        title = getFormattedStringFromJson(object, "title");
        status[0] = getFormattedStringFromJson(object, "status0");
        status[1] = getFormattedStringFromJson(object, "status1");
        status[2] = getFormattedStringFromJson(object, "status2");
        if (object.has("icon")) {
            icon = new ResourceLocation(object.get("icon").getAsString());
        }
        if (object.has("items")) {
            JsonTools.readItemsFromJson(object.getAsJsonArray("items"), getInventoryHelper(), ScreenContainer.SLOT_ITEM, ScreenContainer.SLOT_ITEM + 9);
        }
        markDirtyClient();
    }

    private JsonObject writeFormattedStringToJson(FormattedString str) {
        JsonObject obj = new JsonObject();
        obj.add("text", new JsonPrimitive(str.getText()));
        obj.add("alignment", new JsonPrimitive(str.getAlignment().ordinal()));
        obj.add("color", new JsonPrimitive(str.getColor()));
        return obj;
    }

    private FormattedString getFormattedStringFromJson(JsonObject obj, String tag) {
        if (obj.has(tag)) {
            JsonObject object = obj.get("tag").getAsJsonObject();
            return new FormattedString(object.get("text").getAsString(),
                    TextAlignment.values()[object.get("alignment").getAsInt()],
                    object.get("color").getAsInt());
        } else {
            return null;
        }
    }
}
