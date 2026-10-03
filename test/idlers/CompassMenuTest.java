package idlers;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompassMenuTest {

    @Test
    void theCompassCentreIsOnTheCompass() {
        assertTrue(CompassMenu.contains(566, 20));
    }

    @Test
    void theDiscReachesItsEdgesButNotItsCorners() {
        assertTrue(CompassMenu.contains(551, 20));
        assertFalse(CompassMenu.contains(551, 5));
    }

    @Test
    void theMinimapBesideTheCompassIsNotTheCompass() {
        assertFalse(CompassMenu.contains(590, 20));
    }

    @Test
    void theMenuIsCentredOnTheClickInsideTheMinimapImage() {
        assertEquals(List.of(10, 16), List.of(CompassMenu.menuX(610, 100), CompassMenu.menuY(20, 67)));
    }

    @Test
    void theMenuStaysInsideTheMinimapImage() {
        assertEquals(List.of(0, 72, 0, 89), List.of(CompassMenu.menuX(560, 100), CompassMenu.menuX(720, 100),
                CompassMenu.menuY(0, 67), CompassMenu.menuY(200, 67)));
    }

    @Test
    void theActionsAreClientOnlyIdsTheClientLeavesFree() {
        assertTrue(CompassMenu.FACE_NORTH_ACTION > 1000 && CompassMenu.WORLD_MAP_ACTION < 2000);
        assertNotEquals(CompassMenu.FACE_NORTH_ACTION, CompassMenu.WORLD_MAP_ACTION);
    }
}
