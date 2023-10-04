package net.sf.l2j.gameserver.instance.state;

import net.sf.l2j.gameserver.instance.AbstractInstance;
import net.sf.l2j.gameserver.state.State;

public class FinishingState extends InstanceState<AbstractInstance>
{
	private static final long FINISHING_STATE_DURATION = 5L;
	
	public FinishingState()
	{
		super(FINISHING_STATE_DURATION);
	}
	
	public FinishingState(long time)
	{
		super(time);
	}
	
	@Override
	public void onStateStart(AbstractInstance instance)
	{
		instance.getPlayerGroup().broadcastScreenMessage("You will be teleported back in 5 seconds.", 3000);
	}
	
	@Override
	public void onStateFinish(AbstractInstance instance)
	{
		instance.end();
	}
	
	@Override
	public boolean canMoveToState(State<AbstractInstance> newState)
	{
		return newState == null;
	}
	
}