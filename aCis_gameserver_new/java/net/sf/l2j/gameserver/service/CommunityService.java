package net.sf.l2j.gameserver.service;

import net.sf.l2j.commons.logging.CLogger;

import net.sf.l2j.gameserver.model.actor.Player;

public interface CommunityService
{
	CLogger LOGGER = new CLogger(CommunityService.class.getName());
	
	void onPlayerRequest(Player player, Object... requestParameters);
	
	void onSuccessfulPurchase(Player player, Object... requestParameters);
}