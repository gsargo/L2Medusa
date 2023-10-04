package net.sf.l2j.gameserver.reward;

import net.sf.l2j.commons.data.StatSet;

import net.sf.l2j.gameserver.data.xml.ItemData;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.kind.Item;

public final class ItemReward implements Reward
{
	private int itemId;
	private int itemCount;
	
	public ItemReward(StatSet set)
	{
		itemId = set.getInteger("id");
		itemCount = set.getInteger("count", 1);
	}
	
	public final int getItemId()
	{
		return itemId;
	}
	
	public final int getItemCount()
	{
		return itemCount;
	}
	
	@Override
	public void process(Player player)
	{
		player.addItem(getClass().getSimpleName(), itemId, itemCount, player, true);
	}
	
	@Override
	public String toString()
	{
		final Item item = ItemData.getInstance().getTemplate(itemId);
		return item.getName() + " x" + itemCount;
	}
}