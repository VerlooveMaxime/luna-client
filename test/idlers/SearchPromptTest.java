package idlers;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchPromptTest {

    private static final int SERIAL = 4;

    /** Rows {@code offset} to {@code offset + count - 1} of results holding {@code total} rows in 3 columns. */
    private static SearchPage page(String query, int total, int offset, int count) {
        List<SearchRow> rows = IntStream.range(offset, offset + count)
                .mapToObj(place -> new SearchRow(place, "Row " + place, "", false, new WidgetPicture.None())).toList();
        return new SearchPage(SERIAL, query, total, 3, offset, rows);
    }

    /** A prompt showing the first 15 of 100 opening rows, the window at the top. */
    private static SearchPrompt opened() {
        SearchPrompt prompt = new SearchPrompt();
        prompt.open(SERIAL, "Which item?", "Nothing to pick");
        prompt.receive(page("", 100, 0, 15));
        return prompt;
    }

    private static SearchPrompt typed(String text, long at) {
        SearchPrompt prompt = opened();
        prompt.type(text, at);
        return prompt;
    }

    @Test
    void aPromptKeepsItsTitleAndSerial() {
        SearchPrompt prompt = opened();

        assertEquals(List.of("Which item?", SERIAL), List.of(prompt.title(), prompt.serial()));
    }

    @Test
    void fewerThanThreeLettersAskForTheOpeningRows() {
        assertEquals("", SearchPrompt.queryOf("oa"));
    }

    @Test
    void threeLettersAreAQuery() {
        assertEquals("oak", SearchPrompt.queryOf("oak"));
    }

    @Test
    void spacesAreNotLetters() {
        assertEquals("", SearchPrompt.queryOf(" o a "));
    }

    @Test
    void aQueryIsItsWordsInLowerCaseWhateverTheSpaces() {
        assertEquals("oak logs", SearchPrompt.queryOf("  Oak   LOGS "));
    }

    @Test
    void theOpeningPageStartsTheResults() {
        SearchPrompt prompt = new SearchPrompt();
        prompt.open(SERIAL, "Which item?", "");

        assertTrue(prompt.receive(page("", 100, 0, 15)));
    }

    @Test
    void theResultsTakeTheServersTotalAndColumns() {
        SearchPrompt prompt = new SearchPrompt();
        prompt.open(SERIAL, "Which item?", "");
        prompt.receive(new SearchPage(SERIAL, "", 8, 2, 0, List.of()));

        assertEquals(List.of(2, 4 * SearchGrid.CELL_HEIGHT + 2 * SearchGrid.PAD),
                List.of(prompt.grid().columns(), prompt.grid().contentHeight()));
    }

    @Test
    void aLoadedRowIsAtItsPlace() {
        assertEquals("Row 14", opened().row(14).map(SearchRow::label).orElse(""));
    }

    @Test
    void aRowNotLoadedIsMissing() {
        assertEquals(Optional.empty(), opened().row(15));
    }

    @Test
    void aPageForAnotherPromptIsIgnored() {
        SearchPrompt prompt = opened();

        assertFalse(prompt.receive(new SearchPage(SERIAL + 1, "", 100, 3, 15, List.of(new SearchRow(15, "Other", "", false,
                new WidgetPicture.None())))));
        assertEquals(Optional.empty(), prompt.row(15));
    }

    @Test
    void morePagesOfTheSameResultsAddTheirRows() {
        SearchPrompt prompt = opened();

        assertFalse(prompt.receive(page("", 100, 15, 3)));
        assertEquals("Row 17", prompt.row(17).map(SearchRow::label).orElse(""));
    }

    @Test
    void theWholeWindowLoadedAsksForNothing() {
        assertEquals(Optional.empty(), opened().update(0, 0));
    }

    @Test
    void scrollingDropsRowsMoreThanAScreenAboveTheView() {
        SearchPrompt prompt = opened();

        prompt.update(0, 5 * SearchGrid.CELL_HEIGHT + SearchGrid.PAD);

        assertEquals(List.of(9, 10, 11, 12, 13, 14), prompt.loaded());
    }

    @Test
    void scrollingAsksForTheFirstMissingRowsOfTheWindow() {
        assertEquals(Optional.of(new PageRequest(SERIAL, 15, 15, "")), opened().update(0, 5 * SearchGrid.CELL_HEIGHT + SearchGrid.PAD));
    }

    @Test
    void aRequestStopsAtTheFirstRowAlreadyLoaded() {
        SearchPrompt prompt = new SearchPrompt();
        prompt.open(SERIAL, "Which item?", "");
        prompt.receive(page("", 100, 3, 3));

        assertEquals(Optional.of(new PageRequest(SERIAL, 0, 3, "")), prompt.update(0, 0));
    }

    @Test
    void oneRequestIsInFlightAtATime() {
        SearchPrompt prompt = opened();
        prompt.update(0, 5 * SearchGrid.CELL_HEIGHT + SearchGrid.PAD);

        assertEquals(Optional.empty(), prompt.update(100, 5 * SearchGrid.CELL_HEIGHT + SearchGrid.PAD));
    }

    @Test
    void aRequestWithoutAnAnswerIsAskedAgain() {
        SearchPrompt prompt = opened();
        prompt.update(0, 5 * SearchGrid.CELL_HEIGHT + SearchGrid.PAD);

        assertEquals(Optional.of(new PageRequest(SERIAL, 15, 15, "")),
                prompt.update(SearchPrompt.ANSWER_WAIT, 5 * SearchGrid.CELL_HEIGHT + SearchGrid.PAD));
    }

    @Test
    void anAnswerLetTheNextRequestGo() {
        SearchPrompt prompt = opened();
        prompt.update(0, 5 * SearchGrid.CELL_HEIGHT + SearchGrid.PAD);
        prompt.receive(page("", 100, 15, 6));

        assertEquals(Optional.of(new PageRequest(SERIAL, 21, 9, "")), prompt.update(100, 5 * SearchGrid.CELL_HEIGHT + SearchGrid.PAD));
    }

    @Test
    void aQueryWaitsForTheTypingPause() {
        assertEquals(Optional.empty(), typed("oak", 1000).update(1000 + SearchPrompt.TYPING_PAUSE - 1, 0));
    }

    @Test
    void aQueryIsAskedAfterTheTypingPause() {
        assertEquals(Optional.of(new PageRequest(SERIAL, 0, SearchGrid.firstPage(), "oak")),
                typed("oak", 1000).update(1000 + SearchPrompt.TYPING_PAUSE, 0));
    }

    @Test
    void aQueryAskedIsNotAskedAgainWhileItsAnswerComes() {
        SearchPrompt prompt = typed("oak", 1000);
        prompt.update(1300, 0);

        assertEquals(Optional.empty(), prompt.update(1400, 0));
    }

    @Test
    void typingTheSameTextAgainKeepsThePauseRunning() {
        SearchPrompt prompt = typed("oak", 1000);
        prompt.type("oak", 1200);

        assertEquals(Optional.of(new PageRequest(SERIAL, 0, SearchGrid.firstPage(), "oak")), prompt.update(1300, 0));
    }

    @Test
    void aNewQueryIsAskedWhileAnOlderOneIsInFlight() {
        SearchPrompt prompt = typed("oak", 1000);
        prompt.update(1300, 0);
        prompt.type("oak l", 1400);

        assertEquals(Optional.of(new PageRequest(SERIAL, 0, SearchGrid.firstPage(), "oak l")), prompt.update(1700, 0));
    }

    @Test
    void twoLettersKeepTheOpeningRows() {
        assertEquals(Optional.empty(), typed("oa", 1000).update(2000, 0));
    }

    @Test
    void theOldRowsStayUntilTheAnswer() {
        SearchPrompt prompt = typed("oak", 1000);
        prompt.update(1300, 0);

        assertEquals("Row 0", prompt.row(0).map(SearchRow::label).orElse(""));
    }

    @Test
    void theAnswerReplacesTheRows() {
        SearchPrompt prompt = typed("oak", 1000);
        prompt.update(1300, 0);

        assertTrue(prompt.receive(page("oak", 2, 0, 2)));
        assertEquals(List.of(0, 1), prompt.loaded());
    }

    @Test
    void anAnswerForAQueryNoLongerTypedIsIgnored() {
        SearchPrompt prompt = typed("oak", 1000);
        prompt.update(1300, 0);
        prompt.type("oa", 1400);

        assertFalse(prompt.receive(page("oak", 2, 0, 2)));
        assertEquals("Row 5", prompt.row(5).map(SearchRow::label).orElse(""));
    }

    @Test
    void anIgnoredAnswerStillEndsItsRequest() {
        SearchPrompt prompt = typed("oak", 1000);
        prompt.update(1300, 0);
        prompt.type("oa", 1400);
        prompt.receive(page("oak", 2, 0, 2));

        assertEquals(Optional.of(new PageRequest(SERIAL, 15, 15, "")), prompt.update(1500, 5 * SearchGrid.CELL_HEIGHT + SearchGrid.PAD));
    }

    @Test
    void erasingTheQueryAsksForTheOpeningRowsAgain() {
        SearchPrompt prompt = typed("oak", 1000);
        prompt.update(1300, 0);
        prompt.receive(page("oak", 2, 0, 2));
        prompt.type("", 2000);

        assertEquals(Optional.of(new PageRequest(SERIAL, 0, SearchGrid.firstPage(), "")), prompt.update(2300, 0));
    }

    @Test
    void nothingIsSaidBeforeTheFirstPage() {
        SearchPrompt prompt = new SearchPrompt();
        prompt.open(SERIAL, "Which item?", "Nothing to pick");

        assertEquals(Optional.empty(), prompt.message());
    }

    @Test
    void anEmptyListShowsTheServersLine() {
        SearchPrompt prompt = new SearchPrompt();
        prompt.open(SERIAL, "Which item?", "Nothing to pick");
        prompt.receive(page("", 0, 0, 0));

        assertEquals(Optional.of("Nothing to pick"), prompt.message());
    }

    @Test
    void aQueryWithoutMatchesAsksToShortenTheSearch() {
        SearchPrompt prompt = typed("yew", 1000);
        prompt.receive(page("yew", 0, 0, 0));

        assertEquals(Optional.of(SearchPrompt.NO_MATCH), prompt.message());
    }

    @Test
    void resultsSayNothing() {
        assertEquals(Optional.empty(), opened().message());
    }

    @Test
    void openingAgainDropsTheLastPromptsResults() {
        SearchPrompt prompt = opened();
        prompt.open(SERIAL + 1, "Which rock?", "");

        assertEquals(List.of(List.of(), 0), List.of(prompt.loaded(), prompt.grid().contentHeight() - 2 * SearchGrid.PAD));
    }
}
