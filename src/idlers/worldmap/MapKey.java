package idlers.worldmap;

import java.util.List;

/**
 * The applet's Key: one name per map function id, shown a page at a time; clicking a name makes every icon of that
 * kind flash for {@link #FLASH_TICKS} client ticks, lit five ticks out of every ten.
 */
public final class MapKey {

    /** The 2006 applet's names (mapview build of 2006-03-28), indexed by map function id. */
    public static final List<String> NAMES = List.of(
            "General Store", "Sword Shop", "Magic Shop", "Axe Shop", "Helmet Shop", "Bank", "Quest Start",
            "Amulet Shop", "Mining Site", "Furnace", "Anvil", "Combat Training", "Dungeon", "Staff Shop",
            "Platebody Shop", "Platelegs Shop", "Scimitar Shop", "Archery Shop", "Shield Shop", "Altar", "Herbalist",
            "Jewelery", "Gem Shop", "Crafting Shop", "Candle Shop", "Fishing Shop", "Fishing Spot", "Clothes Shop",
            "Apothecary", "Silk Trader", "Kebab Seller", "Pub/Bar", "Mace Shop", "Tannery", "Rare Trees",
            "Spinning Wheel", "Food Shop", "Cookery Shop", "Mini-Game", "Water Source", "Cooking Range", "Skirt Shop",
            "Potters Wheel", "Windmill", "Mining Shop", "Chainmail Shop", "Silver Shop", "Fur Trader", "Spice Shop",
            "Agility Training", "Vegetable Store", "Slayer Master", "Hair Dressers", "Farming patch", "Makeover Mage",
            "Guide", "Transportation", "Farming shop", "Loom", "Brewery");

    static final int FLASH_TICKS = 50;
    private static final int FLASH_PERIOD = 10;
    private static final int FLASH_LIT = 5;

    private final int rowsPerPage;
    private int firstRow;
    private int selected = -1;
    private int flashTicks;

    public MapKey(int rowsPerPage) {
        this.rowsPerPage = rowsPerPage;
    }

    public int rowsPerPage() {
        return rowsPerPage;
    }

    public int firstRow() {
        return firstRow;
    }

    /** The function id shown on {@code row} of the current page, or -1 below the last name. */
    public int functionOnRow(int row) {
        int function = firstRow + row;
        return row >= 0 && row < rowsPerPage && function < NAMES.size() ? function : -1;
    }

    public boolean hasNextPage() {
        return firstRow + rowsPerPage < NAMES.size();
    }

    public boolean hasPreviousPage() {
        return firstRow > 0;
    }

    public void nextPage() {
        if (hasNextPage()) {
            firstRow += rowsPerPage;
        }
    }

    public void previousPage() {
        firstRow = Math.max(0, firstRow - rowsPerPage);
    }

    public void select(int function) {
        selected = function;
        flashTicks = FLASH_TICKS;
    }

    /** The function being flashed, or -1 once the flash is over. */
    public int flashing() {
        return flashTicks > 0 ? selected : -1;
    }

    public boolean lit() {
        return flashTicks > 0 && flashTicks % FLASH_PERIOD < FLASH_LIT;
    }

    public void tick() {
        if (flashTicks > 0) {
            flashTicks--;
        }
    }
}
