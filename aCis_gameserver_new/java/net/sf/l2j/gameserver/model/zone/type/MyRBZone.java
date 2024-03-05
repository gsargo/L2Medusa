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

public class MyRBZone  extends ZoneType
{
	private boolean _antiLogout = true;
	private boolean _antiMount = true;

	
	public MyRBZone(int id)
	{
		super(id);
		System.out.println("RB Zone loaded: " + id);
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
			
			player.setisInsideMyRBZone(true);
			
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
			player.setisInsideMyRBZone(false);
		
			
			if (_antiLogout)
				player.setInNoLogoutArea(false);
			if (_antiMount)
				player.setInDismountZone(false);	
		}
	}
	
	

}
