package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.gameserver.enums.LootRule;
import net.sf.l2j.gameserver.enums.actors.ClassId;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.group.Party;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.AskJoinParty;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

public final class RequestJoinParty extends L2GameClientPacket
{
	private String _targetName;
	private int _lootRuleId;
	
	@Override
	protected void readImpl()
	{
		_targetName = readS();
		_lootRuleId = readD();
	}
	
	@Override
	protected void runImpl()
	{
		final Player requestor = getClient().getPlayer();
		if (requestor == null)
			return;
		
		final Player target = World.getInstance().getPlayer(_targetName);
		if (target == null || requestor.alonePenalty || target.alonePenalty)
		{
			requestor.sendPacket(SystemMessageId.FIRST_SELECT_USER_TO_INVITE_TO_PARTY);
			return;
		}
		
		if (target.getBlockList().isBlockingAll() || requestor.partyPenalty || target.partyPenalty)
		{
			requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_BLOCKED_EVERYTHING).addCharName(target));
			return;
		}
		
		if (target.getBlockList().isInBlockList(requestor))
		{
			requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_ADDED_YOU_TO_IGNORE_LIST).addCharName(target));
			return;
		}
		
		if (target.equals(requestor) || target.isCursedWeaponEquipped() || requestor.isCursedWeaponEquipped() || !target.getAppearance().isVisible())
		{
			requestor.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
			return;
		}
		
		if (target.isInParty())
		{
			requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_ALREADY_IN_PARTY).addCharName(target));
			return;
		}
		
		if (target.getClient().isDetached())
		{
			requestor.sendMessage("The player you tried to invite is in offline mode.");
			return;
		}
		
		if (requestor.isInTournamentMatch())
		{
			requestor.sendMessage("You can't invite players in Tournament.");
			return;
		}
		
		if (target.isInJail() || requestor.isInJail())
		{
			requestor.sendMessage("The player you tried to invite is currently jailed.");
			return;
		}
		
		if (target.isInOlympiadMode() || requestor.isInOlympiadMode())
			return;
		
		if (requestor.isProcessingRequest())
		{
			requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
			return;
		}
		
		if (target.isProcessingRequest())
		{
			requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_IS_BUSY_TRY_LATER).addCharName(target));
			return;
		}
		
		final Party party = requestor.getParty();
		if (party != null)
		{
			if (!party.isLeader(requestor))
			{
				requestor.sendPacket(SystemMessageId.ONLY_LEADER_CAN_INVITE);
				return;
			}
			
			if (party.getMembersCount() >= 9)
			{
				requestor.sendPacket(SystemMessageId.PARTY_FULL);
				return;
			}
			
			if (party.getPendingInvitation() && !party.isInvitationRequestExpired())
			{
				requestor.sendPacket(SystemMessageId.WAITING_FOR_ANOTHER_REPLY);
				return;
			}
			
			if (party.getMembers().stream().filter(k -> (k.getClassId() == (ClassId.CARDINAL) || k.getClassId() == (ClassId.BISHOP) || k.getClassId() == (ClassId.EVAS_SAINT) || k.getClassId() == (ClassId.SHILLIEN_SAINT))).count() >= 2) // MAX 2 CARDINALS ON PARTY
			{
				requestor.sendMessage("The maximum ammount of healers in the party, has been reached!."); // Failed to add
				return;
			}
			
			if (party.getMembers().stream().filter(k -> k.getClassId() == (ClassId.DOMINATOR)).count() >= 1) // MAX 1 DOMI ON PARTY
			{
				requestor.sendMessage("The maximum ammount of dominators in the party, has been reached!."); // Failed to add
				return;
			}
			
			if (party.getMembers().stream().filter(k -> k.getClassId() == (ClassId.TITAN)).count() >= 2) // MAX 2 TITAN ON PARTY
			{
				requestor.sendMessage("The maximum ammount of titans in the party, has been reached!."); // Failed to add
				return;
			}
			
			if (party.getMembers().stream().filter(k -> (k.getClassId() == (ClassId.PHOENIX_KNIGHT) || k.getClassId() == (ClassId.HELL_KNIGHT) || k.getClassId() == (ClassId.EVAS_TEMPLAR) || k.getClassId() == (ClassId.SHILLIEN_KNIGHT))).count() >= 2) // MAX 2 tank ON PARTY
			{
				requestor.sendMessage("The maximum ammount of tanks in the party, has been reached!."); // Failed to add
				return;
			}
			
			party.setPendingInvitation(true);
		}
		else
			requestor.setLootRule(LootRule.VALUES[_lootRuleId]);
		
		requestor.onTransactionRequest(target);
		requestor.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.YOU_INVITED_S1_TO_PARTY).addCharName(target));
		
		target.sendPacket(new AskJoinParty(requestor.getName(), (party != null) ? party.getLootRule().ordinal() : _lootRuleId));
	}
}