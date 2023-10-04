/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package c1c0s.AutoReward;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;

public class L2Votes
{
	public static Logger _log = Logger.getLogger(L2Votes.class.getName());
	
	private int l2votesVotesCount = 0;
	protected List<String> already_rewarded;
	
	protected static boolean l2votes = false;
	
	private L2Votes()
	{
		_log.info("Auto Reward Manager: L2VOTES Loaded Successfully");
		if (l2votes)
		{
			int l2votes_votes = getl2votesVotes();
			if (l2votes_votes == -1)
			{
				l2votes_votes = 0;
			}
			
			setl2votesVoteCount(l2votes_votes);
		}
		
		ThreadPool.scheduleAtFixedRate(new AutoReward(), Config.VOTES_SYSTEM_INITIAL_DELAYL2VOTES, Config.VOTES_SYSTEM_STEP_DELAYL2VOTES);
		
	}
	
	protected class AutoReward implements Runnable
	{
		@Override
		public void run()
		{
			if (l2votes)
			{
				int l2votes_votes = getl2votesVotes();
				if (l2votes_votes != -1)
				{
					if (l2votes_votes != 0 && l2votes_votes >= getl2votesVoteCount() + Config.VOTES_FOR_REWARDL2VOTES)
					{
						already_rewarded = new ArrayList<>();
						
						Collection<Player> pls = World.getInstance().getPlayers();
						
						for (Player player : pls)
						{
							if (checkSingleBox(player))
							{
								player.addItem("reward", Config.L2VOTES_REWARD_ID, Config.L2VOTES_REWARD_COUNT, player, true);
							}
						}
						
						setl2votesVoteCount(l2votes_votes);
					}
					World.toAllOnlinePlayers(new CreatureSay(0, SayType.PARTYROOM_ALL, "L2VOTES", "votes now " + l2votes_votes + ".  Next reward at " + (getl2votesVoteCount() + Config.VOTES_FOR_REWARDL2VOTES) + ""));
				}
			}
		}
	}
	
	protected boolean checkSingleBox(Player player)
	{
		if (player.getClient() != null && player.getClient().getConnection() != null && !player.getClient().getConnection().isClosed() && !player.getClient().isDetached())
		{
			
			String playerip = player.getClient().getConnection().getInetAddress().getHostAddress();
			
			if (already_rewarded.contains(playerip))
				return false;
			already_rewarded.add(playerip);
			return true;
		}
		return false;
	}
	
	protected int getl2votesVotes()
	{
		int votes = -1;
		try
		{
			final URL obj = new URL(Config.VOTES_SITE_L2VOTES_URL1);
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.addRequestProperty("User-Agent", "L2VOTES");
			
			final int responseCode = con.getResponseCode();
			if (responseCode == 200)
			{
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
				{
					String inputLine;
					
					while ((inputLine = in.readLine()) != null)
					{
						if (inputLine.contains("Votes:"))
						{
							votes = Integer.parseInt(inputLine.split("<a class='votes'>")[1].split("</a>")[0].replace(" ", ""));
							break;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
			System.out.println("Error while getting L2VOTES vote count.");
		}
		
		return votes;
	}
	
	protected void setl2votesVoteCount(int voteCount)
	{
		l2votesVotesCount = voteCount;
	}
	
	protected int getl2votesVoteCount()
	{
		return l2votesVotesCount;
	}
	
	public static L2Votes getInstance()
	{
		if (Config.VOTES_SITE_L2VOTES_URL1 != null && !Config.VOTES_SITE_L2VOTES_URL1.equals(""))
		{
			l2votes = true;
		}
		
		if (l2votes)
			return SingletonHolder._instance;
		return null;
	}
	
	private static class SingletonHolder
	{
		protected static final L2Votes _instance = new L2Votes();
	}
	
}