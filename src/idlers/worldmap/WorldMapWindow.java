package idlers.worldmap;

import java.util.List;

/**
 * The world map window over the game view: open or closed, the loaded map once the loader thread hands it over, the
 * view, the Key and the Overview, mouse handling and drawing. Coordinates are view pixels, (0, 0) top left.
 */
public final class WorldMapWindow {

    static final ZoomLevel FIRST_ZOOM = ZoomLevel.P50;
    static final String LOADING_TEXT = "Please wait... Rendering Map";

    static final int BACKGROUND_RGB = 0;
    static final int TEXT_RGB = 0xffffff;
    static final int PANEL_BORDER_LIGHT = 0x999999;
    static final int PANEL_FILL = 0x777777;
    static final int PANEL_BORDER_DARK = 0x555555;
    static final int BUTTON_BORDER_LIGHT = 0x887755;
    static final int BUTTON_FILL = 0x776644;
    static final int BUTTON_BORDER_DARK = 0x665533;
    static final int ACTIVE_BORDER_LIGHT = 0xaa0000;
    static final int ACTIVE_FILL = 0x990000;
    static final int ACTIVE_BORDER_DARK = 0x880000;
    static final int KEY_HOVER_RGB = 0xbbaaaa;
    static final int KEY_FLASH_RGB = 0xffff00;
    static final int VIEW_BOX_RGB = 0xff0000;
    static final int VIEW_BOX_ALPHA = 0x80;
    static final int PLAYER_RGB = 0xffffff;
    static final int COORDINATES_BASELINE = 16;
    private static final int KEY_ICON_X = 3;
    private static final int KEY_NAME_X = 20;
    private static final int KEY_NAME_BASELINE = 13;
    private static final int OVERVIEW_DOT_RADIUS = 2;
    private static final int SOLID = 256;

    private final int viewWidth;
    private final int viewHeight;
    private final BitmapFont font;
    private volatile LoadedMap pending;
    private volatile String failure;
    private LoadedMap map;
    private MapView view;
    private WorldMapLayout layout;
    private MapKey key;
    private Raster terrain;
    private List<MapRenderer.VisibleFunction> functions = List.of();
    private Bounds terrainBounds;
    private boolean open;
    private boolean keyShown;
    private boolean overviewShown;
    private boolean overviewDragging;
    private boolean centreRequested;
    private int requestedX;
    private int requestedY;
    private int mouseX = -1;
    private int mouseY = -1;

    public WorldMapWindow(int viewWidth, int viewHeight, BitmapFont font) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.font = font;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isLoaded() {
        return map != null;
    }

    /** Opens the window centred on the given tile; before the map has loaded, it is centred once it arrives. */
    public void open(int worldX, int worldY) {
        open = true;
        requestedX = worldX;
        requestedY = worldY;
        centreRequested = true;
        adoptLoadedMap();
    }

    public void close() {
        open = false;
        overviewDragging = false;
        if (view != null) {
            view.endDrag();
        }
    }

    /** Called by the loader thread; the game thread picks the map up on its next tick or frame. */
    public void loaded(LoadedMap loadedMap) {
        pending = loadedMap;
    }

    /** Called by the loader thread when the map cannot be loaded; shown in place of the map. */
    public void failed(String message) {
        failure = message;
    }

    private void adoptLoadedMap() {
        LoadedMap arrived = pending;
        if (map == null && arrived != null) {
            map = arrived;
            view = new MapView(map.data().width(), map.data().height(), viewWidth, viewHeight, FIRST_ZOOM);
            layout = WorldMapLayout.of(viewWidth, viewHeight, map.overview().width(), map.overview().height());
            key = new MapKey(layout.keyRows());
            terrain = Raster.blank(viewWidth, viewHeight);
        }
        if (map != null && centreRequested) {
            view.centreOn(map.data().column(requestedX), map.data().row(requestedY));
            centreRequested = false;
        }
    }

    /**
     * A click at (x, y). Returns whether the window took it: any click inside the view while open, so nothing below
     * (the 3D world, an interface) sees it. Only the left button does anything, as in the applet.
     */
    public boolean press(int x, int y, boolean left) {
        if (!open || !inView(x, y)) {
            return false;
        }
        adoptLoadedMap();
        if (left) {
            pressLeft(x, y);
        }
        return true;
    }

    private void pressLeft(int x, int y) {
        if (closeButton().contains(x, y)) {
            close();
        } else if (map != null) {
            pressMap(x, y);
        }
    }

    private void pressMap(int x, int y) {
        int zoom = zoomButtonAt(x, y);
        if (zoom >= 0) {
            view.zoomTo(ZoomLevel.values()[zoom]);
        } else if (layout.keyToggle().contains(x, y)) {
            keyShown = !keyShown;
        } else if (layout.overviewToggle().contains(x, y)) {
            overviewShown = !overviewShown;
        } else if (keyShown && layout.keyPanel().contains(x, y)) {
            pressKey(x, y);
        } else if (overviewShown && layout.overview().contains(x, y)) {
            overviewDragging = true;
            jumpToOverview(x, y);
        } else {
            view.startDrag(x, y);
        }
    }

    private int zoomButtonAt(int x, int y) {
        for (int i = 0; i < layout.zoomButtons().size(); i++) {
            if (layout.zoomButtons().get(i).contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    private void pressKey(int x, int y) {
        if (layout.previousPage().contains(x, y)) {
            key.previousPage();
        } else if (layout.nextPage().contains(x, y)) {
            key.nextPage();
        } else {
            int function = key.functionOnRow(layout.keyRowAt(x, y));
            if (function >= 0) {
                key.select(function);
            }
        }
    }

    private void jumpToOverview(int x, int y) {
        WorldMapLayout.Rect overview = layout.overview();
        view.centreOn((x - overview.x()) * map.data().width() / overview.width(),
                (y - overview.y()) * map.data().height() / overview.height());
    }

    /** The mouse this tick: where it is (anywhere, view pixels) and whether the left button is held. */
    public void mouse(int x, int y, boolean held) {
        mouseX = x;
        mouseY = y;
        if (map == null) {
            return;
        }
        if (!held) {
            view.endDrag();
            overviewDragging = false;
        } else if (overviewDragging && layout.overview().contains(x, y)) {
            jumpToOverview(x, y);
        } else {
            view.dragTo(x, y);
        }
    }

    /** One client tick: zoom easing, the key's flash. */
    public void tick() {
        adoptLoadedMap();
        if (map != null) {
            view.tick();
            key.tick();
        }
    }

    public void draw(Raster target, int playerWorldX, int playerWorldY) {
        adoptLoadedMap();
        if (map == null) {
            drawNotice(target, failure == null ? List.of(LOADING_TEXT) : List.of(failure.split("\n")));
        } else {
            drawMap(target, playerWorldX, playerWorldY);
        }
        drawButton(target, closeButton(), "Close", false);
    }

    private WorldMapLayout.Rect closeButton() {
        return layout != null ? layout.close() : WorldMapLayout.of(viewWidth, viewHeight, 0, 0).close();
    }

    private boolean inView(int x, int y) {
        return x >= 0 && y >= 0 && x < viewWidth && y < viewHeight;
    }

    private void drawNotice(Raster target, List<String> lines) {
        target.clear(BACKGROUND_RGB);
        int baseline = viewHeight / 2 - font.lineHeight() * (lines.size() - 1) / 2;
        for (String line : lines) {
            font.drawCentred(target, line, viewWidth / 2, baseline, TEXT_RGB, true);
            baseline += font.lineHeight();
        }
    }

    private void drawMap(Raster target, int playerWorldX, int playerWorldY) {
        renderTerrainIfMoved();
        target.copyFrom(terrain);
        MapSprite[] icons = map.assets().functions();
        MapRenderer.drawFunctions(target, functions, icons);
        if (key.flashing() >= 0) {
            MapRenderer.drawFlash(target, functions, icons, key.flashing(), key.lit());
        }
        if (view.settled()) {
            MapRenderer.drawLabels(target, map.data(), view, map.assets().labelFonts());
        }
        drawPlayer(target, playerWorldX, playerWorldY);
        if (overviewShown) {
            drawOverview(target);
        }
        if (keyShown) {
            drawKey(target);
        }
        drawButton(target, layout.overviewToggle(), "Overview", false);
        drawButton(target, layout.keyToggle(), "Key", false);
        for (ZoomLevel level : ZoomLevel.values()) {
            drawButton(target, layout.zoomButtons().get(level.ordinal()), level.label(), view.target() == level);
        }
        drawHoveredTile(target);
    }

    /** The tiles a render covered, to tell whether the view has moved since. */
    private record Bounds(int left, int top, int right, int bottom) {
    }

    /** Re-renders the tiles only when the view moved or zoomed; returns whether it did. */
    boolean renderTerrainIfMoved() {
        Bounds bounds = new Bounds(view.left(), view.top(), view.right(), view.bottom());
        if (bounds.equals(terrainBounds)) {
            return false;
        }
        terrainBounds = bounds;
        terrain.clear(BACKGROUND_RGB);
        functions = MapRenderer.renderTerrain(terrain, map.data(), bounds.left(), bounds.top(), bounds.right(),
                bounds.bottom(), map.assets().mapscenes());
        return true;
    }

    private void drawPlayer(Raster target, int worldX, int worldY) {
        int column = map.data().column(worldX);
        int row = map.data().row(worldY);
        if (map.data().contains(column, row)) {
            int x = view.pixelX(column + 0.5);
            int y = view.pixelY(row + 0.5);
            target.fillRect(x - 3, y - 3, 7, 7, BACKGROUND_RGB);
            target.fillRect(x - 2, y - 2, 5, 5, PLAYER_RGB);
        }
    }

    private void drawOverview(Raster target) {
        WorldMapLayout.Rect area = layout.overview();
        Raster overview = map.overview();
        for (int y = 0; y < overview.height(); y++) {
            for (int x = 0; x < overview.width(); x++) {
                target.plot(area.x() + x, area.y() + y, overview.pixel(x, y));
            }
        }
        target.drawRect(area.x(), area.y(), area.width(), area.height(), BACKGROUND_RGB);
        int width = map.data().width();
        int height = map.data().height();
        int boxX = area.x() + area.width() * view.left() / width;
        int boxY = area.y() + area.height() * view.top() / height;
        int boxWidth = (view.right() - view.left()) * area.width() / width;
        int boxHeight = (view.bottom() - view.top()) * area.height() / height;
        target.fillRectAlpha(boxX, boxY, boxWidth, boxHeight, VIEW_BOX_RGB, VIEW_BOX_ALPHA);
        target.drawRect(boxX, boxY, boxWidth, boxHeight, VIEW_BOX_RGB);
        if (key.flashing() >= 0 && key.lit()) {
            for (MapData.FunctionPoint point : map.data().functionPoints()) {
                if (point.function() == key.flashing()) {
                    target.fillCircle(area.x() + area.width() * point.column() / width,
                            area.y() + area.height() * point.row() / height, OVERVIEW_DOT_RADIUS, KEY_FLASH_RGB, SOLID);
                }
            }
        }
    }

    private void drawKey(Raster target) {
        WorldMapLayout.Rect panel = layout.keyPanel();
        drawBox(target, panel, PANEL_BORDER_LIGHT, PANEL_FILL, PANEL_BORDER_DARK, "");
        drawBox(target, layout.previousPage(), PANEL_BORDER_LIGHT, PANEL_FILL, PANEL_BORDER_DARK, "Prev page");
        drawBox(target, layout.nextPage(), PANEL_BORDER_LIGHT, PANEL_FILL, PANEL_BORDER_DARK, "Next page");
        int hovered = key.functionOnRow(layout.keyRowAt(mouseX, mouseY));
        MapSprite[] icons = map.assets().functions();
        for (int row = 0; row < key.rowsPerPage(); row++) {
            int function = key.functionOnRow(row);
            if (function >= 0) {
                int y = layout.keyRowY(row);
                if (function < icons.length && icons[function] != null) {
                    target.drawSprite(icons[function], panel.x() + KEY_ICON_X, y);
                }
                String name = MapKey.NAMES.get(function);
                int rgb = TEXT_RGB;
                if (function == hovered) {
                    rgb = KEY_HOVER_RGB;
                }
                if (function == key.flashing() && key.lit()) {
                    rgb = KEY_FLASH_RGB;
                }
                font.drawShadowed(target, name, panel.x() + KEY_NAME_X, y + KEY_NAME_BASELINE, rgb);
            }
        }
    }

    private void drawButton(Raster target, WorldMapLayout.Rect button, String text, boolean active) {
        if (active) {
            drawBox(target, button, ACTIVE_BORDER_LIGHT, ACTIVE_FILL, ACTIVE_BORDER_DARK, text);
        } else {
            drawBox(target, button, BUTTON_BORDER_LIGHT, BUTTON_FILL, BUTTON_BORDER_DARK, text);
        }
    }

    /** The applet's {@code drawStringBox}: black outline, bevelled fill, centred shadowed text. */
    private void drawBox(Raster target, WorldMapLayout.Rect box, int light, int fill, int dark, String text) {
        target.drawRect(box.x(), box.y(), box.width(), box.height(), BACKGROUND_RGB);
        int x = box.x() + 1;
        int y = box.y() + 1;
        int width = box.width() - 2;
        int height = box.height() - 2;
        target.fillRect(x, y, width, height, fill);
        target.hline(x, y, width, light);
        target.vline(x, y, height, light);
        target.hline(x, y + height - 1, width, dark);
        target.vline(x + width - 1, y, height, dark);
        font.drawCentred(target, text, x + width / 2, y + height / 2 + 4, TEXT_RGB, true);
    }

    private void drawHoveredTile(Raster target) {
        String text = hoveredTileText();
        if (text != null) {
            font.drawCentred(target, text, viewWidth / 2, COORDINATES_BASELINE, TEXT_RGB, true);
        }
    }

    /** "x, y" of the tile under the mouse, or null when the mouse is off the map or over a button or panel. */
    String hoveredTileText() {
        if (!inView(mouseX, mouseY) || overButtonOrPanel(mouseX, mouseY)) {
            return null;
        }
        int column = view.columnAt(mouseX);
        int row = view.rowAt(mouseY);
        if (!map.data().contains(column, row)) {
            return null;
        }
        return map.data().worldX(column) + ", " + map.data().worldY(row);
    }

    private boolean overButtonOrPanel(int x, int y) {
        return layout.close().contains(x, y) || layout.keyToggle().contains(x, y)
                || layout.overviewToggle().contains(x, y) || zoomButtonAt(x, y) >= 0
                || keyShown && layout.keyPanel().contains(x, y)
                || overviewShown && layout.overview().contains(x, y);
    }

    MapView view() {
        return view;
    }

    MapKey key() {
        return key;
    }

    boolean keyShown() {
        return keyShown;
    }

    boolean overviewShown() {
        return overviewShown;
    }
}
