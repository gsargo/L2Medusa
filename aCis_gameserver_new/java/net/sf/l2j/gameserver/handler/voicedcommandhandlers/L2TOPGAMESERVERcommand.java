package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;

import c1c0s.VoteManagerAPI.L2TOPGAMESERVER;
import c1c0s.VoteManagerAPI.VoteSites;

public class L2TOPGAMESERVERcommand implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"VoteL2TOPGAMESERVER"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (activeChar.isEligibleToVote(VoteSites.L2TOPGAMESERVER))
		{
			L2TOPGAMESERVER rewardSite = new L2TOPGAMESERVER();
			rewardSite.checkVoteReward(activeChar);
			return false;
		}
		activeChar.sendMessage("L2TOPGAMESERVER: " + activeChar.getVoteCountdown(VoteSites.L2TOPGAMESERVER));
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}