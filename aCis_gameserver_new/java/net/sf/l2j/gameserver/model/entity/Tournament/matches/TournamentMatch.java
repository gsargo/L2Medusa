package net.sf.l2j.gameserver.model.entity.Tournament.matches;

import net.sf.l2j.gameserver.model.entity.Tournament.model.TournamentTeam;

/**
 * @author Rouxy
 */
public abstract class TournamentMatch
{
	public abstract boolean register(TournamentTeam team);
	
	public abstract boolean unRegister(TournamentTeam team);
	
	public abstract boolean checkConditions(TournamentTeam team);
	
}
