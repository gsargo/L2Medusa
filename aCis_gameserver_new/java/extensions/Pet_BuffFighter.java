package extensions;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Pet;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;

public class Pet_BuffFighter
{
	private static Logger _log = Logger.getLogger(Pet_BuffFighter.class.getName());
	
	private final int[] _customPet = {35675};
	boolean contains;
	
	private Map<Integer, Player> _players = new HashMap<>();

	public void startTask(Player player) 
	{
		if (!_players.containsKey(player.getObjectId())) 
		{
			_players.put(player.getObjectId(), player);
		}
	}
	
	public void stopTask(Player player) 
	{
		if (_players.containsKey(player.getObjectId())) 
		{
			_players.remove(player.getObjectId());
		}
	}
	
	public static Pet_BuffFighter getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final Pet_BuffFighter _instance = new Pet_BuffFighter();
	}
	
	protected Pet_BuffFighter()
	{
		//ThreadPool.scheduleAtFixedRate(() -> start(), 130000, 130000);		//every 130 seconds
		ThreadPool.scheduleAtFixedRate(() -> start(), 25000, 25000);	
		_log.info("Pet Restore Hp/Mp Task - started");
		
	}
	
	private boolean isCustomPet(int _npcid) 
	{
		contains = IntStream.of(_customPet).anyMatch(x -> x == _npcid);
		if(contains)		
			return true;
		
		return false;
	}
	
	private void restoreHpMp(Player player)
	{
		if(player == null)
			return;
		
		Pet pet = World.getInstance().getPet(player.getObjectId());
			
		if(pet != null && isCustomPet(pet.getNpcId()) && !player.isMounted())
		{
				//Heal visual effect
				MagicSkillUse mgc = new MagicSkillUse(pet, player, 1238, 1, 0, 0);// (Creature cha, int skillId, int skillLevel, int hitTime, int reuseDelay)
				player.sendPacket(mgc);
				SkillTable.getInstance().getInfo(7089, 1).getEffects(pet, player); //blessing of artemis - fighter
				player.getStatus().broadcastStatusUpdate();
		}
	}
	
	
	private void start()
	{
		for (Integer playerId : _players.keySet())
		{
			var player = _players.get(playerId);
			restoreHpMp(player);
		}
	}
}