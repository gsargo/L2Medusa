package c1c0s.VoteManagerAPI;

public class VotedRecord
{
	
	private final String _accountName;
	private final String _ipAddress;
	private final long _dateTimeVoted;
	private final String _voteSiteName;
	
	public VotedRecord(String accountName, String ipAddress, long dateTimeVoted, String voteSiteName)
	{
		_accountName = accountName;
		_ipAddress = ipAddress;
		_dateTimeVoted = dateTimeVoted;
		
		_voteSiteName = voteSiteName;
	}
	
	public long getDateTimeVoted()
	{
		return _dateTimeVoted;
	}
	
	public String getIpAddress()
	{
		return _ipAddress;
	}
	
	public String getAccountName()
	{
		return _accountName;
	}
	
	public String getVoteSiteName()
	{
		return _voteSiteName;
	}
}