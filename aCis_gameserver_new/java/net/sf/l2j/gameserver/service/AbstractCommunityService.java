package net.sf.l2j.gameserver.service;

import java.util.Collections;
import java.util.List;

import net.sf.l2j.commons.data.StatSet;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.multisell.Ingredient;
import net.sf.l2j.gameserver.network.SystemMessageId;

public abstract class AbstractCommunityService implements CommunityService
{
	private final int id;
	private final String description;
	private final List<Ingredient> ingredients;
	
	public AbstractCommunityService(StatSet set)
	{
		id = set.getInteger("id");
		description = set.getString("description", "");
		ingredients = Collections.unmodifiableList(set.getList("ingredients"));
	}
	
	public final int getId()
	{
		return id;
	}
	
	public final String getDescription()
	{
		return description;
	}
	
	public final List<Ingredient> getIngredients()
	{
		return ingredients;
	}
	
	@Override
	public void onPlayerRequest(Player player, Object... requestParameters)
	{
		for (final Ingredient ingredient : ingredients)
		{
			if (player.getInventory().getItemCount(ingredient.getItemId()) < ingredient.getItemCount())
			{
				player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
				return;
			}
		}
		
		for (final Ingredient ingredient : ingredients)
		{
			if (!player.destroyItemByItemId(getClass().getSimpleName(), ingredient.getItemId(), ingredient.getItemCount(), player, true))
				return;
		}
		
		onSuccessfulPurchase(player, requestParameters);
	}
}