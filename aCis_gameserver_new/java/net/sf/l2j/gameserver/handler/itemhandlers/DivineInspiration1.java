package net.sf.l2j.gameserver.handler.itemhandlers;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.skills.L2Skill;

public class DivineInspiration1 implements IItemHandler
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
			break;
		case 1:
		case 2:
		case 3:
			activeChar.sendMessage("You have already learned Divine Inspiration level " + activeChar.getSkillLevel(1405) +".");
			return;	
		case 4:
			activeChar.sendMessage("Maximum Divine Inspiration level already learned.");
			return;
		}
		/*
		if (activeChar.getSkillLevel(1405) > 1)
		{
			activeChar.sendMessage("You have already learned Divine Inspiration, level " + activeChar.getSkillLevel(1405) + ".");
			return;
		}*/
		
		if(activeChar.destroyItem("", item, 1, null, true))
			{
			final String MY_DIVINE_SOUND = Quest.SOUND_JACKPOT;
			final L2Skill skill_div1 = SkillTable.getInstance().getInfo(1405, 1);
			activeChar.addSkill(skill_div1, true, true);
			activeChar.sendSkillList();
			activeChar.broadcastUserInfo();
			activeChar.sendMessage("Your total buff slots have been expanded by 1.");
			// giveItems(player, 20003, 1);
			Quest.playSound((activeChar), MY_DIVINE_SOUND);		
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, 9969, 1, 1000, 0));// custom buff and icon above head
			 
			}
			
	}
	
}