package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.olympiad.Olympiad;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.ShowBoard;


/**
* This class trades Gold Bars for Adena and vice versa.
* 
* @author Ahmed
*/
public class OlySystemCommand implements IVoicedCommandHandler
{
private static String[] _voicedCommands =
{
		"oly"
};


@Override
public boolean useVoicedCommand(String command, Player activeChar, String target)
{

	if(command.equalsIgnoreCase("oly"))
	{
		Olympiad.getInstance().olympiadEnd(activeChar);
		activeChar.sendMessage("You can use up to +6 A grade equipment to participate in Olympiad.");
	}
	
	return true;
}
@Override
public String[] getVoicedCommandList()
{
	return _voicedCommands;
}
}