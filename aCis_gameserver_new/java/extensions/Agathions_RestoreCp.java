package extensions;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import net.sf.l2j.commons.pool.ThreadPool;


import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Pet;

import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;

public class Agathions_RestoreCp
{
	private static Logger _log = Logger.getLogger(Agathions_RestoreCp.class.getName());
	
	private Map<Integer, Player> _players = new HashMap<>();
	
	private final int[] _ExcludedAgat = {41027,41029,41030}; // exclude low level agaths 41027,41029,41030
	boolean contains;

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
	
	public static Agathions_RestoreCp getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final Agathions_RestoreCp _instance = new Agathions_RestoreCp();
	}
	
	protected Agathions_RestoreCp()
	{
		//ThreadPool.scheduleAtFixedRate(() -> start(), 45000, 45000);		//every 45 seconds
		ThreadPool.scheduleAtFixedRate(() -> start(), 10000, 10000);	
		_log.info("Aghathion Restore CP Task - started");
		
	}
	
	private boolean isExcludedAgat(int _npcid) 
	{
		contains = IntStream.of(_ExcludedAgat).anyMatch(x -> x == _npcid);
		if(contains)		
			return true;
		
		return false;
	}
	
	private void restoreCP(Player player)
	{
		if(player == null)
			return;
		
		Pet agathion = World.getInstance().getAgat(player.getObjectId());
		
		if(agathion != null && !isExcludedAgat(agathion.getNpcId()) && (player.getStatus().getCp() != player.getStatus().getMaxCp()))
		{
				//Honor of Paagrio visual effect
				MagicSkillUse mgc = new MagicSkillUse(agathion, player, 1306, 1, 1, 0);// (Creature cha, int skillId, int skillLevel, int hitTime, int reuseDelay)
				player.sendPacket(mgc);
				player.getStatus().setCp(player.getStatus().getCp() + 200);
				player.getStatus().broadcastStatusUpdate();
		}
	}
	
	
	private void start()
	{
		for (Integer playerId : _players.keySet())
		{
			var player = _players.get(playerId);
			restoreCP(player);
		}
	}
}