package net.sf.l2j.gameserver.network.clientpackets;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.communitybbs.CommunityBoard;
import net.sf.l2j.gameserver.communitybbs.manager.TopBBSManager;
import net.sf.l2j.gameserver.data.manager.HeroManager;
import net.sf.l2j.gameserver.data.xml.AdminData;
import net.sf.l2j.gameserver.enums.FloodProtector;
import net.sf.l2j.gameserver.handler.AdminCommandHandler;
import net.sf.l2j.gameserver.handler.BypassHandler;
import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.handler.IBypassHandler;
import net.sf.l2j.gameserver.handler.IVoicedCommandHandler;
import net.sf.l2j.gameserver.handler.VoicedCommandHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.OlympiadManagerNpc;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.memo.PlayerMemo1;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.gameserver.scripting.QuestState;

import Base.Dungeon.InstanceManager1;
import c1c0s.VoteManagerAPI.L2HOPZONE;
import c1c0s.VoteManagerAPI.L2JBRASIL;
import c1c0s.VoteManagerAPI.L2NETWORK;
import c1c0s.VoteManagerAPI.L2TOPZONE;
import c1c0s.VoteManagerAPI.VoteSites;

public final class RequestBypassToServer extends L2GameClientPacket
{
	private static final Logger GMAUDIT_LOG = Logger.getLogger("gmaudit");
	
	private String _command;
	private static final Location tovillage = new Location(41296, -48219, -800);
	private static final Location primeval_north = new Location(11067, -24828, -3640);
	private static final Location primeval_north2 = new Location(8623, -23953, -3752);
	private static final Location primeval_south = new Location(5633, -14334, -3704);
	private static final Location primeval_south2 = new Location(6097, -17431, -3696);
	private static final Location primeval_east = new Location(8570, -18907, -3576);
	private static final Location primeval_east2 = new Location(5222, -21726, -3304);
	private static final Location primeval_west = new Location(3620, -20525, -3304);
	private static final Location primeval_west2 = new Location(8570, -20769, -3432);
	
	private static final Location acropolis_north = new Location(213372, 178232, -3464);
	private static final Location acropolis_south = new Location(211777, 182264, -3656);
	private static final Location acropolis_east = new Location(209556, 180218, -3144);
	private static final Location acropolis_west = new Location(214299, 181363, -3656);

	@Override
	protected void readImpl()
	{
		_command = readS();
	}
	
	@Override
	protected void runImpl()
	{
		if (_command.isEmpty())
			return;
		
		if (!getClient().performAction(FloodProtector.SERVER_BYPASS))
			return;
		
		final Player player = getClient().getPlayer();
		if (player == null)
			return;
		
		if (player.isGM())
			player.sendMessage("[C:] " + _command);
		
		if (_command.startsWith("admin_"))
		{
			String command = _command.split(" ")[0];
			
			final IAdminCommandHandler ach = AdminCommandHandler.getInstance().getHandler(command);
			if (ach == null)
			{
				if (player.isGM())
					player.sendMessage("The command " + command.substring(6) + " doesn't exist.");
				
				LOGGER.warn("No handler registered for admin command '{}'.", command);
				return;
			}
			
			if (!AdminData.getInstance().hasAccess(command, player.getAccessLevel()))
			{
				player.sendMessage("You don't have the access rights to use this command.");
				LOGGER.warn("{} tried to use admin command '{}' without proper Access Level.", player.getName(), command);
				return;
			}
			
			if (Config.GMAUDIT)
				GMAUDIT_LOG.info(player.getName() + " [" + player.getObjectId() + "] used '" + _command + "' command on: " + ((player.getTarget() != null) ? player.getTarget().getName() : "none"));
			
			ach.useAdminCommand(_command, player);
		}
		else if (_command.startsWith("bp_"))
		{
			String command = _command.split(" ")[0];
			IBypassHandler bh = BypassHandler.getInstance().getBypassHandler(command);
			if (bh == null)
			{
				GMAUDIT_LOG.warning("No handler registered for bypass '" + command + "'");
				return;
			}
			bh.handleBypass(_command, player);
		}
		else if (_command.startsWith("player_help "))
		{
			final String path = _command.substring(12);
			if (path.indexOf("..") != -1)
				return;
			
			final StringTokenizer st = new StringTokenizer(path);
			final String[] cmd = st.nextToken().split("#");
			
			final NpcHtmlMessage html = new NpcHtmlMessage(0);
			html.setFile("data/html/help/" + cmd[0]);
			if (cmd.length > 1)
			{
				final int itemId = Integer.parseInt(cmd[1]);
				html.setItemId(itemId);
				
				if (itemId == 7064 && cmd[0].equalsIgnoreCase("lidias_diary/7064-16.htm"))
				{
					final QuestState qs = player.getQuestList().getQuestState("Q023_LidiasHeart");
					if (qs != null && qs.getCond() == 5 && qs.getInteger("diary") == 0)
						qs.set("diary", "1");
				}
			}
			html.disableValidation();
			player.sendPacket(html);
		}
		if (_command.startsWith("bp_reward"))
		{
			int type = Integer.parseInt(_command.substring(10));
			int itemId = 0;
			int count = 1;
			
			switch (type)
			{
				case 0:
				{
					itemId = Config.DUNGEON_ITEM_RENEWAL0; //Pandora's Box x15
					count=15;
					break;
				}
				case 1:
				{
					itemId = Config.DUNGEON_ITEM_RENEWAL1; //Fossils x10
					count=10;
					break;
				}
				case 2:
				{
					itemId = Config.DUNGEON_ITEM_RENEWAL2; //Top grade Lf x3
					count=3;
					break;
				}
				case 3:
				{
					itemId = Config.DUNGEON_ITEM_RENEWAL3;//High grade Lf x5
					count=5;
					break;
				}
				case 4:
				{
					itemId = Config.DUNGEON_ITEM_RENEWAL4; //Adena 10KK
					count=10000000;
					break;
				}
				case 5:
				{
					itemId = Config.DUNGEON_ITEM_RENEWAL5; //Kykeon x40
					count=40;
					break;
				}
				case 6:
				{
					itemId = Config.DUNGEON_ITEM_RENEWAL6;
					
					break;
				}
				case 7:
				{
					itemId = Config.DUNGEON_ITEM_RENEWAL7;
					
					break;
				}
				case 8:
				{
					itemId = Config.DUNGEON_ITEM_RENEWAL8;
					
					break;
				}
				case 9:
				{
					itemId = Config.DUNGEON_ITEM_RENEWAL9;
					
					break;
				}
				case 10:
				{
					itemId = Config.DUNGEON_ITEM_RENEWAL10;
					
					break;
				}
				case 11:
				{
					itemId = Config.DUNGEON_PARTY_ITEM_RENEWAL0;
					
					break;
				}
				case 12:
				{
					itemId = Config.DUNGEON_PARTY_ITEM_RENEWAL1;
					
					break;
				}
				case 13:
				{
					itemId = Config.DUNGEON_PARTY_ITEM_RENEWAL2;
					
					break;
				}
				case 14:
				{
					itemId = Config.DUNGEON_PARTY_ITEM_RENEWAL3;
					
					break;
				}
				case 15:
				{
					itemId = Config.DUNGEON_PARTY_ITEM_RENEWAL4;
					
					break;
				}
			}
			
			if (itemId == 0)
			{
				System.out.println(player.getName() + " tried to send custom id on dungeon solo rewards.");
				return;
			}
			
			if (player.getDungeon() != null)
			{
				ItemInstance item = player.addItem("dungeon reward", itemId, count, null, true);
				item.setEnchantLevel(25);
				player.getInventory().equipItemAndRecord(item);
				PlayerMemo1.setVar(player, "delete_temp_item_" + item.getObjectId(), item.getObjectId(), System.currentTimeMillis() + (1000 * 60 * 60 * 5));
				InstanceManager1.getInstance().getInstance1(0);
				player.setDungeon(null);
				player.teleportTo(Config.DUNGEON_SPAWN_X, Config.DUNGEON_SPAWN_Y, Config.DUNGEON_SPAWN_Z, Config.DUNGEON_SPAWN_RND);
			}
			else
			{
				player.sendMessage("You can not get rewarded");
			}
			
		}
		else if (_command.startsWith("npc_"))
		{
			if (!player.validateBypass(_command))
				return;
			
			int endOfId = _command.indexOf('_', 5);
			String id;
			if (endOfId > 0)
				id = _command.substring(4, endOfId);
			else
				id = _command.substring(4);
			
			try
			{
				final WorldObject object = World.getInstance().getObject(Integer.parseInt(id));
				
				if (object instanceof Npc && endOfId > 0 && player.getAI().canDoInteract(object))
					((Npc) object).onBypassFeedback(player, _command.substring(endOfId + 1));
				
				player.sendPacket(ActionFailed.STATIC_PACKET);
			}
			catch (NumberFormatException nfe)
			{
			}
		}
		else if (_command.startsWith("_showTopBoard"))
		{
			//TopBBSManager.getInstance().showBoard(player, "0");
		}
		else if (_command.startsWith("voiced_"))
		{
			String command = _command.split(" ")[0];
			IVoicedCommandHandler ach = VoicedCommandHandler.getInstance().getVoicedCommandHandler(_command.substring(7));
			if (ach == null)
			{
				player.sendMessage("The command " + command.substring(7) + " does not exist!");
				LOGGER.warn("No handler registered for command '" + _command + "'");
				return;
			}
			ach.useVoicedCommand(_command.substring(7), player, null);
		}
		// Navigate throught Manor windows
		else if (_command.startsWith("manor_menu_select?"))
		{
			WorldObject object = player.getTarget();
			if (object instanceof Npc)
				((Npc) object).onBypassFeedback(player, _command);
		}
		else if (_command.startsWith("bbs_") || _command.startsWith("_bbs") || _command.startsWith("_friend") || _command.startsWith("_mail") || _command.startsWith("_block"))
		{
			CommunityBoard.getInstance().handleCommands(getClient(), _command);
		}
		else if (_command.startsWith("Quest "))
		{
			if (!player.validateBypass(_command))
				return;
			
			String[] str = _command.substring(6).trim().split(" ", 2);
			if (str.length == 1)
				player.getQuestList().processQuestEvent(str[0], "");
			else
				player.getQuestList().processQuestEvent(str[0], str[1]);
		}
		else if (_command.startsWith("_match"))
		{
			String params = _command.substring(_command.indexOf("?") + 1);
			StringTokenizer st = new StringTokenizer(params, "&");
			int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
			int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
			int heroid = HeroManager.getInstance().getHeroByClass(heroclass);
			if (heroid > 0)
				HeroManager.getInstance().showHeroFights(player, heroclass, heroid, heropage);
		}
		else if (_command.startsWith("_diary"))
		{
			String params = _command.substring(_command.indexOf("?") + 1);
			StringTokenizer st = new StringTokenizer(params, "&");
			int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
			int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
			int heroid = HeroManager.getInstance().getHeroByClass(heroclass);
			if (heroid > 0)
				HeroManager.getInstance().showHeroDiary(player, heroclass, heroid, heropage);
		}
		else if (_command.startsWith("arenachange")) // change
		{
			final boolean isManager = player.getCurrentFolk() instanceof OlympiadManagerNpc;
			if (!isManager)
			{
				// Without npc, command can be used only in observer mode on arena
				if (!player.isInObserverMode() || player.isInOlympiadMode() || player.getOlympiadGameId() < 0)
					return;
			}
			
			// Olympiad registration check.
			if (OlympiadManager.getInstance().isRegisteredInComp(player))
			{
				player.sendPacket(SystemMessageId.WHILE_YOU_ARE_ON_THE_WAITING_LIST_YOU_ARE_NOT_ALLOWED_TO_WATCH_THE_GAME);
				return;
			}
			
			final int arenaId = Integer.parseInt(_command.substring(12).trim());
			player.enterOlympiadObserverMode(arenaId);
		}
		
		else if (_command.startsWith("droplist"))
		{
			StringTokenizer st = new StringTokenizer(_command, " ");
			st.nextToken();
			
			int npcId = Integer.parseInt(st.nextToken());
			int page = st.hasMoreTokens() ? Integer.parseInt(st.nextToken()) : 1;
			
			if (st.hasMoreTokens())
				player.ignored(Integer.parseInt(st.nextToken()));
			
			Npc.sendNpcDrop(player, npcId, page);
		}
		//custom code for tovillage (PvP Area & Acropolis )
		if (_command.startsWith("go_tovillage") && (player.isInsidePvPZone())) // Primeval
		{
			if (player.isDead())
			{
				player.doRevive();
				player.teleportTo(tovillage, 20);
			}
			return;		
		}
		else if (_command.startsWith("primeval_north") && player.isInsidePvPZone())
		{
			//int random_tele = (int)(Math.random() * 10 + 1);
			if (player.isDead())
			{
				if((Math.random() * 10 + 1)>5)
				{
					player.doRevive();
					player.teleportTo(primeval_north, 20);
				}
				else
				{
					player.doRevive();
					player.teleportTo(primeval_north2, 20);
				}
			}
			return;
		}
		
		else if (_command.startsWith("primeval_west") && player.isInsidePvPZone())
		{
			//int random_tele = (int)(Math.random() * 10 + 1);
			if (player.isDead())
			{
				if((Math.random() * 10 + 1)>5)
				{
					player.doRevive();			
					player.teleportTo(primeval_west, 20);
				}
				else
				{
					player.doRevive();			
					player.teleportTo(primeval_west2, 20);
				}
			}
			return;
		}
		
		else if (_command.startsWith("primeval_east") && player.isInsidePvPZone())
		{
			if (player.isDead())
			{
				if((Math.random() * 10 + 1)>5)
				{
					player.doRevive();		
					player.teleportTo(primeval_east, 20);
				}
				else
				{
					player.doRevive();		
					player.teleportTo(primeval_east2, 20);
				}
			}
			return;
		}
		
		else if (_command.startsWith("primeval_south") && player.isInsidePvPZone())
		{
			//int random_tele = (int)(Math.random() * 10 + 1);
			if (player.isDead())
			{
				if((Math.random() * 10 + 1)>5)
				{
					player.doRevive();		
					player.teleportTo(primeval_south, 20);
				}
				else
				{
					player.doRevive();		
					player.teleportTo(primeval_south2, 20);
				}
					
			}
			return;
		}
		
		
		if (_command.startsWith("acropolis_north") && player.isInsideClanwarZone())
		{
			if (player.isDead())
			{
				player.doRevive();		
				player.teleportTo(acropolis_north, 20);
			}
			return;
		}
		else if (_command.startsWith("acropolis_south") && player.isInsideClanwarZone())
		{
			if (player.isDead())
			{
				player.doRevive();			
				player.teleportTo(acropolis_south, 20);
			}
			return;
		}
		else if (_command.startsWith("acropolis_east") && player.isInsideClanwarZone())
		{
			if (player.isDead())
			{
				player.doRevive();		
				player.teleportTo(acropolis_east, 20);
			}
			return;
		}
		else if (_command.startsWith("acropolis_west") && player.isInsideClanwarZone())
		{
			if (player.isDead())
			{
				player.doRevive();		
				player.teleportTo(acropolis_west, 20);
			}
			return;
		}
		else if (_command.startsWith("v_hopzone") )
		{
			if (player.isEligibleToVote(VoteSites.L2HOPZONE))
			{
				L2HOPZONE rewardSite = new L2HOPZONE();
				rewardSite.checkVoteReward(player);
				return;
			}
			player.sendMessage("L2HOPZONE: " + player.getVoteCountdown(VoteSites.L2HOPZONE));
			return;
		}
		else if (_command.startsWith("v_topzone") )
		{
			if (player.isEligibleToVote(VoteSites.L2TOPZONE))
			{
				L2TOPZONE rewardSite = new L2TOPZONE();
				rewardSite.checkVoteReward(player);
				return;
			}
			player.sendMessage("L2TOPZONE: " + player.getVoteCountdown(VoteSites.L2TOPZONE));
			return;
		}
		else if (_command.startsWith("v_jbrasil") )
		{
			if (player.isEligibleToVote(VoteSites.L2JBRASIL))
			{
				L2JBRASIL rewardSite = new L2JBRASIL();
				rewardSite.checkVoteReward(player);
				return;
			}
			player.sendMessage("L2JBRASIL: " + player.getVoteCountdown(VoteSites.L2JBRASIL));
			return;
		}
		else if (_command.startsWith("v_network"))
		{
			if (player.isEligibleToVote(VoteSites.L2NETWORK))
			{
				L2NETWORK rewardSite = new L2NETWORK();
				rewardSite.checkVoteReward(player);
				return;
			}
			player.sendMessage("L2NETWORK: " + player.getVoteCountdown(VoteSites.L2NETWORK));
			return;
		}
		
	}
}