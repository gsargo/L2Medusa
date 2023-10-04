package net.sf.l2j.gameserver.instance.listener;

import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Monster;

public interface InstanceListener extends EventListener
{
	void onPlayerDeath(Player player, Creature killer);
	
	void onPlayerLeave(Player player);
	
	void onPlayerDisconnect(Player player);
	
	void onMonsterDeath(Monster monster, Creature killer);
	
	void onNpcTalk(Player player, Npc npc);
}