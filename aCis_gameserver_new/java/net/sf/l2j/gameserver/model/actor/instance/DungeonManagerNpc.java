package net.sf.l2j.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import Base.Dungeon.DungeonManager;

public class DungeonManagerNpc extends Folk
{
	public DungeonManagerNpc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (command.startsWith("dungeon") && !player.isVIP())
		{
			if (DungeonManager.getInstance().isInDungeon(player) || player.isInOlympiadMode())
			{
				player.sendMessage("You are currently unable to enter a Dungeon. Please try again later.");
				return;
			}
			
			int dungeonId = Integer.parseInt(command.substring(8));
			
			if (dungeonId == 1)
			{
				if (player.isInParty())
				{
					player.sendMessage("You can only enter this Dungeon solo.");
					return;
				}
				else if (player.getStatus().getLevel() < 76)
				{
					player.sendMessage("Players above level 76 can apply entrance.");
					return;
				}
				String ip = player.getIP(); // Is ip or hwid?
				if (DungeonManager.getInstance().getPlayerData().containsKey(ip) && DungeonManager.getInstance().getPlayerData().get(ip)[dungeonId] > 0)
				{
					long total = (DungeonManager.getInstance().getPlayerData().get(ip)[dungeonId] + (1000 * 60 * 60 * 12)) - System.currentTimeMillis();
					
					if (total > 0)
					{
						int hours = (int) (total / 1000 / 60 / 60);
						int minutes = (int) ((total / 1000 / 60) - hours * 60);
						int seconds = (int) ((total / 1000) - (hours * 60 * 60 + minutes * 60));
						
						player.sendMessage("You need to wait " + String.format("%02d:%02d:%02d", hours, minutes, seconds) + " to enter the dungeon again.");
						//return;
					}
				}
				
				if (player.getInventory().getItemByItemId(Config.DUNGEON_COIN_ID) == null)//item for solo dungeon
				{
					DungeonManagerNpc.mainHtml(player, 0);
					return;
				}
				
				player.destroyItemByItemId("Quest", Config.DUNGEON_COIN_ID, Config.CONT_DUNGEON_ITEM, player, true);
				DungeonManager.getInstance().enterDungeon(dungeonId, player);
			}
			else if (dungeonId == 2)
			{
				/*if (player.getParty() == null || player.getParty().getMembersCount() < 2)
				{
					player.sendMessage("You need to be in a party with " + 2 + " or more members to enter this Dungeon.");
					//return;
				}
				/*else if (!player.getParty().isLeader(player))
				{
					player.sendMessage("The party leader can only apply entrance.");
					return;
				}
				*/
				String ip = player.getIP(); // Is ip or hwid?
				if (DungeonManager.getInstance().getPlayerData().containsKey(ip) && DungeonManager.getInstance().getPlayerData().get(ip)[dungeonId] > 0)
				{
					long total = (DungeonManager.getInstance().getPlayerData().get(ip)[dungeonId] + (1000 * 60 * 60 * 12)) - System.currentTimeMillis();
					
					if (total > 0)
					{
						int hours = (int) (total / 1000 / 60 / 60);
						int minutes = (int) ((total / 1000 / 60) - hours * 60);
						int seconds = (int) ((total / 1000) - (hours * 60 * 60 + minutes * 60));
						
						player.sendMessage("You need to wait " + String.format("%02d:%02d:%02d", hours, minutes, seconds) + " to enter the dungeon.");
						//return;
					}
				}
				
				if (player.getInventory().getItemByItemId(Config.PARTY_DUNGEON_COIN_ID) == null) // item for party dungeon
				{
					DungeonManagerNpc.mainHtml(player, 0);
					return;
				}
				player.destroyItemByItemId("Quest", Config.PARTY_DUNGEON_COIN_ID, Config.CONT_DUNGEON_ITEM, player, true);
				DungeonManager.getInstance().enterDungeon(dungeonId, player);
			}
		}
		
		if (command.startsWith("dungeon") && player.isVIP())
		{
			if (DungeonManager.getInstance().isInDungeon(player) || player.isInOlympiadMode())
			{
				player.sendMessage("You are currently unable to enter a Dungeon. Please try again later.");
				return;
			}
			
			int dungeonId = Integer.parseInt(command.substring(8));
			
			if (dungeonId == 1)
			{
				if (player.isInParty())
				{
					player.sendMessage("You can only enter this Dungeon alone.");
					return;
				}
				else if (player.getStatus().getLevel() < 76)
				{
					player.sendMessage("Players above level 76 can apply entrance.");
					return;
				}
				String ip = player.getIP(); // Is ip or hwid?
				if (DungeonManager.getInstance().getPlayerData().containsKey(ip) && DungeonManager.getInstance().getPlayerData().get(ip)[dungeonId] > 0)
				{
					long total = (DungeonManager.getInstance().getPlayerData().get(ip)[dungeonId] + (1000 * 60 * 60 * 12)) - System.currentTimeMillis();
					
					if (total > 0)
					{
						int hours = (int) (total / 1000 / 60 / 60);
						int minutes = (int) ((total / 1000 / 60) - hours * 60);
						int seconds = (int) ((total / 1000) - (hours * 60 * 60 + minutes * 60));
						
						player.sendMessage("You need to wait " + String.format("%02d:%02d:%02d", hours, minutes, seconds) + " to enter the dungeon again.");
						//return;
					}
				}
				
				DungeonManager.getInstance().enterDungeon(dungeonId, player);
			}
			else if (dungeonId == 2)
			{
				if (player.getParty() == null || player.getParty().getMembersCount() < 2)
				{
					player.sendMessage("You need to be in a party with " + 2 + " or more members to enter this Dungeon.");
					//return;
				}
				else if (!player.getParty().isLeader(player))
				{
					player.sendMessage("The party leader can only apply entrance.");
					return;
				}
				
				String ip = player.getIP(); // Is ip or hwid?
				if (DungeonManager.getInstance().getPlayerData().containsKey(ip) && DungeonManager.getInstance().getPlayerData().get(ip)[dungeonId] > 0)
				{
					long total = (DungeonManager.getInstance().getPlayerData().get(ip)[dungeonId] + (1000 * 60 * 60 * 12)) - System.currentTimeMillis();
					
					if (total > 0)
					{
						int hours = (int) (total / 1000 / 60 / 60);
						int minutes = (int) ((total / 1000 / 60) - hours * 60);
						int seconds = (int) ((total / 1000) - (hours * 60 * 60 + minutes * 60));
						
						player.sendMessage("You need to wait " + String.format("%02d:%02d:%02d", hours, minutes, seconds) + " to enter the dungeon again.");
						//return;
					}
				}
				DungeonManager.getInstance().enterDungeon(dungeonId, player);
			}
		}
	}
	
	public static void mainHtml(Player activeChar, int time)
	{
		if (activeChar.isOnline())
		{
			NpcHtmlMessage nhm = new NpcHtmlMessage(5);
			StringBuilder html1 = new StringBuilder("");
			html1.append("<html><head><title>Dungeon</title></head><body>");
			html1.append("<br>");
			html1.append("<font color=\"LEVEL\">Grimoire of Darkness</font> is required to enter the solo dungeon.");
			html1.append("<br>");
			html1.append("<font color=\"LEVEL\">True Grimoire of Darkness</font> is required by the party leader, to enter the party dungeon.");
			html1.append("</body></html>");
			nhm.setHtml(html1.toString());
			activeChar.sendPacket(nhm);
		}
		
	}
	
	public static String getPlayerStatus(Player player, int dungeonId)
	{
		String s = "You can enter";
		
		String ip = player.getIP(); // Is ip or hwid?
		if (DungeonManager.getInstance().getPlayerData().containsKey(ip) && DungeonManager.getInstance().getPlayerData().get(ip)[dungeonId] > 0)
		{
			long total = (DungeonManager.getInstance().getPlayerData().get(ip)[dungeonId] + (1000 * 60 * 60 * 12)) - System.currentTimeMillis();
			
			if (total > 0)
			{
				int hours = (int) (total / 1000 / 60 / 60);
				int minutes = (int) ((total / 1000 / 60) - hours * 60);
				int seconds = (int) ((total / 1000) - (hours * 60 * 60 + minutes * 60));
				
				s = String.format("%02d:%02d:%02d", hours, minutes, seconds);
			}
		}
		
		return s;
	}
	
	@Override
	public void showChatWindow(Player player, int val)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(getObjectId());
		htm.setFile("data/html/mods/dungeon/" + getNpcId() + (val == 0 ? "" : "-" + val) + ".htm");
		
		String[] s = htm.getHtml().split("%");
		for (int i = 0; i < s.length; i++)
		{
			if (i % 2 > 0 && s[i].contains("dung "))
			{
				StringTokenizer st = new StringTokenizer(s[i]);
				st.nextToken();
				htm.replace("%" + s[i] + "%", getPlayerStatus(player, Integer.parseInt(st.nextToken())));
			}
		}
		
		htm.replace("%objectId%", getObjectId() + "");
		
		player.sendPacket(htm);
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String filename = "";
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "data/html/mods/dungeon/" + filename + ".htm";
	}
}