package net.sf.l2j.gameserver.model.actor.instance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sf.l2j.commons.pool.ConnectionPool;

import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.xml.PlayerData;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.EnchantResult;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class RankingManager extends Npc
{
	public RankingManager(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}	
	
	 private final StringBuilder _pvp = new StringBuilder();
	 private final StringBuilder _pks = new StringBuilder();
	 private long _nextUpdate;
	
	 public static boolean isBetween(int x, int lower, int upper) 
	 {
		  return lower <= x && x <= upper;
	 }
	 
	 protected String getColor(int index)
	    {
		 if (isBetween(index, 100, 499))
			 return "<font color=FFFF00>";
		 else if(isBetween(index, 500, 749))
			 return "<font color=FFFF00>";
		 else
			 return "<font color=FFFF00>";
	    }
	 
	 
		
	/*@Override
	public void onBypassFeedback(Player player, String command)
	{
		if (player.isProcessingTransaction())
		{
			player.sendPacket(SystemMessageId.ALREADY_TRADING);
			return;
		}
		
		if (player.getActiveEnchantItem() != null)
		{
			player.setActiveEnchantItem(null);
			player.sendPacket(EnchantResult.CANCELLED);
			player.sendPacket(SystemMessageId.ENCHANT_SCROLL_CANCELLED);
		}
		
		else if (command.startsWith("topPvp"))
		{
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			//StringBuilder sb = new StringBuilder();
			_pvp.append("<html><body><center><br>Top 15 PvP:<br><br1>");
			try (Connection con = ConnectionPool.getConnection())
			{
				@SuppressWarnings("resource")
				PreparedStatement stm = con.prepareStatement("SELECT char_name,pvpkills,accesslevel,vip,online FROM characters ORDER BY pvpkills DESC LIMIT 15");
				@SuppressWarnings("resource")
				ResultSet rSet = stm.executeQuery();
				while (rSet.next())
				{
					String pl = rSet.getString("char_name");
					int accessLevel = rSet.getInt("accesslevel");
					if (accessLevel > 0)
					{
						continue;
					}
					int pvpKills = rSet.getInt("pvpkills");
					if (pvpKills == 0)
					{
						continue;
					}
									
					int vip = rSet.getInt("vip");
					if (vip < 0)
					{
						continue;
					}
					String Vip_status = vip == 1 ? "<font color=\"D4AF37\">Yes</font>" : "<font color=\"FF0000\">No</font>";	
					
					int online = rSet.getInt("online");				
					String status = online == 1 ? "<font color=\"00FF00\">Online</font>" : "<font color=\"FF0000\">Offline</font>";
					_pvp.append("<table  align=\"center\" width=305"+"<tr>"+"<td align=\"center\" width=30>"+"Player: " + "</td>" +"<td align=\"center\" width=30>" + "PvPs: "+ "</td>" +"<td align=\"center\" width=30>" + "Rank: "+ "</td>"  +"<td align=\"center\" width=30>" +"Premium: "+ "</td>" +"<td align=\"center\" width=30>"+ "Status: "+ "</td>"+"</tr>"
					+"<tr><td align=\"center\" width=30>"+ "<font color=\"LEVEL\">"+pl+" "+"</font></td>"+ "<td align=\"center\" width=30>"+pvpKills +" "+"</td>" + "<td align=\"center\" width=30>" +player.getRank() +" " +"</td>" + "<td align=\"center\" width=30>" + Vip_status+" " +"</td>" +"<td align=\"center\" width=30>" + status+"</td></tr></table>");
				}
			}
			catch (Exception e)
			{
				System.out.println("Error while selecting top 15 pvp from database.");
			}
			
			_pvp.append("</body></html>");
			htm.setHtml(_pvp.toString());
			player.sendPacket(htm);
		}
		else if (command.startsWith("topPk"))
		{
			NpcHtmlMessage htm = new NpcHtmlMessage(0);
			//StringBuilder sb = new StringBuilder();
			_pks.append("<html><body><center><br>Top 15 PK:<br><br1>");
			try (Connection con = ConnectionPool.getConnection())
			{
				@SuppressWarnings("resource")
				PreparedStatement stm = con.prepareStatement("SELECT char_name,pkkills,accesslevel,vip,online FROM characters ORDER BY pkkills DESC LIMIT 15");
				@SuppressWarnings("resource")
				ResultSet rSet = stm.executeQuery();
				while (rSet.next())
				{
					int accessLevel = rSet.getInt("accesslevel");
					if (accessLevel > 0)
					{
						continue;
					}
					int pkKills = rSet.getInt("pkkills");
					if (pkKills == 0)
					{
						continue;
					}		
					
					String pl = rSet.getString("char_name");
					
					int vip = rSet.getInt("vip");
					if (vip < 0)
					{
						continue;
					}
					String Vip_status = vip == 1 ? "<font color=\"D4AF37\">Yes</font>" : "<font color=\"FF0000\">No</font>";
					int online = rSet.getInt("online");
					String status = online == 1 ? "<font color=\"00FF00\">Online</font>" : "<font color=\"FF0000\">Offline</font>";
					_pks.append("<table  align=\"center\" width=305"+"<tr>"+"<td align=\"center\" width=30>"+"Player: " + "</td>" +"<td align=\"center\" width=30>" + "PKs: "+ "</td>" +"<td align=\"center\" width=30>" + "Rank: "+ "</td>"  +"<td align=\"center\" width=30>" +"Premium: "+ "</td>" +"<td align=\"center\" width=30>"+ "Status: "+ "</td>"+"</tr>"
						+"<tr><td align=\"center\" width=30>"+ "<font color=\"LEVEL\">"+pl+" "+"</font></td>"+ "<td align=\"center\" width=30>"+pkKills +" "+"</td>" + "<td align=\"center\" width=30>" +player.getRank() +" " +"</td>" + "<td align=\"center\" width=30>" + Vip_status+" " +"</td>" +"<td align=\"center\" width=30>" + status+"</td></tr></table>");
				}
			}
			catch (Exception e)
			{
				System.out.println("Error while selecting top 15 pk from database.");
			}
			_pks.append("</body></html>");
			htm.setHtml(_pks.toString());
			player.sendPacket(htm);
		}
	}
	*/
	@Override
	public void showChatWindow(Player player, int val)
	{
		String Noble_status = player.isNoble() ? "<img src=\"L2UI_CH3.myinfo_nobleicon\" width=16 height=16>" :  "-";
		String Hero_status = player.isHero() ? "<img src=\"L2UI_CH3.myinfo_heroicon\" width=16 height=16>" :  "-";
		
		String filename = "data/html/rankingmanager/ranking.htm";
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		
		
		StringBuilder htm = new StringBuilder("");
		htm.append("<table width=295><tr><td width=80 align=left>"+player.getRank()+"</td>"
			+ "<td valign=top width=100 align=center>"+player.getClass_byIcon() + player.getRankColor() +player.getName()+"</font></td>"
			+ "<td width=80 align=right>"+player.isOrNotVIP_panel() + "</td>"
			+ "</tr>"
			+ "</table>");
		htm.append("<img src=\"Sek.cbui371\" width=295 height=1>\r\n"
			+ "<br><br>\r\n"
			+ "<table bgcolor=000000 width=307 border=0>"
			+"<tr>"
			+"<td width=60></td><td align=center width=50>Status</td><td width=60></td>"
			+"</tr>"
			+"</table>"
			+ "<table bgcolor=FFF000 width=315 border=0>\r\n"
			+ "<tr>\r\n"
			+ "<td width=2></td>\r\n"
			+ "<td width=36 >Noblesse:</td><td>"+Noble_status+"</td>\r\n"
			+ "<td width=35></td>\r\n"	
			+ "<td width=28 >Hero:</td><td>"+Hero_status +"</td>\r\n"
			+ "<td width=55></td>\r\n"
			//+ "<td width=60 >Race: "+player.getRaceIcon() +"</td>\r\n"
			+ "<td width=60 >Fame: "+player.getRecomHave() +"</td>\r\n"
			+ "</tr>\r\n"
			+ "</table>\r\n"
			+ "<br><br>\r\n"
			+ "<table bgcolor=000000 width=307 border=0>"
			+"<tr>"
			+"<td width=60></td><td align=center width=50>Rank</td><td width=60></td>"
			+"</tr>"
			+"</table>"
			+ "<table bgcolor=000DFF width=312 border=0>\r\n"
			+ "<tr>\r\n"
			+ "<td width=2></td>\r\n"
			+ "<td width=40 >Rank: "+player.get_rank_by_text() +"</td>\r\n"
			+ "<td width=35></td>\r\n"
			+ "<td width=40 >PvP: "+player.getPvpKills() +"</td>\r\n"
			+ "<td width=60></td>\r\n"
			+ "<td width=60 >PK: "+player.getPkKills() +"</td>\r\n"
			+ "</tr>\r\n"
			+ "</table>\r\n"
			+ "<br><br>\r\n"
			+ "<table bgcolor=000000 width=307 border=0>"
			+"<tr>"
			+"<td width=60></td><td align=center width=50>Class</td><td width=60></td>"
			+"</tr>"
			+"</table>"
			+ "<table bgcolor=FFF000 width=307 border=0>\r\n"
			+ "<tr>\r\n"
			+ "<td width=2></td>\r\n"
			+ "<td width=45>Base: "+PlayerData.getInstance().getClassNameById(player.getBaseClass())+"</td>\r\n"
			+ "<td width=55 >Current: "+player.getClassId().toString()+"</td>\r\n"
			+ "<td width=35 >Level: "+player.getStatus().getLevel()+"</td>\r\n"
			+ "</tr>\r\n"
			+ "</table>");


		html.replace("%rank%", htm.toString());
		player.sendPacket(html);
		
		//player.sendPacket(ActionFailed.STATIC_PACKET);
		//String filename = "data/html/npc/ranking-no.htm";	
		/*String filename = "data/html/rankingmanager/ranking.htm";
		
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		//html_3.replace("%mansion_bosslist%", sb2.toString());
		html.replace("%playerName%", player.getName());
		html.replace("%rank%", player.getRank());
		player.sendPacket(html);*/
	}
	
	public String onFirstTalk(Npc npc, Player player)
	{
		String path = "data/html/CommunityBoard/XXXX.html";

		//_showTopBoard()
		return null;
	}
}