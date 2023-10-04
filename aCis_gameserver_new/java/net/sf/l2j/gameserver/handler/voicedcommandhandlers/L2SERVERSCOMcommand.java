package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;

import c1c0s.VoteManagerAPI.L2SERVERSCOM;
import c1c0s.VoteManagerAPI.VoteSites;

public class L2SERVERSCOMcommand implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"VoteL2SERVERSCOM"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (activeChar.isEligibleToVote(VoteSites.L2SERVERSCOM))
		{
			L2SERVERSCOM rewardSite = new L2SERVERSCOM();
			rewardSite.checkVoteReward(activeChar);
			return false;
		}
		activeChar.sendMessage("L2SERVERSCOM: " + activeChar.getVoteCountdown(VoteSites.L2SERVERSCOM));
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}