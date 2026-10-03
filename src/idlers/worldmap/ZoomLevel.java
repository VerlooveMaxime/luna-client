package idlers.worldmap;

/**
 * The applet's four zoom buttons. {@code factor} is its internal zoom: the view spans {@code 2 * pixels / factor}
 * tiles, so 100% draws a tile 4 pixels wide, like the minimap.
 */
public enum ZoomLevel {
    P37("37%", 3.0),
    P50("50%", 4.0),
    P75("75%", 6.0),
    P100("100%", 8.0);

    private final String label;
    private final double factor;

    ZoomLevel(String label, double factor) {
        this.label = label;
        this.factor = factor;
    }

    public String label() {
        return label;
    }

    public double factor() {
        return factor;
    }
}
