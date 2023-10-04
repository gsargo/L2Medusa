package net.sf.l2j.gameserver.handler.itemhandlers;

import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

public class HeroItem implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		Player activeChar = (Player) playable;
		if (activeChar.isHero())
		{
			((Player) playable).sendMessage("You are already in a Hero status!.");
			return;
		}
		
		// else
		if(((Player) playable).destroyItem("", item, 1, null, true))
		{
			((Player) playable).setHero(true);
			((Player) playable).broadcastUserInfo();
			((Player) playable).sendMessage("You are now a Hero, you are granted with Hero status, and Hero skills.");
			
		}
	}
	
	/*
	 * @Override public void useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) { if (!(playable instanceof Player)) return; Player activeChar = (Player)playable; int itemId = item.getItemId(); if (itemId == 9957) // Item to become hero { activeChar.setHero(true);
	 * activeChar.broadcastUserInfo(); } int itemId = item.getItemId();//Permanent Hero Item Check - Hero on Login if (itemId == 129) { ((Player)playable).setHero(true); } }
	 */
}