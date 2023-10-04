package net.sf.l2j.gameserver.instance.raid;

import net.sf.l2j.gameserver.instance.AbstractInstance;
import net.sf.l2j.gameserver.instance.InstanceType;
import net.sf.l2j.gameserver.instance.group.SpawnGroup;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Monster;

public final class RaidBossInstance extends AbstractInstance
{
	public RaidBossInstance()
	{
	}
	
	@Override
	public void start()
	{
		super.start();
	}
	
	@Override
	public void onPlayerDeath(Player player, Creature killer)
	{
	}
	
	@Override
	public void onMonsterDeath(Monster monster, Creature killer)
	{
		final SpawnGroup spawnGroup = getSpawnGroup();
		if (spawnGroup.getAliveMonstersCount() == 0)
			end();
	}
	
	@Override
	public void processRewards()
	{
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.RAID;
	}
}