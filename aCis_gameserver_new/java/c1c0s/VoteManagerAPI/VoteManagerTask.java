package c1c0s.VoteManagerAPI;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.skills.AbstractEffect;

public class VoteManagerTask implements Runnable
{
	private final Map<Player, Long> _players = new ConcurrentHashMap<>();
	
	protected VoteManagerTask()
	{
		ThreadPool.scheduleAtFixedRate(this, 1000, 1 * 60 * 1000); // start first minute and then each 30 minutes , 30 * 60 * 1000
	}
	
	public final void add(Player player)
	{
		_players.put(player, System.currentTimeMillis());
	}
	
	public final void remove(Creature player)
	{
		_players.remove(player);
	}
	
	public final boolean contain(Creature player)
	{
		return _players.containsKey(player);
	}
	
	@Override
	public final void run()
	{
		if (_players.isEmpty())
			return;
		
		for (Map.Entry<Player, Long> entry : _players.entrySet())
		{
			final Player player = entry.getKey();
			
			player.initVotes(); // make 0 the vote to Sites
			remove(player); // remove from this task
			player.remHwidVotes(player.gethwid()); // remove from player data
		}
		
	}
	
	public static void updateEffects(Player player)
	{
		for (AbstractEffect e : player.getAllEffects())
		{
			if (e != null && e.getSkill().getId() == 5413)
				e.exit();
		}
	}
	
	public static void deleteFromSkillSave(Player player)
	{
		
	}
	
	public static final VoteManagerTask getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final VoteManagerTask _instance = new VoteManagerTask();
	}
}