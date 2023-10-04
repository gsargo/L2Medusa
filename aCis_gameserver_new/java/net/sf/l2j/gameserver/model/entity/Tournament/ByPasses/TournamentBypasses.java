package net.sf.l2j.gameserver.model.entity.Tournament.ByPasses;

import java.util.StringTokenizer;

import net.sf.l2j.gameserver.data.sql.PlayerInfoTable;
import net.sf.l2j.gameserver.handler.IBypassHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Tournament.TournamentManager;
import net.sf.l2j.gameserver.model.entity.Tournament.enums.TournamentFightType;
import net.sf.l2j.gameserver.model.entity.Tournament.matches.TournamentMatch1x1;
import net.sf.l2j.gameserver.model.entity.Tournament.matches.TournamentMatch2x2;
import net.sf.l2j.gameserver.model.entity.Tournament.matches.TournamentMatch3x3;
import net.sf.l2j.gameserver.model.entity.Tournament.matches.TournamentMatch4x4;
import net.sf.l2j.gameserver.model.entity.Tournament.matches.TournamentMatch5x5;
import net.sf.l2j.gameserver.model.entity.Tournament.matches.TournamentMatch9x9;
import net.sf.l2j.gameserver.model.entity.Tournament.model.TournamentTeam;

public class TournamentBypasses implements IBypassHandler
{
	
	@Override
	public boolean handleBypass(String bypass, Player player)
	{
		StringTokenizer st = new StringTokenizer(bypass, " ");
		st.nextToken();
		TournamentTeam team = player.getTournamentTeam();
		
		if (bypass.startsWith("bp_tournamentTeamInfo"))
		{
			if (team != null)
			{
				TournamentManager.getInstance().showHtml(player, "createTeam", TournamentFightType.F2X2);
			}
			else
			{
				player.sendMessage("First you must create a new Battle Arena team.");
			}
		}
		if (bypass.startsWith("bp_leaveTournamentTeam"))
		{
			if (team != null)
			{
				team.removeMember(player);
			}
			else
			{
				player.sendMessage("You do not have a Team.");
			}
		}
		if (bypass.startsWith("bp_registerTournament1x1"))
		{
			TournamentManager.getInstance().showHtml(player, "fights/F1X1", TournamentFightType.F1X1);
			if (!TournamentManager.getInstance().isRunning())
			{
				player.sendMessage("Battle Arena isn't Running!");
				return false;
			}
			if (!player.isInTournamentTeam())
			{
				team = new TournamentTeam(player, null);
			}
			
			if (!TournamentMatch1x1.getInstance().checkConditions(team))
			{
				return false;
			}
			if (TournamentManager.getInstance().getRegisteredTournamentTeams().containsKey(team))
			{
				player.sendMessage("Your team is already registered.");
				return true;
			}
			
			if (team==null || team.getLeader() != player)
			{
				player.sendMessage("Only Leaders can register.");
				return false;
			}
			
			if (TournamentMatch1x1.getInstance().register(team))
			{
				team.sendMessage("Your are on the 1x1 waiting list. ");
				return true;
			}
			
		}
		if (bypass.startsWith("bp_registerTournament2x2"))
		{
			if (!TournamentManager.getInstance().isRunning())
			{
				player.sendMessage("Battle Arena isn't Running!");
				return false;
			}
			if (!player.isInTournamentTeam() || player.getTournamentTeam().getMembers().size() < 2)
			{
				player.sendMessage("You need to invite 1 player to register this mode.");
			}
			else
			{
				if (!TournamentMatch2x2.getInstance().checkConditions(team))
				{
					return false;
				}
				if (TournamentManager.getInstance().getRegisteredTournamentTeams().containsKey(team))
				{
					player.sendMessage("Your team is already registered.");
					return true;
				}
				
				if (team == null || team.getLeader() != player)
				{
					player.sendMessage("Only Leaders can register.");
					return false;
				}
				
				if (TournamentMatch2x2.getInstance().register(team))
				{
					team.sendMessage("Your team is on the 2x2 waiting list. ");
					return true;
				}
			}
			TournamentManager.getInstance().showHtml(player, "fights/F2X2", TournamentFightType.F2X2);
			
		}
		if (bypass.startsWith("bp_registerTournament3x3"))
		{
			if (!TournamentManager.getInstance().isRunning())
			{
				player.sendMessage("Battle Arena isn't Running!");
				return false;
			}
			if (!player.isInTournamentTeam() || player.getTournamentTeam().getMembers().size() < 3)
			{
				player.sendMessage("You need to invite 2 players to register this mode.");
			}
			else
			{
				if (!TournamentMatch3x3.getInstance().checkConditions(team))
				{
					return false;
				}
				if (TournamentManager.getInstance().getRegisteredTournamentTeams().containsKey(team))
				{
					player.sendMessage("Your team is already registered.");
					return true;
				}
				
				if (team == null || team.getLeader() != player)
				{
					player.sendMessage("Only Leaders can register.");
					return false;
				}
				
				if (TournamentMatch3x3.getInstance().register(team))
				{
					team.sendMessage("Your team is on the 3x3 waiting list. ");
					return true;
				}
				
			}
			TournamentManager.getInstance().showHtml(player, "fights/F3X3", TournamentFightType.F3X3);
			
		}
		
		if (bypass.startsWith("bp_registerTournament4x4"))
		{
			if (!TournamentManager.getInstance().isRunning())
			{
				player.sendMessage("Tournament isn't Running!");
				return false;
			}
			if (!player.isInTournamentTeam() || player.getTournamentTeam().getMembers().size() < 4)
			{
				player.sendMessage("You need to invite 3 players to register this mode.");
			}
			else
			{
				if (!TournamentMatch3x3.getInstance().checkConditions(team))
				{
					return false;
				}
				if (TournamentManager.getInstance().getRegisteredTournamentTeams().containsKey(team))
				{
					player.sendMessage("Your team already registered.");
					return true;
				}
				
				if (team == null || team.getLeader() != player)
				{
					player.sendMessage("Only Leaders can register.");
					return false;
				}
				
				if (TournamentMatch4x4.getInstance().register(team))
				{
					team.sendMessage("Your team is on the 4x4 waiting list. ");
					return true;
				}
			}
			TournamentManager.getInstance().showHtml(player, "fights/F4X4", TournamentFightType.F4X4);
			
		}
		
		if (bypass.startsWith("bp_registerTournament5x5"))
		{
			if (!TournamentManager.getInstance().isRunning())
			{
				player.sendMessage("Battle Arena isn't Running!");
				return false;
			}
			if (!player.isInTournamentTeam() || player.getTournamentTeam().getMembers().size() < 5)
			{
				player.sendMessage("You need to invite 4 players to register this mode.");
			}
			else
			{
				if (!TournamentMatch3x3.getInstance().checkConditions(team))
				{
					return false;
				}
				if (TournamentManager.getInstance().getRegisteredTournamentTeams().containsKey(team))
				{
					player.sendMessage("Your team already registered.");
					return true;
				}
				
				if (team == null || team.getLeader() != player)
				{
					player.sendMessage("Only Leaders can register.");
					return false;
				}
				
				if (TournamentMatch5x5.getInstance().register(team))
				{
					team.sendMessage("Your team is on the 5x5 waiting list. ");
					return true;
				}
			}
			TournamentManager.getInstance().showHtml(player, "fights/F5X5", TournamentFightType.F5X5);
			
		}
		
		if (bypass.startsWith("bp_registerTournament9x9"))
		{
			if (!TournamentManager.getInstance().isRunning())
			{
				player.sendMessage("Tournament isn't Running!");
				return false;
			}
			if (!player.isInTournamentTeam() || player.getTournamentTeam().getMembers().size() < 9)
			{
				player.sendMessage("You need to invite 8 players to register this mode.");
			}
			else
			{
				if (!TournamentMatch3x3.getInstance().checkConditions(team))
				{
					return false;
				}
				if (TournamentManager.getInstance().getRegisteredTournamentTeams().containsKey(team))
				{
					player.sendMessage("Your team already registered.");
					return true;
				}
				
				if (team == null ||team.getLeader() != player)
				{
					player.sendMessage("Only Leaders can register.");
					return false;
				}
				
				if (TournamentMatch9x9.getInstance().register(team))
				{
					team.sendMessage("Your team is on the 9x9 waiting list. ");
					return true;
				}
			}
			TournamentManager.getInstance().showHtml(player, "fights/F9X9", TournamentFightType.F9X9);
			
		}
		if (bypass.startsWith("bp_deleteTournamentTeam"))
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
		if (bypass.startsWith("bp_inviteTournamentMember"))
		{
			if (!TournamentManager.getInstance().isRunning())
			{
				player.sendMessage("Battle Arena isn't Running!");
				return false;
			}
			if (TournamentManager.getInstance().isTournamentTeleporting())
			{
				player.sendMessage("Battle Arena members are being teleported, wait 30 seconds to invite someone.");
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
					player.sendMessage("This player is already in a team participating in Battle Arena.");
					return false;
				}
				if (nextMember.isInParty())
				{
					player.sendMessage("You can't invite players in a party. Arena team party will be automatically created!");
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
		if (bypass.startsWith("bp_removeTournamentParticipation"))
		{
			if (!TournamentManager.getInstance().isRunning())
			{
				player.sendMessage("Battle Arena isn't Running!");
				return false;
			}
			if (team != null)
			{
				if (TournamentManager.getInstance().getRegisteredTournamentTeams().containsKey(team))
				{
					TournamentManager.getInstance().getRegisteredTournamentTeams().remove(team);
					team.sendMessage("Your team have been removed from Battle Arena waiting list");
				}
				else
				{
					player.sendMessage("Your team isn't registered.");
					TournamentManager.getInstance().showHtml(player, "main", TournamentFightType.NONE);
					return false;
				}
			}
			else
			{
				player.sendMessage("You do not have a team for Battle Arena.");
			}
			TournamentManager.getInstance().showHtml(player, "main", TournamentFightType.NONE);
			
		}
		
		if (bypass.startsWith("bp_createTournamentTeam"))
		{
			if (!TournamentManager.getInstance().isRunning())
			{
				player.sendMessage("Battle Arena isn't Running!");
				return false;
			}
			if (player.getTournamentTeam() != null)
			{
				player.sendMessage("You can't create a new team for Battle Arena.");
				return false;
			}
			
			if (team == null)
			{
				team = new TournamentTeam(player, null);
			}
			else
			{
				player.sendMessage("Your Battle Arena team has been already created, try to invite someone.");
				return false;
			}
			TournamentManager.getInstance().showHtml(player, "main", TournamentFightType.NONE);
			
		}
		else if (bypass.startsWith("bp_showTournamentPage"))
		{
			String page = st.nextToken();
			TournamentManager.getInstance().showHtml(player, page, TournamentFightType.NONE);
		}
		else if (bypass.startsWith("bp_checkTournamentPlayer"))
		{
			String playerName = st.nextToken();
			String type = st.nextToken();
			int targetObjectId = PlayerInfoTable.getInstance().getPlayerObjectId(playerName);
			TournamentManager.getInstance().showPlayerRankingData(player, targetObjectId, TournamentFightType.valueOf(type));
		}
		else if (bypass.startsWith("bp_tournamentRanking"))
		{
			String type = st.nextToken();
			String rankType = st.nextToken();
			TournamentManager.getInstance().showRanking(player, TournamentFightType.valueOf(type), rankType);
		}
		return false;
	}
	
	@Override
	public String[] getBypassHandlersList()
	{
		
		return new String[]
		{
			"bp_checkTournamentPlayer",
			"bp_showTournamentPage",
			"bp_registerTournament1x1",
			"bp_removeTournamentParticipation",
			"bp_createTournamentTeam",
			"bp_registerTournament2x2",
			"bp_inviteTournamentMember",
			"bp_deleteTournamentTeam",
			"bp_tournamentTeamInfo",
			"bp_inviteTournamentPage",
			"bp_registerTournament3x3",
			"bp_registerTournament4x4",
			"bp_registerTournament5x5",
			"bp_registerTournament9x9",
			"bp_tournamentRanking",
			"bp_leaveTournamentTeam"
		
		};
	}
}
