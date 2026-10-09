package idlers;

import idlers.worldmap.MapSprite;
import idlers.worldmap.MediaSprites;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * The sprites of the client's media archive for widgets defined in code, decoded once per name. The client nulls the
 * archive after unpacking interfaces, so the glue keeps it for this (S01: icons are the cache's own sprites).
 */
public final class CacheSprites {

    private final Function<String, byte[]> archive;
    private final Map<String, MapSprite[]> decoded = new HashMap<>();

    /** {@code archive} answers a file of the media archive by name, null when there is none. */
    public CacheSprites(Function<String, byte[]> archive) {
        this.archive = archive;
    }

    public SpriteFit.Fitted fitted(WidgetSpec.Sprite spec) {
        return fitted(spec.name(), spec.index(), spec.width(), spec.height());
    }

    /** Sprite {@code index} of {@code name} fitted into {@code width} by {@code height}. */
    public SpriteFit.Fitted fitted(String name, int index, int width, int height) {
        MapSprite[] sprites = decoded.computeIfAbsent(name, this::decode);
        if (index < 0 || index >= sprites.length) {
            throw new IllegalArgumentException("Media sprite " + name + " has no index " + index);
        }
        return SpriteFit.fit(sprites[index], width, height);
    }

    private MapSprite[] decode(String name) {
        byte[] dat = archive.apply(name + ".dat");
        if (dat == null) {
            throw new IllegalArgumentException("No media sprite named " + name);
        }
        return MediaSprites.decode(dat, archive.apply("index.dat"));
    }
}
