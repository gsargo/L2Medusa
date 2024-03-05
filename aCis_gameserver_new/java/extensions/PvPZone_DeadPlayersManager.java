package extensions;


import java.util.logging.Logger;

import net.sf.l2j.commons.pool.ThreadPool;  

import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.zone.type.TimeZone;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class PvPZone_DeadPlayersManager
{
	final NpcHtmlMessage html_to_village_primeval = new NpcHtmlMessage(0);
	final NpcHtmlMessage html_to_village_acropolis = new NpcHtmlMessage(1);
	TimeZone _timezone;
	
	private static Logger _log = Logger.getLogger(PvPZone_DeadPlayersManager.class.getName());

	
	public static PvPZone_DeadPlayersManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final PvPZone_DeadPlayersManager _instance = new PvPZone_DeadPlayersManager();
	}
	
	protected PvPZone_DeadPlayersManager()
	{
		
			ThreadPool.scheduleAtFixedRate(() -> start(), 400000, 400000);		//every 6-7 minutes
		_log.info("PvP Zone - To Village Manager: started.");
		
	}
	
	private void start()
	{
	//if(_timezone.isOpen())
		//{
		html_to_village_primeval.setFile("data/html/ondie/pvparea_no_killer.htm");
		html_to_village_acropolis.setFile("data/html/ondie/clanarena_no_killer.htm");
		
		for (Player players : World.getInstance().getPlayers())
		{	
			if(players.isInsidePvPZone() && players.isDead())
			{
				if(players.isInsideClanwarZone())
					players.sendPacket(html_to_village_acropolis); 
				else
					players.sendPacket(html_to_village_primeval); 
			}
			
			
		}
		
	}
		//}

}