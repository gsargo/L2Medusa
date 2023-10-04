package net.sf.l2j.gameserver.model.zone.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.enums.ZoneId;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.zone.type.subtype.ZoneType;
import net.sf.l2j.gameserver.network.serverpackets.InventoryUpdate;
import net.sf.l2j.gameserver.skills.L2Skill;
import net.sf.l2j.gameserver.taskmanager.PvpFlagTaskManager;

public class PvPFlagZone  extends ZoneType
{
	
	// Message on enter, exit, pvp rewards, forbidden items, forbidden skills, pvp flag, forbidden ip, forbidden hwid(guard only), logout forbidden, anti summon, anti request, anti party
	// And clan check for pvp reward (ip, clan, hwid, party)
	private String _nameEnter;
	private String _nameExit;
	private boolean _antiLogout = false;
	private boolean _antiSummon = false;
	private boolean _antiMount = false;
	private boolean _antiRequest = false;
	private boolean _antiParty = false;
	private boolean _pvp = false;
	private int _pvpTime;
	private boolean _rewardIp = false;
	private boolean _rewardHwid = false;
	private boolean _rewardClan = false;
	private boolean _rewardParty = false;
	private final List<EventReward> _pvpReward = new ArrayList<>();
	private final List<Integer> _forbiddenItems = new ArrayList<>();
	private final Map<Integer, Integer> _buffs = new HashMap<>();
	private final List<Integer> _forbiddenSkills = new ArrayList<>();
	private final List<Integer> _EMPTY = new ArrayList<>();
	ScheduledFuture<?> _pvpTask;
	
	public PvPFlagZone(int id)
	{
		super(id);
		System.out.println("PvP Flag Zone loaded: " + id);
	}
	
	@Override
	public void setParameter(String name, String value)
	{
		if (name.equals("nameEnter"))
		{
			_nameEnter = value;
		}
		else if (name.equals("nameExit"))
		{
			_nameExit = value;
		}
		else if (name.equals("pvp"))
		{
			_pvp = Boolean.parseBoolean(value);
		}
		else if (name.equals("pvpTime"))
		{
			_pvpTime = Integer.parseInt(value);
		}
		else if (name.equals("ip"))
		{
			_rewardIp = Boolean.parseBoolean(value);
		}
		else if (name.equals("hwid"))
		{
			_rewardHwid = Boolean.parseBoolean(value);
		}
		else if (name.equals("clan"))
		{
			_rewardClan = Boolean.parseBoolean(value);
		}
		else if (name.equals("party"))
		{
			_rewardParty = Boolean.parseBoolean(value);
		}
		else if (name.equals("logoutForb"))
		{
			_antiLogout = Boolean.parseBoolean(value);
		}
		else if (name.equals("summonForb"))
		{
			_antiSummon = Boolean.parseBoolean(value);
		}
		else if (name.equals("mountForb"))
		{
			_antiMount = Boolean.parseBoolean(value);
		}
		else if (name.equals("requestForb"))
		{
			_antiRequest = Boolean.parseBoolean(value);
		}
		else if (name.equals("partyForb"))
		{
			_antiParty = Boolean.parseBoolean(value);
		}
		else if (name.equals("pvpRewards"))
		{
			String[] token = value.split(";");
			for (String item : token)
				if (!item.isEmpty())
				{
					String[] id = item.split(",");
					try
					{
						_pvpReward.add(new EventReward(Integer.parseInt(id[0]), Integer.parseInt(id[1]), Integer.parseInt(id[2])));
					}
					catch (NumberFormatException ignored)
					{
					}
				}
		}
		else if (name.equals("forbItems"))
		{
			String[] token = value.split(",");
			for (String point : token)
				if (!point.equals(""))
				{
					try
					{
						_forbiddenItems.add(Integer.parseInt(point));
					}
					catch (NumberFormatException ignored)
					{
					}
				}
		}
		else if (name.equals("buffs"))
		{
			String[] token = value.split(";");
			for (String buff : token)
			{
				if (!buff.isEmpty())
				{
					String[] vals = buff.split(",");
					try
					{
						_buffs.put(Integer.parseInt(vals[0]), Integer.parseInt(vals[1]));
					}
					catch (NumberFormatException ignored)
					{
					}
				}
			}
		}
		else if (name.equals("forbSkills"))
		{
			String[] token = value.split(",");
			for (String skills : token)
			{
				if (!skills.isEmpty())
				{
					try
					{
						_forbiddenSkills.add(Integer.parseInt(skills));
					}
					catch (NumberFormatException ignored)
					{
					}
				}
			}
		}
		else
			super.setParameter(name, value);
		
	}
	

	
	@Override
	protected void onEnter(Creature character)
	{
		Player player = character.getActingPlayer();
		
		if (player != null)
		{
			if(player.hasPet()) //summon check
				player.dropAllSummons();
			
			if (player.getStatus().getLevel() <= 75)
			{
				((Player) character).sendMessage("You are not ready for a fight yet, raise your level!");
				int x = 83290;
				int y = 148027;
				int z = -3400;
				character.teleportTo(x, y, z, 50); // Teleport to nearest town if lvl < 75
			}	
			
			if(player.isInParty()) //disperse party on enter
			{
				if(player.getParty().isLeader(player))
					player.getParty().removePartyMember(player, null);
				
				player.getParty().removePartyMember(player, null);
			}
			
			player.setIsInsidePvPZone(true);
			
			
			if (!_forbiddenItems.isEmpty())
				checkForbiddenItems(player);
			if (_pvp)
			{
				PvpFlagTaskManager.getInstance().add(player, _pvpTime);
				// _pvpTask = ThreadPool.scheduleAtFixedRate(() -> PvpFlagTaskManager.getInstance().add(player, _pvpTime), 0, _pvpTime);
			}
			player.sendMessage(_nameEnter);
			player.broadcastUserInfo();
			
			//if (_antiSummon)
				//player.setSummonPenalty(true);
			if (_antiLogout)
				player.setInNoLogoutArea(true);
			if (_antiMount)
			{
				player.setInDismountZone(true);
				if (player.isMounted())
				{
					player.dismount();
				}
			}
			if (_antiRequest)
				player.setAlone(true);
			if (_antiParty)
				player.setPartyPenalty(true);
			if (!_forbiddenSkills.isEmpty())
			{
				for (L2Skill s : player.getSkills().values())
				{
					int id = s.getId();
					if (_forbiddenSkills.contains(id))
						player.removeStatsByOwner(s);
				}
			}
			
			if (!_buffs.isEmpty())
			{
				// Stop all buffs
				// player.stopAllEffects();
				for (int i : _buffs.keySet())
				{
					Integer skill_lvl = _buffs.get(i);
					if (skill_lvl == null)
						continue;
					SkillTable.getInstance().getInfo(i, skill_lvl).getEffects(player, player);
				}
			}
		}
	}
	
	@Override
	protected void onExit(Creature character)
	{
		Player player = character.getActingPlayer();
		
		if (player != null)
		{				
			if (!_forbiddenItems.isEmpty())
				player.setForbItems(_EMPTY);
			if (_pvp)
			{
				// if (_pvpTask != null) {
				// _pvpTask.cancel(true);
				// _pvpTask = null;
				// }
				PvpFlagTaskManager.getInstance().remove(player, true);
				player.setIsInsidePvPZone(false);
			}
			player.sendMessage(_nameExit);
			player.broadcastUserInfo();
			
			if (_antiLogout)
				player.setInNoLogoutArea(false);
			if (_antiMount)
				player.setInDismountZone(false);
			if (_antiRequest)
				player.setAlone(false);
			if (_antiParty)
				player.setPartyPenalty(false);
			if (!_forbiddenSkills.isEmpty())
			{
				for (L2Skill s : player.getSkills().values())
				{
					int id = s.getId();
					if (_forbiddenSkills.contains(id))
						player.addStatFuncs(s.getStatFuncs(player));
				}
			}
			if (!_buffs.isEmpty())
			{
				for (int i : _buffs.keySet())
				{
					Integer skill_lvl = _buffs.get(i);
					if (skill_lvl == null)
						continue;
					player.stopSkillEffects(i);
				}
			}
		}		
	}
	
	public void giveRewards(Player player)
	{
		if (_pvpReward.isEmpty())
			return;
		
		for (EventReward reward : _pvpReward)
		{
			if ((reward != null) && ((reward.chance == 100) || (Rnd.get(100) < reward.chance)))
				player.addItem("L2CustomZone", reward.id, reward.count, player, true);
		}
	}
	
	private void checkForbiddenItems(Player player)
	{
		if (player == null)
			return;
		
		boolean f = false;
		List<ItemInstance> _items = new ArrayList<>();
		for (ItemInstance item : player.getInventory().getItems())
			if (item != null)
			{
				// int bodyPart = item.getItem().getBodyPart();
				if (this._forbiddenItems.contains(item.getItemId()))
				{
					f = true;
					if (item.isEquipped())
					{
						_items.add(item);
						player.getInventory().unequipItemInBodySlotAndRecord(item);
					}
				}
			}
		if (f)
		{
			final InventoryUpdate iu = new InventoryUpdate();
			iu.addItems(_items);
			player.sendPacket(iu);
			player.broadcastUserInfo();
		}
		player.setForbItems(_forbiddenItems);
		_items.clear();
	}
	
	static class EventReward
	{
		int id;
		int count;
		int chance;
		
		public EventReward(int id, int count, int chance)
		{
			this.id = id;
			this.count = count;
			this.chance = chance;
		}
	}
	
	public boolean getIp()
	{
		return _rewardIp;
	}
	
	public boolean getHwid()
	{
		return _rewardHwid;
	}
	
	public boolean getClan()
	{
		return _rewardClan;
	}
	
	public boolean getParty()
	{
		return _rewardParty;
	}
}
