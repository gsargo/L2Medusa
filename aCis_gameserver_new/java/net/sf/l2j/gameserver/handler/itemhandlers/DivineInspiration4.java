package net.sf.l2j.gameserver.handler.itemhandlers;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.skills.L2Skill;

public class DivineInspiration4 implements IItemHandler
{
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		Player activeChar = (Player) playable;
		
		switch((activeChar.getSkillLevel(1405)))
		{
		case 0:
			activeChar.sendMessage("Divine Inspiration Tome 1 required.");
			return;
		case 1:
			activeChar.sendMessage("Divine Inspiration Tome 2 required.");
			return;
		case 2:
			activeChar.sendMessage("Divine Inspiration Tome 3 required.");
			return;
		case 3:
			break;
		case 4:
			activeChar.sendMessage("Maximum Divine Inspiration level already learned.");
			return;
		}
		
		/*if (activeChar.getSkillLevel(1405) > 3)
		{
			activeChar.sendMessage("Maximum Divine Inspiration level already learned.");
			return;
		}
		
		else if (activeChar.getSkillLevel(1405) != 3)
		{
			activeChar.sendMessage("Divine Inspiration Tome 3 required.");
			return;
		}*/
		
		if(activeChar.destroyItem("", item, 1, null, true))
		{
			final String MY_DIVINE_SOUND = Quest.SOUND_JACKPOT;
			final L2Skill skill_div4 = SkillTable.getInstance().getInfo(1405, 4);
			activeChar.addSkill(skill_div4, true, true);
			activeChar.sendSkillList();
			activeChar.broadcastUserInfo();
			activeChar.sendMessage("Your total buff slots have been expanded by 1.");
			// giveItems(player, 20003, 1);
			Quest.playSound((activeChar), MY_DIVINE_SOUND);
			Quest.playSound((activeChar), MY_DIVINE_SOUND);		
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, 9969, 1, 1000, 0));// custom buff and icon above head
		}
	}
	
}