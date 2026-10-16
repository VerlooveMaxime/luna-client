import idlers.worldmap.BitmapFont;
import idlers.worldmap.LabelFonts;
import idlers.worldmap.LoadedMap;
import idlers.worldmap.MapData;
import idlers.worldmap.MapDataDecoder;
import idlers.worldmap.MediaSprites;
import idlers.worldmap.PickedTile;
import idlers.worldmap.Raster;
import idlers.worldmap.WorldMapAssets;
import idlers.worldmap.WorldMapWindow;
import sign.signlink;

import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Set;

/**
 * IdleRS: the world map window over the game view ({@link WorldMapWindow}), fed with the 2006 applet's
 * {@code worldmap.jag} from the cache directory. Loading runs once, on a daemon thread, at the first open. It covers
 * the part of the game view no panel covers, from the view's top-left corner: the whole 512 x 334 view in the fixed
 * mode, more of a larger window in the resizable mode.
 */
final class IdleWorldMap {

    static final String FILE_NAME = "worldmap.jag";

    private final WorldMapWindow window;
    private boolean loading;
    private Rectangle area;

    IdleWorldMap(JagFont uiFont, Rectangle area) {
        this.area = area;
        window = new WorldMapWindow(area.width, area.height, copy(uiFont));
    }

    /** Moves the map to a new area of the frame, a new layout's; it keeps where it looks and its zoom. */
    void place(Rectangle newArea) {
        area = newArea;
        window.resize(newArea.width, newArea.height);
    }

    boolean isOpen() {
        return window.isOpen();
    }

    /** Whether the open map is over frame point (x, y), so the game view under it takes no hover. */
    boolean covers(int frameX, int frameY) {
        return window.isOpen() && area.contains(frameX, frameY);
    }

    void open(int worldX, int worldY) {
        startLoading();
        window.open(worldX, worldY);
    }

    /** Opened by the server for the flow builder: the next still click on the map picks a tile. */
    void openToPick(int worldX, int worldY) {
        startLoading();
        window.openToPick(worldX, worldY);
    }

    /** The tile picked since the last call, or null; the client sends it to the server. */
    PickedTile takePicked() {
        return window.takePicked();
    }

    private void startLoading() {
        if (!loading) {
            loading = true;
            Thread loader = new Thread(this::load, "world-map-loader");
            loader.setDaemon(true);
            loader.start();
        }
    }

    void close() {
        window.close();
    }

    /** A latched click in frame coordinates ({@code button} 1 left, 2 right); true when the map took it. */
    boolean press(int button, int frameX, int frameY) {
        return button != 0 && window.press(frameX - area.x, frameY - area.y, button == 1);
    }

    void tick(int frameMouseX, int frameMouseY, boolean leftHeld) {
        if (window.isOpen()) {
            window.mouse(frameMouseX - area.x, frameMouseY - area.y, leftHeld);
            window.tick();
        }
    }

    /** Draws into the top-left of the current game-view pixel buffer, {@code viewWidth} wide, when open. */
    void draw(int[] pixels, int viewWidth, int playerWorldX, int playerWorldY) {
        if (window.isOpen()) {
            window.draw(new Raster(pixels, area.width, area.height, viewWidth), playerWorldX, playerWorldY);
        }
    }

    private void load() {
        try {
            Archive archive = new Archive(Files.readAllBytes(Path.of(signlink.findcachedir(), FILE_NAME)));
            MapData data = MapDataDecoder.decode(archive::get);
            byte[] index = archive.get("index.dat");
            WorldMapAssets assets = new WorldMapAssets(
                    MediaSprites.decode(archive.get("mapscene.dat"), index),
                    MediaSprites.decode(archive.get("mapfunction.dat"), index),
                    LabelFonts.create(Set.of(GraphicsEnvironment.getLocalGraphicsEnvironment()
                            .getAvailableFontFamilyNames())));
            window.loaded(LoadedMap.build(data, assets));
        } catch (NoSuchFileException e) {
            window.failed("No world map data: " + FILE_NAME + " is missing\nfrom luna-client/cache/ (see README).");
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            window.failed("The world map could not be loaded:\n" + e);
        }
    }

    private static BitmapFont copy(JagFont font) {
        BitmapFont.Glyph[] glyphs = new BitmapFont.Glyph[font.glyphData.length];
        for (int c = 0; c < glyphs.length; c++) {
            glyphs[c] = new BitmapFont.Glyph(font.glyphData[c], font.glyphWidth[c],
                    font.glyphHeight[c], font.glyphAttribute1_1503[c], font.glyphAttribute2_1504[c] - font.anInt1506,
                    font.anIntArray1505[c]);
        }
        return new BitmapFont(glyphs, font.anInt1506, font.anInt1506 + 3);
    }
}
