package net.sf.l2j.gameserver.reward;

import net.sf.l2j.commons.data.StatSet;

import net.sf.l2j.gameserver.factory.Factory;

public final class RewardFactory implements Factory<RewardType, Reward>
{
	@Override
	public Reward createNewInstance(RewardType type, Object... objects)
	{
		final StatSet set = (StatSet) objects[0];
		
		switch (type)
		{
			case ITEM:
				return new ItemReward(set);
		}
		return null;
	}
}