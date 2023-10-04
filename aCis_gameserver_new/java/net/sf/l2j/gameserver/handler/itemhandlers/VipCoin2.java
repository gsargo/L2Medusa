/*
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.l2j.gameserver.handler.itemhandlers;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.scripting.Quest;

/**
 * @author Anarchy
 */
public class VipCoin2 implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		Player player = (Player) playable;
		
		if (player.isVIP())
		{
			player.sendMessage("You already have premium status.");
			return;
		}
		
		if(player.destroyItem("Consume", item.getObjectId(), 1, null, false))
		{
			player.setVIP(true);
			player.setVIPUntil(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 14));
			player.store();
			player.sendMessage("You are now a premium member for 14 days.");
			World.toAllOnlinePlayers(new CreatureSay(0, SayType.PARTYROOM_COMMANDER,"[Premium System]",player.getName()+" is now a premium member!"));

			final String VIP_SOUND = Quest.SOUND_FANFARE;
			Quest.playSound(((Player) playable), VIP_SOUND);
			player.broadcastUserInfo();
			ThreadPool.schedule(new Runnable()
			{
			@Override
				public void run()
				{
					if (player.isOnline() && player.isVIP())
					{
						player.setVIP(false);
						player.setVIPUntil(0);
						player.store();
						player.broadcastUserInfo();
						player.sendMessage("Your premium status has expired.");
					}
				}
			}, player.getVIPUntil() - System.currentTimeMillis());
		}
	}
}
