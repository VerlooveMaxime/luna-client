package idlers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Dragging a tile onto another tile of the same layer to reorder them (S04, Maxime): what a press drags, where a drop
 * moves it, and drawing the dragged tile above its neighbours. A tile is a layer whose face is a draggable
 * {@link WidgetSpec.Tile}; tiles are numbered by their order among the layer's draggable tiles. The client sends the
 * move as packet 123 with the layer's id, in insert mode.
 */
public final class TileDrag {

    /** Moves tile {@code from} to place {@code to} of {@code layer}, the tiles in between shifting by one. */
    public record Move(int from, int to, int layer) {
    }

    /** A layer's children as the client holds them: ids and their positions. */
    public record Children(int[] ids, int[] xs, int[] ys) {
    }

    private final Map<Integer, WidgetSpec> specs;

    public TileDrag(Map<Integer, WidgetSpec> specs) {
        this.specs = specs;
    }

    /** The tile a press on {@code widgetId} drags: the layer of a draggable face. */
    public Optional<Integer> tileOf(int widgetId) {
        if (specs.get(widgetId) instanceof WidgetSpec.Tile face && face.draggable()) {
            return Optional.of(face.parent());
        }
        return Optional.empty();
    }

    /**
     * Where dropping the tile of {@code draggedFace} on {@code target}, any widget inside another tile of the same
     * layer, moves it; nothing for a drop on itself or outside the layer's tiles.
     */
    public Optional<Move> drop(int draggedFace, int target) {
        Optional<Integer> from = tileOf(draggedFace);
        Optional<Integer> to = tileAround(target);
        if (from.isEmpty() || to.isEmpty() || from.equals(to)) {
            return Optional.empty();
        }
        int layer = specs.get(from.get()).parent();
        if (specs.get(to.get()).parent() != layer) {
            return Optional.empty();
        }
        List<Integer> tiles = tiles(layer);
        return Optional.of(new Move(tiles.indexOf(from.get()), tiles.indexOf(to.get()), layer));
    }

    /** {@code children} with {@code id} moved last, so the client draws it above the others. */
    public static Children toFront(Children children, int id) {
        int count = children.ids().length;
        int[] ids = new int[count];
        int[] xs = new int[count];
        int[] ys = new int[count];
        int next = 0;
        int last = count - 1;
        for (int i = 0; i < count; i++) {
            int place = children.ids()[i] == id ? last : next++;
            ids[place] = children.ids()[i];
            xs[place] = children.xs()[i];
            ys[place] = children.ys()[i];
        }
        return new Children(ids, xs, ys);
    }

    private Optional<Integer> tileAround(int widgetId) {
        WidgetSpec spec = specs.get(widgetId);
        while (spec != null) {
            if (isTile(spec)) {
                return Optional.of(spec.id());
            }
            spec = specs.get(spec.parent());
        }
        return Optional.empty();
    }

    private List<Integer> tiles(int layer) {
        return ((WidgetSpec.Layer) specs.get(layer)).children().stream().filter(id -> isTile(specs.get(id))).toList();
    }

    private boolean isTile(WidgetSpec spec) {
        return spec instanceof WidgetSpec.Layer layer && layer.children().stream().anyMatch(child -> tileOf(child).isPresent());
    }
}
