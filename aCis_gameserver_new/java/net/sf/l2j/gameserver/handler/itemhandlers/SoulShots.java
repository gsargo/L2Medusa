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

public class SoulShots implements IItemHandler
{
	private static final int MANA_POT_CD = 8,
	HEALING_POT_CD = 11,
	CP_POT_CD = 3,
	KYKEON_CD = 3,
	NECTAR_CD = 20,
	AMBROSIA_CD = 18;

		 	
	
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		if (!(playable instanceof Player))
			return;
		
		final Player player = (Player) playable;
		final ItemInstance weaponInst = player.getActiveWeaponInstance();
		final Weapon weaponItem = player.getActiveWeaponItem();
		
		final int itemId = item.getItemId();
		
		
		
		//Custom Auto pots code
		 if (itemId == 728 || itemId == 1539 || itemId == 5592 || itemId == 25625 || itemId == 25626 || itemId == 25631)
		 {
			 switch (itemId)
			 {
				 case 728: // mana potion
				 {
					 if (player.isAutoPot(728))
					 {
						 player.sendPacket(new ExAutoSoulShot(728, 0));
						 player.sendMessage("Deactivated auto mana potions.");
						 player.setAutoPot(728, null, false);
					 }
					 else
					 {
						 if (player.getInventory().getItemByItemId(728) != null)
						 {
			
							 if (player.getInventory().getItemByItemId(728).getCount() > 1)
							 {
								 player.sendPacket(new ExAutoSoulShot(728, 1));
								 player.sendMessage("Activated auto mana potions.");
								 player.setAutoPot(728, ThreadPool.scheduleAtFixedRate(new AutoPot(728, player), 1000, MANA_POT_CD * 1000), true);
							 }
				
							 else
							 {
								 MagicSkillUse msu = new MagicSkillUse(player, player, 2279, 2, 0, 100);
								 player.broadcastPacket(msu);
								 ItemSkills is = new ItemSkills();
								 is.useItem(player, player.getInventory().getItemByItemId(728), true);
							 }
						 }
					 }
					 break;
				 }
				 
				 case 25625: // Nectar
				 {
					 if (player.isAutoPot(25625))
					 {
						 player.sendPacket(new ExAutoSoulShot(25625, 0));
						 player.sendMessage("Deactivated auto nectar potions.");
						 player.setAutoPot(25625, null, false);
					 }
					 else
					 {
						 if (player.getInventory().getItemByItemId(25625) != null)
						 {
			
							 if (player.getInventory().getItemByItemId(25625).getCount() > 1)
							 {
								 player.sendPacket(new ExAutoSoulShot(25625, 1));
								 player.sendMessage("Activated auto nectar potions.");
								 player.setAutoPot(25625, ThreadPool.scheduleAtFixedRate(new AutoPot(25625, player), 1000, NECTAR_CD * 1000), true);
							 }
				
							 else
							 {
								 MagicSkillUse msu = new MagicSkillUse(player, player, 2279, 2, 0, 100);
								 player.broadcastPacket(msu);
								 ItemSkills is = new ItemSkills();
								 is.useItem(player, player.getInventory().getItemByItemId(25625), true);
							 }
						 }
					 }
					 break;
				 }
				 
				 case 1539: // greater healing potion
				 {
					 if (player.isAutoPot(1539))
					 {
						 player.sendPacket(new ExAutoSoulShot(1539, 0));
						 player.sendMessage("Deactivated auto healing potions.");
						 player.setAutoPot(1539, null, false);
					 }
					 else
					 {
						 if (player.getInventory().getItemByItemId(1539) != null)
						 {
							 if (player.getInventory().getItemByItemId(1539).getCount() > 1)
							 {
								 player.sendPacket(new ExAutoSoulShot(1539, 1));
								 player.sendMessage("Activated auto healing potions.");
								 player.setAutoPot(1539, ThreadPool.scheduleAtFixedRate(new AutoPot(1539, player), 1000, HEALING_POT_CD * 1000), true);
							 }
							 else
							 {
								 MagicSkillUse msu = new MagicSkillUse(player, player, 2037, 1, 0, 100);
								 player.broadcastPacket(msu);
								 ItemSkills is = new ItemSkills();
								 is.useItem(player, player.getInventory().getItemByItemId(1539), true);
							 }
						 }
					 }
					 break;
				 }
				 
				 case 25626: // Ambrosia
				 {
					 if (player.isAutoPot(25626))
					 {
						 player.sendPacket(new ExAutoSoulShot(25626, 0));
						 player.sendMessage("Deactivated auto ambrosia potions.");
						 player.setAutoPot(25626, null, false);
					 }
					 else
					 {
						 if (player.getInventory().getItemByItemId(25626) != null)
						 {
							 if (player.getInventory().getItemByItemId(25626).getCount() > 1)
							 {
								 player.sendPacket(new ExAutoSoulShot(25626, 1));
								 player.sendMessage("Activated auto ambrosia potions.");
								 player.setAutoPot(25626, ThreadPool.scheduleAtFixedRate(new AutoPot(25626, player), 1000, AMBROSIA_CD * 1000), true);
							 }
							 else
							 {
								 MagicSkillUse msu = new MagicSkillUse(player, player, 2037, 1, 0, 100);
								 player.broadcastPacket(msu);
								 ItemSkills is = new ItemSkills();
								 is.useItem(player, player.getInventory().getItemByItemId(25626), true);
							 }
						 }
					 }
					 break;
				 }
				 
				 case 5592: // greater cp potion
				 {
					 if (player.isAutoPot(5592))
					 {
						 player.sendPacket(new ExAutoSoulShot(5592, 0));
						 player.sendMessage("Deactivated auto cp potions.");
						 player.setAutoPot(5592, null, false);
					 }
					 else
					 {
						 if (player.getInventory().getItemByItemId(5592) != null)
						 {
							 if (player.getInventory().getItemByItemId(5592).getCount() > 1)
							 {
								 player.sendPacket(new ExAutoSoulShot(5592, 1));
								 player.sendMessage("Activated auto cp potions.");
								 player.setAutoPot(5592, ThreadPool.scheduleAtFixedRate(new AutoPot(5592, player), 1000, CP_POT_CD * 1000), true);
								 // ThreadPool.scheduleAtFixedRate(() -> broadcastTimer(), 5*1000, 1000);
							 }
							 else
							 {
								 MagicSkillUse msu = new MagicSkillUse(player, player, 2166, 2, 0, 100);
								 player.broadcastPacket(msu);
								 ItemSkills is = new ItemSkills();
								 is.useItem(player, player.getInventory().getItemByItemId(5592), true);
							 }
						 }
					 }
					 break;
				 }
				 
				 case 25631: // kykeon
				 {
					 if (player.isAutoPot(25631))
					 {
						 player.sendPacket(new ExAutoSoulShot(25631, 0));
						 player.sendMessage("Deactivated auto cp potions.");
						 player.setAutoPot(25631, null, false);
					 }
					 else
					 {
						 if (player.getInventory().getItemByItemId(25631) != null)
						 {
							 if (player.getInventory().getItemByItemId(25631).getCount() > 1)
							 {
								 player.sendPacket(new ExAutoSoulShot(25631, 1));
								 player.sendMessage("Activated auto cp potions.");
								 player.setAutoPot(25631, ThreadPool.scheduleAtFixedRate(new AutoPot(25631, player), 1000, KYKEON_CD * 1000), true);
								 // ThreadPool.scheduleAtFixedRate(() -> broadcastTimer(), 5*1000, 1000);
							 }
							 else
							 {
								 MagicSkillUse msu = new MagicSkillUse(player, player, 2166, 2, 0, 100);
								 player.broadcastPacket(msu);
								 ItemSkills is = new ItemSkills();
								 is.useItem(player, player.getInventory().getItemByItemId(25631), true);
							 }
						 }
					 }
					 break;
				 }
				 
				 
			 }
			 return;
		 }
		
		//END OF CUSTOM AUTO POTS CODE 
		
		
		
		
		// Check if soulshot can be used
		if (weaponInst == null || weaponItem.getSoulShotCount() == 0)
		{
			if (!player.getAutoSoulShot().contains(item.getItemId()))
				player.sendPacket(SystemMessageId.CANNOT_USE_SOULSHOTS);
			
			return;
		}
		
		if (weaponItem.getCrystalType() != item.getItem().getCrystalType())
		{
			if (!player.getAutoSoulShot().contains(item.getItemId()))
				player.sendPacket(SystemMessageId.SOULSHOTS_GRADE_MISMATCH);
			
			return;
		}
		
		// Check if Soulshot are already active.
		if (player.isChargedShot(ShotType.SOULSHOT))
			return;
		
		// Consume Soulshots if player has enough of them.
		int ssCount = weaponItem.getSoulShotCount();
		if (weaponItem.getReducedSoulShot() > 0 && Rnd.get(100) < weaponItem.getReducedSoulShotChance())
			ssCount = weaponItem.getReducedSoulShot();
		
		if (!player.destroyItemWithoutTrace(item.getObjectId(), ssCount))
		{
			if (!player.disableAutoShot(item.getItemId()))
				player.sendPacket(SystemMessageId.NOT_ENOUGH_SOULSHOTS);
			
			return;
		}
		
		final IntIntHolder[] skills = item.getItem().getSkills();
		
		weaponInst.setChargedShot(ShotType.SOULSHOT, true);
		player.sendPacket(SystemMessageId.ENABLED_SOULSHOT);
		player.broadcastPacketInRadius(new MagicSkillUse(player, player, skills[0].getId(), 1, 0, 0), 600);
		



	}
}


/*final class AutoPot implements Runnable {

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
				{
				
				if (activeChar.getStatus().getMp() < 0.70*activeChar.getStatus().getMaxMp())	
					{
					MagicSkillUse msu = new MagicSkillUse(activeChar, activeChar, 2279, 2, 0, 100);
					activeChar.broadcastPacket(msu);
					ItemSkills is = new ItemSkills();
					is.useItem(activeChar, activeChar.getInventory().getItemByItemId(728), true);		
					}
				break;
				}
				
				case 1539:
				{
					if (activeChar.getStatus().getHp() < 0.70*activeChar.getStatus().getMaxHp())
						{	
						MagicSkillUse msu = new MagicSkillUse(activeChar, activeChar, 2037, 1, 0, 100);
						activeChar.broadcastPacket(msu);
						ItemSkills is = new ItemSkills();	
						is.useItem(activeChar, activeChar.getInventory().getItemByItemId(1539), true);	
						}
				break;
				}
				
				case 5592:
				{
					if (activeChar.getStatus().getCp() < 0.70*activeChar.getStatus().getMaxCp())
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
}*/







