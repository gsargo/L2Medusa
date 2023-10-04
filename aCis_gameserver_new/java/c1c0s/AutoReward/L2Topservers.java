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

public class L2Topservers
{
	public static Logger _log = Logger.getLogger(L2Topservers.class.getName());
	
	private int l2topserversVotesCount = 0;
	protected List<String> already_rewarded;
	
	protected static boolean l2topservers = false;
	
	private L2Topservers()
	{
		_log.info("Auto Reward Manager: L2TOPSERVERS Loaded Successfully");
		if (l2topservers)
		{
			int l2topservers_votes = getl2topserversVotes();
			if (l2topservers_votes == -1)
			{
				l2topservers_votes = 0;
			}
			
			setl2topserversVoteCount(l2topservers_votes);
		}
		
		ThreadPool.scheduleAtFixedRate(new AutoReward(), Config.VOTES_SYSTEM_INITIAL_DELAYL2TOPSERVERS, Config.VOTES_SYSTEM_STEP_DELAYL2TOPSERVERS);
	}
	
	protected class AutoReward implements Runnable
	{
		@Override
		public void run()
		{
			if (l2topservers)
			{
				int l2topservers_votes = getl2topserversVotes();
				if (l2topservers_votes != -1)
				{
					if (l2topservers_votes != 0 && l2topservers_votes >= getl2topserversVoteCount() + Config.VOTES_FOR_REWARDL2TOPSERVERS)
					{
						already_rewarded = new ArrayList<>();
						
						Collection<Player> pls = World.getInstance().getPlayers();
						
						for (Player player : pls)
						{
							if (checkSingleBox(player))
							{
								player.addItem("reward", Config.L2TOPSERVERS_REWARD_ID, Config.L2TOPSERVERS_REWARD_COUNT, player, true);
							}
						}
						
						setl2topserversVoteCount(l2topservers_votes);
					}
					World.toAllOnlinePlayers(new CreatureSay(0, SayType.PARTYROOM_ALL, "L2TOPSERVERS", "votes now " + l2topservers_votes + ".  Next reward at " + (getl2topserversVoteCount() + Config.VOTES_FOR_REWARDL2TOPSERVERS) + ""));
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
	
	protected int getl2topserversVotes()
	{
		int votes = -1;
		try
		{
			final URL obj = new URL(Config.VOTES_SITE_L2TOPSERVERS_URL1);
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.addRequestProperty("User-Agent", "L2TopServers");
			
			final int responseCode = con.getResponseCode();
			if (responseCode == 200)
			{
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
				{
					String inputLine;
					
					while ((inputLine = in.readLine()) != null)
					{
						if (inputLine.contains("<img src=\"/img/vote.png\"> VOTE <span>"))
						{
							votes = Integer.parseInt(inputLine.split("<img src=\"/img/vote.png\"> VOTE <span>")[1].split("</span>")[0]);
							break;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
			System.out.println("Error while getting L2TOPSERVERS vote count.");
		}
		
		return votes;
	}
	
	protected void setl2topserversVoteCount(int voteCount)
	{
		l2topserversVotesCount = voteCount;
	}
	
	protected int getl2topserversVoteCount()
	{
		return l2topserversVotesCount;
	}
	
	public static L2Topservers getInstance()
	{
		if (Config.VOTES_SITE_L2TOPSERVERS_URL1 != null && !Config.VOTES_SITE_L2TOPSERVERS_URL1.equals(""))
		{
			l2topservers = true;
		}
		
		if (l2topservers)
			return SingletonHolder._instance;
		return null;
	}
	
	private static class SingletonHolder
	{
		protected static final L2Topservers _instance = new L2Topservers();
	}
	
}