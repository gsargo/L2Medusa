package net.sf.l2j.gameserver.model.entity.Tournament.tasks;

import java.util.List;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Tournament.model.TournamentArena;
import net.sf.l2j.gameserver.model.entity.Tournament.model.TournamentTeam;
import net.sf.l2j.gameserver.model.location.Location;

public class TournamentTeleport implements Runnable
{
	
	private TournamentArena arena;
	private TournamentFight fight;
	private TournamentTeam teamOne;
	private TournamentTeam teamTwo;
	
	public TournamentTeleport(TournamentFight fight, TournamentArena arena, TournamentTeam teamOne, TournamentTeam teamTwo)
	{
		this.fight = fight;
		this.arena = arena;
		this.teamOne = teamOne;
		this.teamTwo = teamTwo;
	}
	
	@Override
	public void run()
	{
		teleportTeamOne();
		teleportTeamTwo();
		teamOne.paralyze();
		teamTwo.paralyze();
		teamOne.screenMessage("Fight will start in " + Config.TOURNAMENT_FIGHT_START_TIME.get(fight.getFightType()) + " seconds");
		teamTwo.screenMessage("Fight will start in " + Config.TOURNAMENT_FIGHT_START_TIME.get(fight.getFightType()) + " seconds");
		
		ThreadPool.schedule(new Unparalyze(), Config.TOURNAMENT_FIGHT_START_TIME.get(fight.getFightType()) * 1000);
		
	}
	
	class Unparalyze implements Runnable
	{
		
		@Override
		public void run()
		{
			teamOne.unparalyze();
			teamTwo.unparalyze();
			fight.setStarted(true);
			teamOne.screenMessage("Battle Started!");
			teamTwo.screenMessage("Battle Started!");
			
		}
		
	}
	
	public void teleportTeamOne()
	{
		int locIndex = 0;
		for (Player player : teamOne.getMembers())
		{
			if (!player.isOnline())
				continue;
			
			List<Location> locs = arena.getTeamOneLocation();
			Location loc = locs.get(locIndex);
			if (loc != null)
			{
				player.setLastX(player.getPosition().getX());
				player.setLastY(player.getPosition().getY());
				player.setLastZ(player.getPosition().getZ());
				player.setInstance(fight.getInstance(), true);
				player.teleToLocation(loc);
				locIndex++;
			}
			else
			{
				fight.getTeamOne().sendMessage("Something goes wrong with locations of team one, please, contact GameMasters.");
				fight.getTeamTwo().sendMessage("Something goes wrong with locations of team one, please, contact GameMasters.");
				fight.finish();
			}
			
		}
	}
	
	public void teleportTeamTwo()
	{
		int locIndex = 0;
		for (Player player : teamTwo.getMembers())
		{
			if (!player.isOnline())
				continue;
			
			List<Location> locs = arena.getTeamTwoLocation();
			Location loc = locs.get(locIndex);
			if (loc != null)
			{
				player.setLastX(player.getPosition().getX());
				player.setLastY(player.getPosition().getY());
				player.setLastZ(player.getPosition().getZ());
				player.setInstance(fight.getInstance(), true);
				player.teleToLocation(loc);
				locIndex++;
			}
			else
			{
				fight.getTeamOne().sendMessage("Something goes wrong with locations of team two, please, contact GameMasters.");
				fight.getTeamTwo().sendMessage("Something goes wrong with locations of team two, please, contact GameMasters.");
				fight.finish();
			}
			
		}
	}
	
	/**
	 * @return the arena
	 */
	public TournamentArena getArena()
	{
		return arena;
	}
	
	/**
	 * @param arena the arena to set
	 */
	public void setArena(TournamentArena arena)
	{
		this.arena = arena;
	}
	
	/**
	 * @return the teamOne
	 */
	public TournamentTeam getTeamOne()
	{
		return teamOne;
	}
	
	/**
	 * @param teamOne the teamOne to set
	 */
	public void setTeamOne(TournamentTeam teamOne)
	{
		this.teamOne = teamOne;
	}
	
	/**
	 * @return the teamTwo
	 */
	public TournamentTeam getTeamTwo()
	{
		return teamTwo;
	}
	
	/**
	 * @param teamTwo the teamTwo to set
	 */
	public void setTeamTwo(TournamentTeam teamTwo)
	{
		this.teamTwo = teamTwo;
	}
	
	public TournamentFight getFight()
	{
		return fight;
	}
	
	public void setFight(TournamentFight fight)
	{
		this.fight = fight;
	}
	
}
