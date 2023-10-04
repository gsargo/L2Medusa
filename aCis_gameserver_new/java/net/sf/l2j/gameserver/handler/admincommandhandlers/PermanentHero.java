package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.sql.Connection;
import java.sql.PreparedStatement;

import net.sf.l2j.commons.pool.ConnectionPool;

import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;

/**
 * @author Giorgos
 */
public class PermanentHero implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_phero"
	};
	
	private static final String CHECK_HERO = "SELECT * FROM permanent_hero WHERE char_obj_id=?";
	private static final String DELETE_HERO = "DELETE FROM permanent_hero WHERE char_obj_id=?";
	private static final String SET_HERO = "INSERT INTO permanent_hero (char_obj_id) VALUES (?)";
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		if (command.startsWith("admin_phero"))
		{
			final Player targetPlayer = getTargetPlayer(player, false);
			if (targetPlayer != null)
			{
				
				if (targetPlayer.getPermanentHero() == false)
				{
					// Set permanent Hero
					try (Connection con = ConnectionPool.getConnection();
						PreparedStatement ps = con.prepareStatement(SET_HERO))
					{
						ps.setInt(1, targetPlayer.getObjectId());
						targetPlayer.setPermanentHero();
						targetPlayer.setHero(true);
						
					}
					catch (final Exception e)
					{
						LOGGER.error("Couldn't set the player as hero {}.", e, targetPlayer.getName());
					}
				}
				else
				{
					// Remove Permanent Hero
					try (Connection con = ConnectionPool.getConnection();
						PreparedStatement ps = con.prepareStatement(DELETE_HERO))
					{
						ps.setInt(1, targetPlayer.getObjectId());
						targetPlayer.setPermanentHero();
						targetPlayer.setHero(false);
						
					}
					catch (final Exception e)
					{
						LOGGER.error("Couldn't delete hero status from player {}.", e, targetPlayer.getName());
					}
				}
			}
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}