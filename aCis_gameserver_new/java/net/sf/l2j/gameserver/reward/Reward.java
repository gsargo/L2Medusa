package net.sf.l2j.gameserver.reward;

import net.sf.l2j.gameserver.model.actor.Player;

public interface Reward
{
	void process(Player player);
}