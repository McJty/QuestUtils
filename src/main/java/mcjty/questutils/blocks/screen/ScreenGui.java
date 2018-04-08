package mcjty.questutils.blocks.screen;

import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.network.Argument;
import mcjty.questutils.QuestUtils;
import mcjty.questutils.blocks.QUTileEntity;
import mcjty.questutils.network.QuestUtilsMessages;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;

import java.awt.Rectangle;

public class ScreenGui extends GenericGuiContainer<ScreenTE> {

    public static final int WIDTH = 243;
    public static final int HEIGHT = 238;

    private TextField idField;
    private TextField iconField;
    private TextField fileField;
    private ColorChoiceLabel borderColor;
    private ColorChoiceLabel screenColor;
    private ToggleButton transp;

    private static final ResourceLocation iconLocation = new ResourceLocation(QuestUtils.MODID, "textures/gui/screen.png");
    private static final ResourceLocation iconGuiElements = new ResourceLocation(QuestUtils.MODID, "textures/gui/guielements.png");

    public ScreenGui(ScreenTE tileEntity, ScreenContainer container) {
        super(QuestUtils.instance, QuestUtilsMessages.INSTANCE, tileEntity, container, 0, "screen");

        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        idField = new TextField(mc, this)
                .setLayoutHint(new PositionalLayout.PositionalHint(40, 6, 173, 14));
        idField.setText(tileEntity.getIdentifier() == null ? "" : tileEntity.getIdentifier());
        idField.addTextEvent((parent, newText) -> updateId());

        Panel stringPanel = getStringPanel("Title", "title", tileEntity.getTitle()).setLayoutHint(new PositionalLayout.PositionalHint(0, 22, WIDTH, 14));

        iconField = new TextField(mc, this)
                .setLayoutHint(new PositionalLayout.PositionalHint(40, 44, 144, 14));
        iconField.setText(tileEntity.getIcon() == null ? "" : tileEntity.getIcon().toString());
        iconField.setTooltips("Resource name for", "the image to show", "Use the file below", "to load this from image");
        iconField.addTextEvent((parent, newText) -> update());

        borderColor = new ColorChoiceLabel(mc, this)
                .setLayoutHint(new PositionalLayout.PositionalHint(12, 200, 52, 14));
        for (EnumDyeColor color : EnumDyeColor.values()) {
            borderColor.addColors(color.getColorValue());
        }
        borderColor.setTooltips("Set the color for the", "border around the items");
        borderColor.setCurrentColor(tileEntity.getBorderColor());
        borderColor.addChoiceEvent((parent, newColor) -> update());

        transp = new ToggleButton(mc, this)
                .setLayoutHint(new PositionalLayout.PositionalHint(190, 58, 20 ,14));
        transp.setTooltips("Transparency mode");
        transp.setCheckMarker(true);
        transp.setPressed(tileEntity.isTransparent());
        transp.addButtonEvent(parent -> update());

        screenColor = new ColorChoiceLabel(mc, this)
                .setLayoutHint(new PositionalLayout.PositionalHint(190, 44, 20, 14));
        for (EnumDyeColor color : EnumDyeColor.values()) {
            screenColor.addColors(color.getColorValue());
        }
        screenColor.setTooltips("Set the color for", "the screen");
        screenColor.setCurrentColor(tileEntity.getColor());
        screenColor.addChoiceEvent((parent, newColor) -> update());


        fileField = new TextField(mc, this)
                .setLayoutHint(new PositionalLayout.PositionalHint(40, 60, 144, 14));
        fileField.setTooltips("Filename for the image", "to show");
        fileField.setText(tileEntity.getFilename() == null ? "" : tileEntity.getFilename());
        fileField.addTextEvent((parent, newText) -> update());

        Panel status0Panel = getStringPanel("Stat0", "status0", tileEntity.getStatus()[0]).setLayoutHint(new PositionalLayout.PositionalHint(0, 82, WIDTH, 14));
        Panel status1Panel = getStringPanel("Stat1", "status1", tileEntity.getStatus()[1]).setLayoutHint(new PositionalLayout.PositionalHint(0, 100, WIDTH, 14));
        Panel status2Panel = getStringPanel("Stat2", "status2", tileEntity.getStatus()[2]).setLayoutHint(new PositionalLayout.PositionalHint(0, 118, WIDTH, 14));

        Panel toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout())
                .addChild(new Label<>(mc, this).setText("ID").setLayoutHint(new PositionalLayout.PositionalHint(12, 6, 26, 14)).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT))
                .addChild(idField)
                .addChild(iconField)
                .addChild(new Label<>(mc, this)
                        .setText("Icon")
                        .setLayoutHint(new PositionalLayout.PositionalHint(12, 44, 26, 14))
                        .setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT))
                .addChild(fileField)
                .addChild(new Label<>(mc, this)
                        .setText("File")
                        .setLayoutHint(new PositionalLayout.PositionalHint(12, 60, 26, 14))
                        .setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT))
                .addChild(borderColor)
                .addChild(screenColor)
                .addChild(transp)
                .addChild(stringPanel)
                .addChild(status0Panel)
                .addChild(status1Panel)
                .addChild(status2Panel)
                ;
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
    }

    private Panel getStringPanel(String label, String prefix, ScreenTE.FormattedString string) {
        Panel panel = new Panel(mc, this).setLayout(new PositionalLayout());

        panel.addChild(new Label<>(mc, this)
                .setText(label)
                .setLayoutHint(new PositionalLayout.PositionalHint(12, 0, 26, 14))
                .setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT));

        TextField fld = new TextField(mc, this)
                .setLayoutHint(new PositionalLayout.PositionalHint(40, 0, 144, 14));
        fld.setText(string == null ? "" : string.getText());

        ChoiceLabel choice = new ChoiceLabel(mc, this)
                .addChoices("L", "C", "R")
                .setLayoutHint(new PositionalLayout.PositionalHint(190, 0, 20, 14));
        if (string != null) {
            switch (string.getAlignment()) {
                case LEFT:
                    choice.setChoice("L");
                    break;
                case CENTER:
                    choice.setChoice("C");
                    break;
                case RIGHT:
                    choice.setChoice("R");
                    break;
            }
        }

        ColorChoiceLabel colorChoice = new ColorChoiceLabel(mc, this)
                .setLayoutHint(new PositionalLayout.PositionalHint(214, 0, 20, 14));
        for (EnumDyeColor color : EnumDyeColor.values()) {
            colorChoice.addColors(color.getColorValue());
        }
        if (string != null) {
            colorChoice.setCurrentColor(string.getColor());
        }

        panel.addChild(fld);
        panel.addChild(choice);
        panel.addChild(colorChoice);

        fld.addTextEvent((parent, newText) -> updateString(prefix, fld.getText(), getAlignment(choice), colorChoice.getCurrentColor()));
        choice.addChoiceEvent((parent, newChoice) -> updateString(prefix, fld.getText(), getAlignment(choice), colorChoice.getCurrentColor()));
        colorChoice.addChoiceEvent((parent, newColor) -> updateString(prefix, fld.getText(), getAlignment(choice), colorChoice.getCurrentColor()));

        return panel;
    }

    private ScreenTE.Alignment getAlignment(ChoiceLabel choice) {
        if ("L".equals(choice.getCurrentChoice())) {
            return ScreenTE.Alignment.LEFT;
        } else if ("C".equals(choice.getCurrentChoice())) {
            return ScreenTE.Alignment.CENTER;
        } else {
            return ScreenTE.Alignment.RIGHT;
        }
    }

    private void updateId() {
        tileEntity.setIdentifier(idField.getText());
        sendServerCommand(QuestUtilsMessages.INSTANCE, QUTileEntity.CMD_SETID,
                new Argument("id", idField.getText()));
    }

    private void update() {
        sendServerCommand(QuestUtilsMessages.INSTANCE, ScreenTE.CMD_UPDATE,
                new Argument("color", borderColor.getCurrentColor()),
                new Argument("screen", screenColor.getCurrentColor()),
                new Argument("transp", transp.isPressed()),
                new Argument("icon", iconField.getText()),
                new Argument("file", fileField.getText()));
    }

    private void updateString(String prefix, String title, ScreenTE.Alignment alignment, int color) {
        sendServerCommand(QuestUtilsMessages.INSTANCE, ScreenTE.CMD_UPDATE_STRING,
                new Argument(prefix, title),
                new Argument(prefix+"A", alignment.ordinal()),
                new Argument(prefix+"C", color));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        drawWindow();
    }
}
