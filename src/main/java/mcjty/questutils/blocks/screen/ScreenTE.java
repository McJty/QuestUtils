package mcjty.questutils.blocks.screen;

import mcjty.lib.container.DefaultSidedInventory;
import mcjty.lib.container.InventoryHelper;
import mcjty.lib.network.Argument;
import mcjty.questutils.blocks.QUTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

public class ScreenTE extends QUTileEntity implements ITickable, DefaultSidedInventory {

    public static final String CMD_CLICK = "click";
    public static final String CMD_HOVER = "hover";
    public static final String CMD_SETBRIGHT = "setBright";
    public static final String CMD_SETTRUETYPE = "setTruetype";
    public static final String CMD_UPDATE = "cmdUpdate";

    private InventoryHelper inventoryHelper = new InventoryHelper(this, ScreenContainer.factory, 1);
    private String title;
    private String[] status = new String[3];

    private int size = 0;                   // Size of screen (0 is normal, 1 is large, 2 is huge)
    private boolean transparent = false;    // Transparent screen.
    private int color = 0;                  // Color of the screen.
    private boolean bright = false;         // True if the screen contents is full bright

    private int trueTypeMode = 0;           // 0 is default, -1 is disabled, 1 is truetype

    public static final int SIZE_NORMAL = 0;
    public static final int SIZE_LARGE = 1;
    public static final int SIZE_HUGE = 2;
    public static final int SIZE_ENOURMOUS = 3;
    public static final int SIZE_GIGANTIC = 4;

    public ScreenTE() {
    }

    @Override
    protected boolean needsCustomInvWrapper() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        int xCoord = getPos().getX();
        int yCoord = getPos().getY();
        int zCoord = getPos().getZ();
        return new AxisAlignedBB(xCoord - size - 1, yCoord - size - 1, zCoord - size - 1, xCoord + size + 1, yCoord + size + 1, zCoord + size + 1); // TODO see if we can shrink this
    }

    @Override
    public void update() {
        if (getWorld().isRemote) {
            checkStateClient();
        } else {
            checkStateServer();
        }
    }

    public void setTitle(String title) {
        this.title = title;
        markDirtyClient();
    }

    public void setStatus(int idx, String status) {
        this.status[idx] = status;
        markDirtyClient();
    }

    public String getTitle() {
        return title;
    }

    public String[] getStatus() {
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
        return 1;
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
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
    }

    @Override
    public void readRestorableFromNBT(NBTTagCompound tagCompound) {
        super.readRestorableFromNBT(tagCompound);
        readBufferFromNBT(tagCompound, inventoryHelper);
        size = tagCompound.getInteger("size");
        transparent = tagCompound.getBoolean("transparent");
        color = tagCompound.getInteger("color");
        bright = tagCompound.getBoolean("bright");
        trueTypeMode = tagCompound.getInteger("truetype");
        title = tagCompound.getString("title");
        status[0] = tagCompound.getString("status0");
        status[1] = tagCompound.getString("status1");
        status[2] = tagCompound.getString("status2");
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
        tagCompound.setInteger("size", size);
        tagCompound.setBoolean("transparent", transparent);
        tagCompound.setInteger("color", color);
        tagCompound.setBoolean("bright", bright);
        tagCompound.setInteger("truetype", trueTypeMode);
        if (title != null) {
            tagCompound.setString("title", title);
        }
        if (status[0] != null) {
            tagCompound.setString("status0", status[0]);
        }
        if (status[1] != null) {
            tagCompound.setString("status1", status[1]);
        }
        if (status[2] != null) {
            tagCompound.setString("status2", status[2]);
        }
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        markDirtyClient();
    }

    public void setSize(int size) {
        this.size = size;
        markDirtyClient();
    }

    public boolean isBright() {
        return bright;
    }

    public void setBright(boolean bright) {
        this.bright = bright;
        markDirtyClient();
    }

    public int getTrueTypeMode() {
        return trueTypeMode;
    }

    public void setTrueTypeMode(int trueTypeMode) {
        this.trueTypeMode = trueTypeMode;
        markDirtyClient();
    }

    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
        markDirtyClient();
    }

    public int getSize() {
        return size;
    }

    public boolean isTransparent() {
        return transparent;
    }

    @Override
    public boolean execute(EntityPlayerMP playerMP, String command, Map<String, Argument> args) {
        boolean rc = super.execute(playerMP, command, args);
        if (rc) {
            return true;
        }
        if (CMD_UPDATE.equals(command)) {
            title = args.get("title").getString();
            status[0] = args.get("status0").getString();
            status[1] = args.get("status1").getString();
            status[2] = args.get("status2").getString();
            markDirtyClient();
            return true;
        } else if (CMD_CLICK.equals(command)) {
//            int x = args.get("x").getInteger();
//            int y = args.get("y").getInteger();
//            int module = args.get("module").getInteger();
            return true;
        } else if (CMD_HOVER.equals(command)) {
            return true;
        } else if (CMD_SETBRIGHT.equals(command)) {
            boolean b = args.get("b").getBoolean();
            setBright(b);
            return true;
        } else if (CMD_SETTRUETYPE.equals(command)) {
            int b = args.get("b").getInteger();
            setTrueTypeMode(b);
            return true;
        }
        return false;
    }
}
