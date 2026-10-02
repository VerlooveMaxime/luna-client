package idlers;

import java.util.List;

/**
 * One widget IdleRS defines in code instead of the cache: a layer holding children at their own (x, y), a filled box,
 * a frame (outline), a text, or a button (a text the client puts a click option on, sent to the server as a button
 * click with this id).
 */
public record WidgetSpec(
        int id,
        int parent,
        Kind kind,
        int x,
        int y,
        int width,
        int height,
        String text,
        int colour,
        int hoverColour,
        int font,
        boolean centred,
        String tooltip,
        List<Integer> children) {

    public enum Kind { LAYER, BOX, FRAME, TEXT, BUTTON }

    /** Client font table indexes, as {@code JagInterface.unpack} receives them. */
    public static final int FONT_SMALL = 0;
    public static final int FONT_PLAIN = 1;
    public static final int FONT_BOLD = 2;

    public static WidgetSpec layer(int id, int parent, int x, int y, int width, int height, List<Integer> children) {
        return new WidgetSpec(id, parent, Kind.LAYER, x, y, width, height, "", 0, 0, FONT_PLAIN, false, "", List.copyOf(children));
    }

    public static WidgetSpec box(int id, int parent, int x, int y, int width, int height, int colour) {
        return new WidgetSpec(id, parent, Kind.BOX, x, y, width, height, "", colour, 0, FONT_PLAIN, false, "", List.of());
    }

    public static WidgetSpec frame(int id, int parent, int x, int y, int width, int height, int colour) {
        return new WidgetSpec(id, parent, Kind.FRAME, x, y, width, height, "", colour, 0, FONT_PLAIN, false, "", List.of());
    }

    public static WidgetSpec text(int id, int parent, int x, int y, int width, int height, String text, int colour, int font) {
        return new WidgetSpec(id, parent, Kind.TEXT, x, y, width, height, text, colour, 0, font, false, "", List.of());
    }

    /** A text centred on {@code x}, as the client draws centred widgets around their x. */
    public static WidgetSpec centredText(int id, int parent, int x, int y, int height, String text, int colour, int font) {
        return new WidgetSpec(id, parent, Kind.TEXT, x, y, 0, height, text, colour, 0, font, true, "", List.of());
    }

    public static WidgetSpec button(int id, int parent, int x, int y, int width, int height, String text, int colour,
            int hoverColour, int font, String tooltip) {
        return new WidgetSpec(id, parent, Kind.BUTTON, x, y, width, height, text, colour, hoverColour, font, false, tooltip, List.of());
    }
}
