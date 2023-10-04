package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;

import c1c0s.VoteManagerAPI.L2TOPZONE;
import c1c0s.VoteManagerAPI.VoteSites;

public class L2TOPZONEcommand implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"VoteL2TOPZONE"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (activeChar.isEligibleToVote(VoteSites.L2TOPZONE))
		{
			L2TOPZONE rewardSite = new L2TOPZONE();
			rewardSite.checkVoteReward(activeChar);
			return false;
		}
		activeChar.sendMessage("L2TOPZONE: " + activeChar.getVoteCountdown(VoteSites.L2TOPZONE));
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}