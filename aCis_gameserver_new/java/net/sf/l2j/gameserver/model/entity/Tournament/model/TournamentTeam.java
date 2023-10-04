package net.sf.l2j.gameserver.model.entity.Tournament.model;

import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.LootRule;
import net.sf.l2j.gameserver.enums.MessageType;
import net.sf.l2j.gameserver.enums.actors.ClassId;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Tournament.TournamentManager;
import net.sf.l2j.gameserver.model.entity.Tournament.enums.TournamentFightType;
import net.sf.l2j.gameserver.model.entity.instance.InstanceManager;
import net.sf.l2j.gameserver.model.group.Party;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import net.sf.l2j.util.RewardHolder;

/**
 * @author Rouxy
 */
public class TournamentTeam
{
	private Player leader;
	private Player target;
	private List<Player> members = new ArrayList<>();
	private Party party;
	private int teamFightId;
	
	public TournamentTeam(Player leader, Player target)
	{
		this.leader = leader;
		this.target = target;
		
		members.add(0, leader);
		leader.setTournamentTeam(this);
		leader.sendMessage("Your Tournament fight has been created.");
		
		if (target != null)
		{
			leader.sendMessage(target.getName() + " entered your Battle Arena team.");
			target.sendMessage(leader.getName() + " entered your Battle Arena team.");
			members.add(1, target);
			target.setTournamentTeam(this);
			
		}
		
	}
	
	public List<Player> getMembers()
	{
		return members;

	}
	
	public void setMembers(List<Player> members)
	{
		this.members = members;
	}
	
	public Player getLeader()
	{
		return leader;
	}
	
	public void setLeader(Player leader)
	{
		this.leader = leader;
	}
	
	public Player getTarget()
	{
		return target;
	}
	
	public void setTarget(Player target)
	{
		this.target = target;
	}
	
	public void sendMessage(String text)
	{
		for (Player player : getMembers())
		{
			
			player.sendMessage("[Tournament]: " + text);
			
		}
	}
	
	public void screenMessage(String text)
	{
		for (Player player : getMembers())
		{
			
			player.sendPacket(new ExShowScreenMessage("[Tournament]: " + text, 4000));
			
		}
	}
	
	public String getName()
	{
		return leader.getName() + "'s Team";
	}
	
	public void addMember(Player player)
	{
		if (player == null || getMembers().contains(player))
			return;
		
		getMembers().add(player);
		player.setTournamentTeam(this);
		player.sendMessage("You entered " + leader.getName() + "'s Battle Arena team.");
		sendMessage("Player " + player.getName() + " joined your Battle Arena team.");
		
	}
	
	public Party getParty()
	{
		return party;
	}
	
	public void setParty(Party party)
	{
		this.party = party;
	}
	
	public int getCountOfClass(ClassId classId)
	{
		int count = 0;
		for (Player player : getMembers())
		{
			if (player.getClassId() == classId)
			{
				count++;
			}
		}
		return count;
	}
	
	public int getHealersCount()
	{
		int count = 0;
		for (Player player : getMembers())
		{
			if (player.getClassId().isHealer())
			{
				count++;
			}
		}
		return count;
		
	}
	
	public int getTankersCount()
	{
		int count = 0;
		for (Player player : getMembers())
		{
			if (player.getClassId().isTanker())
			{
				count++;
			}
		}
		return count;
		
	}
	
	public int getDaggersCount()
	{
		int count = 0;
		for (Player player : getMembers())
		{
			if (player.getClassId().isDagger())
			{
				count++;
			}
		}
		return count;
		
	}
	
	public int getArchersCount()
	{
		int count = 0;
		for (Player player : getMembers())
		{
			if (player.getClassId().isArcher())
			{
				count++;
			}
		}
		return count;
		
	}
	
	public void setInTournamentMatch(boolean val)
	{
		for (Player player : getMembers())
		{
			player.setInTournamentMatch(val);
		}
	}
	
	public void setFightId(int id)
	{
		for (Player player : getMembers())
		{
			player.setTournamentFightId(id);
		}
		teamFightId = id;
	}
	
	public void paralyze()
	{
		for (Player player : getMembers())
		{
			player.setTarget(null);
			player.setInvul(true);
			player.setIsParalyzed(true);
			player.broadcastUserInfo();
		}
	}
	
	public void unparalyze()
	{
		for (Player player : getMembers())
		{
			player.setTarget(null);
			player.setInvul(false);
			player.setIsParalyzed(false);
			player.broadcastUserInfo();
			
		}
	}
	
	public boolean teamIsDefeated()
	{
		for (Player player : getMembers())
		{
			if (!player.isDead())
			{
				return false;
			}
		}
		return true;
	}
	
	public int getAliveMembers()
	{
		int count = 0;
		for (Player player : getMembers())
		{
			if (player.isDead())
				continue;
			//else
				count++;
		}
		return count;
	}
	
	public void reward(boolean winner)
	{
		String text = winner ? "Your team has won the Battle!" : "Your team has lost the Battle.";
		screenMessage(text);
		sendMessage(text);
		for (Player player : getMembers())
		{
			
			for (RewardHolder reward : winner ? Config.TOURNAMENT_FIGHT_REWARD_WINNER : Config.TOURNAMENT_FIGHT_REWARD_LOOSER)
			{
				player.addItem("TournamentReward", reward.getItemId(), reward.getCount(), player, true);
			}
		}
		
	}
	
	public void teleportBack()
	{
		for (Player player : getMembers())
		{
			player.teleportTo(player.getLastX(), player.getLastY(), player.getLastZ(), 0);
		}
	}
	
	public void removeTournamentTeam()
	{
		TournamentManager.getInstance().getRegisteredTournamentTeams().remove(this);
	}
	
	public void setTournamentFightType(TournamentFightType type)
	{
		for (Player player : getMembers())
		{
			player.setTournamentFightType(type);
		}
	}
	
	public void removeMember(Player member)
	{
		
		if (!getMembers().contains(member))
			return;
		member.setTournamentFightId(0);
		member.setTournamentFightType(TournamentFightType.NONE);
		member.setTournamentTeam(null);
		member.setTournamentTeamBeingInvited(false);
		member.sendMessage("Your tournament Team has dispersed.");
		if (party != null)
			party.removePartyMember(member, MessageType.LEFT);
		members.remove(member);
		if (members.size() < 1)
		{
			disbandTeam();
		}
	}
	
	public void prepareToFight()
	{
		// first disband a possible old party
		Party party = null;
		for (Player player : getMembers())
		{
			if (player.isInParty())
			{
				player.getParty().disband();
			}
		}
		if (getMembers().size() > 1)
		{
			party = new Party(leader, getMembers().get(1), LootRule.ITEM_LOOTER);
		}
		for (Player player : getMembers())
		{
			if (party != null && !player.isInParty())
			{
				party.addPartyMember(player);
			}
			// revive dead players
			if (player.isDead())
			{
				player.doRevive();
			}
			// heal players
			player.getStatus().setCpHpMp(player.getStatus().getMaxCp(), player.getStatus().getMaxHp(), player.getStatus().getMaxMp());
			
			// reset tournament match damages
			player.setTournamentMatchDamage(0);
		}
		
	}
	
	public void resetTeamMatchDamage()
	{
		for (Player player : getMembers())
		{
			player.setTournamentMatchDamage(0);
		}
	}
	
	public void backInstance()
	{
		for (Player player : getMembers())
		{
			player.setInstance(InstanceManager.getInstance().getInstance(0), true);
			
		}
	}
	
	public void doRevive()
	{
		
		for (Player player : getMembers())
		{
			
			player.doRevive();
			player.getStatus().setCpHpMp(player.getStatus().getMaxCp(), player.getStatus().getMaxHp(), player.getStatus().getMaxMp());
		}
	}
	
	public void disbandTeam()
	{
		if (TournamentManager.getInstance().getRegisteredTournamentTeams().containsKey(this))
		{
			TournamentManager.getInstance().getRegisteredTournamentTeams().remove(this);
		}
		for (Player member : getMembers())
		{
			member.setTournamentFightId(0);
			member.setTournamentFightType(TournamentFightType.NONE);
			member.setTournamentTeam(null);
			member.setTournamentTeamBeingInvited(false);
			member.sendMessage("Your tournament Team has dispersed.");
		}
		if (getParty() != null)
			getParty().disband();
		setParty(null);
	}
	
	public int getFightId()
	{
		return teamFightId;
	}
	
	public void setTeamFightId(int id)
	{
		teamFightId = id;
	}
	
	public boolean isLeader(Player player)
	{
		return player == leader;
	}
	
	public int getTotalDamage()
	{
		int damage = 0;
		for (Player member : getMembers())
		{
			damage += member.getTournamentMatchDamage();
		}
		return damage;
	}
	
	public void addTeamDefeat(TournamentFightType type)
	{
		for (Player member : getMembers())
		{
			member.addTournamentDefeat(type);
		}
	}
	
	public void addTeamVictory(TournamentFightType type)
	{
		for (Player member : getMembers())
		{
			member.addTournamentVictory(type);
		}
	}
	
	public void addTeamTie(TournamentFightType type)
	{
		for (Player member : getMembers())
		{
			member.addTournamentTie(type);
		}
	}
	
	public void addTotalDamageToPlayers(TournamentFightType type)
	{
		for (Player member : getMembers())
		{
			member.addTournamentDamage(type, member.getTournamentMatchDamage());
		}
	}
	
}
