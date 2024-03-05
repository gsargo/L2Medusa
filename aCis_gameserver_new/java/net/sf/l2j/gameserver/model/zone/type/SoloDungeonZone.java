package net.sf.l2j.gameserver.model.zone.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Pet;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.zone.type.subtype.ZoneType;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.skills.L2Skill;
import net.sf.l2j.gameserver.taskmanager.PvpFlagTaskManager;

public class SoloDungeonZone  extends ZoneType
{
	private boolean _antiLogout = true;
	private boolean _antiMount = true;
	private boolean _antiParty = false;
	private String zoneName = "Area"; // will be set from xml
	
	public SoloDungeonZone(int id)
	{
		super(id);
		System.out.println("Solo Dungeon Zone loaded: " + id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		
		if (name.equals("logoutForb"))
		{
			_antiLogout = Boolean.parseBoolean(value);
		}
		else if (name.equals("mountForb"))
		{
			_antiMount = Boolean.parseBoolean(value);
		}
		else if(name.equals("zoneName"))
		{
			zoneName = value;
		}
		else if (name.equals("partyForb"))
		{
			_antiParty = Boolean.parseBoolean(value);
		}
		
		else
			super.setParameter(name, value);
		
	}
	
	private static void teleToStage(Player player) // teleport back to town method
	{
		if(player!=null)
		{
			int x = 83290;
			int y = 148027;
			int z = -3400;
			player.teleportTo(x, y, z, 50);
		}
	}
	
	@Override
	protected void onEnter(Creature character)
	{
		Player player = character.getActingPlayer();	
		
		
		
		if (player != null)
		{	
			
			player.setIsInsideSoloDungeonZone(true);
			
			//if( !player.isGM()) //summon check
				//player.dropAllAgathions();
			
			if (player.getDungeon()==null && !player.isGM())
			{
				player.sendMessage("You are not registered to any dungeon and you will be teleported to town in 10 seconds.");
				ThreadPool.schedule(() -> teleToStage(player), 10*1000); 
			}	
			
			if(player.isInParty()) //disperse party on enter
			{
				if(player.getParty().isLeader(player))
					player.getParty().removePartyMember(player, null);
				
				player.getParty().removePartyMember(player, null);
			}
			
			if (_antiParty)
				player.setPartyPenalty(true);
			if (_antiLogout)
				player.setInNoLogoutArea(true);
			if (_antiMount && !player.isGM())
			{
				player.setInDismountZone(true);
				if (player.isMounted())
				{
					player.dismount();
				}
			}
			
		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		Player player = character.getActingPlayer();	
		
		if(player!=null)
		{
			player.setIsInsideSoloDungeonZone(false);
		
			if (_antiParty)
				player.setPartyPenalty(false);
			if (_antiLogout)
				player.setInNoLogoutArea(false);
			if (_antiMount)
				player.setInDismountZone(false);	
		}
	}
	
	

}
