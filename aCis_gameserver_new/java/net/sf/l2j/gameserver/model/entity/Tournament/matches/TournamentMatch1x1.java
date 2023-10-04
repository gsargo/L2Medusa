package net.sf.l2j.gameserver.model.entity.Tournament.matches;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.actors.ClassId;
import net.sf.l2j.gameserver.model.entity.Tournament.TournamentManager;
import net.sf.l2j.gameserver.model.entity.Tournament.enums.TournamentFightType;
import net.sf.l2j.gameserver.model.entity.Tournament.model.TournamentTeam;

/**
 * @author Rouxy
 */
public class TournamentMatch1x1 extends TournamentMatch
{
	private List<TournamentTeam> registeredTeams = new ArrayList<>();
	private ScheduledFuture<?> searchFightsTask = null;
	private final TournamentFightType fightType = TournamentFightType.F1X1;
	
	public static TournamentMatch1x1 getInstance()
	{
		return SingleTonHolder._instance;
	}
	
	private static class SingleTonHolder
	{
		protected static TournamentMatch1x1 _instance = new TournamentMatch1x1();
	}
	
	@Override
	public boolean register(TournamentTeam team)
	{
		try
		{
			TournamentManager.getInstance().getRegisteredTournamentTeams().put(team, TournamentFightType.F1X1);
			return true;
		}
		catch (Exception e)
		{
			
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public boolean unRegister(TournamentTeam team)
	{
		try
		{
			TournamentManager.getInstance().getRegisteredTournamentTeams().remove(team);
			return true;
		}
		catch (Exception e)
		{
			
			e.printStackTrace();
			return false;
		}
	}
	
	public ScheduledFuture<?> getSearchFightsTask()
	{
		return searchFightsTask;
	}
	
	public void setSearchFightsTask(ScheduledFuture<?> searchFightsTask)
	{
		this.searchFightsTask = searchFightsTask;
	}
	
	public List<TournamentTeam> getRegisteredTeams()
	{
		return registeredTeams;
	}
	
	public void setRegisteredTeams(List<TournamentTeam> registeredTeams)
	{
		this.registeredTeams = registeredTeams;
	}
	
	@Override
	public boolean checkConditions(TournamentTeam team)
	{
		
		if (team.getMembers().size() != 1)
		{
			team.sendMessage("Only 1 player allowed in this mode.");
			team.screenMessage("Only 1 player allowed in this mode.");
			return false;
		}
		if (team.getCountOfClass(ClassId.DUELIST) > Config.TOURNAMENT_DUELIST_ALLOWED.get(TournamentFightType.F1X1))
		{
			team.sendMessage("Only " + Config.TOURNAMENT_DUELIST_ALLOWED.get(TournamentFightType.F1X1) + " Duelist(s) are allowed in this mode.");
			team.screenMessage("Only " + Config.TOURNAMENT_DUELIST_ALLOWED.get(TournamentFightType.F1X1) + " Duelist(s) are allowed in this mode.");
			return false;
		}
		if (team.getCountOfClass(ClassId.ARCHMAGE) > Config.TOURNAMENT_ARCHMAGE_ALLOWED.get(TournamentFightType.F1X1))
		{
			team.sendMessage("Only " + Config.TOURNAMENT_ARCHMAGE_ALLOWED.get(TournamentFightType.F1X1) + " Archmage(s) are allowed in this mode.");
			team.screenMessage("Only " + Config.TOURNAMENT_ARCHMAGE_ALLOWED.get(TournamentFightType.F1X1) + " Archmage(s) are allowed in this mode.");
			return false;
		}
		if (team.getCountOfClass(ClassId.SOULTAKER) > Config.TOURNAMENT_SOULTAKER_ALLOWED.get(TournamentFightType.F1X1))
		{
			team.sendMessage("Only " + Config.TOURNAMENT_SOULTAKER_ALLOWED.get(TournamentFightType.F1X1) + " Soultaker(s) are allowed in this mode.");
			team.screenMessage("Only " + Config.TOURNAMENT_SOULTAKER_ALLOWED.get(TournamentFightType.F1X1) + " Soultaker(s) are allowed in this mode.");
			return false;
		}
		
		if (team.getCountOfClass(ClassId.STORM_SCREAMER) > Config.TOURNAMENT_STORMSCREAMER_ALLOWED.get(TournamentFightType.F1X1))
		{
			team.sendMessage("Only " + Config.TOURNAMENT_STORMSCREAMER_ALLOWED.get(TournamentFightType.F1X1) + " Storm Screamer(s) are allowed in this mode.");
			team.screenMessage("Only " + Config.TOURNAMENT_STORMSCREAMER_ALLOWED.get(TournamentFightType.F1X1) + " Storm Screamer(s) are allowed in this mode.");
			return false;
		}
		if (team.getCountOfClass(ClassId.MYSTIC_MUSE) > Config.TOURNAMENT_MYSTICMUSE_ALLOWED.get(TournamentFightType.F1X1))
		{
			team.sendMessage("Only " + Config.TOURNAMENT_MYSTICMUSE_ALLOWED.get(TournamentFightType.F1X1) + " Mystic Muse(s) are allowed in this mode.");
			team.screenMessage("Only " + Config.TOURNAMENT_MYSTICMUSE_ALLOWED.get(TournamentFightType.F1X1) + " Mystic Muse(s) are allowed in this mode.");
			return false;
		}
		if (team.getCountOfClass(ClassId.TITAN) > Config.TOURNAMENT_TITAN_ALLOWED.get(TournamentFightType.F1X1))
		{
			team.sendMessage("Only " + Config.TOURNAMENT_TITAN_ALLOWED.get(TournamentFightType.F1X1) + " Titan(s) are allowed in this mode.");
			team.screenMessage("Only " + Config.TOURNAMENT_TITAN_ALLOWED.get(TournamentFightType.F1X1) + " Titan(s) are allowed in this mode.");
			return false;
		}
		if (team.getCountOfClass(ClassId.DOMINATOR) > Config.TOURNAMENT_DOMINATOR_ALLOWED.get(TournamentFightType.F1X1))
		{
			team.sendMessage("Only " + Config.TOURNAMENT_DOMINATOR_ALLOWED.get(TournamentFightType.F1X1) + " Dominator(s) are allowed in this mode.");
			team.screenMessage("Only " + Config.TOURNAMENT_DOMINATOR_ALLOWED.get(TournamentFightType.F1X1) + " Dominator(s) are allowed in this mode.");
			return false;
		}
		if (team.getCountOfClass(ClassId.DOOMCRYER) > Config.TOURNAMENT_DOOMCRYER_ALLOWED.get(TournamentFightType.F1X1))
		{
			team.sendMessage("Only " + Config.TOURNAMENT_DOOMCRYER_ALLOWED.get(TournamentFightType.F1X1) + " Doomcryer(s) are allowed in this mode.");
			team.screenMessage("Only " + Config.TOURNAMENT_DOOMCRYER_ALLOWED.get(TournamentFightType.F1X1) + " Doomcryer(s) are allowed in this mode.");
			return false;
		}
		if (team.getCountOfClass(ClassId.DOOMCRYER) > Config.TOURNAMENT_DOOMCRYER_ALLOWED.get(TournamentFightType.F1X1))
		{
			team.sendMessage("Only " + Config.TOURNAMENT_DOOMCRYER_ALLOWED.get(TournamentFightType.F1X1) + " Doomcryer(s) are allowed in this mode.");
			team.screenMessage("Only " + Config.TOURNAMENT_DOOMCRYER_ALLOWED.get(TournamentFightType.F1X1) + " Doomcryer(s) are allowed in this mode.");
			return false;
		}
		if (team.getArchersCount() > Config.TOURNAMENT_ARCHER_ALLOWED.get(TournamentFightType.F1X1))
		{
			team.sendMessage("Only " + Config.TOURNAMENT_ARCHER_ALLOWED.get(TournamentFightType.F1X1) + " Archer(s) are allowed in this mode.");
			team.screenMessage("Only " + Config.TOURNAMENT_ARCHER_ALLOWED.get(TournamentFightType.F1X1) + " Archer(s) are allowed in this mode.");
			return false;
		}
		if (team.getTankersCount() > Config.TOURNAMENT_TANKER_ALLOWED.get(TournamentFightType.F1X1))
		{
			team.sendMessage("Only " + Config.TOURNAMENT_TANKER_ALLOWED.get(TournamentFightType.F1X1) + " Tanker(s) are allowed in this mode.");
			team.screenMessage("Only " + Config.TOURNAMENT_TANKER_ALLOWED.get(TournamentFightType.F1X1) + " Tanker(s) are allowed in this mode.");
			return false;
		}
		if (team.getHealersCount() > Config.TOURNAMENT_HEALER_ALLOWED.get(TournamentFightType.F1X1))
		{
			team.sendMessage("Only " + Config.TOURNAMENT_HEALER_ALLOWED.get(TournamentFightType.F1X1) + " Healer(s) are allowed in this mode.");
			team.screenMessage("Only " + Config.TOURNAMENT_HEALER_ALLOWED.get(TournamentFightType.F1X1) + " Healer(s) are allowed in this mode.");
			return false;
		}
		if (team.getDaggersCount() > Config.TOURNAMENT_DAGGER_ALLOWED.get(TournamentFightType.F1X1))
		{
			team.sendMessage("Only " + Config.TOURNAMENT_DAGGER_ALLOWED.get(TournamentFightType.F1X1) + " Dagger(s) are allowed in this mode.");
			team.screenMessage("Only " + Config.TOURNAMENT_DAGGER_ALLOWED.get(TournamentFightType.F1X1) + " Dagger(s) are allowed in this mode.");
			return false;
		}
		
		return true;
	}
	
	/**
	 * @return the fightType
	 */
	public TournamentFightType getFightType()
	{
		return fightType;
	}
	
}
