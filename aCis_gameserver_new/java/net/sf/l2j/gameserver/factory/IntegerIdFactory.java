package net.sf.l2j.gameserver.factory;

import java.util.concurrent.atomic.AtomicInteger;

public final class IntegerIdFactory implements IdFactory<Integer>
{
	private final AtomicInteger nextId = new AtomicInteger(0);
	
	@Override
	public final Integer createNextId()
	{
		return nextId.incrementAndGet();
	}
}