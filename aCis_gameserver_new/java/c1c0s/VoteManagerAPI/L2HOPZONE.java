package c1c0s.VoteManagerAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Player;

@VoteSiteInfo(voteSite = VoteSites.L2HOPZONE, apiKey = "")
public class L2HOPZONE extends VoteRewardSite
{
	
	@Override
	protected String getEndpoint(Player player)
	{
		return String.format("https://api.hopzone.net/lineage2/vote?token=%s&ip_address=%s", player.getIpAddress());
	}
	
	@Override
	protected boolean hasVoted(Player player)
	{
		final String L2HOPZONE_API_URL = "https://api.hopzone.net/lineage2/vote?token=%s&ip_address=%s";
		
		try
		{
			final URL obj = new URL(String.format(L2HOPZONE_API_URL, Config.APIKEY_HOPZONE, player.getIpAddress()));
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.setRequestProperty("User-Agent", "L2HOPZONE");
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
				return sb.toString().contains("true");
			}
		}
		catch (Exception e)
		{
			System.out.println("There was a problem on getting vote status on L2HOPZONE for player: " + player.getName());
		}
		return false;
	}
	
}