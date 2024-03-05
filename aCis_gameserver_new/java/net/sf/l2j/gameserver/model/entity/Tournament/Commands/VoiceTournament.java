package net.sf.l2j.gameserver.model.entity.Tournament.Commands;

import java.util.StringTokenizer;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Tournament.TournamentManager;
import net.sf.l2j.gameserver.model.entity.Tournament.enums.TournamentFightType;
import net.sf.l2j.gameserver.model.entity.Tournament.model.TournamentTeam;

/**
 * @author Rouxy
 */
public class VoiceTournament implements IVoicedCommandHandler
{
	
	@Override
	public boolean useVoicedCommand(String command, Player player, String params)
	{
		StringTokenizer st = new StringTokenizer(params, " ");
		st.nextToken();
		TournamentTeam team = player.getTournamentTeam();
		if (command.startsWith("mytour"))
		{
			TournamentManager.getInstance().showHtml(player, "myTour", TournamentFightType.NONE);
		}
		if (command.startsWith("arenainvite"))
		{
			if (!TournamentManager.getInstance().isRunning())
			{
				player.sendMessage("Battle Arena isn't Running!");
				return false;
			}
			if (TournamentManager.getInstance().isTournamentTeleporting())
			{
				player.sendMessage("Battle Arena is teleporting players, wait 30 seconds before inviting someone.");
				return false;
			}
			String nextMemberName = st.nextToken();
			Player nextMember = World.getInstance().getPlayer(nextMemberName);
			if (nextMember == player)
			{
				player.sendMessage("You can't invite yourself!");
				return false;
			}
			
			if (nextMember != null)
			{
				if (nextMember.isInTournamentTeam())
				{
					player.sendMessage("This player is already in a team.");
					return false;
				}
				if (nextMember.isInParty())
				{
					player.sendMessage("You can't invite players in party. Please use .arenainvite and party will be created automatically!");
					return false;
				}
				if (team != null)
				{
					if (team.getLeader() != player)
					{
						player.sendMessage("Only Leaders can invite players.");
						return false;
					}
				}
				TournamentManager.getInstance().askJoinTeam(player, nextMember);
			}
			else
			{
				player.sendMessage("Player " + nextMemberName + " doesn't exists or is not online!");
				return false;
			}
		}
		
		if(command.startsWith("arenaleave"))
		{
			if (team != null)
			{
				if(team.getMembers().size()<=2)
					team.disbandTeam();
				else
					team.removeMember(player);
			}
			else
			{
				player.sendMessage("You do not belong in a team.");
			}
		}
		
		if(command.startsWith("arenadismiss"))
		{
			if (team != null)
			{
				team.disbandTeam();
			}
			else
			{
				player.sendMessage("You do not have a Battle Arena team.");
			}
			TournamentManager.getInstance().showHtml(player, "main", TournamentFightType.NONE);
		}
			
		return false;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		
		return new String[]
		{
			"mytour",
			"arenainvite"
		};
	}
	
}
