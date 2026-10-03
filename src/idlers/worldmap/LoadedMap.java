package idlers.worldmap;

/** Everything the window needs once loading is over, built off the game thread: data, assets and the overview. */
public record LoadedMap(MapData data, WorldMapAssets assets, Raster overview) {

    static final int OVERVIEW_HEIGHT = 100;

    public static LoadedMap build(MapData data, WorldMapAssets assets) {
        Raster overview = Raster.blank(overviewWidth(data), OVERVIEW_HEIGHT);
        MapRenderer.renderTerrain(overview, data, 0, 0, data.width(), data.height(), assets.mapscenes());
        return new LoadedMap(data, assets, overview);
    }

    static int overviewWidth(MapData data) {
        return OVERVIEW_HEIGHT * data.width() / data.height();
    }
}
