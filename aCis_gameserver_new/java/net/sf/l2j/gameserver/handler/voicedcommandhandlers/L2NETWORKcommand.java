package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;

import c1c0s.VoteManagerAPI.L2NETWORK;
import c1c0s.VoteManagerAPI.VoteSites;

public class L2NETWORKcommand implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"VoteL2NETWORK"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (activeChar.isEligibleToVote(VoteSites.L2NETWORK))
		{
			L2NETWORK rewardSite = new L2NETWORK();
			rewardSite.checkVoteReward(activeChar);
			return false;
		}
		activeChar.sendMessage("L2NETWORK: " + activeChar.getVoteCountdown(VoteSites.L2NETWORK));
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}