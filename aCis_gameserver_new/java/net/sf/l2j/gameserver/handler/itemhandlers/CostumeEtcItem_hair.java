package net.sf.l2j.gameserver.handler.itemhandlers;

import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;

/**
 * @author Giorgos
 */
public class CostumeEtcItem_hair implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		Player activeChar = playable.getActingPlayer();
		if (activeChar.isAlikeDead() || activeChar.isOutOfControl())
			return;
		if (checkForCostume(activeChar))
		{
			
			activeChar.setTempHair(0);
			activeChar.setCostumeHeaditemObjId(0); 
			activeChar.broadcastUserInfo();
			activeChar.sendMessage("Skin: Unequiped.");
			return;
		}
		Item costume = item.getItem();
		int hair = costume.getTempHair();
		
		
		activeChar.setTempHair(hair);
		activeChar.setCostumeHeaditemObjId(item.getObjectId());
		
		activeChar.broadcastUserInfo();
		activeChar.sendMessage("Skin: Equiped.");
	}
	
	boolean checkForCostume(Player player)
	{
		return player.getTempHair() != 0;
	}
}
