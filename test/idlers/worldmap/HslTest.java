package idlers.worldmap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HslTest {

    @Test
    void noSaturationGivesGrey() {
        assertEquals(0x808080, Hsl.toRgb(0.3, 0.0, 0.5));
    }

    @Test
    void darkRedUsesTheLowLightnessFormula() {
        assertEquals(0x800000, Hsl.toRgb(0.0, 1.0, 0.25));
    }

    @Test
    void lightColoursUseTheHighLightnessFormula() {
        assertEquals(0xe0a0a0, Hsl.toRgb(0.0, 0.5, 0.75));
    }

    @Test
    void aThirdOfTheWheelIsGreen() {
        assertEquals(0x008000, Hsl.toRgb(1.0 / 3.0, 1.0, 0.25));
    }

    @Test
    void aHueOnTheRisingSlopeMixesInPartOfTheNextChannel() {
        assertEquals(0x804c00, Hsl.toRgb(0.1, 1.0, 0.25));
    }

    @Test
    void aHueOnTheFallingSlopeKeepsTheAppletsRoundingDown() {
        assertEquals(0x007f80, Hsl.toRgb(0.5, 1.0, 0.25));
    }

    @Test
    void aHuePastTwoThirdsWrapsTheRedChannel() {
        assertEquals(0x660080, Hsl.toRgb(0.8, 1.0, 0.25));
    }

    @Test
    void fullLightnessOverflowsLikeTheApplet() {
        assertEquals(256 << 16 | 256 << 8 | 256, Hsl.toRgb(0.0, 0.0, 1.0));
    }
}
