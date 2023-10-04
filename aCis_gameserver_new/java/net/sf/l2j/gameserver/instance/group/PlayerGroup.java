package net.sf.l2j.gameserver.instance.group;

import net.sf.l2j.gameserver.instance.actor.PlayerAdapter;
import net.sf.l2j.gameserver.instance.model.InstanceMap;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.gameserver.network.serverpackets.L2GameServerPacket;

public final class PlayerGroup extends AbstractGroup<PlayerAdapter>
{
	private final int instanceId;
	
	public PlayerGroup(int instanceId)
	{
		this.instanceId = instanceId;
	}
	
	public final int getInstanceId()
	{
		return instanceId;
	}
	
	public final void broadcastPacket(L2GameServerPacket packet)
	{
		findAll().forEach(player -> player.sendPacket(packet));
	}
	
	public final void broadcastMessage(String message)
	{
		findAll().forEach(player -> player.sendMessage(message));
	}
	
	public final void broadcastScreenMessage(String message, int time)
	{
		broadcastPacket(new ExShowScreenMessage(message, time));
	}
	
	public final void teleportPlayersToMap(InstanceMap instanceMap)
	{
		for (final PlayerAdapter player : findAll())
		{
			player.clearCoolDowns();
			player.heal();
			player.setInstanceId(getInstanceId());
		}
	}
	
	public final void teleportPlayersBack()
	{
		for (final PlayerAdapter player : findAll())
		{
			player.setGameInstance(null);
			player.clearCoolDowns();
			player.heal();
			player.setInstanceId(0);
			player.broadcastUserInfo();
			player.teleToLocation(Location.TOWN_LOCATION);
		}
	}
}