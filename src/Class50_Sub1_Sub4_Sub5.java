// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public class Class50_Sub1_Sub4_Sub5 extends Entity {

	public boolean aBoolean1714;
	public int anInt1715;
	public int anInt1716;
	public int anInt1717;
	public int anInt1718;
	public int anInt1719;
	public int anInt1720;
	public int anInt1721;
	public int anInt1722;
	public static client client;
	public Animation animation;
	public int anInt1725;
	public int anInt1726;
	public int[] anIntArray1727;
	public int anInt1728;
	public int anInt1729;
	public int anInt1730;

	public ObjectDefinition method603(int i) {
		int j = -1;

		if (i != 0)
			anInt1728 = 109;

		if (anInt1725 != -1) {
			Varbit varbit = Varbit.varbitTable[anInt1725];
			int varpId = varbit.varpId;
			int leastSignificantBit = varbit.leastSignificantBit;
			int mostSignificantBit = varbit.mostSignificantBit;
			int bitfieldMaxValue = client.BITFIELD_MAX_VALUES[mostSignificantBit - leastSignificantBit];
			j = client.localVarps[varpId] >> leastSignificantBit & bitfieldMaxValue;
		} else if (anInt1726 != -1)
			j = client.localVarps[anInt1726];
		if (j < 0 || j >= anIntArray1727.length || anIntArray1727[j] == -1)
			return null;
		else
			return ObjectDefinition.forId(anIntArray1727[j]);
	}

	public Class50_Sub1_Sub4_Sub5(int i, int j, int k, int l, int i1, byte byte0, int j1, boolean flag, int k1, int l1) {
		aBoolean1714 = false;
		anInt1720 = j1;
		anInt1721 = i1;
		anInt1722 = l1;
		anInt1715 = k1;
		anInt1716 = l;
		anInt1717 = j;
		anInt1718 = k;
		if (i != -1) {
			animation = Animation.animations[i];
			anInt1730 = 0;
			anInt1729 = client.pulseCycle - 1;
			if (flag && animation.anInt298 != -1) {
				anInt1730 = (int) (Math.random() * animation.anInt294);
				anInt1729 -= (int) (Math.random() * animation.method205(0, anInt1730));
			}
		}
		ObjectDefinition class47 = ObjectDefinition.forId(anInt1720);
		anInt1725 = class47.varbitId;
		anInt1726 = class47.anInt781;
		anIntArray1727 = class47.anIntArray805;
		if (byte0 != 3)
			anInt1719 = -126;
	}

	@Override
	public Model getModel() {
		int i = -1;
		if (animation != null) {
			int j = client.pulseCycle - anInt1729;
			if (j > 100 && animation.anInt298 > 0)
				j = 100;
			while (j > animation.method205(0, anInt1730)) {
				j -= animation.method205(0, anInt1730);
				anInt1730++;
				if (anInt1730 < animation.anInt294)
					continue;
				anInt1730 -= animation.anInt298;
				if (anInt1730 >= 0 && anInt1730 < animation.anInt294)
					continue;
				animation = null;
				break;
			}
			anInt1729 = client.pulseCycle - j;
			if (animation != null)
				i = animation.frameIds[anInt1730];
		}
		ObjectDefinition objectDefinition;
		if (anIntArray1727 != null)
			objectDefinition = method603(0);
		else
			objectDefinition = ObjectDefinition.forId(anInt1720);
		if (objectDefinition == null) {
			return null;
		} else {
			Model class50_sub1_sub4_sub4 = objectDefinition.method431(anInt1721, anInt1722, anInt1715,
					anInt1716, anInt1717, anInt1718, i);
			return class50_sub1_sub4_sub4;
		}
	}
}
