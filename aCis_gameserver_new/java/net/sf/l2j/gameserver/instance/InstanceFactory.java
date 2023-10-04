package net.sf.l2j.gameserver.instance;

import net.sf.l2j.gameserver.factory.Factory;
import net.sf.l2j.gameserver.instance.raid.RaidBossInstance;

public final class InstanceFactory implements Factory<InstanceType, AbstractInstance>
{
	@Override
	public AbstractInstance createNewInstance(InstanceType type, Object... objects)
	{
		return switch (type)
		{
			case RAID -> new RaidBossInstance();
		};
	}
	
}