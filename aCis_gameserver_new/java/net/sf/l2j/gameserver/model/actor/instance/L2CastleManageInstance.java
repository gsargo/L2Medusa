package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
//import net.sf.l2j.gameserver.instancemanager.CastleManager;
import net.sf.l2j.gameserver.model.entity.Castle;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.network.serverpackets.SiegeInfo;

public class L2CastleManageInstance extends SiegeNpc // extends Folk ?
{
	
	public static int castleId = 0;
	
	public L2CastleManageInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (player == null)
			return;
		
		/*
		 * if (!canTarget(player)) return;
		 */
		
		else if (command.startsWith("siege"))
		{
			Castle castle = CastleManager.getInstance().getCastleById(Integer.parseInt(command.substring(6)));
			if (castle != null)
				player.sendPacket(new SiegeInfo(castle));
			
			// the problem is that i cant find WHERE to pass this id in order to retrieve the appropriate html for the desired castle.
			// i wish to make an AIO castle manager.still cant find which html is for castle siege registration attack / defend. always 0 ? what do u mean , i just initialized it.
			// Castle castle = castle.getCastleId();
			
			// (OLD) Castle castle = CastleManager.getInstance().getCastleById(castleId);
			/*
			 * if(castleId>0) player.sendPacket(new SiegeInfo(getCastle())); // test - copied from SiegeNpc.java player.sendPacket(castleId); //should pass player's choice
			 */
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		// aden , giran , goddard, rune , gludio are fine , i dont need more than 4-5 :P, go try this. I still dont understand in which html it will replace the values. nothing useful on the
		// htm
		
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(getHtmlPath(getNpcId(), 0));
		html.replace("%objectId%", String.valueOf(getObjectId()));
		// html.replace("%castle%", castleId);
		html.replace("%gludio%", 1);
		html.replace("%dion%", 2);
		html.replace("%giran%", 3);
		html.replace("%oren%", 4);
		html.replace("%aden%", 5);
		html.replace("%innadril%", 6);
		html.replace("%goddard%", 7);
		html.replace("%rune%", 8);
		html.replace("%schuttgart%", 9);
		player.sendPacket(html);
		
	}
	
}