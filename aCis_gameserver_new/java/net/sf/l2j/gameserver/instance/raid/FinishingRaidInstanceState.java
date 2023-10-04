package net.sf.l2j.gameserver.instance.raid;

import net.sf.l2j.gameserver.instance.AbstractInstance;
import net.sf.l2j.gameserver.instance.actor.PlayerAdapter;
import net.sf.l2j.gameserver.instance.state.FinishingState;

public final class FinishingRaidInstanceState extends FinishingState
{
	@Override
	public void onStateFinish(AbstractInstance instance)
	{
		notifyInstanceFinished(instance);
		
		super.onStateFinish(instance);
	}
	
	private final void notifyInstanceFinished(AbstractInstance instance)
	{
		for (final PlayerAdapter playerAdapter : instance.getPlayerGroup().findAll())
			super.onStateFinish(instance);
	}
}