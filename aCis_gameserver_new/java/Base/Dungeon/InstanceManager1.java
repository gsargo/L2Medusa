package Base.Dungeon;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.gameserver.model.actor.instance.Door;

public class InstanceManager1
{
	private Map<Integer, Instance1> instances;
	
	protected InstanceManager1()
	{
		instances = new ConcurrentHashMap<>();
		instances.put(0, new Instance1(0));
	}
	
	public void addDoor(int id, Door door)
	{
		if (!instances.containsKey(id) || id == 0)
			return;
		
		instances.get(id).addDoor(door);
	}
	
	public void deleteInstance(int id)
	{
		if (id == 0)
		{
			System.out.println("Attempt to delete instance with id 0.");
			return;
		}
		
		// delete doors
	}
	
	public synchronized Instance1 createInstance()
	{
		Instance1 instance = new Instance1(InstanceIdFactory.getNextAvailable());
		instances.put(instance.getId(), instance);
		return instance;
	}
	
	public Instance1 getInstance1(int id)
	{
		return instances.get(id);
	}
	
	public static InstanceManager1 getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static final class SingletonHolder
	{
		protected static final InstanceManager1 instance = new InstanceManager1();
	}
}