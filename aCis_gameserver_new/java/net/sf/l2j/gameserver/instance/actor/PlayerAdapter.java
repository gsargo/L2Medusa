package net.sf.l2j.gameserver.instance.actor;

import java.util.concurrent.atomic.AtomicInteger;

import net.sf.l2j.gameserver.instance.AbstractInstance;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.network.serverpackets.L2GameServerPacket;
import net.sf.l2j.gameserver.network.serverpackets.SkillCoolTime;
import net.sf.l2j.gameserver.reward.Reward;

public class PlayerAdapter
{
	private final Player player;
	private final AtomicInteger kills = new AtomicInteger(0);
	
	public PlayerAdapter(Player player)
	{
		this.player = player;
	}
	
	public final Player getPlayer()
	{
		return player;
	}
	
	public final int getKills()
	{
		return kills.get();
	}
	
	public final void increaseKills()
	{
		kills.incrementAndGet();
	}
	
	public String getName()
	{
		return player.getName();
	}
	
	public final boolean isOnline()
	{
		return player != null && player.isOnline();
	}
	
	public final void setInstanceId(int instanceId)
	{
		player.setInstanceId(instanceId);
	}
	
	public final int getInstanceId()
	{
		return player.getInstanceId();
	}
	
	public final void sendPacket(L2GameServerPacket packet)
	{
		player.sendPacket(packet);
	}
	
	public final void sendMessage(String message)
	{
		player.sendMessage(message);
	}
	
	public final void broadcastUserInfo()
	{
		player.broadcastUserInfo();
	}
	
	public final void teleToLocation(Location location)
	{
		player.teleToLocation(location);
	}
	
	public final boolean isDead()
	{
		return player.isDead();
	}
	
	public final void doRevive()
	{
		player.doRevive();
	}
	
	public final void clearCoolDowns()
	{
		player.getReuseTimeStamp().clear();
		player.getDisabledSkills().clear();
		player.sendPacket(new SkillCoolTime(player));
	}
	
	public final void heal()
	{
		if (player.isDead())
			player.doRevive();
		
		player.getStatus().setHpMp(player.getStatus().getMaxHp(), player.getStatus().getMaxMp());
		player.getStatus().setCp(player.getStatus().getMaxCp());
	}
	
	public final void setGameInstance(AbstractInstance gameInstance)
	{
		player.setGameInstance(gameInstance);
	}
	
	public void processReward(Reward reward)
	{
		reward.process(player);
	}
	
	@Override
	public final int hashCode()
	{
		return player.getObjectId();
	}
}