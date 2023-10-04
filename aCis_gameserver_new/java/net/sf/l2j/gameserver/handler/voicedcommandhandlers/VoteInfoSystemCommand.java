package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import c1c0s.VoteManagerAPI.L2HOPZONE;
import c1c0s.VoteManagerAPI.L2JBRASIL;
import c1c0s.VoteManagerAPI.L2TOPZONE;
import c1c0s.VoteManagerAPI.VoteSites;

/**
* This class trades Gold Bars for Adena and vice versa.
* 
* @author Ahmed
*/
public class VoteInfoSystemCommand implements IVoicedCommandHandler
{
private static String[] _voicedCommands =
{
		"vote"
};


@Override
public boolean useVoicedCommand(String command, Player activeChar, String target)
{

	if(command.equalsIgnoreCase("vote"))
	{
		String content_home = HtmCache.getInstance().getHtm("data/html/help/menu/votes.htm");
		final NpcHtmlMessage html_votes = new NpcHtmlMessage(6);
		html_votes.setHtml(content_home);
		activeChar.sendPacket(html_votes);
	}
	
	return true;
}
@Override
public String[] getVoicedCommandList()
{
	return _voicedCommands;
}
}