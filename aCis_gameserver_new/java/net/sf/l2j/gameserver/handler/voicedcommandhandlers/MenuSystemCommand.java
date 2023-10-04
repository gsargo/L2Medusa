package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.ShowBoard;


/**
* This class trades Gold Bars for Adena and vice versa.
* 
* @author Ahmed
*/
public class MenuSystemCommand implements IVoicedCommandHandler
{
private static String[] _voicedCommands =
{
		"menu"
};


@Override
public boolean useVoicedCommand(String command, Player activeChar, String target)
{

	if(command.equalsIgnoreCase("menu"))
	{
		String content_home = HtmCache.getInstance().getHtm("data/html/help/menu/cmds.htm");
		final NpcHtmlMessage html_menu = new NpcHtmlMessage(5);
		html_menu.setHtml(content_home);
		activeChar.sendPacket(html_menu);
    	//separateAndSend(content_home, activeChar);
		//activeChar.sendMessage(".deposit (" + Config.BANKING_SYSTEM_ADENA + " Adena = " + Config.BANKING_SYSTEM_GOLDBARS + " Gold bar) / .withdraw (" + Config.BANKING_SYSTEM_GOLDBARS + " Gold bar = " + Config.BANKING_SYSTEM_ADENA + " Adena)");
	}
	
	
	return true;
}
@Override
public String[] getVoicedCommandList()
{
	return _voicedCommands;
}
}