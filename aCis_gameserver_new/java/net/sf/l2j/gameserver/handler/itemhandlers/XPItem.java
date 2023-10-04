package net.sf.l2j.gameserver.handler.itemhandlers;

import net.sf.l2j.gameserver.data.xml.PlayerLevelData;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.PlayerLevel;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

public class XPItem implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		final Player player = playable.getActingPlayer();
		
		if (player == null)
			return;
		
		if (player.getStatus().getLevel() >= 80)
		{
			player.sendMessage("You have already reached the maximum level for your character!.");
			return;
		}
		
		final PlayerLevel pl = PlayerLevelData.getInstance().getPlayerLevel(80);
		
		final long pXp = player.getStatus().getExp();
		final long tXp = pl.getRequiredExpToLevelUp();
		
		if(player.destroyItem("", item, 1, null, true))
		{
		player.sendMessage("You have reached level 80, may the 12 Gods bless you.");
			player.broadcastUserInfo();
			
			
			if (pXp > tXp)
				player.removeExpAndSp(pXp - tXp, 0);
			else if (pXp < tXp)
				player.addExpAndSp(tXp - pXp, 0);
			
			/*
			 * // final long xp01 =451064882; OLD CODE // final int sp01 = 451064882; // ((Player)playable).addExpAndSp(xp01,sp01); ((Player)playable).getMemos().set("player_lvl","80"); ((Player)playable).getStatus().setLevel(80); ((Player)playable).broadcastUserInfo();
			 * ((Player)playable).sendMessage("You Are Now Welcome to Olympus , May the 12 Gods Be With You."); ((Player)playable).destroyItem("Consume", item.getObjectId(), 1, null, false); //((Player)playable).destroyItem("", item,1, null,true);
			 */
		}
	}
}