package idlers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatMessagesTest {

    @Test
    void aLineEndingWithTheSuffixIsADebuggingLine() {
        assertTrue(ChatMessages.isDebug("[FocusChangedMessageReader] focus: true:debug:"));
    }

    @Test
    void aGameMessageIsNotADebuggingLine() {
        assertFalse(ChatMessages.isDebug("You get some logs."));
    }

    @Test
    void aDebuggingLineIsShownWithoutItsSuffix() {
        assertEquals("[FocusChangedMessageReader] focus: true",
                ChatMessages.withoutDebugSuffix("[FocusChangedMessageReader] focus: true:debug:"));
    }
}
