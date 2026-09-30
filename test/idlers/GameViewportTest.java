package idlers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameViewportTest {

    private static final int GAME_WIDTH = 765;
    private static final int GAME_HEIGHT = 503;

    @Test
    void fitUsesTheWholeAreaWhenItHasTheGameAspectRatio() {
        GameViewport viewport = GameViewport.fit(GAME_WIDTH, GAME_HEIGHT, 0, 0, 1530, 1006);

        assertEquals(new GameViewport(2.0, 0, 0), viewport);
    }

    @Test
    void fitCentresTheGameInAWideArea() {
        GameViewport viewport = GameViewport.fit(GAME_WIDTH, GAME_HEIGHT, 0, 0, 1630, 1006);

        assertEquals(new GameViewport(2.0, 50, 0), viewport);
    }

    @Test
    void fitCentresTheGameInATallArea() {
        GameViewport viewport = GameViewport.fit(GAME_WIDTH, GAME_HEIGHT, 0, 0, 1530, 1106);

        assertEquals(new GameViewport(2.0, 0, 50), viewport);
    }

    @Test
    void fitShiftsTheGameByTheWindowBorders() {
        GameViewport viewport = GameViewport.fit(GAME_WIDTH, GAME_HEIGHT, 4, 30, 765, 503);

        assertEquals(new GameViewport(1.0, 4, 30), viewport);
    }

    @Test
    void fitAllowsAFractionalScale() {
        GameViewport viewport = GameViewport.fit(GAME_WIDTH, GAME_HEIGHT, 0, 0, 1912, 1300);

        assertEquals(1912.0 / GAME_WIDTH, viewport.scale());
    }

    @Test
    void fitNeverShrinksTheGameBelowItsOwnSize() {
        GameViewport viewport = GameViewport.fit(GAME_WIDTH, GAME_HEIGHT, 0, 0, 400, 300);

        assertEquals(1.0, viewport.scale());
    }

    @Test
    void toGameXUndoesTheOffsetAndScale() {
        GameViewport viewport = new GameViewport(2.0, 50, 0);

        assertEquals(10, viewport.toGameX(71));
    }

    @Test
    void toGameYUndoesTheOffsetAndScale() {
        GameViewport viewport = new GameViewport(3.0, 0, 30);

        assertEquals(4, viewport.toGameY(44));
    }

    @Test
    void aPointInTheLeftBarMapsLeftOfTheGame() {
        GameViewport viewport = new GameViewport(2.0, 50, 0);

        assertEquals(-1, viewport.toGameX(49));
    }

    @Test
    void initialScaleIsTheLargestWholeScaleThatFitsWhenAbsent() {
        double scale = GameViewport.initialScale(null, GAME_WIDTH, GAME_HEIGHT, 2560, 1540);

        assertEquals(3.0, scale);
    }

    @Test
    void initialScaleIsAutomaticWhenBlank() {
        double scale = GameViewport.initialScale("  ", GAME_WIDTH, GAME_HEIGHT, 2560, 1500);

        assertEquals(2.0, scale);
    }

    @Test
    void initialScaleIsAutomaticWhenSetToAutoInAnyCase() {
        double scale = GameViewport.initialScale(" Auto ", GAME_WIDTH, GAME_HEIGHT, 3840, 2100);

        assertEquals(4.0, scale);
    }

    @Test
    void initialScaleIsOneWhenTheScreenIsSmallerThanTheGame() {
        double scale = GameViewport.initialScale("auto", GAME_WIDTH, GAME_HEIGHT, 700, 500);

        assertEquals(1.0, scale);
    }

    @Test
    void initialScaleUsesAConfiguredNumber() {
        double scale = GameViewport.initialScale(" 2.5 ", GAME_WIDTH, GAME_HEIGHT, 2560, 1540);

        assertEquals(2.5, scale);
    }

    @Test
    void initialScaleRejectsText() {
        assertThrows(IllegalArgumentException.class,
                () -> GameViewport.initialScale("big", GAME_WIDTH, GAME_HEIGHT, 2560, 1540));
    }

    @Test
    void initialScaleRejectsAScaleBelowOne() {
        assertThrows(IllegalArgumentException.class,
                () -> GameViewport.initialScale("0.5", GAME_WIDTH, GAME_HEIGHT, 2560, 1540));
    }

    @Test
    void initialScaleRejectsNotANumber() {
        assertThrows(IllegalArgumentException.class,
                () -> GameViewport.initialScale("NaN", GAME_WIDTH, GAME_HEIGHT, 2560, 1540));
    }

    @Test
    void initialScaleRejectsInfinity() {
        assertThrows(IllegalArgumentException.class,
                () -> GameViewport.initialScale("Infinity", GAME_WIDTH, GAME_HEIGHT, 2560, 1540));
    }
}
