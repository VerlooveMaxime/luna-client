import idlers.CacheSprites;
import idlers.ModelFit;
import idlers.QuestJournal;
import idlers.SpriteFit;
import idlers.TileDrag;
import idlers.WidgetPicture;
import idlers.WidgetSpec;
import idlers.WidgetSpecs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * IdleRS: builds the {@link JagInterface} of a widget defined in code ({@link WidgetSpecs}) when the client asks for
 * an id the cache has no data for. Rebuilt on demand, so closing an interface can drop them like cache widgets.
 */
final class IdleWidgets {

    static final int CAPACITY = WidgetSpecs.CAPACITY;

    /** Media types of picture widgets: an item's icon on a sprite widget, an npc's body on a model widget. */
    static final int ITEM_ICON = 4;
    static final int NPC_BODY = 6;

    /** The client's item icons are drawn 32 by 32. */
    private static final int ITEM_ICON_SIZE = 32;

    /** Set once the media archive is unpacked; the client drops its own reference right after. */
    private static CacheSprites sprites;

    private static final int NPC_BODIES_KEPT = 64;

    /** The npc bodies built so far, centred, the least recently drawn dropped past {@link #NPC_BODIES_KEPT}. */
    private static final Map<Integer, Model> NPC_BODIES = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, Model> eldest) {
            return size() > NPC_BODIES_KEPT;
        }
    };

    private static final TileDrag TILE_DRAG = new TileDrag(WidgetSpecs.all());

    /** The tile being dragged, -1 for none, and its layer's children as they were before it was drawn on top. */
    private static int draggedTile = -1;
    private static TileDrag.Children childrenBeforeDrag;

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
            case WidgetSpec.Picture picture -> {
                inter.anInt236 = 5;
                inter.modelPitch = picture.pitch();
                inter.modelYaw = picture.yaw();
            }
        }
        return inter;
    }

    /**
     * A code-defined layer inside another: the client sets a layer's clip to its own bounds, so without the parent's
     * clip a tile scrolled out of its scrolling layer would still draw.
     */
    static boolean isNestedLayer(int id) {
        return WidgetSpecs.spec(id).map(spec -> spec instanceof WidgetSpec.Layer && spec.parent() != -1).orElse(false);
    }

    /** Packet 102: what a picture widget shows ({@link WidgetPicture}). */
    static void picture(JagBuffer buffer) {
        JagInterface inter = JagInterface.forId(buffer.getShort());
        WidgetPicture picture = WidgetPicture.read(new WidgetPicture.Reader() {
            public int u8() {
                return buffer.getByte();
            }

            public int u16() {
                return buffer.getShort();
            }

            public String string() {
                return buffer.getString();
            }
        });
        inter.anInt236 = 5;
        inter.sprite = null;
        inter.mediaType = 0;
        switch (picture) {
            case WidgetPicture.None none -> {
            }
            case WidgetPicture.Media media ->
                    inter.sprite = rgbSprite(sprites.fitted(media.name(), media.index(), inter.anInt241, inter.anInt238));
            case WidgetPicture.Item item -> {
                inter.mediaType = ITEM_ICON;
                inter.mediaId = item.id();
            }
            case WidgetPicture.NpcBody npc -> {
                NpcDefinition shown = NpcDefinition.forId(npc.id());
                if (shown.morphIds != null)
                    shown = shown.morph(false);
                inter.anInt236 = 6;
                inter.mediaType = NPC_BODY;
                inter.mediaId = npc.id();
                inter.modelZoom = 0;
                inter.modelAnimation = shown == null ? -1 : shown.standAnimation;
                inter.activeModelAnimation = -1;
                inter.modelAnimationFrame = 0;
            }
        }
    }

    /** Draws an item's icon once its model has arrived; until then the widget stays empty. */
    static void loadItemIcon(JagInterface inter) {
        RgbSprite icon = ItemDefinition.method221((byte) -33, 0, 1, inter.mediaId);
        if (icon == null)
            return;
        inter.sprite = rgbSprite(SpriteFit.fit(SpriteFit.clientSprite(icon.anIntArray1489, ITEM_ICON_SIZE, ITEM_ICON_SIZE),
                inter.anInt241, inter.anInt238));
        inter.mediaType = 0;
    }

    /**
     * Sets the zoom that frames an npc's body, as the widget turns it, before the widget computes its camera: the
     * client divides by depth with no near clip, so a body drawn at zoom 0 would divide by zero. Until the body's
     * models arrive nothing is drawn. A new picture or a rebuilt widget starts at zoom 0.
     */
    static void frameNpcBody(JagInterface inter) {
        Model body = npcBodyModel(inter.mediaId);
        if (body != null && inter.modelZoom == 0)
            inter.modelZoom = ModelFit.zoom(body.verticesX, body.verticesY, body.verticesZ, body.verticesCount,
                    inter.modelPitch, inter.modelYaw, inter.anInt241, inter.anInt238);
    }

    /** {@code JagInterface.method197}'s model for an npc body, centred; null until its models arrive. */
    static Model npcBodyModel(int npcId) {
        Model body = NPC_BODIES.get(npcId);
        if (body != null)
            return body;
        body = NpcDefinition.forId(npcId).getBodyModel();
        if (body == null)
            return null;
        ModelFit.Centre centre = ModelFit.centre(body.verticesX, body.verticesY, body.verticesZ, body.verticesCount);
        body.translate(centre.dx(), centre.dy(), centre.dz());
        NPC_BODIES.put(npcId, body);
        return body;
    }

    /** A press on {@code widgetId}: true when it starts dragging a tile, drawn above its neighbours from now on. */
    static boolean startTileDrag(int widgetId) {
        Optional<Integer> tile = TILE_DRAG.tileOf(widgetId);
        if (tile.isEmpty())
            return false;
        draggedTile = tile.get();
        JagInterface layer = JagInterface.forId(parentOf(draggedTile));
        childrenBeforeDrag = new TileDrag.Children(layer.anIntArray258, layer.anIntArray232, layer.anIntArray276);
        TileDrag.Children front = TileDrag.toFront(childrenBeforeDrag, draggedTile);
        layer.anIntArray258 = front.ids();
        layer.anIntArray232 = front.xs();
        layer.anIntArray276 = front.ys();
        return true;
    }

    static void dragTile(int dx, int dy) {
        if (draggedTile == -1)
            return;
        JagInterface tile = JagInterface.forId(draggedTile);
        tile.drawOffsetX = dx;
        tile.drawOffsetY = dy;
    }

    /** Puts the dragged tile back in its place, before the drop is hit-tested. */
    static void endTileDrag() {
        if (draggedTile == -1)
            return;
        JagInterface tile = JagInterface.forId(draggedTile);
        tile.drawOffsetX = 0;
        tile.drawOffsetY = 0;
        JagInterface layer = JagInterface.forId(parentOf(draggedTile));
        layer.anIntArray258 = childrenBeforeDrag.ids();
        layer.anIntArray232 = childrenBeforeDrag.xs();
        layer.anIntArray276 = childrenBeforeDrag.ys();
        draggedTile = -1;
    }

    /** The move of a tile dragged by {@code face} and dropped on {@code target}, the widget under the mouse. */
    static Optional<TileDrag.Move> droppedTile(int face, int target) {
        return TILE_DRAG.drop(face, target);
    }

    private static int parentOf(int id) {
        return WidgetSpecs.spec(id).orElseThrow().parent();
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
