package net.sf.l2j.gameserver.model.actor.instance;

import java.util.Calendar;
import java.util.StringTokenizer;

import net.sf.l2j.commons.lang.StringUtil;

import net.sf.l2j.gameserver.data.manager.RaidBossManager;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.spawn.BossSpawn;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public final class BossRespawnTime2 extends Gatekeeper
{
	// private final Map<Integer, Integer> boss_map = new ConcurrentHashMap<>(); // Key = boss id , Value = teleport id
	
	int[] bosses_id =
	{
		60010,
		25325,
		60024,
		60007,
		25163,
		25019,
		25126
	};// desired ids to display
	int teleport_counter;
	
	public BossRespawnTime2(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String currentCommand = st.nextToken();
		
		// boss_map.put(60010,37);
		// boss_map.put(25325,38);
		// boss_map.put(60024,39);
		// boss_map.put(60007,40);
		// boss_map.put(25163,41);
		// boss_map.put(25019,42);
		// boss_map.put(25126,43);
		
		/*
		 * if (currentCommand.startsWith("rbteleport")) { Integer medusa, barakiel, hydra, thanatos, typhon, poseidon, minotaur; medusa = barakiel = hydra = thanatos = typhon = poseidon = minotaur = 0; final StringBuilder sb_60010 = new StringBuilder(); // Medusa sb final StringBuilder sb_25325 =
		 * new StringBuilder(); // Barakiel sb final StringBuilder sb_60024 = new StringBuilder(); // Thanatos sb final StringBuilder sb_60007 = new StringBuilder(); // Hydra sb final StringBuilder sb_25163 = new StringBuilder(); // Typhon sb final StringBuilder sb_25019 = new StringBuilder(); //
		 * Poseidon sb final StringBuilder sb_25126 = new StringBuilder(); // Minotaur sb final BossSpawn bs_60010 = RaidBossManager.getInstance().getBossSpawn(60010); // Medusa rb final BossSpawn bs_25325 = RaidBossManager.getInstance().getBossSpawn(25325); // Barakiel rb final BossSpawn bs_60024 =
		 * RaidBossManager.getInstance().getBossSpawn(60024); // Thanatos rb final BossSpawn bs_60007 = RaidBossManager.getInstance().getBossSpawn(60007); // Hydra rb final BossSpawn bs_25163 = RaidBossManager.getInstance().getBossSpawn(25163); // Typhon rb final BossSpawn bs_25019 =
		 * RaidBossManager.getInstance().getBossSpawn(25019); // Poseidon rb final BossSpawn bs_25126 = RaidBossManager.getInstance().getBossSpawn(25126); // Minotaur rb // final StringBuilder sb = new StringBuilder(); //raidboss // final StringBuilder sb2 = new StringBuilder(); // final
		 * StringBuilder sb_grand = new StringBuilder(); //grandboss // RaidBosses' respawn time long time_60010 = bs_60010.getRespawnTime(); // Medusa respawn long time_25325 = bs_25325.getRespawnTime(); // Barakiel respawn long time_60024 = bs_60024.getRespawnTime(); // Thanatos respawn long
		 * time_60007 = bs_60007.getRespawnTime(); // Hydra repsawn long time_25163 = bs_25163.getRespawnTime(); // Typhon respawn long time_25019 = bs_25019.getRespawnTime(); // Poseidon respawn long time_25126 = bs_25126.getRespawnTime(); // Minotaur respawn // RaidBosses' names String name_60010
		 * = bs_60010.getBoss().getName(); // Medusa String name_25325 = bs_25325.getBoss().getName(); // Barakiel String name_60024 = bs_60024.getBoss().getName(); // Thanatos String name_60007 = bs_60007.getBoss().getName(); // Hydra String name_25163 = bs_25163.getBoss().getName(); // Typhon
		 * String name_25019 = bs_25019.getBoss().getName(); // Poseidon String name_25126 = bs_25126.getBoss().getName(); // Minotaur // 60010 Medusa if (time_60010 == 0) { sb_60010.append("<font color=\"aa9970\">" + name_60010 + " Is Alive!"); medusa = 35; } else if (time_60010 < 0) {
		 * sb_60010.append("<font color=\"FF0000\"> " + name_60010 + " Is Dead.</font>"); } else { time_60010 = bs_60010.getRespawnTime() - Calendar.getInstance().getTimeInMillis(); sb_60010.append("<font color=\"b09979\">" + name_60010 + "</font>" + "<font color=\"b09979\">will spawn in</font>" +
		 * ConverTime(time_60010)); } // 25325 Barakiel if (time_25325 == 0) { sb_25325.append("<font color=\"b09979\">" + name_25325 + " Is Alive!"); barakiel = 34; } else if (time_25325 < 0) { sb_25325.append("<font color=\"FF0000\"> " + name_25325 + " Is Dead.</font>"); } else { time_25325 =
		 * bs_25325.getRespawnTime() - Calendar.getInstance().getTimeInMillis(); sb_25325.append("<font color=\"b09979\">" + name_25325 + "</font> " + "<font color=\"b09979\">will spawn in</font>" + ConverTime(time_25325)); } // 60024 Thanatos if (time_60024 == 0) {
		 * sb_60024.append("<font color=\"b09979\">" + name_60024 + " Is Alive!"); thanatos = 37; } else if (time_60024 < 0) { sb_60024.append("<font color=\"FF0000\"> " + name_60024 + " Is Dead.</font>"); } else { time_60024 = bs_60024.getRespawnTime() - Calendar.getInstance().getTimeInMillis();
		 * sb_60024.append("<font color=\"b09979\">" + name_60024 + "</font> " + "<font color=\"b09979\">will spawn in</font>" + ConverTime(time_60024)); } // 60007 Hydra if (time_60007 == 0) { sb_60007.append("<font color=\"b09979\">" + name_60007 + " Is Alive!"); hydra = 33; } else if (time_60007
		 * < 0) { sb_60007.append("<font color=\"FF0000\"> " + name_60007 + " Is Dead.</font><br1>"); } else { time_60007 = bs_60007.getRespawnTime() - Calendar.getInstance().getTimeInMillis(); sb_60007.append("<font color=\"b09979\">" + name_60007 + "</font> " +
		 * "<font color=\"b09979\">will spawn in</font>" + ConverTime(time_60007)); } // 25163 Typhon if (time_25163 == 0) { sb_25163.append("<font color=\"b09979\">" + name_25163 + " Is Alive!"); typhon = 25; } else if (time_25163 < 0) { sb_25163.append("<font color=\"FF0000\"> " + name_25163 +
		 * " Is Dead.</font>"); } else { time_25163 = bs_25163.getRespawnTime() - Calendar.getInstance().getTimeInMillis(); sb_25163.append("<font color=\"b09979\">" + name_25163 + "</font> " + "<font color=\"b09979\">will spawn in</font>" + ConverTime(time_25163)); } // 25019 Poseidon if
		 * (time_25019 == 0) { sb_25019.append("<font color=\"b09979\">" + name_25019 + " Is Alive!"); poseidon = 36; } else if (time_25019 < 0) { sb_25019.append("<font color=\"FF0000\"> " + name_25019 + " Is Dead.</font>"); } else { time_25019 = bs_25019.getRespawnTime() -
		 * Calendar.getInstance().getTimeInMillis(); sb_25019.append("<font color=\"b09979\">" + name_25019 + "</font> " + "<font color=\"b09979\">will spawn in</font>" + ConverTime(time_25019)); } // 25126 Minotaur if (time_25126 == 0) { sb_25126.append("<font color=\"b09979\">" + name_25126 +
		 * " Is Alive!"); minotaur = 38; } else if (time_25126 < 0) { sb_25126.append("<font color=\"FF0000\"> " + name_25126 + " Is Dead.</font><br1>"); } else { time_25126 = bs_25126.getRespawnTime() - Calendar.getInstance().getTimeInMillis(); sb_25126.append("<font color=\"b09979\">" + name_25126
		 * + "</font> " + "<font color=\"b09979\">will spawn in</font>" + ConverTime(time_25126)); } // GrandBosses // for (Map.Entry<Integer, StatSet> set : GrandBossManager.getInstance().getBosses().entrySet() ) // { // Its working? Didnt test it but respawn times seems messy , if u think its ok ,
		 * i can show u sth else that makes me worry.show me /* GrandBoss boss = GrandBossManager.getInstance().getBoss(29028); if(boss == null) { System.out.println("npe error:" + boss); } String name = boss.getName(); StatSet setas = GrandBossManager.getInstance().getStatSet(29028); long time =
		 * setas.getLong("respawn_time"); // if(set.getKey() == 29046 || set.getKey() == 29047) // list of grand boss to NOT show //continue; if (time == 0) { sb_grand.append("<font color=\"b09979\">" +name +" Is Alive!</font><br1>"); } else if (time<0) {
		 * sb_grand.append("<font color=\"FF0000\"> "+name +" Is Dead.</font><br1>"); } else { time = setas.getLong("respawn_time") - Calendar.getInstance().getTimeInMillis();
		 * sb_grand.append("<font color=\"b09979\">"+name+"</font> "+ConverTime(time)+" <font color=\"b09979\">time to spawn.</font><br1>"); } //}
		 */
		
		/*
		 * final NpcHtmlMessage html = new NpcHtmlMessage(0); // html.setFile("data/html/raidinformer/9004.htm"); html.setFile("data/html/gatekeeper/9003-3.htm"); // String filename = getHtmlPath(getNpcId(), 0); // html.setFile(filename); // String filename = "data/html/raidinformer/" + getNpcId() +
		 * ".htm"; // filename = getHtmlPath(getNpcId(), 0); // NpcHtmlMessage html = new NpcHtmlMessage(getObjectId()); // html.setFile(filename); html.replace("%objectId%", getObjectId()); // html.replace("%bosslist%", sb.toString()); html.replace("%medusa_%", sb_60010.toString());
		 * html.replace("%num_medusa%", medusa); html.replace("%barakiel_%", sb_25325.toString()); html.replace("%num_barakiel%", barakiel); html.replace("%thanatos_%", sb_60024.toString()); html.replace("%num_thanatos%", thanatos); html.replace("%hydra_%", sb_60007.toString());
		 * html.replace("%num_hydra%", hydra); html.replace("%typhon_%", sb_25163.toString()); html.replace("%num_typhon%", typhon); html.replace("%poseidon_%", sb_25019.toString()); html.replace("%num_poseidon%", poseidon); html.replace("%minotaur_%", sb_25126.toString());
		 * html.replace("%num_minotaur%", minotaur); // html.replace("%grandboss_list%", sb_grand.toString()); player.sendPacket(html); }
		 */
		
		// OLD CODE
		if (currentCommand.startsWith("rb_myteleport"))
		{
			final StringBuilder sb = new StringBuilder(); // raidboss
			teleport_counter = 37;
			for (int boss : bosses_id) // if boss id exists on the desired list.
			{
				
				final BossSpawn bs = RaidBossManager.getInstance().getBossSpawn(boss);
				
				if (bs == null)
				{
					System.out.println("npe error:" + boss);
					continue;
				}
				
				long time = bs.getRespawnTime();
				// int id = bs.getSpawn().getNpcId();
				String name = bs.getBoss().getName();
				
				if (name == null)
				{
					System.out.println("npe error:" + bs.getBoss().getNpcId());
					continue;
				}
				
				if (time == 0)
				{
					
					// StringUtil.append(sb,"<font color=\"b09979\">" + name + " Is Alive!" + "</font>" + "<a action=\"bypass -h npc_%objectId%_teleport "+teleport_counter+"\"> Teleport </a><br>");
					// sb.append("<font color=\"b09979\">" + name + " Is Alive!" + "</font>" + "<a action=\"bypass -h npc_%objectId%_teleport " + boss + "\">" + "Teleport" + "</a><br>");
					StringUtil.append(sb, "<table  align=\"center\" width=305>" + "<tr>" + "<td align=\"center\" width=50><font color=\"b09979\">" + name + " Is Alive!" + "</font>" + "<button value=\" \" action=\"bypass -h npc_%objectId%_teleport " + teleport_counter + "\"" + " width=32 height=32 back=\"CustomIconPack02.select2\" fore=\"CustomIconPack02.select2\">" + "</td>" + "</tr></table><br>");
					
				}
				else if (time < 0)
				{
					sb.append("<font color=\"FF0000\"> " + name + " Is Dead.</font><br1>");
				}
				else
				{
					time = bs.getRespawnTime() - Calendar.getInstance().getTimeInMillis();
					sb.append("<font color=\"b09979\">" + name + "</font> " + ConverTime(time) + " <font color=\"b09979\">time left to spawn.</font><br1>");
				}
				
				final NpcHtmlMessage html = new NpcHtmlMessage(0);
				html.setFile("data/html/gatekeeper/9004-3.htm");
				// html.replace("%objectId%",getObjectId());
				html.replace("%bosslist%", sb.toString());
				html.replace("%objectId%", getObjectId());
				player.sendPacket(html);
				teleport_counter++;
				
				// html.replace("%boss_num%", bs.getSpawn().getNpcId());
				
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
