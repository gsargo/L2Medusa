package net.sf.l2j.gameserver.handler.voicedcommandhandlers;

import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.model.actor.Player;

import c1c0s.VoteManagerAPI.GAMETOPSEU;
import c1c0s.VoteManagerAPI.VoteSites;

public class GAMETOPSEUcommand implements IVoicedCommandHandler
{
	private static final String[] VOICED_COMMANDS =
	{
		"VoteGAMETOPSEU"
	};
	
	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if (activeChar.isEligibleToVote(VoteSites.GAMETOPSEU))
		{
			GAMETOPSEU rewardSite = new GAMETOPSEU();
			rewardSite.checkVoteReward(activeChar);
			return false;
		}
		activeChar.sendMessage("GAMETOPSEU: " + activeChar.getVoteCountdown(VoteSites.GAMETOPSEU));
		return true;
	}
	
	@Override
	public String[] getVoicedCommandList()
	{
		return VOICED_COMMANDS;
	}
}