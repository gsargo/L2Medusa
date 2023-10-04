package net.sf.l2j.gameserver.handler.itemhandlers;

import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;

public class TitleColorItemPvPShop3 implements IItemHandler
{
	
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
		{
			return;
		}
		
		Player activeChar = (Player) playable;
		
		if (activeChar.getKarma() > 0)
		{
			((Player) playable).sendMessage("You can't use this item while having karma!");
			return;
		}
		
		else if (activeChar.getPvpFlag() > 0)
		{
			((Player) playable).sendMessage("You can't use this item while being flagged!");
			return;
		}
		
		if(activeChar.destroyItem("Consume", item.getObjectId(), 1, null, false))
		{
			/* Light Green */
			activeChar.getMemos().set("title_color", Integer.decode("0x7DC493")); //7DC493
			activeChar.getAppearance().setTitleColor(Integer.decode("0x7DC493"));
			activeChar.sendMessage("The color of your title has been changed. Have Fun!");
			activeChar.broadcastUserInfo();
			return;
			
		}
	}
}