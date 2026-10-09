package idlers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

class QuestJournalTest {

    private final int[] ids = {663, 7332, 682};
    private final int[] xs = {5, 10, 4};
    private final int[] ys = {8, 23, 296};

    @Test
    void theStagesHeaderAndTheTutorialLineComeFirst() {
        QuestJournal.Layout layout = QuestJournal.withStages(ids, xs, ys, 1610);

        assertArrayEquals(new int[] {QuestJournal.STAGES_HEADER, QuestJournal.TUTORIAL_LINE, 663, 7332, 682}, layout.ids());
    }

    @Test
    void theStagesTakeTheTopPlacesOfTheList() {
        QuestJournal.Layout layout = QuestJournal.withStages(ids, xs, ys, 1610);

        assertArrayEquals(new int[] {5, 10, 5, 10, 4}, layout.xs());
        assertArrayEquals(new int[] {8, 23, 42, 57, 330}, layout.ys());
    }

    @Test
    void theListScrollsFurtherByAsMuchAsItMoved() {
        assertEquals(1644, QuestJournal.withStages(ids, xs, ys, 1610).scrollHeight());
    }

    @Test
    void aListThatHasTheStagesIsLeftAsItIs() {
        QuestJournal.Layout once = QuestJournal.withStages(ids, xs, ys, 1610);

        QuestJournal.Layout twice = QuestJournal.withStages(once.ids(), once.xs(), once.ys(), once.scrollHeight());

        assertSame(once.ids(), twice.ids());
        assertEquals(1644, twice.scrollHeight());
    }

    @Test
    void theHeaderIsStyledLikeTheQuestHeaders() {
        WidgetSpec.Text header = assertInstanceOf(WidgetSpec.Text.class, QuestJournal.specs().get(QuestJournal.STAGES_HEADER));

        assertEquals("STAGES:", header.text());
        assertEquals(0xf99b15, header.colour());
        assertEquals(WidgetSpec.FONT_BOLD, header.font());
    }

    @Test
    void theTutorialLineStartsBlankAndRedLikeAnUnstartedQuest() {
        WidgetSpec.Text line = assertInstanceOf(WidgetSpec.Text.class, QuestJournal.specs().get(QuestJournal.TUTORIAL_LINE));

        assertEquals("", line.text());
        assertEquals(0xff0000, line.colour());
        assertEquals(WidgetSpec.FONT_PLAIN, line.font());
    }

    @Test
    void theJournalsIdsFollowTheFlowWidgets() {
        assertEquals(FlowWidgets.ID_LIMIT, QuestJournal.STAGES_HEADER);
    }

    @Test
    void theStagesHangUnderTheJournalTab() {
        assertEquals(QuestJournal.TAB, QuestJournal.specs().get(QuestJournal.TUTORIAL_LINE).parent());
    }
}
