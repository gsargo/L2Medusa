package c1c0s.VoteManagerAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Player;

@VoteSiteInfo(voteSite = VoteSites.TOP100ARENA, apiKey = "")
public class TOP100ARENA extends VoteRewardSite
{
	
	@Override
	protected String getEndpoint(Player player)
	{
		return String.format("https://www.top100arena.com/check_ip/%s?ip=%s", player.getIpAddress());
	}
	
	@Override
	protected boolean hasVoted(Player player)
	{
		final String TOP100ARENA_API_URL = "https://www.top100arena.com/check_ip/%s?ip=%s";
		
		try
		{
			final URL obj = new URL(String.format(TOP100ARENA_API_URL, Config.SERVERID_TOP100ARENA, player.getIpAddress()));
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.setRequestProperty("User-Agent", "TOP100ARENA");
			con.setConnectTimeout(5000);
			
			final int responseCode = con.getResponseCode();
			if (responseCode == 200)
			{
				final StringBuilder sb = new StringBuilder();
				try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream())))
				{
					String inputLine;
					while ((inputLine = in.readLine()) != null)
					{
						sb.append(inputLine);
					}
				}
				return sb.toString().contains("\"voted\":true");
			}
		}
		catch (Exception e)
		{
			System.out.println("There was a problem on getting vote status on TOP100ARENA for player: " + player.getName());
		}
		return false;
	}
	
}