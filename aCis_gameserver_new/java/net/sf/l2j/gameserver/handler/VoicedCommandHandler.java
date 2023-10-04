package net.sf.l2j.gameserver.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.GameServer;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.GAMETOPSEUcommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.InfoSystemCommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.L2HOPZONEcommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.L2ITOPZcommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.L2JBRASILcommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.L2JTOPCOMcommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.L2NETWORKcommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.L2NETcommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.L2SERVERSCOMcommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.L2TOPCOcommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.L2TOPEUcommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.L2TOPGAMESERVERcommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.L2TOPSERVERScommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.L2TOPZONEcommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.L2VOTESCOMcommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.MenuSystemCommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.OlySystemCommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.RankSystemCommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.TOP100ARENAcommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.VoteInfoSystemCommand;
import net.sf.l2j.gameserver.handler.voicedcommandhandlers.BankingSystemCommand;

public class VoicedCommandHandler
{
	private static Logger LOGGER = Logger.getLogger(GameServer.class.getName());
	
	private static VoicedCommandHandler _instance;
	
	private final Map<String, IVoicedCommandHandler> _datatable;
	
	public static VoicedCommandHandler getInstance()
	{
		if (_instance == null)
		{
			_instance = new VoicedCommandHandler();
		}
		
		return _instance;
	}
	
	private VoicedCommandHandler()
	{
		_datatable = new HashMap<>();
		
		// registerVoicedCommandHandler(new Shiff_Mod());
		
		// vote managers start
		registerHandler(new L2JBRASILcommand());
		registerHandler(new L2NETWORKcommand());
		registerHandler(new L2TOPCOcommand());
		registerHandler(new L2TOPZONEcommand());
		registerHandler(new L2HOPZONEcommand());
		registerHandler(new L2TOPSERVERScommand());
		registerHandler(new L2TOPGAMESERVERcommand());
		registerHandler(new L2VOTESCOMcommand());
		registerHandler(new L2SERVERSCOMcommand());
		registerHandler(new L2ITOPZcommand());
		registerHandler(new L2JTOPCOMcommand());
		registerHandler(new GAMETOPSEUcommand());
		registerHandler(new TOP100ARENAcommand());
		registerHandler(new L2NETcommand());
		registerHandler(new L2TOPEUcommand());
		// vote managers end
		registerHandler(new MenuSystemCommand());
		registerHandler(new InfoSystemCommand());
		registerHandler(new VoteInfoSystemCommand());
		registerHandler(new OlySystemCommand());
		registerHandler(new RankSystemCommand());
		if(Config.BANKING_SYSTEM_ENABLED)
		{
		registerHandler(new BankingSystemCommand());
		}
		
		LOGGER.info("VoicedCommandHandler: Loaded " + _datatable.size() + " handlers.");
	}
	
	public void registerHandler(final IVoicedCommandHandler handler)
	{
		String[] ids = handler.getVoicedCommandList();
		
		for (final String id : ids)
		{
			_datatable.put(id, handler);
		}
		
		ids = null;
	}
	
	public IVoicedCommandHandler getVoicedCommandHandler(final String voicedCommand)
	{
		String command = voicedCommand;
		
		if (voicedCommand.indexOf(" ") != -1)
		{
			command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
		}
		return _datatable.get(command);
	}
	
	/**
	 * @return
	 */
	public int size()
	{
		return _datatable.size();
	}
}