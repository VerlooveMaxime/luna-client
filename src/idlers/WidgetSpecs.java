package idlers;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Every widget IdleRS defines in code: the flow widgets, the quest journal's stages, the widget gallery and the search
 * prompt's icons.
 */
public final class WidgetSpecs {

    /** Room the client makes for widget ids; the search prompt's ids come last. */
    public static final int CAPACITY = SearchWidgets.ID_LIMIT;

    private static final Map<Integer, WidgetSpec> ALL = merge(List.of(FlowWidgets.specs(), QuestJournal.specs(), WidgetGallery.specs(),
            SearchWidgets.specs()));

    private WidgetSpecs() {
    }

    public static Optional<WidgetSpec> spec(int id) {
        return Optional.ofNullable(ALL.get(id));
    }

    public static Map<Integer, WidgetSpec> all() {
        return Collections.unmodifiableMap(ALL);
    }

    /**
     * The group id the client keeps for a widget ({@code anInt248}): the id of its root layer, the root's own id for a
     * root, as the cache stores it. Closing an interface drops every widget of its group, nested ones included, so
     * they come back fresh; a direct parent there would leave deeper widgets with stale texts and hidden flags.
     * A parent the cache defines is taken as the root.
     */
    public static int root(int id) {
        int parent = ALL.get(id).parent();
        if (parent == -1) {
            return id;
        }
        return ALL.containsKey(parent) ? root(parent) : parent;
    }

    static Map<Integer, WidgetSpec> merge(List<Map<Integer, WidgetSpec>> sources) {
        Map<Integer, WidgetSpec> merged = new LinkedHashMap<>();
        sources.forEach(source -> source.values().forEach(spec -> put(merged, spec)));
        return merged;
    }

    /** Defines {@code spec} and makes it the next child of the layer {@code children} belongs to. */
    static void add(Map<Integer, WidgetSpec> specs, List<Integer> children, WidgetSpec spec) {
        put(specs, spec);
        children.add(spec.id());
    }

    private static void put(Map<Integer, WidgetSpec> specs, WidgetSpec spec) {
        if (specs.put(spec.id(), spec) != null) {
            throw new IllegalStateException("Widget id " + spec.id() + " defined twice");
        }
    }
}
