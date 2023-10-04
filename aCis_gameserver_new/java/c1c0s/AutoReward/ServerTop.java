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

public class ServerTop
{
	public static Logger _log = Logger.getLogger(ServerTop.class.getName());
	
	private static int servertopVotesCount = 0;
	protected List<String> already_rewarded;
	
	protected static boolean servertop = false;
	
	private ServerTop()
	{
		_log.info("Auto Reward Manager: SERVERTOP Loaded Successfully");
		if (servertop)
		{
			int servertop_votes = getservertopVotes();
			if (servertop_votes == -1)
			{
				servertop_votes = 0;
			}
			
			setservertopVoteCount(servertop_votes);
		}
		
		ThreadPool.scheduleAtFixedRate(new AutoReward(), Config.VOTES_SYSTEM_INITIAL_DELAYSERVERTOP, Config.VOTES_SYSTEM_STEP_DELAYSERVERTOP);
	}
	
	protected class AutoReward implements Runnable
	{
		@Override
		public void run()
		{
			if (servertop)
			{
				int servertop_votes = getservertopVotes();
				if (servertop_votes != -1)
				{
					if (servertop_votes != 0 && servertop_votes >= getservertopVoteCount() + Config.VOTES_FOR_REWARDSERVERTOP)
					{
						already_rewarded = new ArrayList<>();
						
						Collection<Player> pls = World.getInstance().getPlayers();
						
						for (Player player : pls)
						{
							if (checkSingleBox(player))
							{
								player.addItem("reward", Config.SERVERTOP_REWARD_ID, Config.SERVERTOP_REWARD_COUNT, player, true);
							}
						}
						
						setservertopVoteCount(servertop_votes);
					}
					World.toAllOnlinePlayers(new CreatureSay(0, SayType.PARTYROOM_ALL, "SERVERTOP", "votes now " + servertop_votes + ".  Next reward at " + (getservertopVoteCount() + Config.VOTES_FOR_REWARDSERVERTOP) + ""));
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
	
	public static int getservertopVotes()
	{
		int votes = -1;
		try
		{
			final URL obj = new URL(Config.VOTES_SITE_servertop_URL1);
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.addRequestProperty("User-Agent", "SERVERTOP");
			
			final int responseCode = con.getResponseCode();
			if (responseCode == 200)
			{
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
				{
					String inputLine;
					
					while ((inputLine = in.readLine()) != null)
					{
						if (inputLine.contains("<td><span class=\"vot_sv\">"))
						{
							votes = Integer.parseInt(inputLine.split("<td><span class=\"vot_sv\">")[1].split("</span></td>")[0]);
							break;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Error while getting SERVERTOP vote count.");
		}
		
		return votes;
	}
	
	protected static void setservertopVoteCount(int voteCount)
	{
		servertopVotesCount = voteCount;
	}
	
	protected static int getservertopVoteCount()
	{
		return servertopVotesCount;
	}
	
	public static ServerTop getInstance()
	{
		if (Config.VOTES_SITE_servertop_URL1 != null && !Config.VOTES_SITE_servertop_URL1.equals(""))
		{
			servertop = true;
		}
		
		if (servertop)
			return SingletonHolder._instance;
		return null;
	}
	
	private static class SingletonHolder
	{
		protected static final ServerTop _instance = new ServerTop();
	}
	
}