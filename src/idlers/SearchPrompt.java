package idlers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

/**
 * The chatbox search the server opens (packet 103) and pages (105). Every list works the same way (Maxime,
 * 2026-10-09): it opens on its first rows; from {@link #MIN_LETTERS} letters, once the player stops typing for
 * {@link #TYPING_PAUSE} ms, the server is asked for the matches; scrolling asks for the rows coming into the window
 * and drops those leaving it. The client never filters by itself. One request is in flight at a time, asked again
 * after {@link #ANSWER_WAIT} ms without an answer, so dragging the scrollbar cannot flood the server.
 */
public final class SearchPrompt {

    public static final int MIN_LETTERS = 3;
    public static final long TYPING_PAUSE = 300;
    public static final long ANSWER_WAIT = 2000;

    static final String NO_MATCH = "No matches, shorten the search";

    private int serial;
    private String title = "";
    private String emptyLine = "";

    private String typed = "";
    private long typedAt;

    /** The results shown: null until the first page of the prompt arrives. */
    private String query;
    private int total;
    private int columns = SearchGrid.MOST_COLUMNS;
    private final Map<Integer, SearchRow> rows = new HashMap<>();

    /** The query of the request in flight, null for none, and when it went out. */
    private String asked;
    private long askedAt;

    /** A new prompt: its first rows follow in their own packet. */
    public void open(int serial, String title, String emptyLine) {
        this.serial = serial;
        this.title = title;
        this.emptyLine = emptyLine;
        typed = "";
        typedAt = 0;
        query = null;
        total = 0;
        columns = SearchGrid.MOST_COLUMNS;
        rows.clear();
        asked = null;
    }

    /** Names the prompt to the server, so it can drop an answer meant for an earlier prompt. */
    public int serial() {
        return serial;
    }

    public String title() {
        return title;
    }

    /** What the player has typed at {@code now}; the typing pause counts from its last change. */
    public void type(String text, long now) {
        if (!text.equals(typed)) {
            typed = text;
            typedAt = now;
        }
    }

    /** The query {@code text} asks for: its words in lower case from 3 letters on (spaces not counted), "" below. */
    static String queryOf(String text) {
        String words = String.join(" ", Arrays.stream(text.toLowerCase(Locale.ROOT).split(" ")).filter(word -> !word.isEmpty()).toList());
        return words.replace(" ", "").length() >= MIN_LETTERS ? words : "";
    }

    /** Takes a page; true when it starts new results, which show from the top. */
    public boolean receive(SearchPage page) {
        if (page.serial() != serial) {
            return false;
        }
        if (page.query().equals(asked)) {
            asked = null;
        }
        boolean fresh = !page.query().equals(query);
        if (fresh && !page.query().equals(queryOf(typed))) {
            return false;
        }
        if (fresh) {
            query = page.query();
            total = page.total();
            columns = page.columns();
            rows.clear();
        }
        for (int i = 0; i < page.rows().size(); i++) {
            rows.put(page.offset() + i, page.rows().get(i));
        }
        return fresh;
    }

    /**
     * Drops the rows that left the window around {@code scroll}, then says what to ask the server for at {@code now},
     * if anything: the new query once the typing pause is over, else the first rows missing from the window.
     */
    public Optional<PageRequest> update(long now, int scroll) {
        SearchGrid.Window window = grid().window(scroll);
        rows.keySet().removeIf(place -> !window.contains(place));
        boolean waiting = asked != null && now - askedAt < ANSWER_WAIT;
        String wanted = queryOf(typed);
        if (!wanted.equals(query)) {
            if (waiting && wanted.equals(asked) || now - typedAt < TYPING_PAUSE) {
                return Optional.empty();
            }
            return Optional.of(ask(wanted, 0, SearchGrid.firstPage(), now));
        }
        OptionalInt missing = IntStream.range(window.from(), window.to()).filter(place -> !rows.containsKey(place)).findFirst();
        if (waiting || missing.isEmpty()) {
            return Optional.empty();
        }
        int from = missing.getAsInt();
        int count = (int) IntStream.range(from, window.to()).takeWhile(place -> !rows.containsKey(place)).count();
        return Optional.of(ask(query, from, count, now));
    }

    private PageRequest ask(String query, int offset, int count, long now) {
        asked = query;
        askedAt = now;
        return new PageRequest(serial, offset, count, query);
    }

    /** The grid of the results shown, empty until the first page. */
    public SearchGrid grid() {
        return new SearchGrid(columns, total);
    }

    /** The row at {@code place}, if it is loaded. */
    public Optional<SearchRow> row(int place) {
        return Optional.ofNullable(rows.get(place));
    }

    /** The line shown instead of rows: the server's line for an empty list, or that nothing matched. */
    public Optional<String> message() {
        if (query == null || total > 0) {
            return Optional.empty();
        }
        return Optional.of(query.isEmpty() ? emptyLine : NO_MATCH);
    }

    /** The places loaded, in order: what the window kept. */
    List<Integer> loaded() {
        return rows.keySet().stream().sorted().toList();
    }
}
