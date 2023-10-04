package net.sf.l2j.gameserver.handler.itemhandlers;

import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.scripting.Quest;

public class ClanRepItem implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		Player activeChar = (Player) playable;
		
		if (activeChar.getClan() == null)
		{
			((Player) playable).sendMessage("Please join a Clan before using this item!.");
			return;
		}
		
		if (!activeChar.isClanLeader())
		{
			activeChar.sendMessage("This item is usable only by Clan Leaders.");
			return;
		}
		
		if (activeChar.getClan().getLevel() < 5)
		{
			activeChar.sendMessage("Clan level must be 5 or higher in order to use that item.");
			return;
		}
		
		if (activeChar.destroyItem("", item, 1, null, true)) // check if item exists on player's inventory
		{
			
			final String MY_CLAN_SOUND = Quest.SOUND_ITEMGET;
			// ((Player)playable).getClan().addReputationScore(400);
			activeChar.getClan().addReputationScore(400);
			activeChar.sendMessage("Your Clan has been granted with 400 reputation points.");
			Quest.playSound(activeChar, MY_CLAN_SOUND);
			MagicSkillUse fireworks = new MagicSkillUse(activeChar, activeChar, 2024, 1, 1, 0);
			activeChar.broadcastPacket(fireworks);		
			
		}
		
		else
		{
			activeChar.sendMessage("Something went wrong, required items are missing.");
			return;
		}
	}
	
}