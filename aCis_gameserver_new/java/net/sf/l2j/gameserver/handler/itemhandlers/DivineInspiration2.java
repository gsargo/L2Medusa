package net.sf.l2j.gameserver.handler.itemhandlers;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.scripting.Quest;
import net.sf.l2j.gameserver.skills.L2Skill;

public class DivineInspiration2 implements IItemHandler
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
				break;
			case 2:
			case 3:
				activeChar.sendMessage("You have already learned Divine Inspiration level " + activeChar.getSkillLevel(1405) +".");
				return;
			case 4:
				activeChar.sendMessage("Maximum Divine Inspiration level already learned.");
				return;
			}
		/*if (activeChar.getSkillLevel(1405) > 3)
		{
			activeChar.sendMessage("Maximum Divine Inspiration level already learned.");
			return;
		}
		else if (activeChar.getSkillLevel(1405) > 1)
		{
			activeChar.sendMessage("Divine Inspiration Tome "+(activeChar.getSkillLevel(1405)) +" already applied.");
			return;
		}
		else if (activeChar.getSkillLevel(1405) != 1)
		{
			activeChar.sendMessage("Divine Inspiration Tome 1 required.");
			return;
		}
		*/
		if(activeChar.destroyItem("", item, 1, null, true))
		{
			final String MY_DIVINE_SOUND = Quest.SOUND_JACKPOT;
			final L2Skill skill_div2 = SkillTable.getInstance().getInfo(1405, 2);
			//MagicSkillUse mgc_ = new MagicSkillUse(activeChar, 9969, 1, 0, 0);// (Creature cha, int skillId, int skillLevel, int hitTime, int reuseDelay)
			activeChar.addSkill(skill_div2, true, true);
			activeChar.sendSkillList();
			activeChar.broadcastUserInfo();
			activeChar.sendMessage("Your total buff slots have been expanded by 1.");
			Quest.playSound(((Player) playable), MY_DIVINE_SOUND);
			Quest.playSound((activeChar), MY_DIVINE_SOUND);		
			//activeChar.sendPacket(mgc_); // custom buff and icon above head
			activeChar.broadcastPacket(new MagicSkillUse(activeChar, 9969, 1, 1000, 0));// custom buff and icon above head
		}
		
	}
	
}