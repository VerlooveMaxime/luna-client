package idlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * The client window's whole size, borders included, as the player left it: kept in {@link #FILE} so the client reopens
 * at that size instead of the largest that fits (Maxime, 2026-10-09: no resizing after every restart). A size set in
 * {@code IDLERS_CLIENT_SCALE} wins over it. The whole size is kept, not the game area, so window borders that arrive
 * late cannot make it drift from one start to the next.
 */
public record WindowSize(int width, int height) {

    public static final String FILE = "client-window.txt";

    /**
     * The size the window opens at: the {@code saved} one (null for none) when nothing is {@code configured}, kept
     * between the game's own size with its borders and the screen; otherwise the game at {@link GameViewport#initialScale}
     * plus its borders, which throws for a bad configured scale.
     */
    public static WindowSize initial(String configured, WindowSize saved, int gameWidth, int gameHeight, int borderWidth,
            int borderHeight, int screenWidth, int screenHeight) {
        if ((configured == null || configured.isBlank()) && saved != null) {
            return saved.within(gameWidth + borderWidth, gameHeight + borderHeight, screenWidth, screenHeight);
        }
        double scale = GameViewport.initialScale(configured, gameWidth, gameHeight, screenWidth - borderWidth,
                screenHeight - borderHeight);
        return new WindowSize((int) Math.round(gameWidth * scale) + borderWidth, (int) Math.round(gameHeight * scale) + borderHeight);
    }

    /** "1418 912" as a size; empty for anything else. */
    public static Optional<WindowSize> parse(String text) {
        String[] parts = text.strip().split("\\s+");
        if (parts.length != 2) {
            return Optional.empty();
        }
        try {
            return Optional.of(new WindowSize(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public String text() {
        return width + " " + height;
    }

    /** This size, no smaller than the minimums and no larger than the maximums; the minimums win when they clash. */
    public WindowSize within(int minWidth, int minHeight, int maxWidth, int maxHeight) {
        return new WindowSize(Math.max(minWidth, Math.min(width, maxWidth)), Math.max(minHeight, Math.min(height, maxHeight)));
    }

    /** The size saved in {@code file}; empty when there is none, and reported when it cannot be read. */
    public static Optional<WindowSize> read(Path file) {
        try {
            return parse(Files.readString(file));
        } catch (NoSuchFileException e) {
            return Optional.empty();
        } catch (IOException e) {
            System.err.println("Could not read the window size from " + file + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    /** Saves this size in {@code file}; a failure is reported, not thrown, since the size is only a convenience. */
    public void write(Path file) {
        try {
            Files.writeString(file, text());
        } catch (IOException e) {
            System.err.println("Could not save the window size in " + file + ": " + e.getMessage());
        }
    }
}
