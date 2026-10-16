package idlers;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** The minimap image at (550, 4), where the fixed mode puts it, unless a test moves it. */
class CompassMenuTest {

    private static final int AREA_X = 550;
    private static final int AREA_Y = 4;

    @Test
    void theCompassCentreIsOnTheCompass() {
        assertTrue(CompassMenu.contains(566, 20, AREA_X, AREA_Y));
    }

    @Test
    void theDiscReachesItsEdgesButNotItsCorners() {
        assertTrue(CompassMenu.contains(551, 20, AREA_X, AREA_Y));
        assertFalse(CompassMenu.contains(551, 5, AREA_X, AREA_Y));
    }

    @Test
    void theMinimapBesideTheCompassIsNotTheCompass() {
        assertFalse(CompassMenu.contains(590, 20, AREA_X, AREA_Y));
    }

    @Test
    void theCompassMovesWithTheMinimapItsLayoutPlaces() {
        assertEquals(List.of(true, false), List.of(CompassMenu.contains(1044, 16, 1028, 0),
                CompassMenu.contains(566, 20, 1028, 0)));
    }

    @Test
    void theMenuIsCentredOnTheClickInsideTheMinimapImage() {
        assertEquals(List.of(10, 16), List.of(CompassMenu.menuX(610, 100, AREA_X), CompassMenu.menuY(20, 67, AREA_Y)));
    }

    @Test
    void theMenuStaysInsideTheMinimapImage() {
        assertEquals(List.of(0, 72, 0, 89), List.of(CompassMenu.menuX(560, 100, AREA_X),
                CompassMenu.menuX(720, 100, AREA_X), CompassMenu.menuY(0, 67, AREA_Y), CompassMenu.menuY(200, 67, AREA_Y)));
    }

    @Test
    void theMenuIsPlacedInsideAMovedMinimapImage() {
        assertEquals(List.of(10, 20), List.of(CompassMenu.menuX(1088, 100, 1028), CompassMenu.menuY(20, 67, 0)));
    }

    @Test
    void theActionsAreClientOnlyIdsTheClientLeavesFree() {
        assertTrue(CompassMenu.FACE_NORTH_ACTION > 1000 && CompassMenu.WORLD_MAP_ACTION < 2000);
        assertNotEquals(CompassMenu.FACE_NORTH_ACTION, CompassMenu.WORLD_MAP_ACTION);
    }
}
