package c1c0s.VoteManagerAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Player;

@VoteSiteInfo(voteSite = VoteSites.L2SERVERSCOM, apiKey = "")
public class L2SERVERSCOM extends VoteRewardSite
{
	
	@Override
	protected String getEndpoint(Player player)
	{
		return String.format("https://www.l2servers.com/api/checkip.php?hash=%s&server_id=%s&ip=%s", player.getIpAddress());
	}
	
	@Override
	protected boolean hasVoted(Player player)
	{
		final String L2SERVERSCOM_API_URL = "https://www.l2servers.com/api/checkip.php?hash=%s&server_id=%s&ip=%s";
		
		try
		{
			final URL obj = new URL(String.format(L2SERVERSCOM_API_URL, Config.HASH_L2SERVERSCOM, Config.SERVERID_L2SERVERSCOM, player.getIpAddress()));
			final HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			
			con.setRequestProperty("User-Agent", "L2SERVERSCOM");
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
			System.out.println("There was a problem on getting vote status on L2SERVERSCOM for player: " + player.getName());
		}
		return false;
	}
	
}