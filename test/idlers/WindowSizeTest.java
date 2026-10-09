package idlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WindowSizeTest {

    private static final int GAME_WIDTH = 765;
    private static final int GAME_HEIGHT = 503;

    /** A game with 10 px of borders across and 40 down, on a 2000 by 1200 screen. */
    private static WindowSize initial(String configured, WindowSize saved) {
        return WindowSize.initial(configured, saved, GAME_WIDTH, GAME_HEIGHT, 10, 40, 2000, 1200);
    }

    @TempDir
    Path folder;

    @Test
    void aSavedSizeIsKeptWhenNothingIsConfigured() {
        assertEquals(new WindowSize(1418, 912), initial(null, new WindowSize(1418, 912)));
    }

    @Test
    void aBlankScaleCountsAsNothingConfigured() {
        assertEquals(new WindowSize(1418, 912), initial(" ", new WindowSize(1418, 912)));
    }

    @Test
    void aSavedSizeLargerThanTheScreenIsShrunkToIt() {
        assertEquals(new WindowSize(2000, 1200), initial(null, new WindowSize(3000, 1600)));
    }

    @Test
    void aSavedSizeSmallerThanTheGameGrowsToItWithItsBorders() {
        assertEquals(new WindowSize(775, 543), initial(null, new WindowSize(300, 200)));
    }

    @Test
    void withoutASavedSizeTheWindowOpensAtTheLargestWholeScale() {
        assertEquals(new WindowSize(2 * GAME_WIDTH + 10, 2 * GAME_HEIGHT + 40), initial(null, null));
    }

    @Test
    void aConfiguredScaleWinsOverTheSavedSize() {
        assertEquals(new WindowSize(1158, 795), initial("1.5", new WindowSize(1418, 912)));
    }

    @Test
    void autoWinsOverTheSavedSize() {
        assertEquals(new WindowSize(1540, 1046), initial("auto", new WindowSize(1418, 912)));
    }

    @Test
    void aBadConfiguredScaleIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> initial("big", new WindowSize(1418, 912)));
    }

    @Test
    void aSizeReadsAsItsWidthAndHeight() {
        assertEquals(Optional.of(new WindowSize(1418, 912)), WindowSize.parse(" 1418  912\n"));
    }

    @Test
    void textWithoutTwoPartsIsNoSize() {
        assertEquals(Optional.empty(), WindowSize.parse("1418"));
    }

    @Test
    void textThatIsNoNumberIsNoSize() {
        assertEquals(Optional.empty(), WindowSize.parse("wide 912"));
    }

    @Test
    void aSizeWritesAsItReads() {
        assertEquals("1418 912", new WindowSize(1418, 912).text());
    }

    @Test
    void theMinimumsWinWhenTheyClashWithTheMaximums() {
        assertEquals(new WindowSize(775, 543), new WindowSize(1000, 900).within(775, 543, 600, 400));
    }

    @Test
    void aWrittenSizeIsReadBack() {
        Path file = folder.resolve(WindowSize.FILE);
        new WindowSize(1418, 912).write(file);

        assertEquals(Optional.of(new WindowSize(1418, 912)), WindowSize.read(file));
    }

    @Test
    void noFileIsNoSize() {
        assertEquals(Optional.empty(), WindowSize.read(folder.resolve(WindowSize.FILE)));
    }

    @Test
    void aFileThatCannotBeReadIsNoSize() {
        assertEquals(Optional.empty(), WindowSize.read(folder));
    }

    @Test
    void aSizeThatCannotBeWrittenLeavesNoFile() {
        Path file = folder.resolve("missing").resolve(WindowSize.FILE);

        new WindowSize(1418, 912).write(file);

        assertFalse(Files.exists(file));
    }

    @Test
    void writingReplacesTheLastSize() throws IOException {
        Path file = folder.resolve(WindowSize.FILE);
        new WindowSize(1418, 912).write(file);

        new WindowSize(1200, 800).write(file);

        assertEquals("1200 800", Files.readString(file));
    }
}
