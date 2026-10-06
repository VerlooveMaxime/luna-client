package idlers;

import java.util.Map;

/**
 * The icon of the Idle tab (slot 7, which has none in the 377 sprite sheet): an hourglass in the client's browns and
 * golds, drawn from {@link #ART} so it needs no cache sprite. Pixels are 0xRRGGBB, 0 where the tab shows through.
 */
public final class IdleTabIcon {

    static final String[] ART = {
            " kkkkkkkkkkkkk ",
            "kwwwwwwwwwwwwwk",
            "kdddddddddddddk",
            " kkkkkkkkkkkkk ",
            "  kg       gk  ",
            "  kg       gk  ",
            "  kgSsssssSgk  ",
            "   kgSsssSgk   ",
            "    kgSsSgk    ",
            "     kgsgk     ",
            "      ksk      ",
            "     kgsgk     ",
            "    kg s gk    ",
            "   kg  s  gk   ",
            "  kg   s   gk  ",
            "  kg  sss  gk  ",
            "  kg sssss gk  ",
            "  kgSsssssSgk  ",
            " kkkkkkkkkkkkk ",
            "kwwwwwwwwwwwwwk",
            "kdddddddddddddk",
            " kkkkkkkkkkkkk ",
    };

    static final Map<Character, Integer> PALETTE = Map.of(
            'k', 0x2a2016, // outline
            'w', 0xd6a84a, // frame
            'd', 0x8a6424, // frame shade
            'g', 0x9fb4b8, // glass
            's', 0xf0d070, // sand
            'S', 0xc09838 // sand shade
    );

    public static final int WIDTH = ART[0].length();
    public static final int HEIGHT = ART.length;

    /** Where the icon goes in the tab strip image: centred by eye in tab 7's cell (Maxime, 2026-10-06), y 0 to 36. */
    public static final int X = 56;
    public static final int Y = (36 - HEIGHT) / 2;

    private IdleTabIcon() {
    }

    /** Row by row, {@link #WIDTH} pixels each. */
    public static int[] pixels() {
        int[] pixels = new int[WIDTH * HEIGHT];
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                char c = ART[y].charAt(x);
                pixels[y * WIDTH + x] = c == ' ' ? 0 : PALETTE.get(c);
            }
        }
        return pixels;
    }
}
