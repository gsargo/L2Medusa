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
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;

public class Pet_BuffMage
{
	private static Logger _log = Logger.getLogger(Pet_BuffMage.class.getName());
	
	private final int[] _customPet = {35680};
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
	
	public static Pet_BuffMage getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final Pet_BuffMage _instance = new Pet_BuffMage();
	}
	
	protected Pet_BuffMage()
	{
		//ThreadPool.scheduleAtFixedRate(() -> start(), 170000, 170000);		//every 170 seconds
		ThreadPool.scheduleAtFixedRate(() -> start(), 10000, 10000);	
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
				MagicSkillUse mgc = new MagicSkillUse(pet, player, 1327, 1, 3, 0);// (Creature cha, int skillId, int skillLevel, int hitTime, int reuseDelay)
				player.sendPacket(mgc);
				SkillTable.getInstance().getInfo(9976, 1).getEffects(pet, player); //gift of artemis - mage
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