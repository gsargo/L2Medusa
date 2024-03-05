//package net.sf.l2j.gameserver.model.zone.type;
//
//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.ScheduledFuture;
//
//import net.sf.l2j.commons.pool.ThreadPool;
//
//import net.sf.l2j.gameserver.data.xml.MapRegionData;
//import net.sf.l2j.gameserver.model.World;
//import net.sf.l2j.gameserver.model.actor.Creature;
//import net.sf.l2j.gameserver.model.actor.Player;
//import net.sf.l2j.gameserver.model.zone.type.subtype.ZoneType;
//
///**
// * A zone extending {@link ZoneType}, where summoning is forbidden. The place is considered a pvp zone (no flag, no karma). It is used for arenas.
// */
//public class TimeZone extends ZoneType
//{
//	
//	String zone_name = "Area"; // will be set from xml
//	String _time = null;
//	List<Player> _players;
//	boolean _zoneOpen = false;
//	String _zoneCloseTime = "00:00:00";
//	
//	public TimeZone(int id)
//	{
//		super(id);
//		_players = new CopyOnWriteArrayList<>();
//		System.out.println("TIME ZONE LOADED");
//	}
//	
//	@Override
//	public void setParameter(String name, String value)
//	{
//		if (name.equals("time"))
//		{
//			_time = value;
//			System.out.println("TIME ZONE TIME SET");
//			long next = loadStartTime(_time.split(","));
//			ThreadPool.schedule(new zoneReopen(), next - System.currentTimeMillis());
//		}
//		else if (name.equals("zone_name"))
//		{
//			zone_name = value;
//		}
//		else
//			super.setParameter(name, value);
//	}
//	
//	@Override
//	protected void onEnter(Creature character)
//	{
//		System.out.println("TIME ZONE ENTERED " + getId());
//		// character.sendMessage("Enter zone check");
//		if (character.getActingPlayer() != null)
//		{
//			if (_zoneOpen)
//			{
//				_players.add(character.getActingPlayer());
//			}
//			else
//			{
//				_players.remove(character.getActingPlayer());
//				int x = 83290;
//				int y = 148027;
//				int z = -3400;
//				character.teleportTo(x, y, z, 50);
//				character.sendMessage("Zone closed,try again later");
//			}
//		}
//	}
//	
//	@Override
//	protected void onExit(Creature character)
//	{
//		System.out.println("TIME ZONE EXIT " + getId());
//		// character.sendMessage("Enter zone check, on_exit");
//		if (character.getActingPlayer() != null)
//			_players.remove(character.getActingPlayer());
//	}
//	
//	long loadStartTime(String[] _data)
//	{
//		Calendar currentTime = Calendar.getInstance();
//		try
//		{
//			final Optional<Calendar> nextStartTime = Arrays.stream(_data).map(time -> time.split(":")).map(split ->
//			{
//				Calendar timeOfDay = Calendar.getInstance();
//				timeOfDay.setLenient(true);
//				timeOfDay.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split[0]));
//				timeOfDay.set(Calendar.MINUTE, Integer.parseInt(split[1]));
//				
//				if (timeOfDay.getTimeInMillis() <= currentTime.getTimeInMillis())
//				{
//					timeOfDay.add(Calendar.DAY_OF_MONTH, 1);
//				}
//				
//				return timeOfDay;
//				
//			}).min(Comparator.comparingLong(Calendar::getTimeInMillis));
//			
//			return nextStartTime.map(Calendar::getTimeInMillis).orElse(-1L);
//		}
//		catch (Exception e)
//		{
//			LOGGER.warn("There was a problem loading the start time: using default -1 value.", e);
//			return -1;
//		}
//	}
//	
//	public void onOpen()// on open
//	{
//		ZoneClose(ThreadPool.schedule(new ZoneClose(), 1000 * 60 * 60 * 2));
//		
//		World.announceToOnlinePlayers(zone_name + " is now open!");
//		
//	}
//	
//	/**
//	 * @param schedule
//	 */
//	private void ZoneClose(ScheduledFuture<?> schedule)
//	{
//		_zoneOpen = !_zoneOpen;
//		
//	}
//	
//	public void onClose()// on close
//	{
//		
//		World.announceToOnlinePlayers(zone_name + " is now closed!");
//		
//	}
//	
//	class zoneReopen implements Runnable
//	{
//		
//		@Override
//		public void run()
//		{
//			
//			 if (!_zoneOpen) 
//			 	{
//		            System.out.println("TIME ZONE REOPENED");
//		            onOpen();
//		            _zoneOpen = true;
//		            // Schedule the zone closure after 2 hours
//		            ThreadPool.schedule(new ZoneClose(), 1000 * 60 * 60 * 2);
//		        }
//			 else 
//		        {
//		            System.out.println("TIME ZONE CLOSED");
//		            onClose();
//		            _zoneOpen = false;
//		            // Teleport players out of the zone
//		            for (Player pl : _players)
//		            	{
//		                if (pl != null && pl.isOnline())
//		                    pl.teleportTo(MapRegionData.TeleportType.TOWN);
//		            	}
//		            // Schedule the zone reopening based on start times
//		            long next = loadStartTime(_time.split(","));
//		            ThreadPool.schedule(new zoneReopen(), next - System.currentTimeMillis());
//		        }
//			/* old code
//			  System.out.println("TIME ZONE REOPENED");
//			 
//			onOpen();
//			for (Player pl : _players)
//			{
//				if (pl != null && pl.isOnline())
//					pl.teleportTo(MapRegionData.TeleportType.TOWN);
//			}
//			_zoneOpen = !_zoneOpen;
//			if (!_zoneOpen)
//			{
//				long next = loadStartTime(_time.split(","));
//				ThreadPool.schedule(new zoneReopen(), next - System.currentTimeMillis());
//				
//			}
//			else
//			{
//				ThreadPool.schedule(new zoneReopen(), 1000 * 60 * 60 * 2);
//			}
//			*/
//		}
//		
//	}
//	
//	class ZoneClose implements Runnable
//	{
//		ZoneClose()
//		{
//			
//		}
//		
//		@Override
//		public void run()
//		{
//			onClose();
//			
//		}
//		
//	}
//}
package net.sf.l2j.gameserver.model.zone.type;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.data.xml.MapRegionData;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.zone.type.subtype.ZoneType;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage.SMPOS;

/**
 * A zone extending {@link ZoneType} which works on defined times.
 */
public class TimeZone extends ZoneType
{
	private final Set<Player> _players = ConcurrentHashMap.newKeySet();
	
	private static final Location DEFAULT_LOCATION = new Location(83290, 148027, -3400);
	private static final long ZONE_CLOSE_DELAY = TimeUnit.MINUTES.toMillis(119);
	//private static final long ZONE_CLOSE_DELAY = TimeUnit.MINUTES.toMillis(5);
	private static final long ONE_MIN_BEFORE_AREA_CLOSING = TimeUnit.MINUTES.toMillis(118);
	//private static final long ONE_MIN_BEFORE_AREA_CLOSING = TimeUnit.MINUTES.toMillis(4);
	
	ExShowScreenMessage _packet = new ExShowScreenMessage("This area is closing in 60 seconds!", 12*1000, SMPOS.TOP_CENTER, false);

	private ScheduledFuture<?> _task;
	
	private String zoneName = "Area"; // will be set from xml
	private String _time = null;
	private boolean _zoneOpen = false;
	
	public boolean _isopen = false;
	
	
	
	public TimeZone(int id)
	{
		super(id);
		LOGGER.info("TIME ZONE LOADED");
	}
	
	
	@Override
	public void setParameter(String name, String value)
	{
		Objects.requireNonNull(name, "Name cannot be null");
		Objects.requireNonNull(value, "Value cannot be null");
		
		switch (name)
		{
			case "time":
				_time = value;
				LOGGER.info("TimeZone time set.");
				scheduleOpening();
				break;
			case "zone_name":
				zoneName = value;
				break;
			default:
				super.setParameter(name, value);
		}
	}
	
	private void scheduleOpening()
	{
		ThreadPool.schedule(this::openZone, loadStartTime() - System.currentTimeMillis());
		//if (_task == null)
			//_task = ThreadPool.schedule(this::openZone, loadStartTime() - System.currentTimeMillis());
	}
	

	private void openZone()
	{
		LOGGER.info("TIME ZONE REOPENED");
		
		announceAndScheduleClosing();
		
		_zoneOpen = true;
		
		_players.stream().filter(Objects::nonNull).filter(Player::isOnline).forEach(player -> player.teleportTo(MapRegionData.TeleportType.TOWN));
		
		_isopen=true;
	}
	
	public void announceAndScheduleClosing()
	{
		World.announceToOnlinePlayers(zoneName + " is now open!");
		
		//if (_task == null)
			ThreadPool.schedule(this::closeAndScheduleReopening, ZONE_CLOSE_DELAY); //calculate duration
			ThreadPool.schedule(() ->announce1MinBeforeClosing(),ONE_MIN_BEFORE_AREA_CLOSING);//show message on screen, 1 minute before closing
			//_task = ThreadPool.schedule(this::closeAndScheduleReopening, ZONE_CLOSE_DELAY);
	}
	
	public void announce1MinBeforeClosing()
	{
		_players.stream().filter(Objects::nonNull).filter(Player::isOnline).forEach(player -> player.sendPacket(_packet));
	}
	
	private synchronized void closeAndScheduleReopening()
	{
		World.announceToOnlinePlayers(zoneName + " is now closed!");
		scheduleOpening();
		
		for(Player player : _players) //kick players when zone is closed
		{
			kickPlayer(player);
		}
		
		_zoneOpen = false;
		_isopen=false;
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		//LOGGER.info("TIME ZONE ENTERED " + getId());
		
		final Player player = character.getActingPlayer();
		
		if (player == null)
			return;
		
		player.sendMessage("You have entered a time zone");
		
		if (_zoneOpen)
		{
			_players.add(player);
			//player.setIsInsideTimeZone(true);
		}
		else
		{
			kickPlayer(player);
		}
	}
	
	private void kickPlayer(Player player)
	{
		Objects.requireNonNull(player, "Player cannot be null");
		
		removePlayer(player);
		player.setIsInsideTimeZone(false);
		
		player.teleportTo(DEFAULT_LOCATION, 50);
		player.sendMessage("Zone closed,try again later.");
	}
	
	private void removePlayer(Player player)
	{
		_players.remove(player);
	}
	
	@Override
	protected void onExit(Creature character)
	{
		//System.out.println("TIME ZONE EXIT " + getId());
		
		final Player player = character.getActingPlayer();
		if (player == null)
			return;
		
		removePlayer(player);
		//player.setIsInsideTimeZone(false);
	}
	
	private long loadStartTime()
	{
		final Calendar currentTime = Calendar.getInstance();
		try
		{
			final Optional<Calendar> nextStartTime = Arrays.stream(_time.split(",")).map(time -> time.split(":")).map(split ->
			{
				final Calendar timeOfDay = Calendar.getInstance();
				timeOfDay.setLenient(true);
				timeOfDay.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split[0]));
				timeOfDay.set(Calendar.MINUTE, Integer.parseInt(split[1]));
				
				if (timeOfDay.getTimeInMillis() <= currentTime.getTimeInMillis())
					timeOfDay.add(Calendar.DAY_OF_MONTH, 1);
				
				return timeOfDay;
				
			}).min(Comparator.comparingLong(Calendar::getTimeInMillis));
			
			return nextStartTime.map(Calendar::getTimeInMillis).orElse(-1L);
		}
		catch (NumberFormatException e)
		{
			LOGGER.warn("There was a problem loading the start time: using default -1 value.", e);
			return -1;
		}
	}
	
	public boolean isOpen()
	{
		return _isopen;
	}
	
}
