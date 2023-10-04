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

public class Topzone
{
	public static Logger _log = Logger.getLogger(Topzone.class.getName());
	
	private static int topzoneVotesCount = 0;
	protected List<String> already_rewarded;
	
	protected static boolean topzone = false;
	
	private Topzone()
	{
		_log.info("Auto Reward Manager: TOPZONE Loaded Successfully");
		if (topzone)
		{
			int topzone_votes = gettopzoneVotes();
			if (topzone_votes == -1)
			{
				topzone_votes = 0;
			}
			
			settopzoneVoteCount(topzone_votes);
		}
		
		ThreadPool.scheduleAtFixedRate(new AutoReward(), Config.VOTES_SYSTEM_INITIAL_DELAYTOPZONE, Config.VOTES_SYSTEM_STEP_DELAYTOPZONE);
	}
	
	protected class AutoReward implements Runnable
	{
		@Override
		public void run()
		{
			if (topzone)
			{
				int topzone_votes = gettopzoneVotes();
				if (topzone_votes != -1)
				{
					if (topzone_votes != 0 && topzone_votes >= gettopzoneVoteCount() + Config.VOTES_FOR_REWARDTOPZONE)
					{
						already_rewarded = new ArrayList<>();
						
						Collection<Player> pls = World.getInstance().getPlayers();
						
						for (Player player : pls)
						{
							if (checkSingleBox(player))
							{
								player.addItem("reward", Config.TOPZONE_REWARD_ID, Config.TOPZONE_REWARD_COUNT, player, true);
							}
						}
						
						settopzoneVoteCount(topzone_votes);
					}
					World.toAllOnlinePlayers(new CreatureSay(0, SayType.PARTYROOM_ALL, "TOPZONE", "votes now " + topzone_votes + ".  Next reward at " + (gettopzoneVoteCount() + Config.VOTES_FOR_REWARDTOPZONE) + ""));
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
	
	public static int gettopzoneVotes()
	{
		int votes = -1;
		try
		{
			final URL obj = new URL(Config.VOTES_SITE_TOPZONE_URL1);
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.addRequestProperty("User-Agent", "L2TopZone");
			
			final int responseCode = con.getResponseCode();
			if (responseCode == 200)
			{
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
				{
					String inputLine;
					
					while ((inputLine = in.readLine()) != null)
					{
						if (inputLine.contains("fa fa-fw fa-lg fa-thumbs-up"))
						{
							votes = Integer.parseInt(inputLine.split("fa fa-fw fa-lg fa-thumbs-up\"></i>")[1].split("</span>")[0]);
							break;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Error while getting TOPZONE vote count.");
		}
		
		return votes;
	}
	
	protected static void settopzoneVoteCount(int voteCount)
	{
		topzoneVotesCount = voteCount;
	}
	
	protected static int gettopzoneVoteCount()
	{
		return topzoneVotesCount;
	}
	
	public static Topzone getInstance()
	{
		if (Config.VOTES_SITE_TOPZONE_URL1 != null && !Config.VOTES_SITE_TOPZONE_URL1.equals(""))
		{
			topzone = true;
		}
		
		if (topzone)
			return SingletonHolder._instance;
		return null;
	}
	
	private static class SingletonHolder
	{
		protected static final Topzone _instance = new Topzone();
	}
	
}