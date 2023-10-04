package net.sf.l2j.util;

import java.util.concurrent.ScheduledFuture;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.model.actor.Player;

public class PlayerVar
{
	private Player owner;
	private String name;
	private String value;
	private long expire_time;
	
	@SuppressWarnings("rawtypes")
	private ScheduledFuture task;
	
	public PlayerVar(Player owner, String name, String value, long expire_time)
	{
		this.owner = owner;
		this.name = name;
		this.value = value;
		this.expire_time = expire_time;
		
		if (expire_time > 0) // if expires schedule expiration
		{
			task = ThreadPool.schedule(new PlayerVarExpireTask(this), expire_time - System.currentTimeMillis());
		}
	}
	
	public int getIntValue()
	{
		if (isNumeric(getValue()))
		{
			return Integer.parseInt(getValue());
		}
		return -1;
	}
	
	public String getName()
	{
		return name;
	}
	
	public Player getOwner()
	{
		return owner;
	}
	
	public boolean hasExpired()
	{
		return task == null || task.isDone();
	}
	
	public long getTimeToExpire()
	{
		return expire_time - System.currentTimeMillis();
	}
	
	public String getValue()
	{
		return value;
	}
	
	public boolean getValueBoolean()
	{
		if (isNumeric(value))
			return Integer.parseInt(value) > 0;
		
		return value.equalsIgnoreCase("true");
	}
	
	public void setValue(String val)
	{
		value = val;
	}
	
	public void stopExpireTask()
	{
		if (task != null && !task.isDone())
		{
			task.cancel(true);
		}
	}
	
	private static class PlayerVarExpireTask implements Runnable
	{
		private PlayerVar _pv;
		
		public PlayerVarExpireTask(PlayerVar pv)
		{
			_pv = pv;
		}
		
		@Override
		public void run()
		{
			Player pc = _pv.getOwner();
			if (pc == null)
			{
				return;
			}
			
			PlayerVariables.unsetVar(pc, _pv.getName());
		}
	}
	
	public boolean isNumeric(String str)
	{
		try
		{
			@SuppressWarnings("unused")
			double d = Double.parseDouble(str);
		}
		catch (NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}
}
