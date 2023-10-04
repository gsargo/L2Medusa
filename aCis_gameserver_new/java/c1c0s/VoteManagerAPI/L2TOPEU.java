package c1c0s.VoteManagerAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Player;

@VoteSiteInfo(voteSite = VoteSites.L2TOPEU, apiKey = "")
public class L2TOPEU extends VoteRewardSite
{
	
	@Override
	protected String getEndpoint(Player player)
	{
		return String.format("https://l2top.eu/voted/%s/%s", player.getIpAddress());
	}
	
	@Override
	protected boolean hasVoted(Player player)
	{
		final String L2TOPEU_API_URL = "https://l2top.eu/voted/%s/%s";
		
		try
		{
			final URL obj = new URL(String.format(L2TOPEU_API_URL, Config.SERVERID_L2TOPEU, player.getIpAddress()));
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.setRequestProperty("User-Agent", "L2TOPEU");
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
				return sb.toString().contains("voted\":true");
			}
		}
		catch (Exception e)
		{
			System.out.println("There was a problem on getting vote status on L2TOPEU for player: " + player.getName());
		}
		return false;
	}
	
}