package net.sf.l2j.gameserver.model.entity.Tournament.model;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.commons.data.StatSet;

import net.sf.l2j.gameserver.model.entity.Tournament.enums.TournamentFightType;
import net.sf.l2j.gameserver.model.location.Location;

/**
 * @author Rouxy
 */

public class TournamentArena
{
	private final int id;
	private List<Location> teamOneLocation = new ArrayList<>();
	private List<Location> teamTwoLocation = new ArrayList<>();
	private int time;
	
	private final List<TournamentFightType> types = new ArrayList<>();
	
	public TournamentArena(StatSet set, List<Location> teamOneLocation, List<Location> teamTwoLocation)
	{
		String fTypes = set.getString("types");
		for (String type : fTypes.split(";"))
		{
			if (TournamentFightType.valueOf(type) != null)
				types.add(TournamentFightType.valueOf(type));
		}
		id = set.getInteger("id");
		this.teamOneLocation = teamOneLocation;
		this.teamTwoLocation = teamTwoLocation;
	}
	
	public List<Location> getTeamOneLocation()
	{
		return teamOneLocation;
	}
	
	public void setTeamOneLocation(List<Location> teamOneLocation)
	{
		this.teamOneLocation = teamOneLocation;
	}
	
	public List<Location> getTeamTwoLocation()
	{
		return teamTwoLocation;
	}
	
	public void setTeamTwoLocation(List<Location> teamTwoLocation)
	{
		this.teamTwoLocation = teamTwoLocation;
	}
	
	public int getTime()
	{
		return time;
	}
	
	public void setTime(int time)
	{
		this.time = time;
	}
	
	public int getId()
	{
		return id;
	}
	
	public List<TournamentFightType> getTypes()
	{
		return types;
	}
	
}
