package net.sf.l2j.gameserver.service;

import net.sf.l2j.commons.data.StatSet;

import net.sf.l2j.gameserver.factory.Factory;
import net.sf.l2j.gameserver.service.services.PremiumService;

public final class CommunityServiceFactory implements Factory<CommunityServiceType, AbstractCommunityService>
{
	@Override
	public AbstractCommunityService createNewInstance(CommunityServiceType type, Object... parameters)
	{
		final StatSet set = (StatSet) parameters[0];
		
		return switch (type)
		{
			case PREMIUM -> new PremiumService(set);
		};
	}
}