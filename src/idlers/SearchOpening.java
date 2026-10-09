package idlers;

/**
 * A chatbox prompt the server opens (packet 103): a search over its rows, or a name typed on the line (Maxime,
 * 2026-10-09), with the line shown when the list is empty, the text the typed line starts with and the most characters
 * it takes.
 */
public record SearchOpening(int serial, SearchPrompt.Mode mode, String title, String emptyLine, String text, int mostCharacters) {

    public static SearchOpening read(WidgetPicture.Reader in) {
        int serial = in.u8();
        SearchPrompt.Mode mode = SearchPrompt.Mode.values()[in.u8()];
        String title = in.string();
        String emptyLine = in.string();
        String text = in.string();
        return new SearchOpening(serial, mode, title, emptyLine, text, in.u8());
    }
}
