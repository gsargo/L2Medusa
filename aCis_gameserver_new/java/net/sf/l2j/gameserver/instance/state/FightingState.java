package net.sf.l2j.gameserver.instance.state;

import java.util.List;

import net.sf.l2j.gameserver.instance.AbstractInstance;
import net.sf.l2j.gameserver.state.State;

public class FightingState<T extends AbstractInstance> extends InstanceState<T>
{
	private static final List<Long> SECONDS_TO_ANNOUNCE = List.of(300L, 60L, 30L);
	
	public FightingState(long timeInSeconds)
	{
		super(timeInSeconds);
	}
	
	@Override
	public void handle(T event)
	{
		super.handle(event);
	}
	
	@Override
	public boolean canMoveToState(State<T> newState)
	{
		return newState instanceof FinishingState;
	}
	
	@Override
	public void onStateStart(AbstractInstance instance)
	{
		instance.getSpawnGroup().spawnAll();
		instance.getPlayerGroup().teleportPlayersToMap(instance.getInstanceMap());
	}
	
	@Override
	public void onStateFinish(T event)
	{
		event.setState(new FinishingState());
	}
}