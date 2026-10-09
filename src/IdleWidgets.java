import idlers.CacheSprites;
import idlers.QuestJournal;
import idlers.SpriteFit;
import idlers.WidgetSpec;
import idlers.WidgetSpecs;

/**
 * IdleRS: builds the {@link JagInterface} of a widget defined in code ({@link WidgetSpecs}) when the client asks for
 * an id the cache has no data for. Rebuilt on demand, so closing an interface can drop them like cache widgets.
 */
final class IdleWidgets {

    static final int CAPACITY = WidgetSpecs.CAPACITY;

    /** Set once the media archive is unpacked; the client drops its own reference right after. */
    private static CacheSprites sprites;

    private IdleWidgets() {
    }

    static void media(Archive archive) {
        sprites = new CacheSprites(archive::get);
    }

    static JagInterface build(int id) {
        WidgetSpec spec = WidgetSpecs.spec(id).orElse(null);
        if (spec == null)
            return null;
        JagInterface inter = new JagInterface();
        inter.id = id;
        inter.anInt248 = WidgetSpecs.root(id);
        inter.anInt254 = -1;
        inter.anInt241 = spec.width();
        inter.anInt238 = spec.height();
        switch (spec) {
            case WidgetSpec.Layer layer -> {
                inter.anInt236 = 0;
                inter.anInt285 = layer.scrollHeight();
                int count = layer.children().size();
                inter.anIntArray258 = new int[count];
                inter.anIntArray232 = new int[count];
                inter.anIntArray276 = new int[count];
                for (int i = 0; i < count; i++) {
                    WidgetSpec child = WidgetSpecs.spec(layer.children().get(i)).orElseThrow();
                    inter.anIntArray258[i] = child.id();
                    inter.anIntArray232[i] = child.x();
                    inter.anIntArray276[i] = child.y();
                }
            }
            case WidgetSpec.Box box -> box(inter, box.colour(), 0, box.filled());
            case WidgetSpec.Tile tile -> {
                box(inter, tile.colour(), tile.hoverColour(), true);
                inter.anInt289 = 1;
                inter.tooltip = tile.option();
            }
            case WidgetSpec.Text text -> text(inter, text.text(), text.colour(), 0, text.font(), text.centred());
            case WidgetSpec.Button button -> {
                text(inter, button.text(), button.colour(), button.hoverColour(), button.font(), false);
                inter.anInt289 = 1;
                inter.tooltip = button.option();
            }
            case WidgetSpec.Tooltip tooltip -> {
                inter.anInt236 = 8;
                inter.aString230 = tooltip.text();
            }
            case WidgetSpec.Sprite sprite -> {
                inter.anInt236 = 5;
                inter.sprite = rgbSprite(sprites.fitted(sprite));
            }
        }
        return inter;
    }

    private static void box(JagInterface inter, int colour, int hoverColour, boolean filled) {
        inter.anInt236 = 3;
        inter.aBoolean239 = filled;
        inter.anInt240 = colour;
        inter.anInt260 = colour;
        inter.anInt261 = hoverColour;
        inter.anInt226 = hoverColour;
    }

    private static void text(JagInterface inter, String text, int colour, int hoverColour, int font, boolean centred) {
        inter.anInt236 = 4;
        inter.aBoolean272 = centred;
        inter.aClass50_Sub1_Sub1_Sub2_237 = JagInterface.aClass50_Sub1_Sub1_Sub2Array223[font];
        inter.aBoolean247 = true;
        inter.aString230 = text;
        inter.aString249 = "";
        inter.anInt240 = colour;
        inter.anInt260 = colour;
        inter.anInt261 = hoverColour;
        inter.anInt226 = hoverColour;
    }

    /** Puts the stages on top of the quest journal's list, as {@link QuestJournal} lays them out. */
    static void addStages(JagInterface list) {
        QuestJournal.Layout layout = QuestJournal.withStages(list.anIntArray258, list.anIntArray232, list.anIntArray276, list.anInt285);
        list.anIntArray258 = layout.ids();
        list.anIntArray232 = layout.xs();
        list.anIntArray276 = layout.ys();
        list.anInt285 = layout.scrollHeight();
    }

    /** The Idle tab's icon ({@link idlers.IdleTabIcon}) as a client sprite. */
    static RgbSprite tabIcon() {
        return rgbSprite(new SpriteFit.Fitted(idlers.IdleTabIcon.pixels(), idlers.IdleTabIcon.WIDTH, idlers.IdleTabIcon.HEIGHT));
    }

    private static RgbSprite rgbSprite(SpriteFit.Fitted fitted) {
        RgbSprite sprite = new RgbSprite(fitted.width(), fitted.height());
        System.arraycopy(fitted.pixels(), 0, sprite.anIntArray1489, 0, fitted.pixels().length);
        return sprite;
    }
}
