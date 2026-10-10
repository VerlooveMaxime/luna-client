package idlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchGridTest {

    private static SearchGrid grid(int count) {
        return new SearchGrid(3, count);
    }

    static IntStream everyScrollWithinARow() {
        return IntStream.range(0, SearchGrid.CELL_HEIGHT);
    }

    @Test
    void aCellIsAColumnsShareOfTheWidth() {
        assertEquals(List.of(154, 231), List.of(grid(4).cellWidth(), new SearchGrid(2, 4).cellWidth()));
    }

    @Test
    void theGridKeepsItsColumns() {
        assertEquals(2, new SearchGrid(2, 4).columns());
    }

    @Test
    void theContentIsEveryRowOfCellsAndThePadding() {
        assertEquals(3 * SearchGrid.CELL_HEIGHT + 2 * SearchGrid.PAD, grid(7).contentHeight());
    }

    @Test
    void twoRowsOfCellsFitWithoutScrolling() {
        assertFalse(grid(6).scrolls());
    }

    @Test
    void aThirdRowOfCellsScrolls() {
        assertTrue(grid(7).scrolls());
    }

    @Test
    void theScrollStopsAtTheLastRow() {
        assertEquals(88 - SearchGrid.HEIGHT, grid(7).clampScroll(500));
    }

    @Test
    void theScrollNeverGoesAboveTheTop() {
        assertEquals(0, grid(7).clampScroll(-3));
    }

    @Test
    void aGridThatDoesNotScrollStaysAtTheTop() {
        assertEquals(0, grid(2).clampScroll(10));
    }

    @Test
    void cellsFillRowsLeftToRightUnderThePadding() {
        assertEquals(new SearchGrid.Cell(154, SearchGrid.TOP + SearchGrid.PAD + SearchGrid.CELL_HEIGHT), grid(7).cell(4, 0));
    }

    @Test
    void scrollingMovesCellsUp() {
        assertEquals(new SearchGrid.Cell(0, SearchGrid.TOP + SearchGrid.PAD - 10), grid(7).cell(0, 10));
    }

    @Test
    void aCellsIconAndTextSitInsideIt() {
        SearchGrid.Cell cell = new SearchGrid.Cell(154, 47);

        assertEquals(List.of(156, 49, 184), List.of(cell.iconX(), cell.iconY(), cell.textX()));
    }

    @Test
    void cellsPartlyInTheAreaAreDrawn() {
        assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8), grid(12).visible(0));
    }

    @Test
    void cellsScrolledAboveTheAreaAreNotDrawn() {
        assertEquals(List.of(3, 4, 5, 6, 7, 8, 9, 10, 11), grid(12).visible(SearchGrid.CELL_HEIGHT + SearchGrid.PAD));
    }

    @Test
    void aShortLastRowDrawsOnlyItsCells() {
        assertEquals(List.of(0, 1, 2, 3), grid(4).visible(0));
    }

    @Test
    void anEmptyGridDrawsNothing() {
        assertEquals(List.of(), grid(0).visible(0));
    }

    @Test
    void onlyIconsInsideTheAreaShow() {
        assertEquals(List.of(0, 1, 2, 3, 4, 5), grid(12).withIcons(0));
    }

    @Test
    void anIconScrolledAboveTheAreaDoesNotShowThoughItsCellDoes() {
        assertEquals(List.of(3, 4, 5, 6, 7, 8), grid(12).withIcons(SearchGrid.CELL_HEIGHT));
    }

    @ParameterizedTest
    @MethodSource("everyScrollWithinARow")
    void neverMoreIconsShowThanThereAreWidgetsForThem(int scroll) {
        assertTrue(grid(30).withIcons(scroll).size() <= SearchGrid.MOST_ICONS);
    }

    @Test
    void theWindowAtTheTopIsTheRowsInViewAndOneScreenBelow() {
        assertEquals(new SearchGrid.Window(0, 15), grid(100).window(0));
    }

    @Test
    void theWindowKeepsOneScreenAboveTheView() {
        assertEquals(new SearchGrid.Window(9, 30), grid(100).window(5 * SearchGrid.CELL_HEIGHT + SearchGrid.PAD));
    }

    @Test
    void theWindowEndsWithTheResults() {
        assertEquals(new SearchGrid.Window(0, 8), grid(8).window(0));
    }

    @Test
    void aWindowHoldsThePlacesFromItsStartToBeforeItsEnd() {
        SearchGrid.Window window = new SearchGrid.Window(3, 6);

        assertEquals(List.of(false, true, true, false), List.of(window.contains(2), window.contains(3), window.contains(5), window.contains(6)));
    }

    @Test
    void aNewSearchAsksForTheWindowAtTheTopOfTheWidestGrid() {
        assertEquals(grid(100).window(0).to(), SearchGrid.firstPage());
    }

    @Test
    void aPointInACellNamesIt() {
        assertEquals(OptionalInt.of(4), grid(7).at(160, 50, 0));
    }

    @Test
    void aPointInAScrolledCellNamesIt() {
        assertEquals(OptionalInt.of(7), grid(9).at(160, 50, SearchGrid.CELL_HEIGHT));
    }

    @Test
    void aPointLeftOfTheGridNamesNoCell() {
        assertEquals(OptionalInt.empty(), grid(7).at(-1, 50, 0));
    }

    @Test
    void aPointRightOfTheColumnsNamesNoCell() {
        assertEquals(OptionalInt.empty(), grid(7).at(462, 50, 0));
    }

    @Test
    void aPointOnTheTitleNamesNoCell() {
        assertEquals(OptionalInt.empty(), grid(7).at(10, SearchGrid.TOP - 1, 0));
    }

    @Test
    void aPointBelowTheAreaNamesNoCell() {
        assertEquals(OptionalInt.empty(), grid(7).at(10, SearchGrid.TOP + SearchGrid.HEIGHT, 0));
    }

    @Test
    void aPointInThePaddingAboveTheFirstRowNamesNoCell() {
        assertEquals(OptionalInt.empty(), grid(7).at(10, SearchGrid.TOP, 0));
    }

    @Test
    void aPointPastTheLastCellNamesNoCell() {
        assertEquals(OptionalInt.empty(), grid(4).at(400, 50, 0));
    }

    @Test
    void theTitleBandHoldsTheTitlesDescendersWithARowToSpare() {
        assertTrue(SearchWidgets.TITLE_Y + SearchWidgets.TITLE_DESCENT < SearchGrid.TITLE_BAND);
    }

    @Test
    void theTitleBandStaysAboveTheFirstRow() {
        assertTrue(SearchGrid.TITLE_BAND <= SearchGrid.TOP + SearchGrid.PAD);
    }
}
