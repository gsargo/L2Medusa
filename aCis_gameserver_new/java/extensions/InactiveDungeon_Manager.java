package extensions;


import java.util.logging.Logger;

import net.sf.l2j.commons.pool.ThreadPool;  

import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.type.TimeZone;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class InactiveDungeon_Manager
{
	final NpcHtmlMessage html_to_village_primeval = new NpcHtmlMessage(0);
	final NpcHtmlMessage html_to_village_acropolis = new NpcHtmlMessage(1);
	TimeZone _timezone;
	
	private static Logger _log = Logger.getLogger(InactiveDungeon_Manager.class.getName());

	
	public static InactiveDungeon_Manager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final InactiveDungeon_Manager _instance = new InactiveDungeon_Manager();
	}
	
	protected InactiveDungeon_Manager()
	{
		
		ThreadPool.scheduleAtFixedRate(() -> start(), 1200000, 1200000);	//every 20 minutes	
		_log.info("Inactive Dungeon Manager: started.");
		
	}
	
	private static void teleToStage(Player player) // teleport back to town method
	{
		if(player!=null)
		{
			int x = 83290;
			int y = 148027;
			int z = -3400;
			player.teleportTo(x, y, z, 50);
		}
	}
	
	private void start()
	{
		for (Player players : World.getInstance().getPlayers())
		{	
			if( (players.isInsideSoloDungeonZone() || players.isInsidePartyDungeonZone() ) && players.getDungeon() ==null && !players.isInCombat() && !players.isGM())
			{
				ThreadPool.scheduleAtFixedRate(() -> teleToStage(players) , 30000,30000);
			}			
		}	
	}
		
}