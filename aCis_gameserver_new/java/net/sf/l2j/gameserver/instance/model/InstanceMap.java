package net.sf.l2j.gameserver.instance.model;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.enums.TeamType;
import net.sf.l2j.gameserver.instance.InstanceType;
import net.sf.l2j.gameserver.model.location.Location;

public final class InstanceMap
{
	private final int id;
	private final Map<TeamType, List<Location>> locations;
	private final List<InstanceType> availableInstanceTypes;
	
	public InstanceMap(StatSet set)
	{
		id = set.getInteger("id");
		locations = set.getMap("locations");
		availableInstanceTypes = set.getList("instanceTypes");
	}
	
	public final int getId()
	{
		return id;
	}
	
	public final List<Location> getLocations(TeamType teamType)
	{
		return locations.getOrDefault(teamType, Collections.emptyList());
	}
	
	public final Location getRandomLocation(TeamType teamType)
	{
		return Rnd.get(getLocations(teamType));
	}
	
	public boolean isAvailableForInstanceType(InstanceType instanceType)
	{
		return availableInstanceTypes.stream().filter(type -> type.equals(instanceType)).findAny().isPresent();
	}
}