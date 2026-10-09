package idlers;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Every widget IdleRS defines in code: the Idle tab, the quest journal's stages, the widget gallery, the search
 * prompt's icons and the builder's screens, built for the number of step slots the player has, which the server
 * sends; the client builds them again when it changes.
 */
public final class WidgetSpecs {

    private final Map<Integer, WidgetSpec> all;

    private WidgetSpecs(Map<Integer, WidgetSpec> all) {
        this.all = all;
    }

    /** The widgets for a player with {@code builderSlots} step slots. */
    public static WidgetSpecs of(int builderSlots) {
        return new WidgetSpecs(merge(List.of(FlowWidgets.specs(), QuestJournal.specs(), WidgetGallery.specs(), SearchWidgets.specs(),
                BuilderWidgets.specs(builderSlots))));
    }

    public Optional<WidgetSpec> spec(int id) {
        return Optional.ofNullable(all.get(id));
    }

    public Map<Integer, WidgetSpec> all() {
        return Collections.unmodifiableMap(all);
    }

    /** Room the client makes for widget ids: one past the highest. */
    public int capacity() {
        return all.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
    }

    /**
     * The group id the client keeps for a widget ({@code anInt248}): the id of its root layer, the root's own id for a
     * root, as the cache stores it. Closing an interface drops every widget of its group, nested ones included, so
     * they come back fresh; a direct parent there would leave deeper widgets with stale texts and hidden flags.
     * A parent the cache defines is taken as the root.
     */
    public int root(int id) {
        int parent = all.get(id).parent();
        if (parent == -1) {
            return id;
        }
        return all.containsKey(parent) ? root(parent) : parent;
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
