package c1c0s.VoteManagerAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Player;

@VoteSiteInfo(voteSite = VoteSites.L2ITOPZ, apiKey = "")
public class L2ITOPZ extends VoteRewardSite
{
	
	@Override
	protected String getEndpoint(Player player)
	{
		return String.format("https://itopz.com/check/%s/%s/%s/", player.getIpAddress());
	}
	
	@Override
	protected boolean hasVoted(Player player)
	{
		final String L2ITOPZ_API_URL = "https://itopz.com/check/%s/%s/%s/";
		
		try
		{
			final URL obj = new URL(String.format(L2ITOPZ_API_URL, Config.APIKEY_ITOPZ, Config.SERVERID_ITOPZ, player.getIpAddress()));
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.setRequestProperty("User-Agent", "ITOPZ");
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
				return sb.toString().contains("\"isVoted\": \"TRUE\",");
			}
		}
		catch (Exception e)
		{
			System.out.println("There was a problem on getting vote status on L2ITOPZ for player: " + player.getName());
		}
		return false;
	}
	
}