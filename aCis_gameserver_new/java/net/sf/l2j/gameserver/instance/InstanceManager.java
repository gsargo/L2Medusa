package net.sf.l2j.gameserver.instance;

import net.sf.l2j.gameserver.factory.IdFactory;
import net.sf.l2j.gameserver.factory.IntegerIdFactory;

public final class InstanceManager
{
	private final IdFactory<Integer> idFactory = new IntegerIdFactory();
	
	public final int getNextAvailableId()
	{
		return idFactory.createNextId();
	}
	
	public static final InstanceManager getInstance()
	{
		return InstanceHolder.INSTANCE;
	}
	
	private static final class InstanceHolder
	{
		private static final InstanceManager INSTANCE = new InstanceManager();
	}
}