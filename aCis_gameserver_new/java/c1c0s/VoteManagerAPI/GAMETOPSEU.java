package c1c0s.VoteManagerAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Player;

@VoteSiteInfo(voteSite = VoteSites.GAMETOPSEU, apiKey = "")
public class GAMETOPSEU extends VoteRewardSite
{
	
	@Override
	protected String getEndpoint(Player player)
	{
		return String.format("https://gametops.eu/api.php?server=%s&type=voted&ip=%s", player.getIpAddress());
	}
	
	@Override
	protected boolean hasVoted(Player player)
	{
		final String GAMETOPSEU_API_URL = "https://gametops.eu/api.php?server=%s&type=voted&ip=%s";
		
		try
		{
			final URL obj = new URL(String.format(GAMETOPSEU_API_URL, Config.SERVERID_GAMETOPSEU, player.getIpAddress()));
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.setRequestProperty("User-Agent", "GAMETOPSEU");
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
				return sb.toString().contains("TRUE");
			}
		}
		catch (Exception e)
		{
			System.out.println("There was a problem on getting vote status on GAMETOPSEU for player: " + player.getName());
		}
		return false;
	}
	
}