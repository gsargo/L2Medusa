package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;

import c1c0s.VoteManagerAPI.L2JTOPCOM;
import c1c0s.VoteManagerAPI.VoteSites;

public class L2JTOPCOMcommand implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"VoteL2JTOPCOM"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (activeChar.isEligibleToVote(VoteSites.L2JTOPCOM))
		{
			L2JTOPCOM rewardSite = new L2JTOPCOM();
			rewardSite.checkVoteReward(activeChar);
			return false;
		}
		activeChar.sendMessage("L2JTOPCOM: " + activeChar.getVoteCountdown(VoteSites.L2JTOPCOM));
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}