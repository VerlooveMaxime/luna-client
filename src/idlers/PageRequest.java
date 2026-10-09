package idlers;

/** What the client asks the server for (packet 102): {@code count} rows from place {@code offset} of what {@code query} matches. */
public record PageRequest(int serial, int offset, int count, String query) {
}
