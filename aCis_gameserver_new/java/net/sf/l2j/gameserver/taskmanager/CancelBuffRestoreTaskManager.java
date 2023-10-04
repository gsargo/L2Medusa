package net.sf.l2j.gameserver.taskmanager;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.holder.CancelSkillHolder;
import net.sf.l2j.gameserver.skills.L2Skill;

/**
 * @author Giorgos
 */
public class CancelBuffRestoreTaskManager
{
	private Set<Integer> _players = ConcurrentHashMap.newKeySet();
	
	public CancelBuffRestoreTaskManager()
	{
	}
	
	public void start()
	{
		ThreadPool.scheduleAtFixedRate(() -> control(), 1000, 1000);
	}
	
	private void control()
	{
		if (_players.isEmpty())
			return;
		
		for (int objId : _players)
		{
			final Player player = World.getInstance().getPlayer(objId);
			
			if (player == null)
				continue;
			
			for (CancelSkillHolder holder : player.getBuffToRestore())
			{
				final L2Skill skill = holder.getSkill();
				if (skill == null)
				{
					System.out.println("skill is null: " + holder.getId() + " -> " + holder.getValue());
					continue;
				}
				
				if (holder.getTime() < System.currentTimeMillis())
					player.restoreBuff(holder);
				
			}
			
			if (player.getBuffToRestore().isEmpty())
				_players.remove(objId);
		}
	}
	
	public void add(int objId)
	{
		if (_players.contains(objId))
			return;
		
		_players.add(objId);
	}
	
	public static final CancelBuffRestoreTaskManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final CancelBuffRestoreTaskManager INSTANCE = new CancelBuffRestoreTaskManager();
	}
}
