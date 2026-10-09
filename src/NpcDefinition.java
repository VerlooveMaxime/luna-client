// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public class NpcDefinition {

	public void init(byte byte0, JagBuffer class50_sub1_sub2) {
		if(byte0 != 6)
			throw new NullPointerException();

		do {
			int i = class50_sub1_sub2.getByte();
			if (i == 0)
				return;
			if (i == 1) {
				int j = class50_sub1_sub2.getByte();
				modelIds = new int[j];
				for (int j1 = 0; j1 < j; j1++)
					modelIds[j1] = class50_sub1_sub2.getShort();

			} else if (i == 2)
				aString652 = class50_sub1_sub2.getString();
			else if (i == 3)
				aByteArray660 = class50_sub1_sub2.getStringBytes();
			else if (i == 12)
				aByte642 = class50_sub1_sub2.getSignedByte();
			else if (i == 13)
				standAnimation = class50_sub1_sub2.getShort();
			else if (i == 14)
				anInt645 = class50_sub1_sub2.getShort();
			else if (i == 17) {
				anInt645 = class50_sub1_sub2.getShort();
				anInt643 = class50_sub1_sub2.getShort();
				anInt641 = class50_sub1_sub2.getShort();
				anInt633 = class50_sub1_sub2.getShort();
			} else if (i >= 30 && i < 40) {
				if (aStringArray646 == null)
					aStringArray646 = new String[5];
				aStringArray646[i - 30] = class50_sub1_sub2.getString();
				if (aStringArray646[i - 30].equalsIgnoreCase("hidden"))
					aStringArray646[i - 30] = null;
			} else if (i == 40) {
				int k = class50_sub1_sub2.getByte();
				originalColours = new int[k];
				replacementColours = new int[k];
				for (int k1 = 0; k1 < k; k1++) {
					originalColours[k1] = class50_sub1_sub2.getShort();
					replacementColours[k1] = class50_sub1_sub2.getShort();
				}

			} else if (i == 60) {
				int l = class50_sub1_sub2.getByte();
				anIntArray625 = new int[l];
				for (int l1 = 0; l1 < l; l1++)
					anIntArray625[l1] = class50_sub1_sub2.getShort();

			} else if (i == 90)
				anInt648 = class50_sub1_sub2.getShort();
			else if (i == 91)
				anInt627 = class50_sub1_sub2.getShort();
			else if (i == 92)
				anInt637 = class50_sub1_sub2.getShort();
			else if (i == 93)
				aBoolean636 = false;
			else if (i == 95)
				anInt639 = class50_sub1_sub2.getShort();
			else if (i == 97)
				scaleXZ = class50_sub1_sub2.getShort();
			else if (i == 98)
				scaleY = class50_sub1_sub2.getShort();
			else if (i == 99)
				aBoolean644 = true;
			else if (i == 100)
				anInt663 = class50_sub1_sub2.getSignedByte();
			else if (i == 101)
				anInt658 = class50_sub1_sub2.getSignedByte() * 5;
			else if (i == 102)
				anInt638 = class50_sub1_sub2.getShort();
			else if (i == 103)
				anInt651 = class50_sub1_sub2.getShort();
			else if (i == 106) {
				anInt654 = class50_sub1_sub2.getShort();
				if (anInt654 == 65535)
					anInt654 = -1;
				anInt659 = class50_sub1_sub2.getShort();
				if (anInt659 == 65535)
					anInt659 = -1;
				int i1 = class50_sub1_sub2.getByte();
				morphIds = new int[i1 + 1];
				for (int i2 = 0; i2 <= i1; i2++) {
					morphIds[i2] = class50_sub1_sub2.getShort();
					if (morphIds[i2] == 65535)
						morphIds[i2] = -1;
				}

			} else if (i == 107)
				aBoolean631 = false;
		} while (true);
	}

	public static void method358(boolean flag) {
		aClass33_635 = null;
		anIntArray650 = null;
		aClass37Array655 = null;
		aClass50_Sub1_Sub2_657 = null;
		if (flag) {
			for (int i = 1; i > 0; i++);
		}
	}

	public Model getHeadModel() {
		if (morphIds != null) {
			NpcDefinition class37 = morph(false);
			if (class37 == null)
				return null;
			else
				return class37.getHeadModel();
		}
		if (anIntArray625 == null)
			return null;
		boolean flag = false;
		for (int k = 0; k < anIntArray625.length; k++)
			if (!Model.isDownloaded(anIntArray625[k]))
				flag = true;

		if (flag)
			return null;
		Model aclass50_sub1_sub4_sub4[] = new Model[anIntArray625.length];
		for (int l = 0; l < anIntArray625.length; l++)
			aclass50_sub1_sub4_sub4[l] = Model.forId(anIntArray625[l]);

		Model class50_sub1_sub4_sub4;
		if (aclass50_sub1_sub4_sub4.length == 1)
			class50_sub1_sub4_sub4 = aclass50_sub1_sub4_sub4[0];
		else
			class50_sub1_sub4_sub4 = new Model(aclass50_sub1_sub4_sub4.length,
					aclass50_sub1_sub4_sub4);
		if (originalColours != null) {
			for (int i1 = 0; i1 < originalColours.length; i1++)
				class50_sub1_sub4_sub4.replaceColor(originalColours[i1], replacementColours[i1]);

		}
		return class50_sub1_sub4_sub4;
	}

	/** IdleRS: the whole body unlit, like {@link #getHeadModel}, for a picture widget to light and animate. */
	public Model getBodyModel() {
		if (morphIds != null) {
			NpcDefinition current = morph(false);
			return current == null ? null : current.getBodyModel();
		}
		if (modelIds == null)
			return null;
		for (int modelId : modelIds)
			if (!Model.isDownloaded(modelId))
				return null;
		Model[] parts = new Model[modelIds.length];
		for (int i = 0; i < modelIds.length; i++)
			parts[i] = Model.forId(modelIds[i]);
		Model model = parts.length == 1 ? parts[0] : new Model(parts.length, parts);
		if (originalColours != null)
			for (int i = 0; i < originalColours.length; i++)
				model.replaceColor(originalColours[i], replacementColours[i]);
		if (scaleXZ != 128 || scaleY != 128)
			model.resizeModel(scaleXZ, scaleY, scaleXZ);
		return model;
	}

	public boolean method360(int i) {
		while (i >= 0)
			aBoolean662 = !aBoolean662;
		if (morphIds == null)
			return true;
		int packedValue = -1;
		if (anInt654 != -1) {
			Varbit varbit = Varbit.varbitTable[anInt654];
			int k = varbit.varpId;
			int l = varbit.leastSignificantBit;
			int i1 = varbit.mostSignificantBit;
			int j1 = client.BITFIELD_MAX_VALUES[i1 - l];
			packedValue = aClient629.localVarps[k] >> l & j1;
		} else if (anInt659 != -1)
			packedValue = aClient629.localVarps[anInt659];
		return packedValue >= 0 && packedValue < morphIds.length && morphIds[packedValue] != -1;
	}

	public static void unpack(Archive class2) {
		aClass50_Sub1_Sub2_657 = new JagBuffer(class2.get("npc.dat"));
		JagBuffer class50_sub1_sub2 = new JagBuffer(class2.get("npc.idx"));
		anInt649 = class50_sub1_sub2.getShort();
		anIntArray650 = new int[anInt649];
		int i = 2;
		for (int j = 0; j < anInt649; j++) {
			anIntArray650[j] = i;
			i += class50_sub1_sub2.getShort();
		}

		aClass37Array655 = new NpcDefinition[20];
		for (int k = 0; k < 20; k++)
			aClass37Array655[k] = new NpcDefinition();

	}

	public Model method362(int i, int j, int k, int ai[]) {
		if (morphIds != null) {
			NpcDefinition class37 = morph(false);
			if (class37 == null)
				return null;
			else
				return class37.method362(i, j, 0, ai);
		}
		Model class50_sub1_sub4_sub4 = (Model) aClass33_635.get(id);
		if (class50_sub1_sub4_sub4 == null) {
			boolean flag = false;
			for (int l = 0; l < modelIds.length; l++)
				if (!Model.isDownloaded(modelIds[l]))
					flag = true;

			if (flag)
				return null;
			Model aclass50_sub1_sub4_sub4[] = new Model[modelIds.length];
			for (int i1 = 0; i1 < modelIds.length; i1++)
				aclass50_sub1_sub4_sub4[i1] = Model.forId(modelIds[i1]);

			if (aclass50_sub1_sub4_sub4.length == 1)
				class50_sub1_sub4_sub4 = aclass50_sub1_sub4_sub4[0];
			else
				class50_sub1_sub4_sub4 = new Model(aclass50_sub1_sub4_sub4.length,
						aclass50_sub1_sub4_sub4);
			if (originalColours != null) {
				for (int j1 = 0; j1 < originalColours.length; j1++)
					class50_sub1_sub4_sub4.replaceColor(originalColours[j1], replacementColours[j1]);

			}
			class50_sub1_sub4_sub4.groupIndicesByTransform();
			class50_sub1_sub4_sub4.initLighting(64 + anInt663, 850 + anInt658, -30, -50, -30, true);
			aClass33_635.put(class50_sub1_sub4_sub4, id);
		}
		Model class50_sub1_sub4_sub4_1 = Model.SCRATCH_MODEL;
		if (k != 0)
			aBoolean647 = !aBoolean647;
		class50_sub1_sub4_sub4_1.copyFrom(AnimationFrame.isFrameTransparent(i) & AnimationFrame.isFrameTransparent(j),
				class50_sub1_sub4_sub4);
		if (i != -1 && j != -1)
			class50_sub1_sub4_sub4_1.applyBlendedAnimation(j, 0, i, ai);
		else if (i != -1)
			class50_sub1_sub4_sub4_1.applyAnimation(i, (byte) 6);
		if (scaleXZ != 128 || scaleY != 128)
			class50_sub1_sub4_sub4_1.resizeModel(scaleXZ, scaleY, scaleXZ);
		class50_sub1_sub4_sub4_1.calculateRadius();
		class50_sub1_sub4_sub4_1.faceIndicesByBone = null;
		class50_sub1_sub4_sub4_1.vertexIndicesByBone = null;
		if (aByte642 == 1)
			class50_sub1_sub4_sub4_1.isPriorityPicking = true;
		return class50_sub1_sub4_sub4_1;
	}

	public NpcDefinition morph(boolean flag) {
		if (flag)
			anInt640 = -212;
		int i = -1;
		if (anInt654 != -1) {
			Varbit class49 = Varbit.varbitTable[anInt654];
			int j = class49.varpId;
			int k = class49.leastSignificantBit;
			int l = class49.mostSignificantBit;
			int i1 = client.BITFIELD_MAX_VALUES[l - k];
			i = aClient629.localVarps[j] >> k & i1;
		} else if (anInt659 != -1)
			i = aClient629.localVarps[anInt659];
		if (i < 0 || i >= morphIds.length || morphIds[i] == -1)
			return null;
		else
			return forId(morphIds[i]);
	}

	public static NpcDefinition forId(int id) {
		for (int j = 0; j < 20; j++)
			if (aClass37Array655[j].id == id)
				return aClass37Array655[j];

		anInt661 = (anInt661 + 1) % 20;
		NpcDefinition def = aClass37Array655[anInt661] = new NpcDefinition();
		aClass50_Sub1_Sub2_657.position = anIntArray650[id];
		def.id = id;
		def.init(aByte653, aClass50_Sub1_Sub2_657);
		return def;
	}

	public NpcDefinition() {
		standAnimation = -1;
		aBoolean623 = true;
		anInt624 = 932;
		anInt627 = -1;
		id = -1L;
		scaleY = 128;
		aBoolean631 = true;
		scaleXZ = 128;
		anInt633 = -1;
		aBoolean636 = true;
		anInt637 = -1;
		anInt638 = -1;
		anInt639 = -1;
		anInt640 = 7;
		anInt641 = -1;
		aByte642 = 1;
		anInt643 = -1;
		aBoolean644 = false;
		anInt645 = -1;
		aBoolean647 = false;
		anInt648 = -1;
		anInt651 = 32;
		aString652 = "null";
		anInt654 = -1;
		anInt659 = -1;
		aBoolean662 = false;
	}

	public int standAnimation;
	public int morphIds[];
	public boolean aBoolean623;
	public int anInt624;
	public int anIntArray625[];
	public int modelIds[];
	public int anInt627;
	public long id;
	public static client aClient629;
	public int scaleY;
	public boolean aBoolean631;
	public int scaleXZ;
	public int anInt633;
	public int originalColours[];
	public static LruHashTable aClass33_635 = new LruHashTable(30);
	public boolean aBoolean636;
	public int anInt637;
	public int anInt638;
	public int anInt639;
	public int anInt640;
	public int anInt641;
	public byte aByte642;
	public int anInt643;
	public boolean aBoolean644;
	public int anInt645;
	public String aStringArray646[];
	public boolean aBoolean647;
	public int anInt648;
	public static int anInt649;
	public static int anIntArray650[];
	public int anInt651;
	public String aString652;
	public static byte aByte653 = 6;
	public int anInt654;
	public static NpcDefinition aClass37Array655[];
	public int replacementColours[];
	public static JagBuffer aClass50_Sub1_Sub2_657;
	public int anInt658;
	public int anInt659;
	public byte aByteArray660[];
	public static int anInt661;
	public boolean aBoolean662;
	public int anInt663;

}
