package net.sf.l2j.gameserver.model.entity.Tournament.Commands;

import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Tournament.TournamentManager;

/**
 * @author Rouxy
 */
public class AdminTournament implements IAdminCommandHandler
{
	
	@Override
	public void useAdminCommand(String command, Player player)
	{
		if (command.startsWith("admin_tour"))
		{
			if (TournamentManager.getInstance().isRunning())
				TournamentManager.getInstance().finishEvent();
			else
				TournamentManager.getInstance().startEvent();
		}
		
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		
		return new String[]
		{
			"admin_tour"
		};
	}
	
}
