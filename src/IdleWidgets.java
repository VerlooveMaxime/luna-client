import idlers.BuilderWidgets;
import idlers.CacheSprites;
import idlers.FlowWidgets;
import idlers.ModelFit;
import idlers.QuestJournal;
import idlers.SpriteFit;
import idlers.TileDrag;
import idlers.WidgetPicture;
import idlers.WidgetSpec;
import idlers.WidgetSpecs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * IdleRS: builds the {@link JagInterface} of a widget defined in code ({@link WidgetSpecs}) when the client asks for
 * an id the cache has no data for. Rebuilt on demand, so closing an interface can drop them like cache widgets.
 */
final class IdleWidgets {

    /**
     * Sub-opcodes of packet 108, the IdleRS packet: the builder's step slots, the Idle tab's saved-flow slots, where a
     * configure list sits.
     */
    static final int BUILDER_SLOTS = 0;
    static final int SAVED_FLOW_SLOTS = 1;
    static final int LIST_PLACEMENT = 2;

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

    /** The widgets as built for the step and saved-flow slots the server last said the player has, and their drag rules. */
    private static int builderSlots;
    private static int savedFlowSlots;
    private static List<BuilderWidgets.ListPlacement> lists = BuilderWidgets.unplacedLists();
    private static WidgetSpecs specs = WidgetSpecs.of(builderSlots, savedFlowSlots, lists);
    private static TileDrag tileDrag = new TileDrag(specs.all());

    /** The tile being dragged, -1 for none, and its layer's children as they were before it was drawn on top. */
    private static int draggedTile = -1;
    private static TileDrag.Children childrenBeforeDrag;

    private IdleWidgets() {
    }

    static void media(Archive archive) {
        sprites = new CacheSprites(archive::get);
    }

    /** Room the client makes for widget ids. */
    static int capacity() {
        return specs.capacity();
    }

    /** Packet 108: a sub-opcode, then its content: a slot count, or a list's place. */
    static void idlePacket(JagBuffer buffer) {
        int sub = buffer.getByte();
        if (sub == LIST_PLACEMENT) {
            placeList(buffer.getByte(), buffer.getByte(), buffer.getByte(), buffer.getByte());
            return;
        }
        int slots = buffer.getShort();
        if (sub == BUILDER_SLOTS && slots != builderSlots) {
            builderSlots = slots;
            rebuild(BuilderWidgets.ROOT);
        }
        if (sub == SAVED_FLOW_SLOTS && slots != savedFlowSlots) {
            savedFlowSlots = slots;
            rebuild(FlowWidgets.TAB);
        }
    }

    /**
     * Configure list {@code list} moves to where the server placed it: later builds take it from the specs, and the
     * widgets already built are moved in place, so the open screen keeps everything else it shows.
     */
    private static void placeList(int list, int firstRow, int rows, int lines) {
        BuilderWidgets.ListPlacement placement = BuilderWidgets.listPlacement(list, firstRow, rows, lines);
        lists = BuilderWidgets.placed(lists, list, placement);
        specs = WidgetSpecs.of(builderSlots, savedFlowSlots, lists);
        int id = BuilderWidgets.list(list);
        JagInterface configure = JagInterface.interfaces[BuilderWidgets.CONFIGURE];
        if (configure != null)
            for (int i = 0; i < configure.childIds.length; i++)
                if (configure.childIds[i] == id)
                    configure.childY[i] = placement.y();
        JagInterface layer = JagInterface.interfaces[id];
        if (layer != null) {
            layer.anInt238 = placement.height();
            layer.anInt285 = placement.scrollHeight();
            layer.anInt231 = placement.clampedScroll(layer.anInt231);
        }
    }

    /** The widgets built again for the counts the server last sent; the ones built before in {@code root}'s group are dropped. */
    private static void rebuild(int root) {
        WidgetSpecs old = specs;
        specs = WidgetSpecs.of(builderSlots, savedFlowSlots, lists);
        tileDrag = new TileDrag(specs.all());
        JagInterface.grow(specs.capacity());
        old.all().keySet().stream().filter(id -> old.root(id) == root).forEach(id -> JagInterface.interfaces[id] = null);
    }

    static JagInterface build(int id) {
        WidgetSpec spec = specs.spec(id).orElse(null);
        if (spec == null)
            return null;
        JagInterface inter = new JagInterface();
        inter.id = id;
        inter.anInt248 = specs.root(id);
        inter.anInt254 = -1;
        inter.anInt241 = spec.width();
        inter.anInt238 = spec.height();
        switch (spec) {
            case WidgetSpec.Layer layer -> {
                inter.anInt236 = 0;
                inter.anInt285 = layer.scrollHeight();
                int count = layer.children().size();
                inter.childIds = new int[count];
                inter.childX = new int[count];
                inter.childY = new int[count];
                for (int i = 0; i < count; i++) {
                    WidgetSpec child = specs.spec(layer.children().get(i)).orElseThrow();
                    inter.childIds[i] = child.id();
                    inter.childX[i] = child.x();
                    inter.childY[i] = child.y();
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
        return specs.spec(id).map(spec -> spec instanceof WidgetSpec.Layer && spec.parent() != -1).orElse(false);
    }

    /** Packet 102: what a picture widget shows ({@link WidgetPicture}). */
    static void picture(JagBuffer buffer) {
        JagInterface inter = JagInterface.forId(buffer.getShort());
        show(inter, WidgetPicture.read(reader(buffer)));
    }

    /** The parts of an incoming packet our readers take: unsigned bytes and shorts, newline-ended strings. */
    static WidgetPicture.Reader reader(JagBuffer buffer) {
        return new WidgetPicture.Reader() {
            public int u8() {
                return buffer.getByte();
            }

            public int u16() {
                return buffer.getShort();
            }

            public String string() {
                return buffer.getString();
            }
        };
    }

    /** Makes picture widget {@code inter} show {@code picture}, from the next draw on. */
    static void show(JagInterface inter, WidgetPicture picture) {
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
                inter.anInt236 = 6;
                inter.mediaType = NPC_BODY;
                inter.mediaId = npc.id();
                inter.modelZoom = 0;
                inter.modelAnimation = standAnimation(npc.id());
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
        if (body == null || inter.modelZoom != 0)
            return;
        ModelFit.Vertices framed = framed(posed(body, inter.mediaId));
        inter.modelZoom = ModelFit.zoom(framed.xs(), framed.ys(), framed.zs(), framed.count(), inter.modelPitch, inter.modelYaw,
                inter.anInt241, inter.anInt238);
    }

    /** {@code JagInterface.method197}'s model for an npc body, centred; null until its models arrive. */
    static Model npcBodyModel(int npcId) {
        Model body = NPC_BODIES.get(npcId);
        if (body != null)
            return body;
        body = NpcDefinition.forId(npcId).getBodyModel();
        if (body == null)
            return null;
        ModelFit.Vertices framed = framed(posed(body, npcId));
        ModelFit.Centre centre = ModelFit.centre(framed.xs(), framed.ys(), framed.zs(), framed.count());
        body.translate(centre.dx(), centre.dy(), centre.dz());
        NPC_BODIES.put(npcId, body);
        return body;
    }

    /**
     * The body as its widget first draws it, in the first frame of its stand animation, which moves or shrinks some
     * bodies a lot (a giant frog unposed is four times its drawn size). A copy whose vertices sit in the client's
     * shared buffers: use it at once.
     */
    private static Model posed(Model body, int npcId) {
        int animation = standAnimation(npcId);
        if (animation == -1 || Animation.animations[animation] == null)
            return body;
        Model posed = new Model(false, false, true, body, false);
        posed.groupIndicesByTransform();
        posed.applyAnimation(Animation.animations[animation].frameIds[0], (byte) 6);
        return posed;
    }

    /** The stand animation of the npc as the player sees it, -1 for none. */
    private static int standAnimation(int npcId) {
        NpcDefinition shown = NpcDefinition.forId(npcId);
        if (shown.morphIds != null)
            shown = shown.morph(false);
        return shown == null ? -1 : shown.standAnimation;
    }

    private static ModelFit.Vertices framed(Model body) {
        return ModelFit.framed(body.verticesX, body.verticesY, body.verticesZ, body.verticesCount, body.faceIndicesX,
                body.faceIndicesY, body.faceIndicesZ, body.faceTransparency, body.faceCount);
    }

    /** A press on {@code widgetId}: true when it starts dragging a tile, drawn above its neighbours from now on. */
    static boolean startTileDrag(int widgetId) {
        Optional<Integer> tile = tileDrag.tileOf(widgetId);
        if (tile.isEmpty())
            return false;
        draggedTile = tile.get();
        JagInterface layer = JagInterface.forId(parentOf(draggedTile));
        childrenBeforeDrag = new TileDrag.Children(layer.childIds, layer.childX, layer.childY);
        TileDrag.Children front = TileDrag.toFront(childrenBeforeDrag, draggedTile);
        layer.childIds = front.ids();
        layer.childX = front.xs();
        layer.childY = front.ys();
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
        layer.childIds = childrenBeforeDrag.ids();
        layer.childX = childrenBeforeDrag.xs();
        layer.childY = childrenBeforeDrag.ys();
        draggedTile = -1;
    }

    /** The move of a tile dragged by {@code face} and dropped on {@code target}, the widget under the mouse. */
    static Optional<TileDrag.Move> droppedTile(int face, int target) {
        return tileDrag.drop(face, target);
    }

    private static int parentOf(int id) {
        return specs.spec(id).orElseThrow().parent();
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
        QuestJournal.Layout layout = QuestJournal.withStages(list.childIds, list.childX, list.childY, list.anInt285);
        list.childIds = layout.ids();
        list.childX = layout.xs();
        list.childY = layout.ys();
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
