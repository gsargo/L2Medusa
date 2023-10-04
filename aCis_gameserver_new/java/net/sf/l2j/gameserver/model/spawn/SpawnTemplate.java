package net.sf.l2j.gameserver.model.spawn;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.model.location.SpawnLocation;

public final class SpawnTemplate
{
	private final int npcId;
	private final int respawnDelay;
	private final SpawnLocation location;
	
	public SpawnTemplate(StatSet set)
	{
		npcId = set.getInteger("id");
		respawnDelay = set.getInteger("respawn_delay", 0);
		location = new SpawnLocation(set.getInteger("x"), set.getInteger("y"), set.getInteger("z"), set.getInteger("heading", Rnd.get(65535)));
	}
	
	public final int getNpcId()
	{
		return npcId;
	}
	
	public final int getRespawnDelay()
	{
		return respawnDelay;
	}
	
	public final SpawnLocation getLocation()
	{
		return location;
	}
}