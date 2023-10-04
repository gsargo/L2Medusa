package c1c0s.VoteManagerAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Player;

@VoteSiteInfo(voteSite = VoteSites.L2TOPZONE, apiKey = "")
public class L2TOPZONE extends VoteRewardSite
{
	
	@Override
	protected String getEndpoint(Player player)
	{
		return String.format("https://api.l2topzone.com/v1/vote?token=%s&ip=%s", player.getIpAddress());
	}
	
	@Override
	protected boolean hasVoted(Player player)
	{
		final String L2TOPZONE_API_URL = "https://api.l2topzone.com/v1/vote?token=%s&ip=%s";
		
		try
		{
			final URL obj = new URL(String.format(L2TOPZONE_API_URL, Config.API_KEYTOPZONE, player.getIpAddress()));
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.setRequestProperty("User-Agent", "L2TOPZONE");
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
				return sb.toString().contains("isVoted\":true");
			}
		}
		catch (Exception e)
		{
			System.out.println("There was a problem on getting vote status on L2TOPZONE for player: " + player.getName());
		}
		return false;
	}
	
}