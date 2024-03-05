package net.sf.l2j.gameserver.mods.antibot;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.LoginServerThread;
import net.sf.l2j.gameserver.enums.PunishmentType;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.network.serverpackets.ExServerPrimitive;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage.SMPOS;

/**
 * @author L2Medusa
 */
public class Captcha implements Runnable
{
	Player _player = null;
	State _currentState = State.IN_PROGRESS;
	final Map<Integer, CaptchaBox> _boxs = new ConcurrentHashMap<>();
	int _boxNumbers = 3;
	private final AtomicInteger _boxCounter = new AtomicInteger();
	int _checkedBoxs = 0;
	
	private static final String UPDATE_JAIL = "UPDATE characters SET x=-114356, y=-249645, z=-2984, punish_level=?, punish_timer=? WHERE char_name=?";
	
	public enum State
	{
		IN_PROGRESS,
		PASSED,
		FAILED
	}
	
	protected int _countdown = 33;
	protected Future<?> _task = null;
	
	public Captcha(Player player, int timer)
	{
		this._countdown = timer;
		this._player = player;
		feedBoxs();
		generateBoxs();
		startCaptcha();
	}
	
	private void startCaptcha()
	{
		_task = ThreadPool.scheduleAtFixedRate(this, 1000, 1000);
	}
	
	private void closeCaptcha()
	{
		_task.cancel(true);
		_task = null;
		_player.clearDebugPackets();
		results();
	}
	
	private void results()
	{
		if (_checkedBoxs == _boxNumbers)
			_currentState = State.PASSED;
		else
			punishment();
	}
	
	/*
	 * Here u decide what to do if player fails the captcha
	 */
	private void punishment()
	{
		CaptchaZones zone = AntibotProtection.getInstance().getCaptchaZone(_player);
		_currentState = State.FAILED;
		
		switch (zone._punishSelect)
		{
			case 1:
				if (!_player.isOnline())
					jailOfflinePlayer(_player, _player.getName(), zone._punishParams);
				else
					_player.getPunishment().setType(PunishmentType.JAIL, zone._punishParams);
				break;
			case 2:
				LoginServerThread.getInstance().sendAccessLevel(_player.getName(), -100);
				break;
			case 3:
				if (!_player.isOnline())
					return;
				
				_player.logout(true);
				break;
		}
	}
	
	private void feedBoxs()
	{
		for (int i = 0; i < _boxNumbers; i++)
		{
			final int counter = _boxCounter.incrementAndGet();
			Location tempLoc = new Location(_player.getX() + Rnd.get(-80, 80), _player.getY() + Rnd.get(-80, 80), _player.getZ());
			CaptchaBox box = new CaptchaBox(false, tempLoc, counter);
			_boxs.put(counter, box);
		}
	}
	
	private void checkPlayerLoc()
	{
		if (checkifPassed())
			return;
		
		for (CaptchaBox box : _boxs.values())
		{
			if (_player.isIn3DRadius(box._boxLoc, 13))
			{
				if (box._unlocked == false)
					_checkedBoxs++;
				
				box._unlocked = true;
				updatePointers();
			}
		}
		
	}
	
	private static void jailOfflinePlayer(Player player, String playerName, int delay)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_JAIL))
		{
			ps.setInt(1, PunishmentType.JAIL.ordinal());
			ps.setLong(2, ((delay > 0) ? delay * 60000L : 0));
			ps.setString(3, playerName);
			ps.execute();
			
			final int count = ps.getUpdateCount();
			if (count == 0)
				player.sendMessage("This Player isn't found.");
			else
				player.sendMessage(playerName + " has been jailed" + ((delay > 0) ? " for " + delay + " minutes." : "."));
		}
		catch (Exception e)
		{
			// LOGGER.error("Couldn't jail offline Player.", e);
		}
	}
	
	private boolean checkifPassed()
	{
		boolean tempHold = false;
		
		if (_checkedBoxs == _boxNumbers)
		{
			_currentState = State.PASSED;
			closeCaptcha();
			tempHold = true;
		}
		
		return tempHold;
	}
	
	private void updatePointers()
	{
		_player.clearDebugPackets();
		generateBoxs();
	}
	
	private void generateBoxs()
	{
		final ExServerPrimitive debug = _player.getDebugPacket("antibot");
		
		for (CaptchaBox box : _boxs.values())
		{
			if (box._unlocked == true)
			{
				debug.addPoint("PASSED.", Color.green, true, box._boxLoc.getX(), box._boxLoc.getY(), box._boxLoc.getZ() + 33);
				debug.addLine(Color.cyan, box._boxLoc, box._boxLoc.getX(), box._boxLoc.getY(), box._boxLoc.getZ() + 33);
			}
			else
			{
				debug.addPoint("Move here.", Color.red, true, box._boxLoc.getX(), box._boxLoc.getY(), box._boxLoc.getZ());
			}
		}
		debug.sendTo(_player);
	}
	
	private void calculateTime(long seconds)
	{
		long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
		long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);
		
		if (seconds >= 60)
			_player.sendPacket(new ExShowScreenMessage(1, -1, SMPOS.TOP_CENTER, false, 1, 0, 0, true, 1000, false, "Anti Bot Protection " + minute + "m:" + second + "s " + "<>  Status: " + antibotStatusText() + " # Move to Points # Status: "));
		else
			_player.sendPacket(new ExShowScreenMessage(1, -1, SMPOS.TOP_CENTER, false, 1, 0, 0, true, 1000, false, "Anti Bot Protection " + second + "'s <> Status: " + antibotStatusText() + " # Move to Points."));
	}
	
	private String antibotStatusText()
	{
		String tempHolder = "";
		
		if (_countdown > 0)
			tempHolder = "In Progress.";
		else if (_checkedBoxs < _boxNumbers)
			tempHolder = "Failed.";
		else if (_checkedBoxs == _boxNumbers)
			tempHolder = "Passed.";
		
		return tempHolder;
	}
	
	@Override
	public void run()
	{
		if (_countdown < 0)
		{
			closeCaptcha();
		}
		
		switch (_countdown)
		{
			case 0:
				closeCaptcha();
				break;
		}
		checkPlayerLoc();
		calculateTime(_countdown);
		_countdown--;
	}
	
}