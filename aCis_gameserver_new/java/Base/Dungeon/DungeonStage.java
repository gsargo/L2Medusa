package Base.Dungeon;

import java.util.List;
import java.util.Map;

import net.sf.l2j.gameserver.model.location.Location;

public class DungeonStage
{
	private int order;
	private Location location;
	private boolean teleport;
	private int minutes;
	private Map<Integer, List<Location>> mobs;
	
	public DungeonStage(int order, Location location, boolean teleport, int minutes, Map<Integer, List<Location>> mobs)
	{
		this.order = order;
		this.location = location;
		this.teleport = teleport;
		this.minutes = minutes;
		this.mobs = mobs;
	}
	
	public int getOrder()
	{
		return order;
	}
	
	public Location getLocation()
	{
		return location;
	}
	
	public boolean teleport()
	{
		return teleport;
	}
	
	public int getMinutes()
	{
		return minutes;
	}
	
	public Map<Integer, List<Location>> getMobs()
	{
		return mobs;
	}
}