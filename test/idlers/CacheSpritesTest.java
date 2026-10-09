package idlers;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CacheSpritesTest {

    /** A media archive with one file, "dot": a single 1x1 red sprite on a 1x1 canvas. */
    private static final Map<String, byte[]> FILES = Map.of(
            "dot.dat", new byte[]{0, 0, 1},
            "index.dat", new byte[]{0, 1, 0, 1, 2, (byte) 0xff, 0, 0, 0, 0, 0, 1, 0, 1, 0});

    private final List<String> asked = new ArrayList<>();
    private final Function<String, byte[]> archive = name -> {
        asked.add(name);
        return FILES.get(name);
    };
    private final CacheSprites sprites = new CacheSprites(archive);

    private static WidgetSpec.Sprite spec(String name, int index) {
        return new WidgetSpec.Sprite(1, -1, 0, 0, 3, 1, name, index);
    }

    @Test
    void aSpriteIsFittedIntoItsWidget() {
        assertArrayEquals(new int[]{0, 0xff0000, 0}, sprites.fitted(spec("dot", 0)).pixels());
    }

    @Test
    void eachFileIsReadOnce() {
        sprites.fitted(spec("dot", 0));
        sprites.fitted(spec("dot", 0));

        assertEquals(List.of("dot.dat", "index.dat"), asked);
    }

    @Test
    void anUnknownNameFailsLoudly() {
        assertThrows(IllegalArgumentException.class, () -> sprites.fitted(spec("nothing", 0)));
    }

    @Test
    void anIndexPastTheLastSpriteFailsLoudly() {
        assertThrows(IllegalArgumentException.class, () -> sprites.fitted(spec("dot", 1)));
    }

    @Test
    void aNegativeIndexFailsLoudly() {
        assertThrows(IllegalArgumentException.class, () -> sprites.fitted(spec("dot", -1)));
    }
}
