package idlers.worldmap;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OverlayShapesTest {

    /**
     * Expected tiles generated on 2026-10-03 from the 32 loops of LostCity's {@code MapView.drawOverlayShape}
     * (rows top to bottom, '/' between rows, 'O' overlay, '.' underlay), drawn 4 x 4 and 5 x 3.
     */
    @ParameterizedTest(name = "shape {0} rotation {1}")
    @CsvSource(textBlock = """
            1, 0, O.../OO../OOO./OOOO, O..../OO.../OOO..
            1, 1, OOOO/OOO./OO../O..., OOO../OO.../O....
            1, 2, OOOO/.OOO/..OO/...O, OOOOO/.OOOO/..OOO
            1, 3, ...O/..OO/.OOO/OOOO, ..OOO/.OOOO/OOOOO
            2, 0, OO../OO../O.../O..., OO.../O..../O....
            2, 1, OOOO/..OO/..../...., OOOOO/..OOO/....O
            2, 2, ...O/...O/..OO/..OO, ....O/....O/...OO
            2, 3, ..../..../OO../OOOO, O..../OOO../OOOOO
            3, 0, ..OO/..OO/...O/...O, ...OO/....O/....O
            3, 1, ..../..../..OO/OOOO, ....O/..OOO/OOOOO
            3, 2, O.../O.../OO../OO.., O..../O..../OO...
            3, 3, OOOO/OO../..../...., OOOOO/OOO../O....
            4, 0, .OOO/.OOO/OOOO/OOOO, .OOOO/OOOOO/OOOOO
            4, 1, O.../OOO./OOOO/OOOO, O..../OOO../OOOOO
            4, 2, OOOO/OOOO/OOO./OOO., OOOOO/OOOOO/OOOO.
            4, 3, OOOO/OOOO/.OOO/...O, OOOOO/..OOO/....O
            5, 0, OOO./OOO./OOOO/OOOO, OOOO./OOOOO/OOOOO
            5, 1, OOOO/OOOO/OOO./O..., OOOOO/OOO../O....
            5, 2, OOOO/OOOO/.OOO/.OOO, OOOOO/OOOOO/.OOOO
            5, 3, ...O/.OOO/OOOO/OOOO, ....O/..OOO/OOOOO
            6, 0, OOO./OOO./OOO./OOO., OOO../OOO../OOO..
            6, 1, OOOO/OOOO/OOOO/...., OOOOO/OOOOO/.....
            6, 2, ..OO/..OO/..OO/..OO, ..OOO/..OOO/..OOO
            6, 3, ..../..../OOOO/OOOO, ...../OOOOO/OOOOO
            7, 0, ..../..../O.../OO.., ...../O..../OO...
            7, 1, OO../O.../..../...., OO.../O..../.....
            7, 2, ..OO/...O/..../...., ...OO/....O/.....
            7, 3, ..../..../...O/..OO, ...../....O/...OO
            8, 0, OOOO/OOOO/OOOO/.OOO, OOOOO/OOOOO/.OOOO
            8, 1, .OOO/OOOO/OOOO/OOOO, .OOOO/OOOOO/OOOOO
            8, 2, OOO./OOOO/OOOO/OOOO, OOOO./OOOOO/OOOOO
            8, 3, OOOO/OOOO/OOOO/OOO., OOOOO/OOOOO/OOOO.
            9, 0, OOOO/OOO./OO../O..., OOO../OO.../O....
            9, 1, OOOO/.OOO/..OO/...O, OOOOO/.OOOO/..OOO
            9, 2, ...O/..OO/.OOO/OOOO, ..OOO/.OOOO/OOOOO
            9, 3, O.../OO../OOO./OOOO, O..../OO.../OOO..
            10, 0, ...O/..OO/.OOO/OOOO, ..OOO/.OOOO/OOOOO
            10, 1, O.../OO../OOO./OOOO, O..../OO.../OOO..
            10, 2, OOOO/OOO./OO../O..., OOO../OO.../O....
            10, 3, OOOO/.OOO/..OO/...O, OOOOO/.OOOO/..OOO
            11, 0, OOOO/OOOO/OOOO/OOO., OOOOO/OOOOO/OOOO.
            11, 1, OOOO/OOOO/OOOO/.OOO, OOOOO/OOOOO/.OOOO
            11, 2, .OOO/OOOO/OOOO/OOOO, .OOOO/OOOOO/OOOOO
            11, 3, OOO./OOOO/OOOO/OOOO, OOOO./OOOOO/OOOOO
            """)
    void matchesTheAppletsLoops(int shape, int rotation, String square, String wide) {
        assertEquals(square, pattern(shape, rotation, 4, 4));
        assertEquals(wide, pattern(shape, rotation, 5, 3));
    }

    @Test
    void shapeZeroIsAllOverlay() {
        assertTrue(OverlayShapes.isOverlay(0, 0, 3, 0, 4, 4));
    }

    @Test
    void anUnknownShapeIsAllOverlay() {
        assertTrue(OverlayShapes.isOverlay(12, 0, 3, 0, 4, 4));
    }

    @Test
    void fillPaintsTheOverlayAndUnderlayPixels() {
        Raster raster = Raster.blank(6, 4);

        OverlayShapes.fill(raster, 2, 0, 4, 4, 0x111111, 0x222222, 1, 0);

        assertEquals(0x222222, raster.pixel(2, 3));
        assertEquals(0x111111, raster.pixel(5, 0));
        assertEquals(0, raster.pixel(1, 0));
    }

    private static String pattern(int shape, int rotation, int width, int height) {
        StringBuilder text = new StringBuilder();
        for (int y = 0; y < height; y++) {
            if (y > 0) {
                text.append('/');
            }
            for (int x = 0; x < width; x++) {
                text.append(OverlayShapes.isOverlay(shape, rotation, x, y, width, height) ? 'O' : '.');
            }
        }
        return text.toString();
    }
}
