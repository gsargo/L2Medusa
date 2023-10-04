package c1c0s.VoteManagerAPI;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VoteApiService
{
	private static final Logger LOGGER = Logger.getLogger(VoteApiService.class.getName());
	
	public static String getApiResponse(String endpoint)
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		try
		{
			URL url = new URL(endpoint);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.addRequestProperty("User-Agent", "Mozilla/4.76");
			connection.setRequestMethod("GET");
			
			connection.setReadTimeout(5 * 1000);
			connection.connect();
			
			if (connection.getResponseCode() != 200)
			{
				LOGGER.log(Level.WARNING, "VoteApiService getApiResponse returned " + connection.getResponseCode());
				connection.disconnect();
				return "";
			}
			
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream())))
			{
				String line;
				while ((line = reader.readLine()) != null)
				{
					stringBuilder.append(line).append("\n");
				}
			}
			connection.disconnect();
			return stringBuilder.toString();
		}
		catch (Exception e)
		{
			LOGGER.log(Level.SEVERE, "Something went wrong in VoteApiService getApiResponse", e);
			return "";
		}
	}
}