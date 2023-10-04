package Base.Dungeon;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.gameserver.model.actor.instance.Door;

public class Instance1
{
	private int id;
	private List<Door> doors;
	
	public Instance1(int id)
	{
		this.id = id;
		doors = new ArrayList<>();
	}
	
	public void openDoors()
	{
		for (Door door : doors)
			door.openMe();
	}
	
	public void closeDoors()
	{
		for (Door door : doors)
			door.closeMe();
	}
	
	public void addDoor(Door door)
	{
		doors.add(door);
	}
	
	public List<Door> getDoors()
	{
		return doors;
	}
	
	public int getId()
	{
		return id;
	}
}