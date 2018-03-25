package mcjty.questutils.blocks.itemcomparator;

import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.ImageChoiceLabel;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.network.Argument;
import mcjty.lib.varia.RedstoneMode;
import mcjty.questutils.QuestUtils;
import mcjty.questutils.blocks.QUTileEntity;
import mcjty.questutils.network.QuestUtilsMessages;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class ItemComparatorGui extends GenericGuiContainer<ItemComparatorTE> {

    public static final int WIDTH = 183;
    public static final int HEIGHT = 238;

    private ImageChoiceLabel redstoneMode;
    private TextField idField;

    private static final ResourceLocation iconLocation = new ResourceLocation(QuestUtils.MODID, "textures/gui/item_comparator.png");
    private static final ResourceLocation iconGuiElements = new ResourceLocation(QuestUtils.MODID, "textures/gui/guielements.png");

    public ItemComparatorGui(ItemComparatorTE tileEntity, ItemComparatorContainer container) {
        super(QuestUtils.instance, QuestUtilsMessages.INSTANCE, tileEntity, container, 0, "item_comparator");

        xSize = WIDTH;
        ySize = HEIGHT;
    }

    private void initRedstoneMode() {
        redstoneMode = new ImageChoiceLabel(mc, this).
                addChoiceEvent((parent, newChoice) -> changeRedstoneMode()).
                addChoice(RedstoneMode.REDSTONE_IGNORED.getDescription(), "Redstone mode:\nIgnored", iconGuiElements, 0, 0).
                addChoice(RedstoneMode.REDSTONE_OFFREQUIRED.getDescription(), "Redstone mode:\nOff to activate", iconGuiElements, 16, 0).
                addChoice(RedstoneMode.REDSTONE_ONREQUIRED.getDescription(), "Redstone mode:\nOn to activate", iconGuiElements, 32, 0);
        redstoneMode.setLayoutHint(new PositionalLayout.PositionalHint(154, 46, 16, 16));
        redstoneMode.setCurrentChoice(tileEntity.getRSMode().ordinal());
    }

    private void changeRedstoneMode() {
        tileEntity.setRSMode(RedstoneMode.values()[redstoneMode.getCurrentChoiceIndex()]);
        sendServerCommand(QuestUtilsMessages.INSTANCE, ItemComparatorTE.CMD_RSMODE, new Argument("rs", RedstoneMode.values()[redstoneMode.getCurrentChoiceIndex()].getDescription()));
    }

    @Override
    public void initGui() {
        super.initGui();

        initRedstoneMode();

        idField = new TextField(mc, this)
                .setLayoutHint(new PositionalLayout.PositionalHint(30, 6, 143, 14));
        idField.setText(tileEntity.getIdentifier() == null ? "" : tileEntity.getIdentifier());
        idField.addTextEvent((parent, newText) -> {
            updateId();
        });

        Panel toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout())
                .addChild(new Label<>(mc, this).setText("ID").setLayoutHint(new PositionalLayout.PositionalHint(12, 6, 16, 14)).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT))
                .addChild(idField)
                .addChild(new Label<>(mc, this).setText("Filter").setLayoutHint(new PositionalLayout.PositionalHint(12, 22, 18*4, 14)).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT))
                .addChild(new Label<>(mc, this).setText("Buffer").setLayoutHint(new PositionalLayout.PositionalHint(102, 22, 18*4, 14)).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT));
//                .addChild(redstoneMode);
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
    }

    private void updateId() {
        tileEntity.setIdentifier(idField.getText());
        sendServerCommand(QuestUtilsMessages.INSTANCE, QUTileEntity.CMD_SETID, new Argument("id", idField.getText()));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        drawWindow();
    }
}
