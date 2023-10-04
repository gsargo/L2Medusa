package net.sf.l2j.util;

/**
 * @author Rouxy
 */
public class RewardHolder
{
	private int itemId;
	private int count;
	private int chance;
	
	public RewardHolder(int itemId, int count)
	{
		this.itemId = itemId;
		this.count = count;
		this.chance = 100;
	}
	
	public int getItemId()
	{
		return itemId;
	}
	
	public void setItemId(int itemId)
	{
		this.itemId = itemId;
	}
	
	public int getCount()
	{
		return count;
	}
	
	public void setCount(int count)
	{
		this.count = count;
	}
	
	public int getChance()
	{
		return chance;
	}
	
	public void setChance(int chance)
	{
		this.chance = chance;
	}
	
}
