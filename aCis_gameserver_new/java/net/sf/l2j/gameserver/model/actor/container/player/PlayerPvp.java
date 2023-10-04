package net.sf.l2j.gameserver.model.actor.container.player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.network.serverpackets.UserInfo;

/**
 * @author Giorgos
 */
public class PlayerPvp
{
	private Player _player;
	private Map<Integer, Long> _antiFeed = new ConcurrentHashMap<>();
	
	public PlayerPvp(Player player)
	{
		_player = player;
	}
	
	public void check(Player victim)
	{
		// System.out.println("Test log");
		// final int id = victim.getObjectId();
		
		// if (!canBeRewarded(id))
		// return;
		
		// Update pvp map data.
		// _antiFeed.put(id, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1));
		
		// Add PvP point to attacker.
		_player.setPvpKills(_player.getPvpKills() + 1);
		
		// Add PvP Trophy.
		// _player.addItem("Loot", 5592, 5, _player, true);
		// _player.sendMessage("You won a PvP Trophy for a pvp kill!");
		
		// Send UserInfo packet to attacker with its Karma and PK Counter.
		_player.sendPacket(new UserInfo(_player));
	}
	
	/*
	 * private boolean canBeRewarded(int id) { return !_antiFeed.containsKey(id) || System.currentTimeMillis() > _antiFeed.get(id); }
	 */
}
