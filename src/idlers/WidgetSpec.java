package idlers;

import java.util.List;

/**
 * One widget IdleRS defines in code instead of the cache. Every kind sits at (x, y) inside its parent layer; the
 * client builds it from this spec each time it asks for the id ({@code IdleWidgets}).
 */
public sealed interface WidgetSpec {

    int id();

    int parent();

    int x();

    int y();

    int width();

    int height();

    /** Client font table indexes, as {@code JagInterface.unpack} receives them. */
    int FONT_SMALL = 0;
    int FONT_PLAIN = 1;
    int FONT_BOLD = 2;

    /** Holds children at their own (x, y); scrolls, with the client's scrollbar on its right, when its content is taller. */
    record Layer(int id, int parent, int x, int y, int width, int height, int scrollHeight, List<Integer> children)
            implements WidgetSpec {

        public Layer {
            children = List.copyOf(children);
        }

        public boolean scrolls() {
            return scrollHeight > height;
        }
    }

    /** A filled box, or only its outline. */
    record Box(int id, int parent, int x, int y, int width, int height, int colour, boolean filled) implements WidgetSpec {
    }

    /** A text; a centred one is centred on its x, as the client draws centred widgets. */
    record Text(int id, int parent, int x, int y, int width, int height, String text, int colour, int font, boolean centred)
            implements WidgetSpec {
    }

    /** A text with a click option, sent to the server as a button click with this id. */
    record Button(int id, int parent, int x, int y, int width, int height, String text, int colour, int hoverColour,
            int font, String option) implements WidgetSpec {
    }

    /**
     * A filled box that lights up under the mouse and has a click option, sent as a button click: the face of a
     * GE-style tile, a layer holding it and the tile's other widgets. A draggable face lets the player drag its tile
     * onto another draggable tile of the same layer ({@link TileDrag}).
     */
    record Tile(int id, int parent, int x, int y, int width, int height, int colour, int hoverColour, String option,
            boolean draggable) implements WidgetSpec {
    }

    /** An invisible area; after about two seconds of hover the client shows its text in a light-yellow box below it. */
    record Tooltip(int id, int parent, int x, int y, int width, int height, String text) implements WidgetSpec {
    }

    /** Sprite {@code index} of {@code name} in the cache's media archive, fitted into the widget ({@link SpriteFit}). */
    record Sprite(int id, int parent, int x, int y, int width, int height, String name, int index) implements WidgetSpec {
    }

    /**
     * Empty until the server sends what it shows ({@link WidgetPicture}): a media sprite or an item's icon fitted into
     * it, or an npc's whole body, framed by {@link ModelFit} and seen from {@code pitch} and {@code yaw} (2048 units a
     * turn, as the client's model widgets).
     */
    record Picture(int id, int parent, int x, int y, int width, int height, int pitch, int yaw) implements WidgetSpec {
    }

    static Layer layer(int id, int parent, int x, int y, int width, int height, List<Integer> children) {
        return new Layer(id, parent, x, y, width, height, height, children);
    }

    static Layer scrollLayer(int id, int parent, int x, int y, int width, int height, int scrollHeight, List<Integer> children) {
        return new Layer(id, parent, x, y, width, height, scrollHeight, children);
    }

    static Box box(int id, int parent, int x, int y, int width, int height, int colour) {
        return new Box(id, parent, x, y, width, height, colour, true);
    }

    static Box frame(int id, int parent, int x, int y, int width, int height, int colour) {
        return new Box(id, parent, x, y, width, height, colour, false);
    }

    static Text text(int id, int parent, int x, int y, int width, int height, String text, int colour, int font) {
        return new Text(id, parent, x, y, width, height, text, colour, font, false);
    }

    static Text centredText(int id, int parent, int x, int y, int height, String text, int colour, int font) {
        return new Text(id, parent, x, y, 0, height, text, colour, font, true);
    }

    static Button button(int id, int parent, int x, int y, int width, int height, String text, int colour,
            int hoverColour, int font, String option) {
        return new Button(id, parent, x, y, width, height, text, colour, hoverColour, font, option);
    }
}
