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

public class Hopzone
{
	public static Logger _log = Logger.getLogger(Hopzone.class.getName());
	
	private int hopzoneVotesCount = 0;
	protected List<String> already_rewarded;
	
	protected static boolean hopzone = false;
	
	private Hopzone()
	{
		_log.info("Auto Reward Manager: HOPZONE Loaded Successfully");
		if (hopzone)
		{
			int hopzone_votes = getHopZoneVotes();
			if (hopzone_votes == -1)
			{
				hopzone_votes = 0;
			}
			
			setHopZoneVoteCount(hopzone_votes);
		}
		
		ThreadPool.scheduleAtFixedRate(new AutoReward(), Config.VOTES_SYSTEM_INITIAL_DELAYHOPZONE, Config.VOTES_SYSTEM_STEP_DELAYHOPZONE);
	}
	
	protected class AutoReward implements Runnable
	{
		@Override
		public void run()
		{
			if (hopzone)
			{
				int hopzone_votes = getHopZoneVotes();
				if (hopzone_votes != -1)
				{
					if (hopzone_votes != 0 && hopzone_votes >= getHopZoneVoteCount() + Config.VOTES_FOR_REWARDHOPZONE)
					{
						already_rewarded = new ArrayList<>();
						
						Collection<Player> pls = World.getInstance().getPlayers();
						
						for (Player player : pls)
						{
							if (checkSingleBox(player))
							{
								player.addItem("reward", Config.HOPZONE_REWARD_ID, Config.HOPZONE_REWARD_COUNT, player, true);
							}
						}
						
						setHopZoneVoteCount(hopzone_votes);
					}
					World.toAllOnlinePlayers(new CreatureSay(0, SayType.PARTYROOM_ALL, "HOPZONE", "votes now " + hopzone_votes + ".  Next reward at " + (getHopZoneVoteCount() + Config.VOTES_FOR_REWARDHOPZONE) + ""));
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
	
	protected int getHopZoneVotes()
	{
		int votes = -1;
		try
		{
			final URL obj = new URL(Config.VOTES_SITE_HOPZONE_URL1);
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.addRequestProperty("User-Agent", "L2Hopzone");
			con.setConnectTimeout(5000);
			
			final int responseCode = con.getResponseCode();
			if (responseCode == 200)
			{
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
				{
					String inputLine;
					
					while ((inputLine = in.readLine()) != null)
					{
						if (inputLine.contains(":server-votes=\""))
						{
							votes = Integer.parseInt(inputLine.split(":server-votes=\"")[1].split("\"")[0]);
							break;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
			System.out.println("Error while getting HOPZONE vote count.");
		}
		
		return votes;
	}
	
	protected void setHopZoneVoteCount(int voteCount)
	{
		hopzoneVotesCount = voteCount;
	}
	
	protected int getHopZoneVoteCount()
	{
		return hopzoneVotesCount;
	}
	
	public static Hopzone getInstance()
	{
		if (Config.VOTES_SITE_HOPZONE_URL1 != null && !Config.VOTES_SITE_HOPZONE_URL1.equals(""))
		{
			hopzone = true;
		}
		
		if (hopzone)
			return SingletonHolder._instance;
		return null;
	}
	
	private static class SingletonHolder
	{
		protected static final Hopzone _instance = new Hopzone();
	}
	
}