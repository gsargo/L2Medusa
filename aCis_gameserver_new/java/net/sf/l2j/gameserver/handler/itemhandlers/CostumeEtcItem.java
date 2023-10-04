package net.sf.l2j.gameserver.handler.itemhandlers;

import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Item;

/**
 * @author Giorgos
 */
public class CostumeEtcItem implements IItemHandler
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
			activeChar.setTempChest(0);
			activeChar.setTempLegs(0);
			activeChar.setTempGloves(0);
			activeChar.setTempFeet(0);
			activeChar.setTempHair(0);
			activeChar.setTempFace(0);
			activeChar.broadcastUserInfo();
			activeChar.sendMessage("Skin: Equiped.");
			return;
		}
		Item costume = item.getItem();
		int chest = costume.getTempChest();
		int legs = costume.getTempLegs();
		int gloves = costume.getTempGloves();
		int feet = costume.getTempFeet();
		int hair = costume.getTempHair();
		int face = costume.getTempFace();
		activeChar.setTempChest(chest);
		activeChar.setTempLegs(legs);
		activeChar.setTempGloves(gloves);
		activeChar.setTempFeet(feet);
		activeChar.setTempHair(hair);
		activeChar.setTempFace(face);
		activeChar.broadcastUserInfo();
		activeChar.sendMessage("Skin: Unequiped.");
	}
	
	boolean checkForCostume(Player player)
	{
		return player.getTempChest() != 0 || player.getTempLegs() != 0 || player.getTempGloves() != 0 || player.getTempFeet() != 0 || player.getTempHair() != 0 || player.getTempFace() != 0;
	}
}
