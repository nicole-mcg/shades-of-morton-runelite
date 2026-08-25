package com.shadesofmorton;

import java.util.HashMap;
import java.util.Map;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;

/**
 * A shade key: the key item, the chest object it unlocks, and its metal. One constant per
 * key ({@code <METAL>_<COLOUR>}), 5 metals x 5 colours.
 */
public enum ShadeKey
{
	// bronze
	BRONZE_BLOODRED(ItemID.SHADEKEY_BRONZE_BLOODRED, ObjectID.SHADECHEST_BRONZE_BLOODRED, ShadeKeyMetal.BRONZE),
	BRONZE_BROWN(ItemID.SHADEKEY_BRONZE_BROWN, ObjectID.SHADECHEST_BRONZE_BROWN, ShadeKeyMetal.BRONZE),
	BRONZE_CRIMSON(ItemID.SHADEKEY_BRONZE_CRIMSON, ObjectID.SHADECHEST_BRONZE_CRIMSON, ShadeKeyMetal.BRONZE),
	BRONZE_BLACK(ItemID.SHADEKEY_BRONZE_BLACK, ObjectID.SHADECHEST_BRONZE_BLACK, ShadeKeyMetal.BRONZE),
	BRONZE_PURPLE(ItemID.SHADEKEY_BRONZE_PURPLE, ObjectID.SHADECHEST_BRONZE_PURPLE, ShadeKeyMetal.BRONZE),

	// steel
	STEEL_BLOODRED(ItemID.SHADEKEY_STEEL_BLOODRED, ObjectID.SHADECHEST_STEEL_BLOODRED, ShadeKeyMetal.STEEL),
	STEEL_BROWN(ItemID.SHADEKEY_STEEL_BROWN, ObjectID.SHADECHEST_STEEL_BROWN, ShadeKeyMetal.STEEL),
	STEEL_CRIMSON(ItemID.SHADEKEY_STEEL_CRIMSON, ObjectID.SHADECHEST_STEEL_CRIMSON, ShadeKeyMetal.STEEL),
	STEEL_BLACK(ItemID.SHADEKEY_STEEL_BLACK, ObjectID.SHADECHEST_STEEL_BLACK, ShadeKeyMetal.STEEL),
	STEEL_PURPLE(ItemID.SHADEKEY_STEEL_PURPLE, ObjectID.SHADECHEST_STEEL_PURPLE, ShadeKeyMetal.STEEL),

	// black
	BLACK_BLOODRED(ItemID.SHADEKEY_BLACK_BLOODRED, ObjectID.SHADECHEST_BLACK_BLOODRED, ShadeKeyMetal.BLACK),
	BLACK_BROWN(ItemID.SHADEKEY_BLACK_BROWN, ObjectID.SHADECHEST_BLACK_BROWN, ShadeKeyMetal.BLACK),
	BLACK_CRIMSON(ItemID.SHADEKEY_BLACK_CRIMSON, ObjectID.SHADECHEST_BLACK_CRIMSON, ShadeKeyMetal.BLACK),
	BLACK_BLACK(ItemID.SHADEKEY_BLACK_BLACK, ObjectID.SHADECHEST_BLACK_BLACK, ShadeKeyMetal.BLACK),
	BLACK_PURPLE(ItemID.SHADEKEY_BLACK_PURPLE, ObjectID.SHADECHEST_BLACK_PURPLE, ShadeKeyMetal.BLACK),

	// silver
	SILVER_BLOODRED(ItemID.SHADEKEY_SILVER_BLOODRED, ObjectID.SHADECHEST_SILVER_BLOODRED, ShadeKeyMetal.SILVER),
	SILVER_BROWN(ItemID.SHADEKEY_SILVER_BROWN, ObjectID.SHADECHEST_SILVER_BROWN, ShadeKeyMetal.SILVER),
	SILVER_CRIMSON(ItemID.SHADEKEY_SILVER_CRIMSON, ObjectID.SHADECHEST_SILVER_CRIMSON, ShadeKeyMetal.SILVER),
	SILVER_BLACK(ItemID.SHADEKEY_SILVER_BLACK, ObjectID.SHADECHEST_SILVER_BLACK, ShadeKeyMetal.SILVER),
	SILVER_PURPLE(ItemID.SHADEKEY_SILVER_PURPLE, ObjectID.SHADECHEST_SILVER_PURPLE, ShadeKeyMetal.SILVER),

	// gold — chest object IDs have no gameval constant; raw IDs captured in-game.
	GOLD_BLOODRED(ItemID.SHADEKEY_GOLD_BLOODRED, 41212, ShadeKeyMetal.GOLD),
	GOLD_BROWN(ItemID.SHADEKEY_GOLD_BROWN, 41213, ShadeKeyMetal.GOLD),
	GOLD_CRIMSON(ItemID.SHADEKEY_GOLD_CRIMSON, 41214, ShadeKeyMetal.GOLD),
	GOLD_BLACK(ItemID.SHADEKEY_GOLD_BLACK, 41215, ShadeKeyMetal.GOLD),
	GOLD_PURPLE(ItemID.SHADEKEY_GOLD_PURPLE, 41216, ShadeKeyMetal.GOLD);

	private final int keyItemId;
	private final int chestObjectId;
	private final ShadeKeyMetal metal;

	ShadeKey(int keyItemId, int chestObjectId, ShadeKeyMetal metal)
	{
		this.keyItemId = keyItemId;
		this.chestObjectId = chestObjectId;
		this.metal = metal;
	}

	public int getKeyItemId()
	{
		return keyItemId;
	}

	public int getChestObjectId()
	{
		return chestObjectId;
	}

	public ShadeKeyMetal getMetal()
	{
		return metal;
	}

	private static final Map<Integer, ShadeKey> BY_KEY_ITEM_ID = buildKeyItemLookup();
	private static final Map<Integer, ShadeKey> BY_CHEST_OBJECT_ID = buildChestObjectLookup();

	private static Map<Integer, ShadeKey> buildKeyItemLookup()
	{
		final Map<Integer, ShadeKey> map = new HashMap<>();
		for (ShadeKey key : values())
		{
			map.put(key.keyItemId, key);
		}
		return Map.copyOf(map);
	}

	private static Map<Integer, ShadeKey> buildChestObjectLookup()
	{
		final Map<Integer, ShadeKey> map = new HashMap<>();
		for (ShadeKey key : values())
		{
			map.put(key.chestObjectId, key);
		}
		return Map.copyOf(map);
	}

	/**
	 * @return the key for a key item id, or {@code null} if the item isn't a shade key.
	 */
	public static ShadeKey fromKeyItemId(int keyItemId)
	{
		return BY_KEY_ITEM_ID.get(keyItemId);
	}

	/**
	 * @return the key whose chest has this object id, or {@code null} if none.
	 */
	public static ShadeKey fromChestObjectId(int chestObjectId)
	{
		return BY_CHEST_OBJECT_ID.get(chestObjectId);
	}
}
