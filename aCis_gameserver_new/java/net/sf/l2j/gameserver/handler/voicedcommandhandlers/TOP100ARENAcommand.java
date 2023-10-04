package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;

import c1c0s.VoteManagerAPI.TOP100ARENA;
import c1c0s.VoteManagerAPI.VoteSites;

public class TOP100ARENAcommand implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"VoteTOP100ARENA"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (activeChar.isEligibleToVote(VoteSites.TOP100ARENA))
		{
			TOP100ARENA rewardSite = new TOP100ARENA();
			rewardSite.checkVoteReward(activeChar);
			return false;
		}
		activeChar.sendMessage("TOP100ARENA: " + activeChar.getVoteCountdown(VoteSites.TOP100ARENA));
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}