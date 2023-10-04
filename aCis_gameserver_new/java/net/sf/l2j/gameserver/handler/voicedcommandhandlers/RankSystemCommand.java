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
public class RankSystemCommand implements IVoicedCommandHandler
{
private static String[] _voicedCommands =
{
		"ranks"
};


@Override
public boolean useVoicedCommand(String command, Player activeChar, String target)
{

	if(command.equalsIgnoreCase("ranks"))
	{
		String content_ranks = HtmCache.getInstance().getHtm("data/html/help/menu/rank-info.htm");
		final NpcHtmlMessage html_ranks = new NpcHtmlMessage(0);
		html_ranks.setHtml(content_ranks);
		activeChar.sendPacket(html_ranks);
	}
	
	return true;
}
@Override
public String[] getVoicedCommandList()
{
	return _voicedCommands;
}
}