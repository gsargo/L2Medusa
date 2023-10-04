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

public class GameTopsEU
{
	public static Logger _log = Logger.getLogger(GameTopsEU.class.getName());
	
	private int GametopseuVotesCount = 0;
	protected List<String> already_rewarded;
	
	protected static boolean Gametopseu = false;
	
	private GameTopsEU()
	{
		_log.info("Auto Reward Manager: GAMETOPSEU Loaded Successfully");
		if (Gametopseu)
		{
			int Gametopseu_votes = getGametopseuVotes();
			if (Gametopseu_votes == -1)
			{
				Gametopseu_votes = 0;
			}
			
			setGametopseuVoteCount(Gametopseu_votes);
		}
		
		ThreadPool.scheduleAtFixedRate(new AutoReward(), Config.VOTES_SYSTEM_INITIAL_DELAYGAMETOPSEU, Config.VOTES_SYSTEM_STEP_DELAYGAMETOPSEU);
		
	}
	
	protected class AutoReward implements Runnable
	{
		@Override
		public void run()
		{
			if (Gametopseu)
			{
				int Gametopseu_votes = getGametopseuVotes();
				if (Gametopseu_votes != -1)
				{
					if (Gametopseu_votes != 0 && Gametopseu_votes >= getGametopseuVoteCount() + Config.GAMETOPSEUVOTES_FOR_REWARD)
					{
						already_rewarded = new ArrayList<>();
						
						Collection<Player> pls = World.getInstance().getPlayers();
						
						for (Player player : pls)
						{
							if (checkSingleBox(player))
							{
								player.addItem("reward", Config.Gametopseu_REWARD_ID, Config.Gametopseu_REWARD_COUNT, player, true);
							}
						}
						
						setGametopseuVoteCount(Gametopseu_votes);
					}
					World.toAllOnlinePlayers(new CreatureSay(0, SayType.PARTYROOM_ALL, "GAMETOPSEU", "votes now " + Gametopseu_votes + ".  Next reward at " + (getGametopseuVoteCount() + Config.GAMETOPSEUVOTES_FOR_REWARD) + ""));
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
	
	protected int getGametopseuVotes()
	{
		int votes = -1;
		try
		{
			final URL obj = new URL(Config.VOTES_SITE_Gametopseu_URL1);
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.addRequestProperty("User-Agent", "L2TOPCO");
			
			final int responseCode = con.getResponseCode();
			if (responseCode == 200)
			{
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
				{
					String inputLine;
					while ((inputLine = in.readLine()) != null)
					{
						votes = Integer.valueOf(inputLine);
						break;
					}
				}
			}
		}
		catch (Exception e)
		{
			System.out.println(e);
			System.out.println("Error while getting GAMETOPSEU vote count.");
		}
		
		return votes;
	}
	
	protected void setGametopseuVoteCount(int voteCount)
	{
		GametopseuVotesCount = voteCount;
	}
	
	protected int getGametopseuVoteCount()
	{
		return GametopseuVotesCount;
	}
	
	public static GameTopsEU getInstance()
	{
		if (Config.VOTES_SITE_Gametopseu_URL1 != null && !Config.VOTES_SITE_Gametopseu_URL1.equals(""))
		{
			Gametopseu = true;
		}
		
		if (Gametopseu)
			return SingletonHolder._instance;
		return null;
	}
	
	private static class SingletonHolder
	{
		protected static final GameTopsEU _instance = new GameTopsEU();
	}
	
}