package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;

import c1c0s.VoteManagerAPI.L2TOPSERVERS;
import c1c0s.VoteManagerAPI.VoteSites;

public class L2TOPSERVERScommand implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"VoteL2TOPSERVERS"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (activeChar.isEligibleToVote(VoteSites.L2TOPSERVERS))
		{
			L2TOPSERVERS rewardSite = new L2TOPSERVERS();
			rewardSite.checkVoteReward(activeChar);
			return false;
		}
		activeChar.sendMessage("L2TOPSERVERS: " + activeChar.getVoteCountdown(VoteSites.L2TOPSERVERS));
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}