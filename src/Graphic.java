// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public class Graphic extends Entity {

	public void method604(byte byte0, int i) {
		anInt1738 += i;
		if (byte0 == 1)
			byte0 = 0;
		else
			return;
		while (anInt1738 > spotAnimation.animation.method205(0, anInt1737)) {
			anInt1738 -= spotAnimation.animation.method205(0, anInt1737);
			anInt1737++;
			if (anInt1737 >= spotAnimation.animation.anInt294
					&& (anInt1737 < 0 || anInt1737 >= spotAnimation.animation.anInt294)) {
				anInt1737 = 0;
				aBoolean1736 = true;
			}
		}
	}

	public Graphic(int i, int j, int k, int l, int i1, int j1, int k1, int l1) {
		aBoolean1735 = true;
		aBoolean1736 = false;
		spotAnimation = SpotAnimation.spotAnimations[i1];
		anInt1731 = j;
		anInt1732 = i;
		anInt1733 = k1;
		if (l1 != 10709) {
			for (int i2 = 1; i2 > 0; i2++);
		}
		anInt1734 = k;
		anInt1740 = j1 + l;
		aBoolean1736 = false;
	}

	@Override
	public Model getModel() {
		Model class50_sub1_sub4_sub4 = spotAnimation.getModel();
		if (class50_sub1_sub4_sub4 == null)
			return null;
		int i = spotAnimation.animation.frameIds[anInt1737];
		Model class50_sub1_sub4_sub4_1 = new Model(false, false, true,
				class50_sub1_sub4_sub4, AnimationFrame.isFrameTransparent(i));
		if (!aBoolean1736) {
			class50_sub1_sub4_sub4_1.groupIndicesByTransform();
			class50_sub1_sub4_sub4_1.applyAnimation(i, (byte) 6);
			class50_sub1_sub4_sub4_1.faceIndicesByBone = null;
			class50_sub1_sub4_sub4_1.vertexIndicesByBone = null;
		}
		if (spotAnimation.anInt561 != 128 || spotAnimation.anInt562 != 128)
			class50_sub1_sub4_sub4_1.resizeModel(spotAnimation.anInt561, spotAnimation.anInt562, spotAnimation.anInt561
            );
		if (spotAnimation.anInt563 != 0) {
			if (spotAnimation.anInt563 == 90)
				class50_sub1_sub4_sub4_1.rotate90Y();
			if (spotAnimation.anInt563 == 180) {
				class50_sub1_sub4_sub4_1.rotate90Y();
				class50_sub1_sub4_sub4_1.rotate90Y();
			}
			if (spotAnimation.anInt563 == 270) {
				class50_sub1_sub4_sub4_1.rotate90Y();
				class50_sub1_sub4_sub4_1.rotate90Y();
				class50_sub1_sub4_sub4_1.rotate90Y();
			}
		}
		class50_sub1_sub4_sub4_1.initLighting(64 + spotAnimation.anInt564, 850 + spotAnimation.anInt565, -30, -50, -30,
				true);
		return class50_sub1_sub4_sub4_1;
	}

	public int anInt1731;
	public int anInt1732;
	public int anInt1733;
	public int anInt1734;
	public boolean aBoolean1735;
	public boolean aBoolean1736;
	public int anInt1737;
	public int anInt1738;
	public SpotAnimation spotAnimation;
	public int anInt1740;
}
