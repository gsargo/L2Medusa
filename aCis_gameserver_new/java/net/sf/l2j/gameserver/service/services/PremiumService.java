package net.sf.l2j.gameserver.service.services;

import java.util.concurrent.TimeUnit;

import net.sf.l2j.commons.data.StatSet;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.service.AbstractCommunityService;

public final class PremiumService extends AbstractCommunityService
{
	private final int durationInDays;
	
	public PremiumService(StatSet set)
	{
		super(set);
		
		durationInDays = set.getInteger("days");
	}
	
	@Override
	public void onSuccessfulPurchase(Player player, Object... requestParameters)
	{
		if (player.isPremium())
		{
			player.setPremiumExpire(player.getPremiumExpire() + TimeUnit.DAYS.toMillis(durationInDays));
			player.sendMessage("Your premium period has been extended by " + durationInDays + " days.");
			return;
		}
		
		player.setPremiumExpire(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(durationInDays));
		player.sendMessage("You have successfully purchased premium status for " + durationInDays + " days.");
	}
}