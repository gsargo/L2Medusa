package net.sf.l2j.gameserver.handler.itemhandlers;

import net.sf.l2j.gameserver.data.xml.DoorData;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Door;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public class CustomDoorKey_Asphodel implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player player = (Player) playable;
		final WorldObject target = player.getTarget();
		
		if (!(target instanceof Door))
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		final Door door = (Door) target;
		
		if (!(player.isIn3DRadius(door, 1000))) // default radius : Npc.INTERACTION_DISTANCE
		{
			player.sendPacket(SystemMessageId.DIST_TOO_FAR_CASTING_STOPPED);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
			
		final int doorId = door.getDoorId();
		
		//if(doorId == 26210041 || doorId == 26210042 || doorId == 26210043 || doorId == 26210044)
			if (!playable.destroyItem("Consume", item.getObjectId(), 1, null, true))
				return;
		
		if(item.getItemId() != 12584)
			return;
		
		switch (doorId)
		{
			case 26210041:
				DoorData.getInstance().getDoor(26210041).openMe();
				break;
				
			case 26210042:
				DoorData.getInstance().getDoor(26210042).openMe();
				break;
				
			case 26210043:
				DoorData.getInstance().getDoor(26210043).openMe();
				break;
				
			case 26210044:
				DoorData.getInstance().getDoor(26210044).openMe();
				break;
				
			default:
				player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addItemName(12584));
				break;
				
		}
			
		/*switch (item.getItemId())
		{
			case 12584:
				if (doorId == 26210041) //asphodel door
				{
					DoorData.getInstance().getDoor(26210041).openMe();
				}
				if (doorId == 26210042) //asphodel door
				{
					DoorData.getInstance().getDoor(26210042).openMe();
				}
				if (doorId == 26210043) //asphodel door
				{
					DoorData.getInstance().getDoor(26210043).openMe();
				}
				if (doorId == 26210044) //asphodel door
				{
					DoorData.getInstance().getDoor(26210044).openMe();
				}
				else
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED).addItemName(12584));
				break;						
			
		}*/
	}
}