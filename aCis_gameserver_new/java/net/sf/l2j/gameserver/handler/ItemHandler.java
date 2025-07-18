package net.sf.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;

import net.sf.l2j.gameserver.handler.itemhandlers.BeastSoulShots;
import net.sf.l2j.gameserver.handler.itemhandlers.BeastSpices;
import net.sf.l2j.gameserver.handler.itemhandlers.BeastSpiritShots;
import net.sf.l2j.gameserver.handler.itemhandlers.BlessedSpiritShots;
import net.sf.l2j.gameserver.handler.itemhandlers.Books;
import net.sf.l2j.gameserver.handler.itemhandlers.Calculators;
import net.sf.l2j.gameserver.handler.itemhandlers.ClanRepItem;
import net.sf.l2j.gameserver.handler.itemhandlers.CostumeEtcItem;
import net.sf.l2j.gameserver.handler.itemhandlers.CostumeEtcItem_hair;
import net.sf.l2j.gameserver.handler.itemhandlers.CustomDoorKey;
import net.sf.l2j.gameserver.handler.itemhandlers.CustomDoorKey_Asphodel;
import net.sf.l2j.gameserver.handler.itemhandlers.DivineInspiration1;
import net.sf.l2j.gameserver.handler.itemhandlers.DivineInspiration2;
import net.sf.l2j.gameserver.handler.itemhandlers.DivineInspiration3;
import net.sf.l2j.gameserver.handler.itemhandlers.DivineInspiration4;
import net.sf.l2j.gameserver.handler.itemhandlers.Elixirs;
import net.sf.l2j.gameserver.handler.itemhandlers.EnchantScrolls;
import net.sf.l2j.gameserver.handler.itemhandlers.FishShots;
import net.sf.l2j.gameserver.handler.itemhandlers.Harvesters;
import net.sf.l2j.gameserver.handler.itemhandlers.HeroItem;
import net.sf.l2j.gameserver.handler.itemhandlers.HeroItem_Perma;
import net.sf.l2j.gameserver.handler.itemhandlers.HtmlMap;
import net.sf.l2j.gameserver.handler.itemhandlers.ItemSkills;
import net.sf.l2j.gameserver.handler.itemhandlers.Keys;
import net.sf.l2j.gameserver.handler.itemhandlers.Maps;
import net.sf.l2j.gameserver.handler.itemhandlers.MercenaryTickets;
import net.sf.l2j.gameserver.handler.itemhandlers.NobleItem;
import net.sf.l2j.gameserver.handler.itemhandlers.PaganKeys;
import net.sf.l2j.gameserver.handler.itemhandlers.PetFoods;
import net.sf.l2j.gameserver.handler.itemhandlers.Recipes;
import net.sf.l2j.gameserver.handler.itemhandlers.RollingDices;
import net.sf.l2j.gameserver.handler.itemhandlers.ScrollsOfResurrection;
import net.sf.l2j.gameserver.handler.itemhandlers.Seeds;
import net.sf.l2j.gameserver.handler.itemhandlers.SevenSignsRecords;
import net.sf.l2j.gameserver.handler.itemhandlers.SoulCrystals;
import net.sf.l2j.gameserver.handler.itemhandlers.SoulShots;
import net.sf.l2j.gameserver.handler.itemhandlers.SpecialXMas;
import net.sf.l2j.gameserver.handler.itemhandlers.SpiritShots;
import net.sf.l2j.gameserver.handler.itemhandlers.SummonItems;
import net.sf.l2j.gameserver.handler.itemhandlers.TitleColorItem;
import net.sf.l2j.gameserver.handler.itemhandlers.TitleColorItem2;
import net.sf.l2j.gameserver.handler.itemhandlers.TitleColorItemDon;
import net.sf.l2j.gameserver.handler.itemhandlers.TitleColorItemPvPShop;
import net.sf.l2j.gameserver.handler.itemhandlers.TitleColorItemPvPShop2;
import net.sf.l2j.gameserver.handler.itemhandlers.TitleColorItemPvPShop3;
import net.sf.l2j.gameserver.handler.itemhandlers.TitleColorItemPvPShop4;
import net.sf.l2j.gameserver.handler.itemhandlers.TitleColorItemPvPShop5;
import net.sf.l2j.gameserver.handler.itemhandlers.VipCoin1;
import net.sf.l2j.gameserver.handler.itemhandlers.VipCoin2;
import net.sf.l2j.gameserver.handler.itemhandlers.VipCoin3;
import net.sf.l2j.gameserver.handler.itemhandlers.VipCoin4;
import net.sf.l2j.gameserver.handler.itemhandlers.XPItem;
import net.sf.l2j.gameserver.model.item.kind.EtcItem;

public class ItemHandler
{
	private final Map<Integer, IItemHandler> _entries = new HashMap<>();
	
	protected ItemHandler()
	{
		registerHandler(new BeastSoulShots());
		registerHandler(new BeastSpices());
		registerHandler(new BeastSpiritShots());
		registerHandler(new BlessedSpiritShots());
		registerHandler(new Books());
		registerHandler(new Calculators());
		registerHandler(new Elixirs());
		registerHandler(new EnchantScrolls());
		registerHandler(new FishShots());
		registerHandler(new Harvesters());
		registerHandler(new ItemSkills());
		registerHandler(new Keys());
		registerHandler(new Maps());
		registerHandler(new MercenaryTickets());
		registerHandler(new PaganKeys());
		registerHandler(new PetFoods());
		registerHandler(new Recipes());
		registerHandler(new RollingDices());
		registerHandler(new ScrollsOfResurrection());
		registerHandler(new Seeds());
		registerHandler(new SevenSignsRecords());
		registerHandler(new SoulShots());
		registerHandler(new SpecialXMas());
		registerHandler(new SoulCrystals());
		registerHandler(new SpiritShots());
		registerHandler(new SummonItems());
		registerHandler(new HeroItem());
		registerHandler(new NobleItem());
		registerHandler(new TitleColorItem());
		registerHandler(new TitleColorItem2());
		registerHandler(new TitleColorItemDon());
		registerHandler(new XPItem());
		registerHandler(new CostumeEtcItem());
		registerHandler(new CostumeEtcItem_hair());
		registerHandler(new ClanRepItem());
		registerHandler(new HeroItem_Perma());
		registerHandler(new DivineInspiration1());
		registerHandler(new DivineInspiration2());
		registerHandler(new DivineInspiration3());
		registerHandler(new DivineInspiration4());
		registerHandler(new TitleColorItemPvPShop());
		registerHandler(new TitleColorItemPvPShop2());
		registerHandler(new TitleColorItemPvPShop3());
		registerHandler(new TitleColorItemPvPShop4());
		registerHandler(new TitleColorItemPvPShop5());
		registerHandler(new VipCoin1());
		registerHandler(new VipCoin2());
		registerHandler(new VipCoin3());
		registerHandler(new VipCoin4());
		registerHandler(new CustomDoorKey());
		registerHandler(new CustomDoorKey_Asphodel());
		registerHandler(new HtmlMap());
	}
	
	private void registerHandler(IItemHandler handler)
	{
		_entries.put(handler.getClass().getSimpleName().intern().hashCode(), handler);
	}
	
	public IItemHandler getHandler(EtcItem item)
	{
		if (item == null || item.getHandlerName() == null)
			return null;
		
		return _entries.get(item.getHandlerName().hashCode());
	}
	
	public int size()
	{
		return _entries.size();
	}
	
	public static ItemHandler getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemHandler INSTANCE = new ItemHandler();
	}
}