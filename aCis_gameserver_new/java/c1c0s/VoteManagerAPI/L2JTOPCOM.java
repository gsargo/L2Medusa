package c1c0s.VoteManagerAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Player;

@VoteSiteInfo(voteSite = VoteSites.L2JTOPCOM, apiKey = "")
public class L2JTOPCOM extends VoteRewardSite
{
	
	@Override
	protected String getEndpoint(Player player)
	{
		return String.format("https://l2jtop.com/api/%s/ip/%s/", player.getIpAddress());
	}
	
	@Override
	protected boolean hasVoted(Player player)
	{
		final String L2JTOPCOM_API_URL = "https://l2jtop.com/api/%s/ip/%s/";
		
		try
		{
			final URL obj = new URL(String.format(L2JTOPCOM_API_URL, Config.APIKEY_L2JTOPCOM, player.getIpAddress()));
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.setRequestProperty("User-Agent", "L2JTOPCOM");
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
				return sb.toString().contains("is_voted\":true");
			}
		}
		catch (Exception e)
		{
			System.out.println("There was a problem on getting vote status on L2JTOPCOM for player: " + player.getName());
		}
		return false;
	}
	
}