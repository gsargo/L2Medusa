package c1c0s.VoteManagerAPI;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;

public abstract class VoteRewardSite
{
	
	private static final Logger LOGGER = Logger.getLogger(VoteRewardSite.class.getName());
	
	protected abstract String getEndpoint(Player player);
	
	public void checkVoteReward(Player player)
	{
		try
		{
			if (player.isVoting1())
			{
				player.sendMessage("You are already voting.");
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			if (World.getInstance().getPlayers().stream().filter(Player::isVoting1).count() >= 5)
			{
				player.sendMessage("Try again in a few seconds.");
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			player.setIsVoting1(true);
			
			if (!player.isEligibleToVote(getVoteSiteInfo().voteSite()))
			{
				player.setIsVoting1(false);
				player.sendMessage("You can't vote yet.");
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			ThreadPool.execute(() ->
			{
				try
				{
					if (!hasVoted(player))
					{
						player.setIsVoting1(false);
						player.sendMessage(String.format("Please vote for us in %s ", getVoteSiteInfo().voteSite().getName()));
						player.sendPacket(ActionFailed.STATIC_PACKET);
						return;
					}
					
					long dateTimevoted = System.currentTimeMillis();
					
					VotedRecord votedRecord = new VotedRecord(player.getAccountName(), player.getIpAddress(), dateTimevoted, getVoteSiteInfo().voteSite().getName());
					
					VoteDay.addVotedRecord(votedRecord);
					player.setLastVotedTimestamp(getVoteSiteInfo().voteSite(), dateTimevoted);
					reward(player);
					
					player.setVotesToSites(1); // increase by 1 the vote Sites that player voted
					player.addHwidVotes(player.gethwid(), 1); // save them to player Data for future usage
					
					player.setIsVoting1(false);
				}
				catch (Exception e)
				{
					handleExceptionForVoteAttempt(player, e);
				}
				
			});
			
		}
		catch (Exception e)
		{
			handleExceptionForVoteAttempt(player, e);
		}
	}
	
	protected abstract boolean hasVoted(Player player);
	
	protected void reward(Player player)
	{
		player.sendMessage("Thank you for voting.");
		player.addItem("Vote Reward", Config.VoteManagerApi_REWARD_ID, Config.VoteManagerApi_REWARD_COUNT, player, true);
		
	}
	
	private VoteSiteInfo getVoteSiteInfo()
	{
		return getClass().getAnnotation(VoteSiteInfo.class);
	}
	
	String getApiKey()
	{
		return getVoteSiteInfo().apiKey();
	}
	
	private static void handleExceptionForVoteAttempt(Player player, Exception e)
	{
		player.setIsVoting1(false);
		player.sendPacket(ActionFailed.STATIC_PACKET);
		LOGGER.log(Level.WARNING, "There was an error during a vote attempt", e);
	}
	
	@Override
	public String toString()
	{
		return getClass().getAnnotation(VoteSiteInfo.class).voteSite().getName();
	}
}