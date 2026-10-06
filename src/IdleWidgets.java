import idlers.FlowWidgets;
import idlers.WidgetSpec;

/**
 * IdleRS: builds the {@link JagInterface} of a widget defined in code ({@link FlowWidgets}) when the client asks for
 * an id the cache has no data for. Rebuilt on demand, so closing an interface can drop them like cache widgets.
 */
final class IdleWidgets {

    static final int CAPACITY = FlowWidgets.ID_LIMIT;

    private IdleWidgets() {
    }

    static JagInterface build(int id) {
        WidgetSpec spec = FlowWidgets.spec(id);
        if (spec == null)
            return null;
        JagInterface inter = new JagInterface();
        inter.id = id;
        inter.anInt248 = spec.parent();
        inter.anInt254 = -1;
        inter.anInt241 = spec.width();
        inter.anInt238 = spec.height();
        if (spec.kind() == WidgetSpec.Kind.LAYER) {
            inter.anInt236 = 0;
            int count = spec.children().size();
            inter.anIntArray258 = new int[count];
            inter.anIntArray232 = new int[count];
            inter.anIntArray276 = new int[count];
            for (int i = 0; i < count; i++) {
                WidgetSpec child = FlowWidgets.spec(spec.children().get(i));
                inter.anIntArray258[i] = child.id();
                inter.anIntArray232[i] = child.x();
                inter.anIntArray276[i] = child.y();
            }
        } else if (spec.kind() == WidgetSpec.Kind.BOX || spec.kind() == WidgetSpec.Kind.FRAME) {
            inter.anInt236 = 3;
            inter.aBoolean239 = spec.kind() == WidgetSpec.Kind.BOX;
            inter.anInt240 = spec.colour();
            inter.anInt260 = spec.colour();
        } else {
            inter.anInt236 = 4;
            inter.aBoolean272 = spec.centred();
            inter.anInt289 = spec.kind() == WidgetSpec.Kind.BUTTON ? 1 : 0;
            inter.aClass50_Sub1_Sub1_Sub2_237 = JagInterface.aClass50_Sub1_Sub1_Sub2Array223[spec.font()];
            inter.aBoolean247 = true;
            inter.aString230 = spec.text();
            inter.aString249 = "";
            inter.anInt240 = spec.colour();
            inter.anInt260 = spec.colour();
            inter.anInt261 = spec.hoverColour();
            inter.anInt226 = spec.hoverColour();
            inter.tooltip = spec.tooltip();
        }
        return inter;
    }

    /** The Idle tab's icon ({@link idlers.IdleTabIcon}) as a client sprite. */
    static RgbSprite tabIcon() {
        RgbSprite sprite = new RgbSprite(idlers.IdleTabIcon.WIDTH, idlers.IdleTabIcon.HEIGHT);
        int[] pixels = idlers.IdleTabIcon.pixels();
        System.arraycopy(pixels, 0, sprite.anIntArray1489, 0, pixels.length);
        return sprite;
    }
}
