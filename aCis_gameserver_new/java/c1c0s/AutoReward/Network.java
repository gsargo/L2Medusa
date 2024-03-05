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

public class Network
{
	public static Logger _log = Logger.getLogger(Network.class.getName());
	
	private static int networkVotesCount = 0;
	protected List<String> already_rewarded;
	
	protected static boolean network = false;
	
	private Network()
	{
		_log.info("Auto Reward Manager: L2NETWORK Loaded Successfully");
		if (network)
		{
			int network_votes = getNetworkVotes();
			if (network_votes == -1)
			{
				network_votes = 0;
			}
			
			setNetworkVoteCount(network_votes);
		}
		
		ThreadPool.scheduleAtFixedRate(new AutoReward(), Config.VOTES_SYSTEM_INITIAL_DELAYNETWORK, Config.VOTES_SYSTEM_STEP_DELAYNETWORK);
		
	}
	
	protected class AutoReward implements Runnable
	{
		@Override
		public void run()
		{
			if (network)
			{
				int network_votes = getNetworkVotes();
				if (network_votes != -1)
				{
					if (network_votes != 0 && network_votes >= getNetworkVoteCount() + Config.VOTES_FOR_REWARDNETWORK)
					{
						already_rewarded = new ArrayList<>();
						
						Collection<Player> pls = World.getInstance().getPlayers();
						
						for (Player player : pls)
						{
							if (checkSingleBox(player))
							{
								player.addItem("reward", Config.NETWORK_REWARD_ID, Config.NETWORK_REWARD_COUNT, player, true);
							}
						}
						
						setNetworkVoteCount(network_votes);
					}
					World.toAllOnlinePlayers(new CreatureSay(0, SayType.PARTYROOM_ALL, "L2NETWORK", "votes now " + network_votes + ".  Next reward at " + (getNetworkVoteCount() + Config.VOTES_FOR_REWARDNETWORK) + ""));
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
	
	protected static int getNetworkVotes()
	{
		int votes = -1;
		
		try
		{
			final URL obj = new URL(Config.VOTES_SITE_NETWORK_URL1);
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.addRequestProperty("User-Agent", "L2Network");
			
			final int responseCode = con.getResponseCode();
			if (responseCode == 200)
			{
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
				{
					String inputLine;
					while ((inputLine = in.readLine()) != null)
					{
						if (inputLine.contains("btn-label "))
						{
							votes = Integer.valueOf(inputLine.split("btn-label-right\">")[1].replace("</span>", "").replace(",", ""));
							break;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
			System.out.println("Error while getting NETWORK vote count.");
		}
		
		return votes;
	}
	
	protected static void setNetworkVoteCount(int voteCount)
	{
		networkVotesCount = voteCount;
	}
	
	protected static int getNetworkVoteCount()
	{
		return networkVotesCount;
	}
	
	public static Network getInstance()
	{
		if (Config.VOTES_SITE_NETWORK_URL1 != null && !Config.VOTES_SITE_NETWORK_URL1.equals(""))
		{
			network = true;
		}
		
		if (network)
			return SingletonHolder._instance;
		return null;
	}
	
	private static class SingletonHolder
	{
		protected static final Network _instance = new Network();
	}
	
}