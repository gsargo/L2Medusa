package net.sf.l2j.gameserver.handler.itemhandlers;

import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.scripting.Quest;

public class NobleItem implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		Player activeChar = (Player) playable;
		if (activeChar.isNoble())
		{
			((Player) playable).sendMessage("You are already a member of the nobility!");
			return;
		}
		
		final String MY_NOBLE_SOUND = Quest.SOUND_FINISH;
		((Player) playable).setNoble(true, true); // else
		((Player) playable).broadcastUserInfo();
		((Player) playable).sendMessage("You have been granted with Noblesse status.");
		// giveItems(player, 20003, 1);
		((Player) playable).addItem("", 20003, 1, null, false); // Nobless Skeleton Circlet
		Quest.playSound(((Player) playable), MY_NOBLE_SOUND);
		((Player) playable).destroyItem("", item, 1, null, true);
	}
	
}