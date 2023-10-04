package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;

import Base.Dungeon.Dungeon;

public class DungeonMob extends Monster
{
	private Dungeon dungeon;
	
	public DungeonMob(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		if (dungeon != null) // It will be dungeon == null when mob is spawned from admin and not from dungeon
			ThreadPool.schedule(() -> dungeon.onMobKill(this), 1000 * 2);
		
		return true;
	}
	
	public void setDungeon(Dungeon dungeon)
	{
		this.dungeon = dungeon;
	}
}