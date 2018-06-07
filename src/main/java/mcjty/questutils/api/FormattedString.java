package mcjty.questutils.api;

public class FormattedString {
    private final String text;
    private final TextAlignment alignment;
    private final int color;

    public FormattedString(String text, TextAlignment alignment, int color) {
        this.text = text;
        this.alignment = alignment;
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public TextAlignment getAlignment() {
        return alignment;
    }

    public int getColor() {
        return color;
    }
}
