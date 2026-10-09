package idlers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelFitTest {

    /** A quarter turn in the client's angles. */
    private static final int QUARTER = 512;

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
        assertEquals(2845, ModelFit.fillingZoom(new int[]{100, -100}, new int[2], new int[2], 2, 0, 0, 40, 40));
    }

    @Test
    void theTallerSideOfTheWidgetLeavesRoomToTheTighterOne() {
        assertEquals(2845, ModelFit.fillingZoom(new int[]{100, -100}, new int[2], new int[2], 2, 0, 0, 40, 80));
    }

    @Test
    void heightIsFittedToTheWidgetsHeight() {
        assertEquals(2845, ModelFit.fillingZoom(new int[2], new int[]{100, -100}, new int[2], 2, 0, 0, 80, 40));
    }

    @Test
    void aModelTurnedSideOnShowsItsLengthInsteadOfItsWidth() {
        // A long, thin model along z: turned a quarter, its 400 units span the widget.
        assertEquals(5689, ModelFit.fillingZoom(new int[2], new int[2], new int[]{200, -200}, 2, 0, QUARTER, 40, 40));
    }

    @Test
    void aModelSeenEndOnOnlyKeepsItsNearestPointInFrontOfTheCamera() {
        // Straight on, the same model is a point: the camera only stays 50 units in front of its near end.
        assertEquals(250, ModelFit.fillingZoom(new int[2], new int[2], new int[]{200, -200}, 2, 0, 0, 40, 40));
    }

    @Test
    void aModelSeenFromAboveShowsItsDepthAsHeight() {
        assertEquals(5689, ModelFit.fillingZoom(new int[2], new int[2], new int[]{200, -200}, 2, QUARTER, 0, 40, 40));
    }

    @Test
    void aPartNearerTheCameraWeighsMore() {
        // The nearer of two equally wide points needs the camera further back.
        int far = ModelFit.fillingZoom(new int[]{100}, new int[1], new int[]{100}, 1, 0, 0, 40, 40);
        int near = ModelFit.fillingZoom(new int[]{100}, new int[1], new int[]{-100}, 1, 0, 0, 40, 40);

        assertEquals(200, near - far);
    }

    @Test
    void aModelBiggerThanTheReferenceFillsTheWidget() {
        int[] xs = {300, -300};

        assertEquals(ModelFit.fillingZoom(xs, new int[2], new int[2], 2, 0, 0, 40, 40), ModelFit.zoom(xs, new int[2], new int[2], 2, 0, 0, 40, 40));
    }

    @Test
    void aSmallerModelKeepsItsSizeBesideTheReference() {
        // As far back as a model of 160 units needs: 512 * 160 / 18 = 4551.1; this one is 120 units, three-quarters.
        assertEquals(4552, ModelFit.zoom(new int[]{120, -120}, new int[2], new int[2], 2, 0, 0, 40, 40));
    }

    @Test
    void aTinyModelStillTakesHalfTheWidget() {
        int[] xs = {20, -20};

        assertEquals(2 * ModelFit.fillingZoom(xs, new int[2], new int[2], 2, 0, 0, 40, 40), ModelFit.zoom(xs, new int[2], new int[2], 2, 0, 0, 40, 40));
    }
}
