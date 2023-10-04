package net.sf.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.entity.Tournament.ByPasses.TournamentBypasses;

public class BypassHandler
{
	private static Logger _log = Logger.getLogger(BypassHandler.class.getName());
	private final Map<Integer, IBypassHandler> _datatable = new HashMap<>();
	
	public static BypassHandler getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private BypassHandler()
	{
		registerBypassHandler(new TournamentBypasses());
	}
	
	public void registerBypassHandler(IBypassHandler handler)
	{
		String[] ids = handler.getBypassHandlersList();
		for (int i = 0; i < ids.length; i++)
		{
			_datatable.put(ids[i].hashCode(), handler);
		}
	}
	
	public IBypassHandler getBypassHandler(String bypass)
	{
		String command = bypass;
		if (bypass.indexOf(" ") != -1)
			command = bypass.substring(0, bypass.indexOf(" "));
		if (Config.DEBUG)
			_log.fine("getting handler for command: " + command + " -> " + (_datatable.get(command.hashCode()) != null));
		return _datatable.get(command.hashCode());
	}
	
	public int size()
	{
		return _datatable.size();
	}
	
	private static class SingletonHolder
	{
		protected static final BypassHandler _instance = new BypassHandler();
	}
}
