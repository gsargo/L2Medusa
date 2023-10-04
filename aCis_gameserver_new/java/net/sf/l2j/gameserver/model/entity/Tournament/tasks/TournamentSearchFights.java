package net.sf.l2j.gameserver.model.entity.Tournament.tasks;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.idfactory.IdFactory;
import net.sf.l2j.gameserver.model.entity.Tournament.TournamentManager;
import net.sf.l2j.gameserver.model.entity.Tournament.Data.TournamentArenaParser;
import net.sf.l2j.gameserver.model.entity.Tournament.enums.TournamentFightType;
import net.sf.l2j.gameserver.model.entity.Tournament.model.TournamentArena;
import net.sf.l2j.gameserver.model.entity.Tournament.model.TournamentTeam;
import net.sf.l2j.gameserver.model.entity.instance.Instance;
import net.sf.l2j.gameserver.model.entity.instance.InstanceManager;

public class TournamentSearchFights implements Runnable
{
	
	private List<TournamentTeam> teams = new ArrayList<>();
	
	private TournamentFightType fightType;
	
	public TournamentSearchFights(TournamentFightType fightType)
	{
		this.fightType = fightType;
	}
	
	@Override
	public void run()
	{
		teams = TournamentManager.getInstance().getRegisteredTeamsByType(fightType);
		if (teams.size() >= 2)
		{
			try
			{
				if (Config.TOURNAMENT_DEBUG)
				{
					TournamentManager.getInstance().debugInfo("Tournament is Searching for " + fightType.name() + " fights.");
				}
				boolean found = false;
				// Search teams for a new fight
				List<TournamentTeam> searchList = teams;
				int teamOneIndex = Rnd.get(searchList.size());
				TournamentTeam teamOne = searchList.get(teamOneIndex);
				searchList.remove(teamOneIndex);
				int teamTwoIndex = Rnd.get(searchList.size());
				TournamentTeam teamTwo = searchList.get(teamTwoIndex);
				searchList.clear();
				if (teamOne != teamTwo)
				{
					found = true;
				}
				
				// Create a new tournament fight
				if (found)
				{
					// remove teams from registered list
					TournamentManager.getInstance().getRegisteredTournamentTeams().remove(teamOne);
					TournamentManager.getInstance().getRegisteredTournamentTeams().remove(teamTwo);
					
					// Select an arena properly
					TournamentArena arena = TournamentArenaParser.getInstance().getRandomArenaForType(fightType);
					// Create a instance to run the match
					Instance instance = InstanceManager.getInstance().createInstance();
					
					int fightId = IdFactory.getInstance().getNextId();
					if (arena != null)
					{
						TournamentFight fight = new TournamentFight(fightId, fightType, instance, teamOne, teamTwo, arena);
						
						if (Config.TOURNAMENT_DEBUG)
						{
							TournamentManager.getInstance().debugInfo("A new fight (ID: " + fight.getId() + ") [" + fightType.name() + "] started: " + teamOne.getName() + " vs " + teamTwo.getName());
						}
						
					}
					
				}
			}
			catch (Exception e)
			{
				TournamentManager.getInstance().debugInfo("[Tournament Search Fights]: Could not start a fight: ");
				e.printStackTrace();
				
			}
			
		}
	}
}