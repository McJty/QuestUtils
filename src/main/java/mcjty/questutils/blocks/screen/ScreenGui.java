package mcjty.questutils.blocks.screen;

import mcjty.lib.gui.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.TypedMap;
import mcjty.questutils.QuestUtils;
import mcjty.questutils.blocks.QUTileEntity;
import mcjty.questutils.network.QuestUtilsMessages;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;

import java.awt.Rectangle;

import static mcjty.questutils.blocks.screen.ScreenTE.*;

public class ScreenGui extends GenericGuiContainer<ScreenTE> {

    public static final int WIDTH = 243;
    public static final int HEIGHT = 238;

    private TextField iconField;
    private TextField fileField;
    private ColorChoiceLabel borderColor;
    private ColorChoiceLabel screenColor;
    private ToggleButton transp;
    private ChoiceLabel size;

    private static final ResourceLocation iconLocation = new ResourceLocation(QuestUtils.MODID, "textures/gui/screen.png");
    private static final ResourceLocation iconGuiElements = new ResourceLocation(QuestUtils.MODID, "textures/gui/guielements.png");

    private final String[] SIZES = { "1x1", "2x2", "3x3", "4x4", "5x5" };

    public ScreenGui(ScreenTE tileEntity, ScreenContainer container) {
        super(QuestUtils.instance, QuestUtilsMessages.INSTANCE, tileEntity, container, 0, "screen");

        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        TextField idField = new TextField(mc, this)
                .setName("id")
                .setLayoutHint(new PositionalLayout.PositionalHint(40, 6, 173, 14));

        Panel stringPanel = getStringPanel("Title", PARAM_TITLE, PARAM_TITLE_A, PARAM_TITLE_C, tileEntity.getTitle()).setLayoutHint(new PositionalLayout.PositionalHint(0, 22, WIDTH, 14));

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
                .setLayoutHint(new PositionalLayout.PositionalHint(213, 44, 20 ,14));
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

        size = new ChoiceLabel(mc, this)
            .addChoices(SIZES)
            .setLayoutHint(new PositionalLayout.PositionalHint(190, 58, 43, 14));
        size.setChoice(SIZES[tileEntity.getSize()]);
        size.addChoiceEvent((parent, newChoice) -> update());

        Panel status0Panel = getStringPanel("Stat0", PARAM_STATUS0, PARAM_STATUS0_A, PARAM_STATUS0_C, tileEntity.getStatus()[0]).setLayoutHint(new PositionalLayout.PositionalHint(0, 82, WIDTH, 14));
        Panel status1Panel = getStringPanel("Stat1", PARAM_STATUS1, PARAM_STATUS1_A, PARAM_STATUS1_C, tileEntity.getStatus()[1]).setLayoutHint(new PositionalLayout.PositionalHint(0, 100, WIDTH, 14));
        Panel status2Panel = getStringPanel("Stat2", PARAM_STATUS2, PARAM_STATUS2_A, PARAM_STATUS2_C, tileEntity.getStatus()[2]).setLayoutHint(new PositionalLayout.PositionalHint(0, 118, WIDTH, 14));

        Panel toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout())
                .addChild(new Label<>(mc, this).setText("ID").setLayoutHint(new PositionalLayout.PositionalHint(12, 6, 26, 14)).setHorizontalAlignment(HorizontalAlignment.ALIGN_LEFT))
                .addChild(idField)
                .addChild(iconField)
                .addChild(new Label<>(mc, this)
                        .setText("Icon")
                        .setLayoutHint(new PositionalLayout.PositionalHint(12, 44, 26, 14))
                        .setHorizontalAlignment(HorizontalAlignment.ALIGN_LEFT))
                .addChild(fileField)
                .addChild(new Label<>(mc, this)
                        .setText("File")
                        .setLayoutHint(new PositionalLayout.PositionalHint(12, 60, 26, 14))
                        .setHorizontalAlignment(HorizontalAlignment.ALIGN_LEFT))
                .addChild(borderColor)
                .addChild(screenColor)
                .addChild(transp)
                .addChild(size)
                .addChild(stringPanel)
                .addChild(status0Panel)
                .addChild(status1Panel)
                .addChild(status2Panel)
                ;
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);

        window.bind(QuestUtilsMessages.INSTANCE, "id", tileEntity, QUTileEntity.VALUE_ID.getName());
    }


    private Panel getStringPanel(String label, Key<String> titleKey, Key<Integer> alignKey, Key<Integer> colorKey, ScreenTE.FormattedString string) {
        Panel panel = new Panel(mc, this).setLayout(new PositionalLayout());

        panel.addChild(new Label<>(mc, this)
                .setText(label)
                .setLayoutHint(new PositionalLayout.PositionalHint(12, 0, 26, 14))
                .setHorizontalAlignment(HorizontalAlignment.ALIGN_LEFT));

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

        fld.addTextEvent((parent, newText) -> updateString(titleKey, alignKey, colorKey, fld.getText(), getAlignment(choice), colorChoice.getCurrentColor()));
        choice.addChoiceEvent((parent, newChoice) -> updateString(titleKey, alignKey, colorKey, fld.getText(), getAlignment(choice), colorChoice.getCurrentColor()));
        colorChoice.addChoiceEvent((parent, newColor) -> updateString(titleKey, alignKey, colorKey, fld.getText(), getAlignment(choice), colorChoice.getCurrentColor()));

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

    private void update() {
        sendServerCommand(QuestUtilsMessages.INSTANCE, ScreenTE.CMD_UPDATE,
                TypedMap.builder()
                        .put(PARAM_COLOR, borderColor.getCurrentColor())
                        .put(PARAM_SCREEN, screenColor.getCurrentColor())
                        .put(PARAM_TRANSP, transp.isPressed())
                        .put(PARAM_SIZE, getSize())
                        .put(PARAM_ICON, iconField.getText())
                        .put(PARAM_FILE, fileField.getText())
                        .build());
    }

    private int getSize() {
        return size.getCurrentChoice().charAt(0)-'1';
    }

    private void updateString(Key<String> titleKey, Key<Integer> alignKey, Key<Integer> colorKey, String title, ScreenTE.Alignment alignment, int color) {
        sendServerCommand(QuestUtilsMessages.INSTANCE, ScreenTE.CMD_UPDATE_STRING,
                TypedMap.builder()
                        .put(titleKey, title)
                        .put(alignKey, alignment.ordinal())
                        .put(colorKey, color)
                        .build());
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        drawWindow();
    }
}
