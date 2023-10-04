package net.sf.l2j.gameserver.model.itemcontainer.listeners;

import net.sf.l2j.gameserver.enums.Paperdoll;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

public class CostumeListener implements OnEquipListener
{
	private static CostumeListener instance = new CostumeListener();
	
	public static CostumeListener getInstance()
	{
		return instance;
	}
	
	@Override
	public void onEquip(Paperdoll slot, ItemInstance item, Playable actor)
	{
		if (item.getItem().getTemplate())
		{
			actor.getActingPlayer().setTempHair(item.getItem().getTempHair());
			actor.getActingPlayer().setTempFace(item.getItem().getTempFace());
			actor.getActingPlayer().setTempChest(item.getItem().getTempChest());
			actor.getActingPlayer().setTempLegs(item.getItem().getTempLegs());
			actor.getActingPlayer().setTempGloves(item.getItem().getTempGloves());
			actor.getActingPlayer().setTempFeet(item.getItem().getTempFeet());
		}
	}
	
	@Override
	public void onUnequip(Paperdoll slot, ItemInstance item, Playable actor)
	{
		if (item.getItem().getTemplate())
		{
			actor.getActingPlayer().setTempHair(0);
			actor.getActingPlayer().setTempFace(0);
			actor.getActingPlayer().setTempChest(0);
			actor.getActingPlayer().setTempLegs(0);
			actor.getActingPlayer().setTempGloves(0);
			actor.getActingPlayer().setTempFeet(0);
		}
	}
}
