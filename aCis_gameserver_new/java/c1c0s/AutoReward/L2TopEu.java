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

public class L2TopEu
{
	public static Logger _log = Logger.getLogger(L2TopEu.class.getName());
	
	private static int L2TOPEUVotesCount = 0;
	protected List<String> already_rewarded;
	
	protected static boolean L2TOPEU = false;
	
	private L2TopEu()
	{
		_log.info("Auto Reward Manager: L2TOPEU Loaded Successfully");
		if (L2TOPEU)
		{
			int L2TOPEU_votes = getL2TOPEUVotes();
			if (L2TOPEU_votes == -1)
			{
				L2TOPEU_votes = 0;
			}
			
			setL2TOPEUVoteCount(L2TOPEU_votes);
		}
		
		ThreadPool.scheduleAtFixedRate(new AutoReward(), Config.VOTES_SYSTEM_INITIAL_DELAYL2TOPEU, Config.VOTES_SYSTEM_STEP_DELAYL2TOPEU);
	}
	
	protected class AutoReward implements Runnable
	{
		@Override
		public void run()
		{
			if (L2TOPEU)
			{
				int L2TOPEU_votes = getL2TOPEUVotes();
				if (L2TOPEU_votes != -1)
				{
					if (L2TOPEU_votes != 0 && L2TOPEU_votes >= getL2TOPEUVoteCount() + Config.VOTES_FOR_REWARDL2TOPEU)
					{
						already_rewarded = new ArrayList<>();
						
						Collection<Player> pls = World.getInstance().getPlayers();
						
						for (Player player : pls)
						{
							if (checkSingleBox(player))
							{
								player.addItem("reward", Config.L2TOPEU_REWARD_ID, Config.L2TOPEU_REWARD_COUNT, player, true);
							}
						}
						
						setL2TOPEUVoteCount(L2TOPEU_votes);
					}
					World.toAllOnlinePlayers(new CreatureSay(0, SayType.PARTYROOM_ALL, "L2TOPEU", "votes now " + L2TOPEU_votes + ".  Next reward at " + (getL2TOPEUVoteCount() + Config.VOTES_FOR_REWARDL2TOPEU) + ""));
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
	
	public static int getL2TOPEUVotes()
	{
		int votes = -1;
		try
		{
			final URL obj = new URL(Config.VOTES_SITE_L2TOPEU_URL1);
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.addRequestProperty("User-Agent", "L2TOPEU");
			
			final int responseCode = con.getResponseCode();
			if (responseCode == 200)
			{
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
				{
					String inputLine;
					
					while ((inputLine = in.readLine()) != null)
					{
						if (inputLine.contains("\"votes\":\""))
						{
							votes = Integer.parseInt(inputLine.split("\"votes\":\"")[1].split("\"")[0]);
							break;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Error while getting L2TOPEU vote count.");
		}
		
		return votes;
	}
	
	protected static void setL2TOPEUVoteCount(int voteCount)
	{
		L2TOPEUVotesCount = voteCount;
	}
	
	protected static int getL2TOPEUVoteCount()
	{
		return L2TOPEUVotesCount;
	}
	
	public static L2TopEu getInstance()
	{
		if (Config.VOTES_SITE_L2TOPEU_URL1 != null && !Config.VOTES_SITE_L2TOPEU_URL1.equals(""))
		{
			L2TOPEU = true;
		}
		
		if (L2TOPEU)
			return SingletonHolder._instance;
		return null;
	}
	
	private static class SingletonHolder
	{
		protected static final L2TopEu _instance = new L2TopEu();
	}
	
}