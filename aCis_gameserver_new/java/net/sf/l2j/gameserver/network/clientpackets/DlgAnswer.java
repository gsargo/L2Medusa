package net.sf.l2j.gameserver.network.clientpackets;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.enums.LootRule;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.Tournament.TournamentManager;
import net.sf.l2j.gameserver.model.entity.Tournament.model.TournamentTeam;
import net.sf.l2j.gameserver.model.group.Party;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ExShowScreenMessage;

public final class DlgAnswer extends L2GameClientPacket
{
	private int _messageId;
	private int _answer;
	private int _requesterId;
	
	@Override
	protected void readImpl()
	{
		_messageId = readD();
		_answer = readD();
		_requesterId = readD();
	}
	
	@Override
	public void runImpl()
	{
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (_messageId == SystemMessageId.RESSURECTION_REQUEST_BY_S1.getId() || _messageId == SystemMessageId.DO_YOU_WANT_TO_BE_RESTORED.getId())
			player.reviveAnswer(_answer);
		else if (_messageId == SystemMessageId.S1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId())
			player.teleportAnswer(_answer, _requesterId);
		else if (_messageId == 1983 && Config.ALLOW_WEDDING)
			player.engageAnswer(_answer);
		else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE.getId())
			player.activateGate(_answer, 1);
		else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE.getId())
			player.activateGate(_answer, 0);
		
		else if (_messageId == SystemMessageId.S1.getId())
		{
			if (TournamentManager.getInstance().isTournamentTeleporting())
			{
				if (_answer == 1)
				{
					player.teleportTo(Config.TOURNAMENT_NPC_LOCATION.getX(), Config.TOURNAMENT_NPC_LOCATION.getY(), Config.TOURNAMENT_NPC_LOCATION.getZ(), 500);
				}
			}
			else if (player.isTournamentTeamBeingInvited())
			{
				Player leader = World.getInstance().getPlayer(player.getTournamentTeamRequesterId());
				if (leader != null)
				{
					TournamentTeam team = leader.getTournamentTeam();
					if (_answer == 1)
					{
						if (team == null)
						{
							team = new TournamentTeam(leader, player);
							player.sendPacket(new ExShowScreenMessage("Your Team have been created!", 3000));
						}
						else
						{
							team.addMember(player);
							leader.sendPacket(new ExShowScreenMessage(player.getName() + " entered your team.", 3000));
							player.sendPacket(new ExShowScreenMessage("You entered " + team.getName() + ".", 3000));
						}
						Party party = leader.getParty();
						if (party != null)
						{
							party.addPartyMember(player);
						}
						else
						{
							party = new Party(leader, player, LootRule.ITEM_LOOTER);
							team.setParty(party);
						}
						// String pageToOpen = null;
						// TournamentFightType type = TournamentFightType.NONE;
						// switch (team.getMembers().size())
						// {
						// case 1:
						// pageToOpen = "fights/F1X1";
						// type = TournamentFightType.F1X1;
						// break;
						// case 2:
						// pageToOpen = "fights/F2X2";
						// type = TournamentFightType.F2X2;
						// break;
						// case 3:
						// pageToOpen = "fights/F3X3";
						// type = TournamentFightType.F3X3;
						// break;
						// case 4:
						// pageToOpen = "fights/F4X4";
						// type = TournamentFightType.F4X4;
						// break;
						// case 5:
						// pageToOpen = "fights/F5X5";
						// type = TournamentFightType.F5X5;
						// break;
						// case 9:
						// pageToOpen = "fights/F9X9";
						// type = TournamentFightType.F9X9;
						// break;
						//
						// }
						// TournamentManager.getInstance().showHtml(leader, pageToOpen, type);
						// TournamentManager.getInstance().showHtml(player, pageToOpen, type);
						
					}
					else
					{
						leader.sendMessage(player.getName() + " denied your Tournament Team request.");
						return;
					}
				}
				player.setTournamentTeamRequesterId(0);
				player.setTournamentTeamBeingInvited(false);
			}
		}
	}
}