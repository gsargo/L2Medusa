package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.data.manager.PartyMatchRoomManager;
import net.sf.l2j.gameserver.enums.MessageType;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Tournament.TournamentManager;
import net.sf.l2j.gameserver.model.entity.Tournament.model.TournamentTeam;
import net.sf.l2j.gameserver.model.group.Party;
import net.sf.l2j.gameserver.model.group.PartyMatchRoom;
import net.sf.l2j.gameserver.network.serverpackets.ExPartyRoomMember;
import net.sf.l2j.gameserver.network.serverpackets.PartyMatchDetail;

public final class RequestWithdrawParty extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
	}
	
	@Override
	protected void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		TournamentTeam team = player.getTournamentTeam();
		
		final Party party = player.getParty();
		if (party == null)
			return;
		
		if(player.getDungeon()!=null && player.isInsidePartyDungeonZone())
		{
			player.sendMessage("You can not withdraw from your party while participating in a dungeon!");
			return;
		}

		
		if(player.isInTournamentMatch())
		{
			player.sendMessage("You can not withdraw from your party while participating in Battle Arena!");
			return;
		}
		
		if(player.getTournamentTeam()!=null && !player.isInTournamentMode() && !player.isInTournamentMatch())
		{
			if(player.getTournamentTeam().getMembers().size()<=2)
				player.getTournamentTeam().disbandTeam();
			else
				player.getTournamentTeam().removeMember(player);		
		}
		
		if(player.isInTournamentMode() && !player.isInTournamentMatch())
		{
			for (Player pl : team.getMembers())
			{
				pl.sendMessage("One of your team members has left. The participation has been cancelled! ");
			}
			TournamentManager.getInstance().getRegisteredTournamentTeams().remove(player.getTournamentTeam());
			
			if(player.getTournamentTeam().getMembers().size()<=2)
				player.getTournamentTeam().disbandTeam();
			else
				player.getTournamentTeam().removeMember(player);
		}
		/*
		if(player.isInTournamentMode() && !player.isInTournamentMatch() && !player.getParty().isLeader(player)) // remove from tournament team as well
		{
			if (player.getTournamentTeam()!=null)
			{
				player.getTournamentTeam().removeMember(player);
			}
		}
		
		if(player.isInTournamentMode() && !player.isInTournamentMatch() && player.getParty().isLeader(player))
		{
			player.sendMessage("You can not withdraw from your party while participating in Battle Arena! You can use .arenadismiss instead!");
			return;
		}
		*/
		party.removePartyMember(player, MessageType.LEFT);
			
		
		if (player.isInPartyMatchRoom())
		{
			final PartyMatchRoom room = PartyMatchRoomManager.getInstance().getRoom(player.getPartyRoom());
			if (room != null)
			{
				player.sendPacket(new PartyMatchDetail(room));
				player.sendPacket(new ExPartyRoomMember(room, 0));
				
				// Remove PartyMatchRoom member.
				room.removeMember(player);
			}
		}
	}
}