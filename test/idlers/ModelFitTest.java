package idlers;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelFitTest {

    /** A quarter turn in the client's angles. */
    private static final int QUARTER = 512;

    /** A triangle 0-1-2 and a triangle 2-3-4: three vertices of their own each, vertex 2 shared. */
    private static final int[] XS = {1, 2, 3, 4, 5};
    private static final int[] FACES_A = {0, 2};
    private static final int[] FACES_B = {1, 3};
    private static final int[] FACES_C = {2, 4};

    private static ModelFit.Vertices framed(int[] transparency) {
        return ModelFit.framed(XS, XS, XS, 5, FACES_A, FACES_B, FACES_C, transparency, 2);
    }

    @Test
    void aSeeThroughFacesOwnVerticesAreNotFramed() {
        ModelFit.Vertices framed = framed(new int[]{0, 144});

        assertEquals(List.of(3, List.of(1, 2, 3)), List.of(framed.count(), Arrays.stream(framed.xs()).boxed().toList()));
    }

    @Test
    void aFaceLessThanHalfSeeThroughIsFramed() {
        assertEquals(5, framed(new int[]{0, ModelFit.SEE_THROUGH - 1}).count());
    }

    @Test
    void aModelWithoutTransparencyIsFramedWhole() {
        assertEquals(5, framed(null).count());
    }

    @Test
    void aModelSeeThroughEverywhereIsFramedWhole() {
        ModelFit.Vertices framed = framed(new int[]{200, 200});

        assertEquals(List.of(5, XS), List.of(framed.count(), framed.xs()));
    }

    @Test
    void theMiddleOfTheBoundsMovesToTheOrigin() {
        // A box from (0, -200, -10) to (40, 0, 10): an npc standing on the origin, 200 units tall, its y pointing down.
        ModelFit.Centre centre = ModelFit.centre(new int[]{0, 40, 0, 40}, new int[]{0, 0, -200, -200}, new int[]{-10, 10, -10, 10}, 4);

        assertEquals(new ModelFit.Centre(-20, 100, 0), centre);
    }

    @Test
    void onlyTheModelsOwnVerticesCount() {
        assertEquals(-20, ModelFit.centre(new int[]{0, 40, 999}, new int[3], new int[3], 2).dx());
    }

    @Test
    void aModelSeenStraightOnFillsTheWidgetsWidth() {
        // 512 * 100 / (40 * 0.9 / 2) = 2844.4: the point 100 units to the side lands 18 pixels from the middle.
        assertEquals(2845, ModelFit.zoom(new int[]{100, -100}, new int[2], new int[2], 2, 0, 0, 40, 40));
    }

    @Test
    void theTallerSideOfTheWidgetLeavesRoomToTheTighterOne() {
        assertEquals(2845, ModelFit.zoom(new int[]{100, -100}, new int[2], new int[2], 2, 0, 0, 40, 80));
    }

    @Test
    void heightIsFittedToTheWidgetsHeight() {
        assertEquals(2845, ModelFit.zoom(new int[2], new int[]{100, -100}, new int[2], 2, 0, 0, 80, 40));
    }

    @Test
    void aModelTurnedSideOnShowsItsLengthInsteadOfItsWidth() {
        // A long, thin model along z: turned a quarter, its 400 units span the widget.
        assertEquals(5689, ModelFit.zoom(new int[2], new int[2], new int[]{200, -200}, 2, 0, QUARTER, 40, 40));
    }

    @Test
    void aModelSeenEndOnOnlyKeepsItsNearestPointInFrontOfTheCamera() {
        // Straight on, the same model is a point: the camera only stays 50 units in front of its near end.
        assertEquals(250, ModelFit.zoom(new int[2], new int[2], new int[]{200, -200}, 2, 0, 0, 40, 40));
    }

    @Test
    void aModelSeenFromAboveShowsItsDepthAsHeight() {
        assertEquals(5689, ModelFit.zoom(new int[2], new int[2], new int[]{200, -200}, 2, QUARTER, 0, 40, 40));
    }

    @Test
    void aPartNearerTheCameraWeighsMore() {
        // The nearer of two equally wide points needs the camera further back.
        int far = ModelFit.zoom(new int[]{100}, new int[1], new int[]{100}, 1, 0, 0, 40, 40);
        int near = ModelFit.zoom(new int[]{100}, new int[1], new int[]{-100}, 1, 0, 0, 40, 40);

        assertEquals(200, near - far);
    }

    @Test
    void aSmallModelFillsTheWidgetAsABigOneDoes() {
        int small = ModelFit.zoom(new int[]{20, -20}, new int[2], new int[2], 2, 0, 0, 40, 40);
        int big = ModelFit.zoom(new int[]{200, -200}, new int[2], new int[2], 2, 0, 0, 40, 40);

        assertEquals(10 * small, big, 10);
    }
}
