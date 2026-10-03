package idlers.worldmap;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapKeyTest {

    @Test
    void namesEverySixtyFunctionsOfThe2006Applet() {
        assertEquals(List.of(60, "General Store", "Bank", "Brewery"), List.of(MapKey.NAMES.size(),
                MapKey.NAMES.get(0), MapKey.NAMES.get(5), MapKey.NAMES.get(59)));
    }

    @Test
    void theFirstPageStartsAtTheFirstName() {
        MapKey key = new MapKey(15);

        assertEquals(List.of(0, 14, -1), List.of(key.functionOnRow(0), key.functionOnRow(14), key.functionOnRow(15)));
    }

    @Test
    void aNegativeRowShowsNothing() {
        assertEquals(-1, new MapKey(15).functionOnRow(-1));
    }

    @Test
    void nextPageMovesByAWholePage() {
        MapKey key = new MapKey(15);

        key.nextPage();

        assertEquals(15, key.functionOnRow(0));
        assertTrue(key.hasPreviousPage());
    }

    @Test
    void rowsPastTheLastNameShowNothing() {
        MapKey key = new MapKey(25);
        key.nextPage();
        key.nextPage();

        assertEquals(List.of(59, -1), List.of(key.functionOnRow(9), key.functionOnRow(10)));
    }

    @Test
    void nextPageStopsOnTheLastPage() {
        MapKey key = new MapKey(30);
        key.nextPage();

        key.nextPage();

        assertEquals(30, key.firstRow());
        assertFalse(key.hasNextPage());
    }

    @Test
    void previousPageStopsOnTheFirstPage() {
        MapKey key = new MapKey(15);

        key.previousPage();

        assertEquals(0, key.firstRow());
        assertFalse(key.hasPreviousPage());
    }

    @Test
    void nothingFlashesUntilANameIsClicked() {
        MapKey key = new MapKey(15);

        assertEquals(-1, key.flashing());
        assertFalse(key.lit());
    }

    @Test
    void selectingANameFlashesItsKind() {
        MapKey key = new MapKey(15);

        key.select(5);

        assertEquals(5, key.flashing());
    }

    @Test
    void theFlashIsLitFiveTicksOutOfTen() {
        MapKey key = new MapKey(15);
        key.select(5);
        boolean litAtStart = key.lit();
        tick(key, 5);

        assertTrue(litAtStart);
        assertFalse(key.lit());
    }

    @Test
    void theFlashEndsAfterFiftyTicks() {
        MapKey key = new MapKey(15);
        key.select(5);

        tick(key, MapKey.FLASH_TICKS + 1);

        assertEquals(-1, key.flashing());
    }

    private static void tick(MapKey key, int ticks) {
        for (int i = 0; i < ticks; i++) {
            key.tick();
        }
    }
}
