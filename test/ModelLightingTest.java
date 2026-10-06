import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** A one-face model lit like an item icon: initLighting(64, 768, -50, -10, -50). */
class ModelLightingTest {

    private static final int MODEL_ID = 0;
    private static final int COLOUR = (5 << 10) | (4 << 7) | 64;

    /**
     * Vertices (0,0,0), (0,10,0), (0,0,10) in the 377 model format: one face of the given render type, its normal along
     * +x, so it faces away from the light at (-50, -10, -50).
     */
    private static Model triangle(int renderType) {
        byte[] data = {
            0, 2, 6,            // vertex flags: y moves for the second vertex, y and z for the third
            1,                  // face compression type: three indices follow
            (byte) renderType,  // face render type
            64, 65, 65,         // face indices 0, 1, 2 as signed smart deltas
            (byte) (COLOUR >> 8), (byte) COLOUR,
            74, 54,             // y deltas +10, -10
            74,                 // z delta +10
            0, 3, 0, 1,         // footer: 3 vertices, 1 face,
            0, 1, 0, 0, 0, 0,   // no texture faces, render types, global priority 0, no alpha or bones,
            0, 0, 0, 2, 0, 1, 0, 3, // x, y, z and face index data lengths
        };
        Model.modelHeaders = new ModelHeader[1];
        Model.unpackModelHeader(data, MODEL_ID, (byte) 7);
        return Model.forId(MODEL_ID);
    }

    @Test
    void aFlatFaceKeepsTheLightOfItsOwnNormal() {
        Model model = triangle(1);

        model.initLighting(64, 768, -50, -10, -50, true);

        // Intensity 64 + (-50 * 256) / (213 + 106) = 24, so lightness 64 * 24 >> 7 = 12; ambient alone would give 32.
        assertEquals(12, model.faceColorsA[0] & 0x7f);
    }
}
