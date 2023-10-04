package net.sf.l2j.gameserver.model.entity.Tournament.tasks;

import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Tournament.TournamentManager;
import net.sf.l2j.gameserver.model.entity.Tournament.model.TournamentTeam;

/**
 * @author Rouxy
 */
public class OutOfTheZoneTask implements Runnable
{
	private TournamentTeam teamOne;
	private int teamOneOutSideTimes;
	private TournamentTeam teamTwo;
	private int teamTwoOutSideTimes;
	
	public OutOfTheZoneTask(TournamentTeam teamOne, TournamentTeam teamTwo)
	{
		this.teamOne = teamOne;
		this.teamTwo = teamTwo;
	}
	
	public boolean isOutOfTheZone(TournamentTeam team)
	{
		for (Player member : team.getMembers())
		{
			if (member == null)
			{
				return true;
			}
			if (!member.isInsideZone(ZoneId.TOURNAMENT))
			{
				team.sendMessage(member.getName() + " is out side of the zone. Return to zone or lose battle.");
				return true;
			}
			
		}
		return false;
	}
	
	@Override
	public void run()
	{
		
		if (teamOne.getFightId() != 0 && teamTwo.getFightId() != 0 && teamOne.getFightId() == teamTwo.getFightId())
		{
			TournamentFight fight = TournamentManager.getInstance().getCurrentFights().get(teamOne.getFightId());
			if (fight != null && fight.isStarted())
			{
				if (isOutOfTheZone(teamOne))
					teamOneOutSideTimes++;
				else
					teamOneOutSideTimes = 0;
				if (isOutOfTheZone(teamTwo))
					teamTwoOutSideTimes++;
				else
					teamTwoOutSideTimes = 0;
				
				if (teamOneOutSideTimes >= 10)
					fight.finish(teamTwo);
				if (teamTwoOutSideTimes >= 10)
					fight.finish(teamOne);
			}
		}
		
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
	
	/**
	 * @return the teamOneOutSideTimes
	 */
	public int getTeamOneOutSideTimes()
	{
		return teamOneOutSideTimes;
	}
	
	/**
	 * @param teamOneOutSideTimes the teamOneOutSideTimes to set
	 */
	public void setTeamOneOutSideTimes(int teamOneOutSideTimes)
	{
		this.teamOneOutSideTimes = teamOneOutSideTimes;
	}
	
	/**
	 * @return the teamTwoOutSideTimes
	 */
	public int getTeamTwoOutSideTimes()
	{
		return teamTwoOutSideTimes;
	}
	
	/**
	 * @param teamTwoOutSideTimes the teamTwoOutSideTimes to set
	 */
	public void setTeamTwoOutSideTimes(int teamTwoOutSideTimes)
	{
		this.teamTwoOutSideTimes = teamTwoOutSideTimes;
	}
	
}
