package idlers;

/**
 * Server chat lines with an IdleRS meaning, marked by a suffix the way 377 marks trade and duel requests. A debugging
 * line ends with {@link #DEBUG_SUFFIX} (Luna's {@code GameChatboxMessageWriter.DEBUG_SUFFIX}): it goes to the chat
 * history like any game message but never covers the tutorial's help box with "Click here to continue".
 */
public final class ChatMessages {

    public static final String DEBUG_SUFFIX = ":debug:";

    private ChatMessages() {
    }

    public static boolean isDebug(String message) {
        return message.endsWith(DEBUG_SUFFIX);
    }

    /** {@code message} without its debug suffix; only for a message {@link #isDebug} accepts. */
    public static String withoutDebugSuffix(String message) {
        return message.substring(0, message.length() - DEBUG_SUFFIX.length());
    }
}
