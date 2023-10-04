package net.sf.l2j.gameserver.instance;

import java.util.concurrent.ScheduledFuture;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.data.xml.InstanceData;
import net.sf.l2j.gameserver.instance.actor.PlayerAdapter;
import net.sf.l2j.gameserver.instance.group.PlayerGroup;
import net.sf.l2j.gameserver.instance.group.SpawnGroup;
import net.sf.l2j.gameserver.instance.listener.InstanceListener;
import net.sf.l2j.gameserver.instance.model.InstanceMap;
import net.sf.l2j.gameserver.instance.state.InstanceState;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;

public abstract class AbstractInstance implements GameInstance, InstanceListener, Runnable
{
	private final int instanceId;
	private final SpawnGroup spawnGroup;
	private final PlayerGroup playerGroup;
	private final InstanceMap instanceMap;
	private volatile InstanceState<AbstractInstance> state;
	private volatile ScheduledFuture<?> instanceTask;
	
	public AbstractInstance()
	{
		this(InstanceManager.getInstance().getNextAvailableId());
	}
	
	public AbstractInstance(int instanceId)
	{
		this.instanceId = instanceId;
		spawnGroup = new SpawnGroup(instanceId, this);
		spawnGroup.addSpawns(InstanceData.getInstance().getSpawns(getInstanceType()));
		playerGroup = new PlayerGroup(instanceId);
		instanceMap = InstanceData.getInstance().getRandomInstanceMap(getInstanceType());
	}
	
	public final int getInstanceId()
	{
		return instanceId;
	}
	
	public final SpawnGroup getSpawnGroup()
	{
		return spawnGroup;
	}
	
	public final PlayerGroup getPlayerGroup()
	{
		return playerGroup;
	}
	
	public final InstanceMap getInstanceMap()
	{
		return instanceMap;
	}
	
	public final synchronized void setState(InstanceState<AbstractInstance> newState)
	{
		if (!canMoveToState(newState))
			return;
		
		this.state = newState;
		if (newState != null)
			newState.onStateStart(this);
	}
	
	private final boolean canMoveToState(InstanceState<AbstractInstance> newState)
	{
		if (state == null)
			return true;
		
		return state.canMoveToState(newState);
	}
	
	@Override
	public void start()
	{
		instanceTask = ThreadPool.scheduleAtFixedRate(this, 0L, 1000L);
	}
	
	@Override
	public void end()
	{
		stopInstanceTask();
		
		getSpawnGroup().unspawnAll();
		getSpawnGroup().clear();
		processRewards();
		getPlayerGroup().teleportPlayersBack();
		getPlayerGroup().clear();
		setState(null);
	}
	
	private final synchronized void stopInstanceTask()
	{
		if (instanceTask != null)
		{
			instanceTask.cancel(false);
			instanceTask = null;
		}
	}
	
	@Override
	public void onPlayerLeave(Player player)
	{
		final PlayerAdapter playerAdapter = playerGroup.remove(player.getObjectId());
		if (playerAdapter == null)
			return;
		
		player.setInstanceId(0);
		player.setGameInstance(null);
		player.teleToLocation(Location.TOWN_LOCATION);
		
		if (playerGroup.isEmpty())
			end();
	}
	
	@Override
	public void onPlayerDisconnect(Player player)
	{
		onPlayerLeave(player);
	}
	
	@Override
	public final void run()
	{
		state.handle(this);
	}
	
	@Override
	public void onNpcTalk(Player player, Npc npc)
	{
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
}