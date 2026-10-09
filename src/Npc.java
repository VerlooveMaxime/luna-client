// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public class Npc extends Actor {

	public Model method569() {
		if (super.currentAnimation >= 0 && super.animationDelay == 0) {
			int i = Animation.animations[super.currentAnimation].frameIds[super.animationFrame];
			int k = -1;
			if (super.anInt1588 >= 0 && super.anInt1588 != super.anInt1634)
				k = Animation.animations[super.anInt1588].frameIds[super.anInt1589];
			return def.method362(i, k, 0, Animation.animations[super.currentAnimation].anIntArray299);
		}
		int j = -1;
		if (super.anInt1588 >= 0)
			j = Animation.animations[super.anInt1588].frameIds[super.anInt1589];
		return def.method362(j, -1, 0, null);
	}

	@Override
	public Model getModel() {
		if (def == null)
			return null;
		Model class50_sub1_sub4_sub4 = method569();
		if (class50_sub1_sub4_sub4 == null)
			return null;
		super.anInt1594 = ((Entity) (class50_sub1_sub4_sub4)).height;
		if (super.anInt1614 != -1 && super.anInt1615 != -1) {
			SpotAnimation class27 = SpotAnimation.spotAnimations[super.anInt1614];
			Model class50_sub1_sub4_sub4_1 = class27.getModel();
			if (class50_sub1_sub4_sub4_1 != null) {
				int i = class27.animation.frameIds[super.anInt1615];
				Model class50_sub1_sub4_sub4_2 = new Model(false, false, true,
						class50_sub1_sub4_sub4_1, AnimationFrame.isFrameTransparent(i));
				class50_sub1_sub4_sub4_2.translate(0, -super.anInt1618, 0);
				class50_sub1_sub4_sub4_2.groupIndicesByTransform();
				class50_sub1_sub4_sub4_2.applyAnimation(i, (byte) 6);
				class50_sub1_sub4_sub4_2.faceIndicesByBone = null;
				class50_sub1_sub4_sub4_2.vertexIndicesByBone = null;
				if (class27.anInt561 != 128 || class27.anInt562 != 128)
					class50_sub1_sub4_sub4_2.resizeModel(class27.anInt561, class27.anInt562, class27.anInt561);
				class50_sub1_sub4_sub4_2.initLighting(64 + class27.anInt564, 850 + class27.anInt565, -30, -50, -30, true);
				Model aclass50_sub1_sub4_sub4[] = { class50_sub1_sub4_sub4, class50_sub1_sub4_sub4_2 };
				class50_sub1_sub4_sub4 = new Model(2, true, 0, aclass50_sub1_sub4_sub4);
			}
		}
		if (def.aByte642 == 1)
			class50_sub1_sub4_sub4.isPriorityPicking = true;
		return class50_sub1_sub4_sub4;
	}

	@Override
	public boolean isVisible() {
		return def != null;
	}

	public Npc() {
		
	}

	public NpcDefinition def;
}
