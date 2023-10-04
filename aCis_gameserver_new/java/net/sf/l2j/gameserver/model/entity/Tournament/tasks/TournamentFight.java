package net.sf.l2j.gameserver.model.entity.Tournament.tasks;

import java.util.concurrent.ScheduledFuture;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Tournament.TournamentManager;
import net.sf.l2j.gameserver.model.entity.Tournament.enums.TournamentFightType;
import net.sf.l2j.gameserver.model.entity.Tournament.model.TournamentArena;
import net.sf.l2j.gameserver.model.entity.Tournament.model.TournamentTeam;
import net.sf.l2j.gameserver.model.entity.instance.Instance;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage.SMPOS;

/**
 * @author Zaun
 */
public class TournamentFight
{
	private TournamentFightType fightType = TournamentFightType.NONE;
	private Instance instance = null;
	
	private TournamentArena tournamentArena;
	private TournamentTeam teamOne;
	private TournamentTeam teamTwo;
	private int id;
	private ScheduledFuture<?> finishTask = null;
	private ScheduledFuture<?> timer = null;
	private long fightBeginTime;
	private ScheduledFuture<?> outOfTheZoneTask = null;
	private boolean started;
	
	public TournamentFight(int id, TournamentFightType fightType, Instance instance, TournamentTeam teamOne, TournamentTeam teamTwo, TournamentArena tournamentArena)
	{
		
		this.fightType = fightType;
		this.id = id;
		this.instance = instance;
		this.teamOne = teamOne;
		this.teamTwo = teamTwo;
		this.tournamentArena = tournamentArena;
		
		teamOne.setInTournamentMatch(true);
		teamOne.setFightId(id);
		teamOne.setTournamentFightType(fightType);
		teamOne.screenMessage("Battle Arena Match created! You will be teleported in: " + Config.TOURNAMENT_TIME_TO_TELEPORT + " seconds.");
		teamOne.screenMessage("Battle Arena Match created! You will be teleported in: " + Config.TOURNAMENT_TIME_TO_TELEPORT + " seconds.");
		teamOne.prepareToFight();
		
		teamTwo.setInTournamentMatch(true);
		teamTwo.setFightId(id);
		teamTwo.setTournamentFightType(fightType);
		teamTwo.screenMessage("Battle Arena Match created! You will be teleported in: " + Config.TOURNAMENT_TIME_TO_TELEPORT + " seconds.");
		teamTwo.screenMessage("Battle Arena Match created! You will be teleported in: " + Config.TOURNAMENT_TIME_TO_TELEPORT + " seconds.");
		teamTwo.prepareToFight();
		
		fightBeginTime = System.currentTimeMillis();
		TournamentManager.getInstance().getCurrentFights().put(id, this);
		TournamentManager.getInstance().setAllTimeFights(TournamentManager.getInstance().getAllTimeFights() + 1);
		ThreadPool.schedule(new TournamentTeleport(this, tournamentArena, teamOne, teamTwo), Config.TOURNAMENT_TIME_TO_TELEPORT * 1000);
		finishTask = ThreadPool.schedule(new FinishFight(), (Config.TOURNAMENT_FIGHT_DURATION.get(fightType)) * 60 * 1000 + (Config.TOURNAMENT_TIME_TO_TELEPORT + Config.TOURNAMENT_FIGHT_START_TIME.get(fightType) * 1000));
		timer = ThreadPool.scheduleAtFixedRate(new Timer(), 1000, 1010);
		setOutOfTheZoneTask(ThreadPool.scheduleAtFixedRate(new OutOfTheZoneTask(teamOne, teamTwo), 0, 1000));
		
	}
	
	class Timer implements Runnable
	{
		
		@Override
		public void run()
		{
			if (isStarted())
				broadCastTimer();
		}
		
	}
	
	public void broadCastTimer()
	{
		int secondsLeft = (int) (((fightBeginTime + (1000 * 60 * Config.TOURNAMENT_FIGHT_DURATION.get(fightType) + (Config.TOURNAMENT_TIME_TO_TELEPORT + Config.TOURNAMENT_FIGHT_START_TIME.get(fightType) * 1000))) - System.currentTimeMillis()) / 1000);
		int minutes = secondsLeft / 60;
		int seconds = secondsLeft % 60;
		ExShowScreenMessage packet = new ExShowScreenMessage(String.format("%02d:%02d", minutes, seconds), 1010, SMPOS.BOTTOM_RIGHT, false);
		for (Player player : teamOne.getMembers())
			player.sendPacket(packet);
		for (Player player : teamTwo.getMembers())
			player.sendPacket(packet);
	}
	
	private class FinishFight implements Runnable
	{
		
		@Override
		public void run()
		{
			
			finish();
		}
		
	}
	
	class TeleportBack implements Runnable
	{
		TournamentTeam team;
		
		public TeleportBack(TournamentTeam team)
		{
			this.team = team;
		}
		
		@Override
		public void run()
		{
			team.doRevive();
			team.teleportBack();
			team.backInstance();
		}
		
	}
	
	public void finish()
	{
		try
		{
			if (getWinner() != null)
			{
				// reward victorious team
				getWinner().reward(true);
				// store +1 victory
				getWinner().addTeamVictory(fightType);
				
				if (getLooser() != null)
				{
					// reward looser team
					getLooser().reward(false);
					getLooser().addTeamDefeat(fightType);
					
				}
				
			}
			else
			{
				teamOne.screenMessage("Fight ended in a tie!");
				teamTwo.screenMessage("Fight ended in a tie!");
				teamOne.addTeamTie(fightType);
				teamTwo.addTeamTie(fightType);
			}
			
			// both teams code below
			
			// Team One
			teamOne.addTotalDamageToPlayers(fightType);
			teamOne.resetTeamMatchDamage(); // reset damage
			teamOne.setInTournamentMatch(false);
			teamOne.setFightId(0);
			ThreadPool.schedule(new TeleportBack(teamOne), 5000);
			teamOne.removeTournamentTeam();
			teamOne.setTournamentFightType(TournamentFightType.NONE);
			// store fight
			store(teamOne);
			
			// Team Two
			teamTwo.addTotalDamageToPlayers(fightType);
			teamTwo.resetTeamMatchDamage();// reset damage
			teamTwo.setInTournamentMatch(false);
			teamTwo.setFightId(0);
			ThreadPool.schedule(new TeleportBack(teamTwo), 5000);
			teamTwo.removeTournamentTeam();
			teamTwo.setTournamentFightType(TournamentFightType.NONE);
			// store fight
			store(teamTwo);
			
			TournamentManager.getInstance().getCurrentFights().remove(id);
			
			// cancel tasks
			if (finishTask != null)
			{
				finishTask.cancel(true);
				finishTask = null;
			}
			
			if (timer != null)
			{
				timer.cancel(true);
				timer = null;
			}
			
			if (outOfTheZoneTask != null)
			{
				outOfTheZoneTask.cancel(true);
				outOfTheZoneTask = null;
			}
			
		}
		catch (Exception e)
		{
			TournamentManager.getInstance().debugInfo("[Battle Arena]: Fight could not be completed: ");
			e.printStackTrace();
		}
		
	}
	
	public void finish(TournamentTeam winner)
	{
		try
		{
			TournamentTeam looser = null;
			if (winner != null)
			{
				looser = (winner == teamOne) ? teamTwo : teamOne;
				
				// reward victorious team
				winner.reward(true);
				// store +1 victory
				winner.addTeamVictory(fightType);
				
				winner.sendMessage("Your team has won the battle because enemy players were out of arena zone.");
			}
			
			if (looser != null)
			{
				looser.reward(false);
				// reward looser team
				looser.reward(false);
				looser.addTeamDefeat(fightType);
				
				looser.sendMessage("Your team has lost the battle because team players were out of arena zone.");
			}
			
			// both teams code below
			
			// Team One
			teamOne.addTotalDamageToPlayers(fightType);
			teamOne.resetTeamMatchDamage(); // reset damage
			teamOne.setInTournamentMatch(false);
			teamOne.setFightId(0);
			ThreadPool.schedule(new TeleportBack(teamOne), 5000);
			teamOne.removeTournamentTeam();
			teamOne.setTournamentFightType(TournamentFightType.NONE);
			// store fight
			store(teamOne);
			
			// Team Two
			teamTwo.addTotalDamageToPlayers(fightType);
			teamTwo.resetTeamMatchDamage();// reset damage
			teamTwo.setInTournamentMatch(false);
			teamTwo.setFightId(0);
			ThreadPool.schedule(new TeleportBack(teamTwo), 5000);
			teamTwo.removeTournamentTeam();
			teamTwo.setTournamentFightType(TournamentFightType.NONE);
			// store fight
			store(teamTwo);
			
			TournamentManager.getInstance().getCurrentFights().remove(id);
			
			// cancel tasks
			if (finishTask != null)
			{
				finishTask.cancel(true);
				finishTask = null;
			}
			
			if (timer != null)
			{
				timer.cancel(true);
				timer = null;
			}
			
			if (outOfTheZoneTask != null)
			{
				outOfTheZoneTask.cancel(true);
				outOfTheZoneTask = null;
			}
			
		}
		catch (Exception e)
		{
			TournamentManager.getInstance().debugInfo("[Battle Arena]: Fight could not be completed: ");
			e.printStackTrace();
		}
		
	}
	
	public TournamentTeam getLooser()
	{
		if (getWinner() != null)
		{
			if (getWinner() == teamOne)
			{
				return teamTwo;
			}
			else if (getWinner() == teamTwo)
			{
				return teamOne;
			}
		}
		return null;
	}
	
	public TournamentTeam getWinner()
	{
		int teamOneLive = teamOne.getAliveMembers();
		int teamTwoLive = teamTwo.getAliveMembers();
		if (teamOneLive > teamTwoLive)
		{
			return teamOne;
		}
		else if (teamTwoLive > teamOneLive)
		{
			return teamTwo;
		}
		else if (teamOne.getTotalDamage() > teamTwo.getTotalDamage())
		{
			return teamOne;
		}
		else if (teamTwo.getTotalDamage() > teamOne.getTotalDamage())
		{
			return teamTwo;
		}
		return null;
		
	}
	
	public TournamentFightType getFightType()
	{
		return fightType;
	}
	
	public void setFightType(TournamentFightType fightType)
	{
		this.fightType = fightType;
	}
	
	public Instance getInstance()
	{
		return instance;
	}
	
	public void setInstance(Instance instance)
	{
		this.instance = instance;
	}
	
	public TournamentArena getTournamentArena()
	{
		return tournamentArena;
	}
	
	public void setTournamentArena(TournamentArena tournamentArena)
	{
		this.tournamentArena = tournamentArena;
	}
	
	public TournamentTeam getTeamTwo()
	{
		return teamTwo;
	}
	
	public void setTeamTwo(TournamentTeam teamTwo)
	{
		this.teamTwo = teamTwo;
	}
	
	public TournamentTeam getTeamOne()
	{
		return teamOne;
	}
	
	public void setTeamOne(TournamentTeam teamOne)
	{
		this.teamOne = teamOne;
	}
	
	public int getId()
	{
		return id;
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public ScheduledFuture<?> getFinishTask()
	{
		return finishTask;
	}
	
	public void setFinishTask(ScheduledFuture<?> finishTask)
	{
		this.finishTask = finishTask;
	}
	
	/**
	 * @return the outOfTheZoneTask
	 */
	public ScheduledFuture<?> getOutOfTheZoneTask()
	{
		return outOfTheZoneTask;
	}
	
	/**
	 * @param outOfTheZoneTask the outOfTheZoneTask to set
	 */
	public void setOutOfTheZoneTask(ScheduledFuture<?> outOfTheZoneTask)
	{
		this.outOfTheZoneTask = outOfTheZoneTask;
	}
	
	/**
	 * @return the started
	 */
	public boolean isStarted()
	{
		return started;
	}
	
	/**
	 * @param started the started to set
	 */
	public void setStarted(boolean started)
	{
		this.started = started;
	}
	
	public void store(TournamentTeam team)
	{
		for (Player member : team.getMembers())
			TournamentManager.getInstance().updateData(member, fightType);
	}
}
