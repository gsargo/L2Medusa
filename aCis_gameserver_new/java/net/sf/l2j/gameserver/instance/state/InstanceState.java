package net.sf.l2j.gameserver.instance.state;

import java.util.concurrent.atomic.AtomicLong;

import net.sf.l2j.gameserver.instance.AbstractInstance;
import net.sf.l2j.gameserver.state.State;

public abstract class InstanceState<T extends AbstractInstance> implements State<T>
{
	private final AtomicLong timeLeftInSeconds;
	
	public InstanceState(long timeInSeconds)
	{
		timeLeftInSeconds = new AtomicLong(timeInSeconds);
	}
	
	protected final long getTimeLeftInSeconds()
	{
		return timeLeftInSeconds.get();
	}
	
	@Override
	public void handle(T event)
	{
		if (timeLeftInSeconds.decrementAndGet() == 0)
			onStateFinish(event);
	}
}