package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.xml.ArmorSetData;
import net.sf.l2j.gameserver.enums.Paperdoll;
import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.enums.StatusType;
import net.sf.l2j.gameserver.enums.actors.Sex;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.ArmorSet;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.item.kind.Armor;
import net.sf.l2j.gameserver.model.item.kind.Item;
import net.sf.l2j.gameserver.model.item.kind.Weapon;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.CreatureSay;
import net.sf.l2j.gameserver.network.serverpackets.EnchantResult;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.StatusUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;
import net.sf.l2j.gameserver.skills.L2Skill;

public final class RequestEnchantItem extends AbstractEnchantPacket
{
	private int _objectId;
	String find_gender;
	
	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null || _objectId == 0)
			return;
		
		if (!player.isOnline() || getClient().isDetached())
		{
			player.setActiveEnchantItem(null);
			return;
		}
		
		if (player.isProcessingTransaction() || player.isOperating())
		{
			player.sendPacket(SystemMessageId.CANNOT_ENCHANT_WHILE_STORE);
			player.setActiveEnchantItem(null);
			player.sendPacket(EnchantResult.CANCELLED);
			return;
		}
		
		final ItemInstance item = player.getInventory().getItemByObjectId(_objectId);
		ItemInstance scroll = player.getActiveEnchantItem();
		
		if (item == null || scroll == null)
		{
			player.setActiveEnchantItem(null);
			player.sendPacket(SystemMessageId.ENCHANT_SCROLL_CANCELLED);
			player.sendPacket(EnchantResult.CANCELLED);
			return;
		}
		
		// template for scroll
		final EnchantScroll scrollTemplate = getEnchantScroll(scroll);
		if (scrollTemplate == null)
			return;
		
		// first validation check
		if (!scrollTemplate.isValid(item) || !isEnchantable(item))
		{
			player.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION);
			player.setActiveEnchantItem(null);
			player.sendPacket(EnchantResult.CANCELLED);
			return;
		}
		
		// attempting to destroy scroll
		scroll = player.getInventory().destroyItem("Enchant", scroll.getObjectId(), 1, player, item);
		if (scroll == null)
		{
			player.sendPacket(SystemMessageId.NOT_ENOUGH_ITEMS);
			player.setActiveEnchantItem(null);
			player.sendPacket(EnchantResult.CANCELLED);
			return;
		}
		
		if (player.getActiveTradeList() != null)
		{
			player.cancelActiveTrade();
			player.sendPacket(SystemMessageId.TRADE_ATTEMPT_FAILED);
			return;
		}
		
		synchronized (item)
		{
			double chance = scrollTemplate.getChance(item);
			find_gender = player.getAppearance().getSex() == Sex.MALE ? "his" : "her";
			// last validation check
			if (item.getOwnerId() != player.getObjectId() || !isEnchantable(item) || chance < 0)
			{
				player.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITION);
				player.setActiveEnchantItem(null);
				player.sendPacket(EnchantResult.CANCELLED);
				return;
			}
			
			// success
			if (Rnd.nextDouble() < chance)
			{
				// announce the success
				SystemMessage sm;
				String sm2;
				
				
				if (item.getEnchantLevel() == 0)
				{
					sm = SystemMessage.getSystemMessage(SystemMessageId.S1_SUCCESSFULLY_ENCHANTED);
					sm.addItemName(item.getItemId());
					player.sendPacket(sm);
				}
				else if(item.getEnchantLevel() >0 && item.getEnchantLevel() <=15) // from + 1 to +15 , just send message to the player
				{
					sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2_SUCCESSFULLY_ENCHANTED);
					sm.addNumber(item.getEnchantLevel());
					sm.addItemName(item.getItemId());
					player.sendPacket(sm);
				}	
				else //from +15 and above , inform the players on trade chat about the successful enchantment
				{
					sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2_SUCCESSFULLY_ENCHANTED);
					sm.addNumber(item.getEnchantLevel());
					sm.addItemName(item.getItemId());
					
					sm2 = player.getName() + " has successfully enchanted " + find_gender + " +" + item.getEnchantLevel() +" " + item.getName() + " with a " +scrollTemplate.ScrollType() +"!";
					World.toAllOnlinePlayers(new CreatureSay(0, SayType.TRADE,"[Enchant System]",sm2));
					
					if(item.getEnchantLevel()==19)
					{
						MagicSkillUse mgc = new MagicSkillUse(player, 9932, 1, 0, 0); //huge fireworks for +20
						player.sendPacket(mgc);
					}
					
				}
				
				item.setEnchantLevel(item.getEnchantLevel() + 1);
				item.updateDatabase();
				
				// If item is equipped, verify the skill obtention (+4 duals, +6 armorset).
				if (item.isEquipped())
				{
					final Item it = item.getItem();
					
					// Add skill bestowed by +4 duals.
					if (it instanceof Weapon && item.getEnchantLevel() == 4)
					{
						final L2Skill enchant4Skill = ((Weapon) it).getEnchant4Skill();
						if (enchant4Skill != null)
						{
							player.addSkill(enchant4Skill, false);
							player.sendSkillList();
						}
					}
					// Add skill bestowed by +6 armorset.
					else if (it instanceof Armor && item.getEnchantLevel() == 6)
					{
						// Checks if player is wearing a chest item
						final int chestId = player.getInventory().getItemIdFrom(Paperdoll.CHEST);
						if (chestId != 0)
						{
							final ArmorSet armorSet = ArmorSetData.getInstance().getSet(chestId);
							if (armorSet != null && armorSet.isEnchanted6(player)) // has all parts of set enchanted to 6 or more
							{
								final int skillId = armorSet.getEnchant6skillId();
								if (skillId > 0)
								{
									final L2Skill skill = SkillTable.getInstance().getInfo(skillId, 1);
									if (skill != null)
									{
										player.addSkill(skill, false);
										player.sendSkillList();
									}
								}
							}
						}
					}
				}
				player.sendPacket(EnchantResult.SUCCESS);
			}
			else
			{
				// Drop passive skills from items.
				if (item.isEquipped())
				{
					final Item it = item.getItem();
					
					// Remove skill bestowed by +4 duals.
					if (it instanceof Weapon && item.getEnchantLevel() >= 4 && !scrollTemplate.isCrystal())
					{
						final L2Skill enchant4Skill = ((Weapon) it).getEnchant4Skill();
						if (enchant4Skill != null)
						{
							player.removeSkill(enchant4Skill.getId(), false);
							player.sendSkillList();
						}
					}
					// Add skill bestowed by +6 armorset.
					else if (it instanceof Armor && item.getEnchantLevel() >= 6 && !scrollTemplate.isCrystal())
					{
						// Checks if player is wearing a chest item
						final int chestId = player.getInventory().getItemIdFrom(Paperdoll.CHEST);
						if (chestId != 0)
						{
							final ArmorSet armorSet = ArmorSetData.getInstance().getSet(chestId);
							if (armorSet != null && armorSet.isEnchanted6(player)) // has all parts of set enchanted to 6 or more
							{
								final int skillId = armorSet.getEnchant6skillId();
								if (skillId > 0)
								{
									player.removeSkill(skillId, false);
									player.sendSkillList();
								}
							}
						}
					}
				}
				
				if (scrollTemplate.isBlessed())
				{
					int item_ench_lvl= item.getEnchantLevel(); //store enchant value before it fails
					String item_name = item.getName(); //store item name before it fails
					// blessed enchant - clear enchant value
					player.sendMessage("Failed in Blessed Enchant. The enchant value of the item became 3");
					
					item.setEnchantLevel(3);
					item.updateDatabase();
					player.sendPacket(EnchantResult.UNSUCCESS);
					
					if(item_ench_lvl>=16)				
						World.toAllOnlinePlayers(new CreatureSay(0, SayType.TRADE,"[Enchant System]", player.getName() + " has failed to enchant " + find_gender +" +" + item_ench_lvl +" " +item_name+ " with a " +scrollTemplate.ScrollType() +"."));
						
				}
				
				else if (scrollTemplate.isCrystal())
				{
					int item_ench_lvl= item.getEnchantLevel(); //store enchant value before it fails
					String item_name = item.getName(); //store item name before it fails
					// crystal enchant - enchant value remains the same
					// player.sendPacket(SystemMessageId.CRYSTAL_ENCHANT_FAILED);
					player.sendMessage(" Failed in Crystal Enchant. The enchant value of the item remains the same.");
					player.sendPacket(EnchantResult.UNSUCCESS);
					
					if(item_ench_lvl>=16)				
						World.toAllOnlinePlayers(new CreatureSay(0, SayType.TRADE,"[Enchant System]", player.getName() + " has failed to enchant "+ find_gender + " +" + item_ench_lvl +" " +item_name+ " with a " +scrollTemplate.ScrollType()+"."));
				}
				
				else
				{
					int item_ench_lvl= item.getEnchantLevel(); //store enchant value before it fails
					String item_name = item.getName(); //store item name before it fails
					
					// enchant failed, destroy item
					int crystalId = item.getItem().getCrystalItemId();
					int count = item.getCrystalCount() - (item.getItem().getCrystalCount() + 1) / 2;
					if (count < 1)
						count = 1;
					
					ItemInstance destroyItem = player.getInventory().destroyItem("Enchant", item, player, null);
					if (destroyItem == null)
					{
						player.setActiveEnchantItem(null);
						player.sendPacket(EnchantResult.CANCELLED);
						return;
					}
					
					if (crystalId != 0)
					{
						player.getInventory().addItem("Enchant", crystalId, count, player, destroyItem);
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_S2_S1_S).addItemName(crystalId).addItemNumber(count));
					}
					
					InventoryUpdate iu = new InventoryUpdate();
					if (destroyItem.getCount() == 0)
						iu.addRemovedItem(destroyItem);
					else
						iu.addModifiedItem(destroyItem);
					
					player.sendPacket(iu);
					
					if(item_ench_lvl>=16)				
						World.toAllOnlinePlayers(new CreatureSay(0, SayType.TRADE,"[Enchant System]", player.getName() + " has failed to enchant "+ find_gender + " +" + item_ench_lvl +" " +item_name+ " with a " +scrollTemplate.ScrollType() +"."));
					
					// Messages.
					if (item.getEnchantLevel() > 0)
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ENCHANTMENT_FAILED_S1_S2_EVAPORATED).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
					else
						player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.ENCHANTMENT_FAILED_S1_EVAPORATED).addItemName(item.getItemId()));
					
					World.getInstance().removeObject(destroyItem);
					if (crystalId == 0)
						player.sendPacket(EnchantResult.UNK_RESULT_4);
					else
						player.sendPacket(EnchantResult.UNK_RESULT_1);
					
					StatusUpdate su = new StatusUpdate(player);
					su.addAttribute(StatusType.CUR_LOAD, player.getCurrentWeight());
					player.sendPacket(su);
				}
			}
			
			player.sendPacket(new ItemList(player, false));
			player.broadcastUserInfo();
			player.setActiveEnchantItem(null);
		}
	}
}