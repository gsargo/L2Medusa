package net.sf.l2j.gameserver.handler.itemhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.commons.pool.ConnectionPool;

import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

public class HeroItem_Perma_30 implements IItemHandler
{
	// private static final String UPDATE_HERO = "UPDATE permanent_hero SET hero=1 WHERE char_name=?";
	private static final String SET_HERO = "INSERT INTO permanent_hero (char_obj_id,expired_date) VALUES (?,?)";
	
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		Player activeChar = (Player) playable;
		if (activeChar.isHero())
		{
			activeChar.sendMessage("You are already in a Hero status!.");
			return;
		}	
			
			if(activeChar.destroyItem("", item, 1, null, true))
			{
				try (Connection con = ConnectionPool.getConnection();
					PreparedStatement ps = con.prepareStatement(SET_HERO))
					{
					// ps.setString(1, activeChar.getName());
					ps.setInt(1, activeChar.getObjectId());
					ps.setLong(2, System.currentTimeMillis() + TimeUnit.DAYS.toMillis(30)); //+ 30 day
					ps.executeUpdate();		
					activeChar.setHero(true);
					activeChar.broadcastUserInfo();
					activeChar.sendMessage("You are now a Hero, you are granted with hero status, and hero skills.");
					}
					catch (final Exception e)
					{
						LOGGER.error("Couldn't update player hero status.", e, activeChar.getName());
					}
			}

		
	}
}

/*
 * ((Player)playable).setHero(true); //else ((Player)playable).broadcastUserInfo(); ((Player)playable).sendMessage("You Are Now a Hero,You Are Granted With Hero Status , And Hero Skills."); // ((Player)playable).destroyItem("", item, null, true); ((Player)playable).destroyItem("", item,1,
 * null,true); }/* /*@Override public void useItem(L2Playable playable, L2ItemInstance item, boolean forceUse) { if (!(playable instanceof Player)) return; Player activeChar = (Player)playable; int itemId = item.getItemId(); if (itemId == 9957) // Item to become hero { activeChar.setHero(true);
 * activeChar.broadcastUserInfo(); } int itemId = item.getItemId();//Permanent Hero Item Check - Hero on Login if (itemId == 129) { ((Player)playable).setHero(true); } }
 */
