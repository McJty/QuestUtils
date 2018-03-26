package mcjty.questutils.blocks.screen;

import mcjty.lib.container.GenericGuiContainer;
import mcjty.lib.gui.Window;
import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.Label;
import mcjty.lib.gui.widgets.Panel;
import mcjty.lib.gui.widgets.TextField;
import mcjty.lib.network.Argument;
import mcjty.questutils.QuestUtils;
import mcjty.questutils.blocks.QUTileEntity;
import mcjty.questutils.network.QuestUtilsMessages;
import net.minecraft.util.ResourceLocation;

import java.awt.Rectangle;

public class ScreenGui extends GenericGuiContainer<ScreenTileEntity> {

    public static final int WIDTH = 183;
    public static final int HEIGHT = 238;

    private TextField idField;
    private TextField titleField;
    private TextField status0Field;
    private TextField status1Field;
    private TextField status2Field;

    private static final ResourceLocation iconLocation = new ResourceLocation(QuestUtils.MODID, "textures/gui/screen.png");
    private static final ResourceLocation iconGuiElements = new ResourceLocation(QuestUtils.MODID, "textures/gui/guielements.png");

    public ScreenGui(ScreenTileEntity tileEntity, ScreenContainer container) {
        super(QuestUtils.instance, QuestUtilsMessages.INSTANCE, tileEntity, container, 0, "screen");

        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();

        idField = new TextField(mc, this)
                .setLayoutHint(new PositionalLayout.PositionalHint(30, 6, 143, 14));
        idField.setText(tileEntity.getIdentifier() == null ? "" : tileEntity.getIdentifier());
        idField.addTextEvent((parent, newText) -> updateId());

        titleField = new TextField(mc, this)
                .setLayoutHint(new PositionalLayout.PositionalHint(30, 22, 143, 14));
        titleField.setText(tileEntity.getTitle() == null ? "" : tileEntity.getTitle());
        titleField.addTextEvent((parent, newText) -> update());

        status0Field = new TextField(mc, this)
                .setLayoutHint(new PositionalLayout.PositionalHint(30, 72, 143, 14));
        status0Field.setText(tileEntity.getStatus()[0] == null ? "" : tileEntity.getStatus()[0]);
        status0Field.addTextEvent((parent, newText) -> update());
        status1Field = new TextField(mc, this)
                .setLayoutHint(new PositionalLayout.PositionalHint(30, 90, 143, 14));
        status1Field.setText(tileEntity.getStatus()[1] == null ? "" : tileEntity.getStatus()[1]);
        status1Field.addTextEvent((parent, newText) -> update());
        status2Field = new TextField(mc, this)
                .setLayoutHint(new PositionalLayout.PositionalHint(30, 108, 143, 14));
        status2Field.setText(tileEntity.getStatus()[2] == null ? "" : tileEntity.getStatus()[2]);
        status2Field.addTextEvent((parent, newText) -> update());

        Panel toplevel = new Panel(mc, this).setBackground(iconLocation).setLayout(new PositionalLayout())
                .addChild(new Label<>(mc, this).setText("ID").setLayoutHint(new PositionalLayout.PositionalHint(12, 6, 16, 14)).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT))
                .addChild(idField)
                .addChild(new Label<>(mc, this).setText("Title").setLayoutHint(new PositionalLayout.PositionalHint(12, 22, 16, 14)).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT))
                .addChild(titleField)
                .addChild(new Label<>(mc, this).setText("Stat0").setLayoutHint(new PositionalLayout.PositionalHint(12, 72, 16, 14)).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT))
                .addChild(status0Field)
                .addChild(new Label<>(mc, this).setText("Stat1").setLayoutHint(new PositionalLayout.PositionalHint(12, 90, 16, 14)).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT))
                .addChild(status1Field)
                .addChild(new Label<>(mc, this).setText("Stat2").setLayoutHint(new PositionalLayout.PositionalHint(12, 108, 16, 14)).setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT))
                .addChild(status2Field)
                ;
        toplevel.setBounds(new Rectangle(guiLeft, guiTop, xSize, ySize));

        window = new Window(this, toplevel);
    }

    private void updateId() {
        tileEntity.setIdentifier(idField.getText());
        sendServerCommand(QuestUtilsMessages.INSTANCE, QUTileEntity.CMD_SETID,
                new Argument("id", idField.getText()));
    }

    private void update() {
        tileEntity.setIdentifier(idField.getText());
        sendServerCommand(QuestUtilsMessages.INSTANCE, ScreenTileEntity.CMD_UPDATE,
                new Argument("title", titleField.getText()),
                new Argument("status0", status0Field.getText()),
                new Argument("status1", status1Field.getText()),
                new Argument("status2", status2Field.getText()));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float v, int i, int i2) {
        drawWindow();
    }
}
