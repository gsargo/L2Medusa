package net.sf.l2j.gameserver.model.zone.type;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.data.xml.MapRegionData;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.type.subtype.ZoneType;

/**
 * A zone extending {@link ZoneType}, where summoning is forbidden. The place is considered a pvp zone (no flag, no karma). It is used for arenas.
 */
public class TimeZone_backup extends ZoneType
{
	
	String _time = null;
	List<Player> _players;
	boolean _zoneOpen = false;
	String _zoneCloseTime = "00:00:00";
	
	public TimeZone_backup(int id)
	{
		super(id);
		_players = new CopyOnWriteArrayList<>();
		System.out.println("TIME ZONE LOADED");
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("time"))
		{
			_time = value;
			System.out.println("TIME ZONE TIME SET");
			long next = loadStartTime(_time.split(","));
			ThreadPool.schedule(new zoneReopen(), next - System.currentTimeMillis());
		}
		else
			super.setParameter(name, value);
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		System.out.println("TIME ZONE ENTERED " + getId());
		// character.sendMessage("Enter zone check");
		if (character.getActingPlayer() != null)
		{
			if (_zoneOpen)
			{
				_players.add(character.getActingPlayer());
			}
			else
			{
				_players.remove(character.getActingPlayer());
				int x = 83290;
				int y = 148027;
				int z = -3400;
				character.teleportTo(x, y, z, 50);
				character.sendMessage("Zone closed,try again later");
			}
		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		System.out.println("TIME ZONE EXIT " + getId());
		// character.sendMessage("Enter zone check, on_exit");
		if (character.getActingPlayer() != null)
			_players.remove(character.getActingPlayer());
	}
	
	long loadStartTime(String[] _data)
	{
		Calendar nextStartTime = null;
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar testStartTime;
			for (String timeOfDay : _data)
			{
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));
				
				if (testStartTime.getTimeInMillis() <= currentTime.getTimeInMillis())
				{
					testStartTime.add(Calendar.DAY_OF_MONTH, 1);
				}
				
				if (nextStartTime == null || testStartTime.getTimeInMillis() < nextStartTime.getTimeInMillis())
				{
					nextStartTime = testStartTime;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return nextStartTime.getTimeInMillis();
	}
	
	class zoneReopen implements Runnable
	{
		
		@Override
		public void run()
		{
			System.out.println("TIME ZONE REOPENED");
			for (Player pl : _players)
			{
				if (pl != null && pl.isOnline())
					pl.teleportTo(MapRegionData.TeleportType.TOWN);
			}
			_zoneOpen = !_zoneOpen;
			if (!_zoneOpen)
			{
				long next = loadStartTime(_time.split(","));
				ThreadPool.schedule(new zoneReopen(), next - System.currentTimeMillis());
			}
			else
			{
				ThreadPool.schedule(new zoneReopen(), 1000 * 60 * 60 * 2);
			}
		}
	}
}
