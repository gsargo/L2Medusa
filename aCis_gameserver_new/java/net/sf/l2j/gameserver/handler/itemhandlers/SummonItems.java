package net.sf.l2j.gameserver.handler.itemhandlers;

import java.util.List;
import java.util.stream.IntStream;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.data.xml.SummonItemData;
import net.sf.l2j.gameserver.enums.items.ItemLocation;
import net.sf.l2j.gameserver.geoengine.GeoEngine;
import net.sf.l2j.gameserver.handler.IItemHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Playable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.ChristmasTree;
import net.sf.l2j.gameserver.model.actor.instance.Pet;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.holder.IntIntHolder;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.location.SpawnLocation;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

import extensions.Agathions_RestoreCp;
import extensions.Pet_BuffFighter;
import extensions.Pet_BuffMage;
import extensions.Pet_RestoreHpMp;

public class SummonItems implements IItemHandler
{

	
	@Override
	public void useItem(Playable playable, ItemInstance item, boolean forceUse)
	{
		
		if (!(playable instanceof Player))
			return;
		

		
		final Player player = (Player) playable;
		
		

		
		if (player.isSitting())
		{
			player.sendPacket(SystemMessageId.CANT_MOVE_SITTING);
			return;
		}
		
		if (player.isInObserverMode())
			return;
		
		if (player.isAllSkillsDisabled() || player.getCast().isCastingNow())
			return;
		
		final IntIntHolder sitem = SummonItemData.getInstance().getSummonItem(item.getItemId());
		
		if((player.isInsidePvPZone() && sitem.getValue()!=3 ) || (player.isInsideSoloDungeonZone()  ) || player.isInsidePartyDungeonZone() )
		{
			//player.sendMessage("You can not use a summon inside PvP Zone");
			return;
		}
		
		if ((player.getSummon() != null || player.isMounted()) && sitem.getValue() > 0 && sitem.getValue() != 3)
		{
			player.sendPacket(SystemMessageId.SUMMON_ONLY_ONE);
			return;
		}
				
		if (player.getAttack().isAttackingNow())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_SUMMON_IN_COMBAT);
			return;
		}
		
		final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(sitem.getId());
		if (npcTemplate == null)
			return;
		
		switch (sitem.getValue())
		{
			case 0: // static summons (like Christmas tree)
				final List<ChristmasTree> trees = player.getKnownTypeInRadius(ChristmasTree.class, 1200);
				if (!trees.isEmpty())
				{
					player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CANNOT_SUMMON_S1_AGAIN).addCharName(trees.get(0)));
					return;
				}
				
				if (!player.destroyItem("Summon", item, 1, null, false))
					return;
				
				player.getMove().stop();
				
				try
				{
					final Spawn spawn = new Spawn(npcTemplate);
					spawn.setLoc(player.getPosition());
					spawn.setRespawnState(false);
					
					final Npc npc = spawn.doSpawn(true);
					npc.setTitle(player.getName());
					npc.setWalkOrRun(false);
				}
				catch (Exception e)
				{
					player.sendPacket(SystemMessageId.TARGET_CANT_FOUND);
				}
				break;
			
			case 1: // summon pet through an item
				player.getAI().tryToCast(player, SkillTable.getInstance().getInfo(2046, 1), false, false, item.getObjectId());
				player.sendPacket(SystemMessageId.SUMMON_A_PET);
				Pet_RestoreHpMp.getInstance().startTask(player);
				Pet_BuffFighter.getInstance().startTask(player);
				Pet_BuffMage.getInstance().startTask(player);
				break;
			
			case 2: // wyvern
				player.getMove().stop();
				player.mount(sitem.getId(), item.getObjectId());
				break;
			
			case 3:
				// Check for summon item validity.
				if (item.getOwnerId() != player.getObjectId() || item.getLocation() != ItemLocation.INVENTORY)
					return;
				
				// Owner has a pet listed in world.
				Pet agathion = World.getInstance().getAgat(player.getObjectId());
				if (agathion != null)
				{
					if (agathion.getNpcId() == sitem.getId())
					{
						agathion.unSummon(player);
						player.sendMessage("Agathion unsummoned");
						return;
					}
					player.sendMessage("You already have a summoned agathion");
					return;
				}
				
				// Check summon item validity.
				final IntIntHolder summonItem = SummonItemData.getInstance().getSummonItem(item.getItemId());
				if (summonItem == null)
					return;
				
				// Add the pet instance to world.
				final Pet pet = Pet.restore(item, npcTemplate, player);
				if (pet == null)
					return;
				
				World.getInstance().addAgat(player.getObjectId(), pet);
				Agathions_RestoreCp.getInstance().startTask(player);
		
				// player.setSummon(pet);
				
				pet.forceRunStance();
				pet.setTitle(" ");
				// pet.setTitle(player.getName());
				// pet.startFeed();
				
				final SpawnLocation spawnLoc = player.getPosition().clone();
				spawnLoc.addStrictOffset(20);
				spawnLoc.setHeadingTo(player.getPosition());
				spawnLoc.set(GeoEngine.getInstance().getValidLocation(player, spawnLoc));
				
				pet.spawnMe(spawnLoc);
				pet.getAI().setFollowStatus(true);
				//player.setAutoLoot(true);
		}
	}
}