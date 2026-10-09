import idlers.CacheSprites;
import idlers.WidgetSpec;
import idlers.WidgetSpecs;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Every sprite a code-defined widget names exists in the 377 cache's media archive. Skipped without the cache. */
class CacheSpritesInCacheTest {

    private static final File CACHE = new File("cache");
    private static final int MEDIA = 4;

    private static CacheSprites sprites;

    @BeforeAll
    static void openMediaArchive() throws IOException {
        Assumptions.assumeTrue(new File(CACHE, "main_file_cache.dat").exists(), "no cache in luna-client/cache");
        try (RandomAccessFile dat = new RandomAccessFile(new File(CACHE, "main_file_cache.dat"), "r");
             RandomAccessFile index = new RandomAccessFile(new File(CACHE, "main_file_cache.idx0"), "r")) {
            Archive media = new Archive(new FileStore(1, 0x927c0, dat, index).get(MEDIA));
            sprites = new CacheSprites(media::get);
        }
    }

    static Stream<WidgetSpec.Sprite> spriteSpecs() {
        return WidgetSpecs.of(0).all().values().stream().filter(WidgetSpec.Sprite.class::isInstance).map(WidgetSpec.Sprite.class::cast);
    }

    @ParameterizedTest
    @MethodSource("spriteSpecs")
    void theNamedSpriteExists(WidgetSpec.Sprite spec) {
        assertEquals(spec.width() * spec.height(), sprites.fitted(spec).pixels().length);
    }
}
