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

public class L2Net
{
	public static Logger _log = Logger.getLogger(L2Net.class.getName());
	
	private static int l2netVotesCount = 0;
	protected List<String> already_rewarded;
	
	protected static boolean l2net = false;
	
	private L2Net()
	{
		_log.info("Auto Reward Manager: L2NET Loaded Successfully");
		if (l2net)
		{
			int l2net_votes = getl2netVotes();
			if (l2net_votes == -1)
			{
				l2net_votes = 0;
			}
			
			setl2netVoteCount(l2net_votes);
		}
		
		ThreadPool.scheduleAtFixedRate(new AutoReward(), Config.VOTES_SYSTEM_INITIAL_DELAYL2NET, Config.VOTES_SYSTEM_STEP_DELAYL2NET);
	}
	
	protected class AutoReward implements Runnable
	{
		@Override
		public void run()
		{
			if (l2net)
			{
				int l2net_votes = getl2netVotes();
				if (l2net_votes != -1)
				{
					if (l2net_votes != 0 && l2net_votes >= getl2netVoteCount() + Config.VOTES_FOR_REWARDL2NET)
					{
						already_rewarded = new ArrayList<>();
						
						Collection<Player> pls = World.getInstance().getPlayers();
						
						for (Player player : pls)
						{
							if (checkSingleBox(player))
							{
								player.addItem("reward", Config.L2NET_REWARD_ID, Config.L2NET_REWARD_COUNT, player, true);
							}
						}
						
						setl2netVoteCount(l2net_votes);
					}
					World.toAllOnlinePlayers(new CreatureSay(0, SayType.PARTYROOM_ALL, "L2NET", "votes now " + l2net_votes + ".  Next reward at " + (getl2netVoteCount() + Config.VOTES_FOR_REWARDL2NET) + ""));
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
	
	public static int getl2netVotes()
	{
		int votes = -1;
		try
		{
			final URL obj = new URL(Config.VOTES_SITE_L2NET_URL1);
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.addRequestProperty("User-Agent", "L2l2net");
			
			final int responseCode = con.getResponseCode();
			if (responseCode == 200)
			{
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
				{
					String inputLine;
					
					while ((inputLine = in.readLine()) != null)
					{
						if (inputLine.contains("<td id=\"votes_value\">"))
						{
							votes = Integer.parseInt(inputLine.split("<td id=\"votes_value\">")[1].split("</td>")[0]);
							break;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Error while getting l2net vote count.");
		}
		
		return votes;
	}
	
	protected static void setl2netVoteCount(int voteCount)
	{
		l2netVotesCount = voteCount;
	}
	
	protected static int getl2netVoteCount()
	{
		return l2netVotesCount;
	}
	
	public static L2Net getInstance()
	{
		if (Config.VOTES_SITE_L2NET_URL1 != null && !Config.VOTES_SITE_L2NET_URL1.equals(""))
		{
			l2net = true;
		}
		
		if (l2net)
			return SingletonHolder._instance;
		return null;
	}
	
	private static class SingletonHolder
	{
		protected static final L2Net _instance = new L2Net();
	}
	
}