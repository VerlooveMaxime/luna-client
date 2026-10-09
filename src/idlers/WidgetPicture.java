package idlers;

/**
 * What the server shows in a {@link WidgetSpec.Picture} with packet 102 (the widget id, then this): nothing, a sprite of
 * the media archive, an item's inventory icon, or an npc's whole body. One widget takes any of them, so a step's
 * slot needs one picture whatever its target is.
 */
public sealed interface WidgetPicture {

    int NONE = 0;
    int MEDIA = 1;
    int ITEM = 2;
    int NPC_BODY = 3;

    record None() implements WidgetPicture {
    }

    record Media(String name, int index) implements WidgetPicture {
    }

    record Item(int id) implements WidgetPicture {
    }

    record NpcBody(int id) implements WidgetPicture {
    }

    /** The parts of the client's packet buffer a picture is read with. */
    interface Reader {

        int u8();

        int u16();

        String string();
    }

    /** A source byte, then a media sprite's name and index, an item id or an npc id. */
    static WidgetPicture read(Reader in) {
        int source = in.u8();
        return switch (source) {
            case NONE -> new None();
            case MEDIA -> new Media(in.string(), in.u8());
            case ITEM -> new Item(in.u16());
            case NPC_BODY -> new NpcBody(in.u16());
            default -> throw new IllegalArgumentException("No picture source " + source);
        };
    }
}
