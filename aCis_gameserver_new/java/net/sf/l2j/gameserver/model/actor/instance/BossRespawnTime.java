package net.sf.l2j.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import net.sf.l2j.commons.lang.StringUtil;

import net.sf.l2j.gameserver.data.manager.RaidBossManager;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.enums.BossStatus;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.spawn.BossSpawn;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public final class BossRespawnTime extends Gatekeeper
{
	// private final Map<Integer, Integer> boss_map = new ConcurrentHashMap<>(); // Key = boss id , Value = teleport id
	
	int[] bosses_id =
	{
		60010,
		25325,
		60007,
		25019,
		25126,
		25163,
		60036,
		60050,
		62359 // Laestrygon
	};// desired ids to display
	
	int[] mansion_bosses_id =
	{
		60036,
		60050
	};// desired ids to display for hades mansion
	
	
		// int teleport_counter;
	
	public BossRespawnTime(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	private static int get_tele(int _bossid)
	{
		
		switch (_bossid)
		{
			case 60010:// Hydra teleport id from teleports.xml
				return 38;
			
			case 25325:// Barakiel teleport id from teleports.xml
				return 39;
			
			case 60007:
				return 40;
			
			case 25019:
				return 42;
			
			case 25126:
				return 43;
			
			case 25163:
				return 44;
				
			case 60036: // Hades
				return 41;
				
			case 60050: // Cerberus
				return 41;
				
			case 62359: //Laestrygon
				return 45;
			
			default:
				return 0;
		}
		
	}
	
	private static int get_tele_mansion(int _bossid)
	{
		switch (_bossid)
		{
		case 60036: // Hades
			return 0;
			
		case 60050: // Cerberus
			return 1;
			
		default:
			return 0;
		}
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String currentCommand = st.nextToken();
		
		// CODE
		if (currentCommand.startsWith("rb_myteleport"))
		{
			final StringBuilder sb = new StringBuilder("<font color=\"353535\">_________________________________________________</font>\r\n"
				+ "<br>"); // raidboss
			// teleport_counter=37;
			for (int boss : bosses_id) // if boss id exists on the desired list.
			{
				// final BossSpawn bs = RaidBossManager.getInstance().getBossSpawn(boss);
				final BossSpawn bs = RaidBossManager.getInstance().getBossSpawn(boss);
				
				if (bs == null)
				{
					System.out.println("npe error:" + boss);
					continue;
				}
				
				final BossStatus status = bs.getStatus();
							
				final String name = NpcData.getInstance().getTemplate(boss).getName();
				// System.out.println("String value for ID: " + boss + "= " + name + ", time= " + time);
				
				if (status == BossStatus.ALIVE)					
					StringUtil.append(sb, "<table  align=\"center\" width=295 height=28 border=0  >" + "<tr>" + "<td align=\"left\" width=145><font color=\"b09979\">     " + name + "</font>" + "<font color=\"81b287\">" + " is alive!" + "</font> </td>" + "<td valign=top align=\"left\" width=40 >" + " <button value=\" \" action=\"bypass -h npc_%objectId%_teleport " + get_tele(boss) + "\"" + " width=28 height=28 back=\"CustomIconPack02.select3\" fore=\"CustomIconPack02.select3\"> </td>" +
				"</tr></table><br1><font color=\"353535\">_________________________________________________</font>");
				else
					sb.append("<table align=\"center\" width=305 height=28 >" + "<tr><td align=\"left\" width=140>" + "<font color=\"b09979\">" +"    "+ name + "</font> " + bs.getTimeLeft() + " <font color=\"b09979\">time left to spawn.</font></td></tr></table><br1><br1><font color=\"353535\">_________________________________________________</font>");	
				
				
				final NpcHtmlMessage html = new NpcHtmlMessage(0); 
				html.setFile("data/html/gatekeeper/9003-3.htm");
				// html.replace("%objectId%",getObjectId());
				html.replace("%bosslist%", sb.toString());
				html.replace("%objectId%", getObjectId());				
				
				player.sendPacket(html);
				//player.sendPacket(html_2);
	
				
				// teleport_counter++;
				
			}
			
		}
		
		if (currentCommand.startsWith("mansion"))
		{
			if(player.isInCombat())
			{
				player.sendMessage("You can not use the Tesseract while being in combat");
				return;
			}
			final StringBuilder sb2 = new StringBuilder(); // hades + cerberus
			// teleport_counter=37;
			for (int boss : mansion_bosses_id) // if boss id exists on the desired list.
			{
				// final BossSpawn bs = RaidBossManager.getInstance().getBossSpawn(boss);
				final BossSpawn bs2 = RaidBossManager.getInstance().getBossSpawn(boss);
				
				if (bs2 == null)
				{
					System.out.println("npe error:" + boss);
					continue;
				}
				
				final BossStatus status = bs2.getStatus();
				
				final String name = NpcData.getInstance().getTemplate(boss).getName();
				// System.out.println("String value for ID: " + boss + "= " + name + ", time= " + time);
				
				if (status == BossStatus.ALIVE)
					StringUtil.append(sb2, "<table  align=\"center\" width=305>" + "<tr>" + "<td align=\"center\" width=305><font color=\"FF0000\">" + name + "</font>" + "<font color=\"81b287\">" + " is alive!" + "</font>" + "<button value=\"Enter Palace\" action=\"bypass -h npc_%objectId%_teleport "+get_tele_mansion(boss) + "\"" + " width=100 height=25 back=\"fbuttons.bigbutton_down\" fore=\"fButtons.bigbutton_over\">" + "</td>" + "</tr></table><br>");					
				else				
					sb2.append("<table align=\"center\" width=305" + "<tr><td>" +"<font color=\"b09979\">" + name + "</font> " + bs2.getTimeLeft() + " <font color=\"b09979\">time left to spawn.</font></center></td></tr></table><br1>"); //flaming portal (hades + cerberus)	
						
				final NpcHtmlMessage html_3 = new NpcHtmlMessage(0);
				html_3.setFile("data/html/gatekeeper/62356-1.htm");
				html_3.replace("%mansion_bosslist%", sb2.toString()); //Hades Mansions GK
				html_3.replace("%objectId%", getObjectId());			
			
				player.sendPacket(html_3);				
			}
			
		}
		
		// System.out.println("error:" + command);
		super.onBypassFeedback(player, command);
		
	}
	
	private static String ConverTime(long mseconds)
	{
		long reminder = mseconds;
		
		long hours = (long) Math.ceil((mseconds / (60 * 60 * 1000)));
		reminder = mseconds - (hours * 60 * 60 * 1000);
		
		long minutes = (long) Math.ceil((reminder / (60 * 1000)));
		reminder = reminder - (minutes * (60 * 1000));
		
		long seconds = (long) Math.ceil((reminder / 1000));
		
		return hours + ":" + minutes + ":" + seconds;
	}
	
}
