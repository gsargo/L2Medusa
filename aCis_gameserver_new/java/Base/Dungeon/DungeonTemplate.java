package Base.Dungeon;

import java.util.Map;

public class DungeonTemplate
{
	private int id;
	private String name;
	private int players;
	private Map<Integer, Integer> rewards;
	private String rewardHtm;
	private Map<Integer, DungeonStage> stages;
	
	public DungeonTemplate(int id, String name, int players, Map<Integer, Integer> rewards, String rewardHtm, Map<Integer, DungeonStage> stages)
	{
		this.id = id;
		this.name = name;
		this.players = players;
		this.rewards = rewards;
		this.rewardHtm = rewardHtm;
		this.stages = stages;
	}
	
	public int getId()
	{
		return id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getPlayers()
	{
		return players;
	}
	
	public Map<Integer, Integer> getRewards()
	{
		return rewards;
	}
	
	public String getRewardHtm()
	{
		return rewardHtm;
	}
	
	public Map<Integer, DungeonStage> getStages()
	{
		return stages;
	}
}