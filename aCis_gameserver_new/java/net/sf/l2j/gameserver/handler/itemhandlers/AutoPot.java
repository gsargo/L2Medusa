package net.sf.l2j.gameserver.handler.itemhandlers;

import net.sf.l2j.commons.random.Rnd;

//import net.sf.l2j.gameserver.ThreadPoolManager;
import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.gameserver.network.serverpackets.ExAutoSoulShot;

import net.sf.l2j.gameserver.enums.items.ShotType;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;



public class AutoPot implements Runnable {

	private int id;
	private Player activeChar;


	public AutoPot(int id, Player activeChar)
	{
		this.id = id;
		this.activeChar = activeChar;
	}
			
		
	@Override
	public void run()
	{
		
		if (activeChar.getInventory().getItemByItemId(id) == null)
		{
			activeChar.sendPacket(new ExAutoSoulShot(id, 0));	
			activeChar.setAutoPot(id, null, false);
			return;	
		}
		switch (id)	
		{	
			case 728:
			case 25625:
			{
			
			if (activeChar.getStatus().getMp() < 0.90*activeChar.getStatus().getMaxMp())	
				{
					MagicSkillUse msu = new MagicSkillUse(activeChar, activeChar, 2279, 2, 0, 100);
					activeChar.broadcastPacket(msu);
					ItemSkills is = new ItemSkills();
					is.useItem(activeChar, activeChar.getInventory().getItemByItemId(id), true);		
				}
				break;
			}
			
				
			
			case 1539:
			case 25626:
			{
				if (activeChar.getStatus().getHp() < 0.90*activeChar.getStatus().getMaxHp())
				{	
					MagicSkillUse msu = new MagicSkillUse(activeChar, activeChar, 2037, 1, 0, 100);
					activeChar.broadcastPacket(msu);
					ItemSkills is = new ItemSkills();	
					is.useItem(activeChar, activeChar.getInventory().getItemByItemId(id), true);	
				}
				break;
			}
			
			case 5592:
			case 25631:
			{
				if (activeChar.getStatus().getCp() < 0.90*activeChar.getStatus().getMaxCp())
				{
					MagicSkillUse msu = new MagicSkillUse(activeChar, activeChar, 2166, 2, 0, 100);
					activeChar.broadcastPacket(msu);
					ItemSkills is = new ItemSkills();
					is.useItem(activeChar, activeChar.getInventory().getItemByItemId(5592), true);
				}
				break;
			}
			
			
			
			
		}
	

		if (activeChar.getInventory().getItemByItemId(id) == null)
		{
			 activeChar.sendPacket(new ExAutoSoulShot(id, 0));
			 activeChar.setAutoPot(id, null, false);
		}
	}
}