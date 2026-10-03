package idlers.worldmap;

import org.junit.jupiter.api.Test;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LabelFontsTest {

    @Test
    void prefersHelveticaWhenInstalled() {
        assertEquals("Helvetica", LabelFonts.family(Set.of("Liberation Sans", "Helvetica", "Arial")));
    }

    @Test
    void fallsBackToTheNextPreferredFamily() {
        assertEquals("Liberation Sans", LabelFonts.family(Set.of("DejaVu Sans", "Liberation Sans")));
    }

    @Test
    void fallsBackToAwtSansSerifWithoutAnyPreferredFamily() {
        assertEquals(Font.SANS_SERIF, LabelFonts.family(Set.of("DejaVu Sans")));
    }

    @Test
    void picksTheAppletsSizeForEachLabelSizeAndZoom() {
        assertEquals(List.of(11, 17, 30), List.of(LabelFonts.pixelSize(LabelFonts.SMALL, ZoomLevel.P37),
                LabelFonts.pixelSize(LabelFonts.TOWN, ZoomLevel.P50), LabelFonts.pixelSize(LabelFonts.KINGDOM,
                        ZoomLevel.P100)));
    }

    @Test
    void anUnknownLabelSizeIsClampedToTheNearestKnownOne() {
        assertEquals(List.of(11, 30), List.of(LabelFonts.pixelSize(-1, ZoomLevel.P37),
                LabelFonts.pixelSize(7, ZoomLevel.P100)));
    }

    @Test
    void buildsEachPixelSizeOnceAndSharesIt() {
        List<Integer> built = new ArrayList<>();
        LabelFonts fonts = new LabelFonts(size -> {
            built.add(size);
            return TestMaps.blockFont();
        });

        assertEquals(List.of(11, 12, 14, 17, 19, 22, 26, 30), built.stream().sorted().toList());
        assertSame(fonts.font(LabelFonts.SMALL, ZoomLevel.P75), fonts.font(LabelFonts.TOWN, ZoomLevel.P37));
    }

    @Test
    void createRasterisesLargerFontsForLargerSizes() {
        LabelFonts fonts = LabelFonts.create(Set.of());

        BitmapFont small = fonts.font(LabelFonts.SMALL, ZoomLevel.P37);
        BitmapFont kingdom = fonts.font(LabelFonts.KINGDOM, ZoomLevel.P100);

        assertNotSame(small, kingdom);
        assertTrue(kingdom.width("Lumbridge") > small.width("Lumbridge"));
    }
}
