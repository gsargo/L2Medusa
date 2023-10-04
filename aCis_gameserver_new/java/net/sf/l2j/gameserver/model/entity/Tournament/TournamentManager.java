package net.sf.l2j.gameserver.model.entity.Tournament;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;

import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.sql.PlayerInfoTable;
import net.sf.l2j.gameserver.data.sql.SpawnTable;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.enums.SayType;
import net.sf.l2j.gameserver.handler.AdminCommandHandler;
import net.sf.l2j.gameserver.handler.VoicedCommandHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Creature;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.entity.Tournament.Commands.AdminTournament;
import net.sf.l2j.gameserver.model.entity.Tournament.Commands.VoiceTournament;
import net.sf.l2j.gameserver.model.entity.Tournament.Data.TournamentArenaParser;
import net.sf.l2j.gameserver.model.entity.Tournament.enums.TournamentFightType;
import net.sf.l2j.gameserver.model.entity.Tournament.model.TournamentTeam;
import net.sf.l2j.gameserver.model.entity.Tournament.tasks.TournamentFight;
import net.sf.l2j.gameserver.model.entity.Tournament.tasks.TournamentSearchFights;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.ConfirmDlg;
import net.sf.l2j.gameserver.network.serverpackets.L2GameServerPacket;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import net.sf.l2j.util.Mysql;
import net.sf.l2j.util.PlayerVariables;

/**
 * @author Rouxy
 */
public class TournamentManager
{
	private static final Logger _log = Logger.getLogger(TournamentManager.class.getName());
	private Map<TournamentTeam, TournamentFightType> registeredTournamentTeams = new HashMap<>();
	private Map<Integer, TournamentFight> currentFights = new HashMap<>();
	private Calendar nextEvent;
	private final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
	private Spawn _npcSpawn;
	private boolean running;
	private ScheduledFuture<?> finishEventTask = null;
	private boolean tournamentTeleporting;
	private int allTimeFights = 0;
	TimeZone timeZone = TimeZone.getTimeZone("GMT+3");
	
	public TournamentManager()
	{
		TournamentArenaParser.getInstance();
		ThreadPool.scheduleAtFixedRate(new TournamentSearchFights(TournamentFightType.F1X1), 0, Config.TOURNAMENT_TIME_SEARCH_FIGHTS * 1000);
		ThreadPool.scheduleAtFixedRate(new TournamentSearchFights(TournamentFightType.F2X2), 0, Config.TOURNAMENT_TIME_SEARCH_FIGHTS * 1000);
		ThreadPool.scheduleAtFixedRate(new TournamentSearchFights(TournamentFightType.F3X3), 0, Config.TOURNAMENT_TIME_SEARCH_FIGHTS * 1000);
		ThreadPool.scheduleAtFixedRate(new TournamentSearchFights(TournamentFightType.F4X4), 0, Config.TOURNAMENT_TIME_SEARCH_FIGHTS * 1000);
		ThreadPool.scheduleAtFixedRate(new TournamentSearchFights(TournamentFightType.F5X5), 0, Config.TOURNAMENT_TIME_SEARCH_FIGHTS * 1000);
		ThreadPool.scheduleAtFixedRate(new TournamentSearchFights(TournamentFightType.F9X9), 0, Config.TOURNAMENT_TIME_SEARCH_FIGHTS * 1000);
		VoicedCommandHandler.getInstance().registerHandler(new VoiceTournament());
		AdminCommandHandler.getInstance().registerHandler(new AdminTournament());
		
		startCalculationOfNextEventTime();
	}
	
	public static TournamentManager getInstance()
	{
		return SingleTonHolder._instance;
	}
	
	private static class SingleTonHolder
	{
		protected static TournamentManager _instance = new TournamentManager();
	}
	
	public String getNextTime()
	{
		if (nextEvent.getTime() != null)
		{
			return format.format(nextEvent.getTime());
		}
		return "Erro";
	}
	
	public void startCalculationOfNextEventTime()
	{
		try
		{
			Calendar currentTime = Calendar.getInstance();
			Calendar testStartTime = null;
			long flush2 = 0L;
			long timeL = 0L;
			int count = 0;
			for (String timeOfDay : Config.TOURNAMENT_EVENT_INTERVAL_BY_TIME_OF_DAY)
			{
				testStartTime = Calendar.getInstance();
				testStartTime.setLenient(true);
				String[] splitTimeOfDay = timeOfDay.split(":");
				testStartTime.set(11, Integer.parseInt(splitTimeOfDay[0]));
				testStartTime.set(12, Integer.parseInt(splitTimeOfDay[1]));
				testStartTime.set(13, 0);
				if (testStartTime.getTimeInMillis() < currentTime.getTimeInMillis())
				{
					testStartTime.add(5, 1);
				}
				timeL = testStartTime.getTimeInMillis() - currentTime.getTimeInMillis();
				if (count == 0)
				{
					flush2 = timeL;
					nextEvent = testStartTime;
				}
				if (timeL < flush2)
				{
					flush2 = timeL;
					nextEvent = testStartTime;
				}
				count++;
			}
			_log.info("[Battle Arena]: Next Event time: " + nextEvent.getTime().toString());
			ThreadPool.schedule(new StartEventTask(), flush2);
		}
		catch (Exception e)
		{
			System.out.println("[Battle Arena]: " + e);
		}
	}
	
	public static void toAllOnlinePlayers(L2GameServerPacket packet)
	{
		for (Player player : World.getInstance().getPlayers())
		{
			if (player.isOnline())
				player.sendPacket(packet);
		}
	}
	
	public void announceToAllOnlinePlayers(String text)
	{
		for (Player player : World.getInstance().getPlayers())
		{
			if (player.isOnline())
				player.sendChatMessage(0, SayType.CRITICAL_ANNOUNCE, "", text);
		}
	}
	
	class FinishEventTask implements Runnable
	{
		FinishEventTask()
		{
			
		}
		
		@Override
		public void run()
		{
			finishEvent();
			
		}
		
	}
	
	public static String formatTime(Date date, TimeZone timeZone) 
	{   
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(timeZone);
        
        return sdf.format(date);
    }
	
	
	public void finishEvent()
	{
		_log.info("----------------------------------------------------------------------------");
		_log.info("[Event]: Battle Arena Event has been finished.");
		_log.info("----------------------------------------------------------------------------");
		announceToAllOnlinePlayers("[Event]: Battle Arena Event has just finished");
		announceToAllOnlinePlayers("[Event]: Thank you for participating!");
		// announceToAllOnlinePlayers("[Event]: All fights have been stored");
		announceToAllOnlinePlayers("[Event]: Next event: " + formatTime(nextEvent.getTime(),timeZone)+" GMT +2");
		// announceToAllOnlinePlayers("[Event]: Next event: " + getNextTime());
		unspawnNpc();
		setRunning(false);
		if (getFinishEventTask() != null)
		{
			getFinishEventTask().cancel(true);
			finishEventTask = null;
		}
	}
	
	public void startEvent()
	{
		_log.info("----------------------------------------------------------------------------");
		_log.info("[Event]: Battle Arena Event has started.");
		_log.info("----------------------------------------------------------------------------");
		spawnNpcEvent();
		setRunning(true);
		announceToAllOnlinePlayers("[Event]: Solo and Party PvP Battles!");
		announceToAllOnlinePlayers("[Event]: Battles: 1x1 / 2x2 / 3x3 / 5x5");
		announceToAllOnlinePlayers("[Event]: Battle Arena Teleporter has now spawned in Aden Town!");
		announceToAllOnlinePlayers("[Event]: Event duration: " + Config.TOURNAMENT_EVENT_DURATION + " minutes");
		setFinishEventTask(ThreadPool.schedule(new FinishEventTask(), Config.TOURNAMENT_EVENT_DURATION * 60 * 1000));
		for (Player player : World.getInstance().getPlayers())
		{
			askTeleport(player);
		}
	}
	
	class StartEventTask implements Runnable
	{
		@Override
		public void run()
		{
			startEvent();
			
		}
		
	}
	
	public void unspawnNpc()
	{
		if (_npcSpawn == null)
		{
			return;
		}
		_npcSpawn.getNpc().deleteMe();
		_npcSpawn.setRespawnState(false);
		SpawnTable.getInstance().deleteSpawn(_npcSpawn, true);
	}
	
	public void spawnNpcEvent()
	{
		
		NpcTemplate tmpl = NpcData.getInstance().getTemplate(Config.TOURNAMENT_NPC_ID);
		Location npcLoc = Config.TOURNAMENT_NPC_LOCATION;
		try
		{
			_npcSpawn = new Spawn(tmpl);
			
			_npcSpawn.setLoc(npcLoc.getX(), npcLoc.getY(), npcLoc.getZ(), 281);
			_npcSpawn.setRespawnDelay(1);
			
			SpawnTable.getInstance().addSpawn(_npcSpawn, false);
			
			_npcSpawn.setRespawnState(true);
			_npcSpawn.doSpawn(false);
			_npcSpawn.getNpc().getStatus().setHp(9.99999999E8D);
			_npcSpawn.getNpc().isAggressive();
			_npcSpawn.getNpc().decayMe();
			_npcSpawn.getNpc().spawnMe(_npcSpawn.getNpc().getX(), _npcSpawn.getNpc().getY(), _npcSpawn.getNpc().getZ());
			_npcSpawn.getNpc().broadcastPacket(new MagicSkillUse(_npcSpawn.getNpc(), _npcSpawn.getNpc(), 1034, 1, 1, 1));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void askJoinTeam(Player leader, Player target)
	{
		ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.S1.getId());
		confirm.addString("Do you wish to join " + leader.getName() + "'s Battle Arena team?");
		confirm.addTime(30000);
		target.setTournamentTeamRequesterId(leader.getObjectId());
		target.setTournamentTeamBeingInvited(true);
		target.sendPacket(confirm);
		leader.sendMessage(target.getName() + " was invited to your team.");
		
	}
	
	public void askTeleport(Player player)
	{
		ConfirmDlg confirm = new ConfirmDlg(SystemMessageId.S1.getId());
		confirm.addString("Do you wish to teleport to Battle Arena Zone?");
		confirm.addTime(30000);
		setTournamentTeleporting(true);
		ThreadPool.schedule(new Runnable()
		{
			@Override
			public void run()
			{
				setTournamentTeleporting(false);
			}
		}, 30000);
		player.sendPacket(confirm);
	}
	
	public void debugInfo(String text)
	{
		_log.info("[Tournament]: " + text);
	}
	
	public List<TournamentFight> getCurrentFights(TournamentFightType type)
	{
		List<TournamentFight> list = new ArrayList<>();
		for (Map.Entry<Integer, TournamentFight> entry : currentFights.entrySet())
		{
			if (entry.getValue().getFightType() == type)
				list.add(entry.getValue());
		}
		return list;
	}
	
	public TournamentFight getFight(int id)
	{
		return currentFights.get(id);
	}
	
	public Map<Integer, TournamentFight> getCurrentFights()
	{
		return currentFights;
	}
	
	public void setCurrentFights(Map<Integer, TournamentFight> currentFights)
	{
		this.currentFights = currentFights;
	}
	
	public void onDisconnect(Player player)
	{
		if (player.isInTournamentTeam())
		{
			TournamentTeam team = player.getTournamentTeam();
			team.getMembers().remove(player);
			team.sendMessage(player.getName() + " left the team.");
			if (team.getMembers().size() <= 1)
			{
				team.disbandTeam();
				return;
			}
			if (team.isLeader(player))
			{
				Player newLeader = team.getMembers().get(Rnd.get(team.getMembers().size()));
				if (newLeader != null)
				{
					team.setLeader(newLeader);
					newLeader.sendMessage("You have became the new Battle Arena team leader");
				}
				team.sendMessage(newLeader + " has became the new team Leader");
				
			}
		}
	}
	
	public void onKill(Creature killer, Player killed)
	{
		if (killed.isInTournamentMatch())
		{
			if (killer instanceof Player)
			{
				Player killerPlayer = killer.getActingPlayer();
				if (killerPlayer.isInTournamentMatch())
				{
					if (killerPlayer.getTournamentFightId() == killed.getTournamentFightId() && killed.getTournamentFightId() != 0)
					{
						TournamentFight fight = TournamentManager.getInstance().getFight(killed.getTournamentFightId());
						if (fight != null)
						{
							// add single kill to killer
							killerPlayer.addTournamentKill(killerPlayer.getTournamentFightType());
							killerPlayer.sendMessage("You have killed: " + killed.getName());
							
							if (killed.getTournamentTeam() != null)
							{
								if (killed.getTournamentTeam().teamIsDefeated())
								{
									fight.finish();
								}
							}
						}
					}
				}
			}
		}
	}
	
	public List<TournamentTeam> getRegisteredTeamsByType(TournamentFightType type)
	{
		List<TournamentTeam> teams = new ArrayList<>();
		for (Map.Entry<TournamentTeam, TournamentFightType> entry : registeredTournamentTeams.entrySet())
		{
			if (entry.getValue().equals(type))
			{
				teams.add(entry.getKey());
			}
		}
		return teams;
	}
	
	/**
	 * @return the tournamentTeams
	 */
	public Map<TournamentTeam, TournamentFightType> getRegisteredTournamentTeams()
	{
		return registeredTournamentTeams;
	}
	
	/**
	 * @param tournamentTeams the tournamentTeams to set
	 */
	public void setTournamentTeams(Map<TournamentTeam, TournamentFightType> tournamentTeams)
	{
		this.registeredTournamentTeams = tournamentTeams;
	}
	
	public boolean isInTournamentMode(Player player)
	{
		for (Map.Entry<TournamentTeam, TournamentFightType> entry : registeredTournamentTeams.entrySet())
		{
			if (entry.getKey().getMembers().contains(player))
			{
				return true;
			}
			
		}
		return false;
	}
	
	// Npc html part
	
	public void showHtml(Player player, String page, TournamentFightType type)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setFile("data/html/mods/tournament/" + page + ".htm");
		
		htm.replace("%missingMembers%", getMembersMessageForFightType(player, type));
		htm.replace("%memberslist%", player.getTournamentTeam() != null ? generateMemberList(player.getTournamentTeam()) : "<br><font color=ff0000>There are no members in your team</font>");
		htm.replace("%inviteBoxRegButton%", getInviteBoxOrRegisterButton(player, type));
		htm.replace("%fightType%", type.equals(TournamentFightType.NONE) ? "" : type.name().substring(1).toLowerCase());
		
		// Fight Data
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentVictories().entrySet())
		{
			htm.replace("%victories" + entry.getKey().name() + "%", player.getTournamentVictories().get(entry.getKey()));
		}
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentDefeats().entrySet())
		{
			htm.replace("%defeats" + entry.getKey().name() + "%", player.getTournamentDefeats().get(entry.getKey()));
		}
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentTies().entrySet())
		{
			htm.replace("%ties" + entry.getKey().name() + "%", player.getTournamentTies().get(entry.getKey()));
		}
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentKills().entrySet())
		{
			htm.replace("%kills" + entry.getKey().name() + "%", player.getTournamentKills().get(entry.getKey()));
		}
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentDamage().entrySet())
		{
			htm.replace("%damage" + entry.getKey().name() + "%", player.getTournamentDamage().get(entry.getKey()));
		}
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentDamage().entrySet())
		{
			htm.replace("%dpf" + entry.getKey().name() + "%", getDamagePerFight(player, entry.getKey()));
		}
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentVictories().entrySet())
		{
			htm.replace("%fightsDone" + entry.getKey().name() + "%", player.getTournamentFightsDone(entry.getKey()));
		}
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentVictories().entrySet())
		{
			htm.replace("%teamsReg" + entry.getKey().name() + "%", registeredTournamentTeams.size());
		}
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentVictories().entrySet())
		{
			htm.replace("%activeFights" + entry.getKey().name() + "%", getCurrentFights(entry.getKey()).size());
		}
		
		htm.replace("%allTimeFights%", getAllTimeFights());
		htm.replace("%tourPoints%", player.getTournamentPoints());
		htm.replace("%killstotal%", player.getTotalTournamentKills());
		htm.replace("%totalDmg%", player.getTournamentTotalDamage());
		htm.replace("%playerName%", player.getName());
		htm.replace("%dpfTotal%", getDamagePerFight(player));
		htm.replace("%wdt%", getWinDefeatTie(player));
		htm.replace("%totalFights%", player.getTotalTournamentFightsDone());
		
		player.sendPacket(htm);
	}
	
	public String getInviteBoxOrRegisterButton(Player player, TournamentFightType type)
	{
		StringBuilder sb = new StringBuilder();
		if (getMissingMembersForFightType(player, type) == 0)
		{
			sb.append("<table width=300>");
			sb.append("<tr>");
			sb.append("<td align=center><font color=LEVEL>Your Battle Arena team is ready!!</font></td>");
			sb.append("</tr>");
			sb.append("</table>");
		}
		else
		{
			sb.append("<center>");
			sb.append("Type the name of your partner or use command: <br1><font color=93C47D>\".arenainvite playername\"</font>");
			sb.append("</center>");
			sb.append("<table width=300>");
			sb.append("<tr>");
			sb.append("<td>Player Name</td>");
			sb.append("<td><edit var=\"playerName\" width=120 height=15></td>");
			sb.append("<td><button value=\"Invite\" action=\"bypass -h bp_inviteTournamentMember $playerName\" width=45 height=15 back=\"sek.cbui94\" fore=\"sek.cbui92\"></td>");
			sb.append("</tr>");
			sb.append("</table>");
		}
		
		return sb.toString();
	}
	
	public String getMembersMessageForFightType(Player player, TournamentFightType type)
	{
		if (!player.isInTournamentTeam())
		{
			return "<br><font color=ff0000>You are not a member of a Battle Arena team</font>";
		}
		if (type != TournamentFightType.NONE)
		{
			return "<br>You need to invite <font color=LEVEL>" + getMissingMembersForFightType(player, type) + "</font>members to register in" + type.name().substring(1).toLowerCase() + " fights.";
		}
		return "";
	}
	
	public int getMissingMembersForFightType(Player player, TournamentFightType type)
	{
		int membersCount = 0;
		if (!player.isInTournamentTeam())
		{
			return -1;
		}
		membersCount = player.getTournamentTeam().getMembers().size();
		switch (type)
		{
			case F1X1:
				return 0;
			case F2X2:
				return 2 - membersCount;
			case F3X3:
				return 3 - membersCount;
			case F4X4:
				return 4 - membersCount;
			case F5X5:
				return 5 - membersCount;
			case F9X9:
				return 9 - membersCount;
			default:
				return -1;
		}
	}
	
	public String generateMemberList(TournamentTeam team)
	{
		StringBuilder sb = new StringBuilder();
		//int bgcolor = 0;
		for (Player member : team.getMembers())
		{
			sb.append("<table width=315 bgcolor=000000");
			sb.append("<tr>");
			sb.append("<td width=300 align=center>"+member.getRankColor()+member.getName()+"</font>,</td>");
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("</table>");
		
			/*sb.append("<img src=\"Sek.cbui371\" width=300 height=1>");
			if (bgcolor % 2 == 0)
				sb.append("<table width=315  bgcolor=090000>");
			else
				sb.append("<table width=315 bgcolor=000000>");
			
			sb.append("<tr>");
			sb.append("<td fixwidth=50></td>");
			sb.append("<td align=center>");
			sb.append("<font color=LEVEL>" + member.getName() + "</font>");
			sb.append("</td>");
			sb.append("<td fixwidth=50></td>");
			sb.append("</tr>");
			sb.append("</table>");
			bgcolor++;*/
		}
		
		return sb.toString();
	}
	
	public void onPlayerEnter(Player player)
	{
		// catch data from memo
		loadTournamentData(player);
		
		// check data and insert if have no result for all types
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentVictories().entrySet())
		{
			checkData(player, entry.getKey());
		}
	}
	
	public void checkData(Player player, TournamentFightType type)
	{
		
		Connection con = null;
		PreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = ConnectionPool.getConnection();
			offline = con.prepareStatement("SELECT * FROM tournament_player_data WHERE obj_id=? AND fight_type=?");
			offline.setInt(1, player.getObjectId());
			offline.setString(2, type.name());
			rs = offline.executeQuery();
			boolean hasResult = rs.next();
			if (!hasResult)
			{
				insertData(player, type);
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			Mysql.closeQuietly(con, offline, rs);
		}
		
	}
	
	public void insertData(Player player, TournamentFightType type)
	{
		
		Connection con = null;
		PreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = ConnectionPool.getConnection();
			offline = con.prepareStatement("REPLACE INTO tournament_player_data (obj_id, fight_type, fights_done, victories, defeats, ties, kills, damage, wdt, dpf) VALUES (?,?,?,?,?,?,?,?,?,?)");
			offline.setInt(1, player.getObjectId());
			offline.setString(2, type.name());
			offline.setInt(3, player.getTotalTournamentFightsDone());
			offline.setInt(4, player.getTournamentVictories().get(type));
			offline.setInt(5, player.getTournamentDefeats().get(type));
			offline.setInt(6, player.getTournamentTies().get(type));
			offline.setInt(7, player.getTournamentKills().get(type));
			offline.setInt(8, player.getTournamentDamage().get(type));
			offline.setString(9, "" + getWinDefeatTie(player, type));
			offline.setInt(10, getDamagePerFight(player, type));
			offline.execute();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			Mysql.closeQuietly(con, offline, rs);
		}
		
	}
	
	public void updateData(Player player, TournamentFightType type)
	{
		
		Connection con = null;
		PreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = ConnectionPool.getConnection();
			offline = con.prepareStatement("UPDATE tournament_player_data SET fights_done=?, victories=?, defeats=?, ties=?, kills=?, damage=?, wdt=?, dpf=? WHERE obj_id=? AND fight_type=?");
			offline.setInt(1, player.getTotalTournamentFightsDone());
			offline.setInt(2, player.getTournamentVictories().get(type));
			offline.setInt(3, player.getTournamentDefeats().get(type));
			offline.setInt(4, player.getTournamentTies().get(type));
			offline.setInt(5, player.getTournamentKills().get(type));
			offline.setInt(6, player.getTournamentDamage().get(type));
			offline.setString(7, "" + getWinDefeatTie(player, type));
			offline.setInt(8, getDamagePerFight(player, type));
			offline.setInt(9, player.getObjectId());
			offline.setString(10, type.name());
			offline.execute();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			Mysql.closeQuietly(con, offline, rs);
		}
		
	}
	
	public void initializeTournamentMaps(Player player)
	{
		player.getTournamentDamage().put(TournamentFightType.F1X1, 0);
		player.getTournamentDamage().put(TournamentFightType.F2X2, 0);
		player.getTournamentDamage().put(TournamentFightType.F3X3, 0);
		player.getTournamentDamage().put(TournamentFightType.F4X4, 0);
		player.getTournamentDamage().put(TournamentFightType.F5X5, 0);
		player.getTournamentDamage().put(TournamentFightType.F9X9, 0);
		
		player.getTournamentDefeats().put(TournamentFightType.F1X1, 0);
		player.getTournamentDefeats().put(TournamentFightType.F2X2, 0);
		player.getTournamentDefeats().put(TournamentFightType.F3X3, 0);
		player.getTournamentDefeats().put(TournamentFightType.F4X4, 0);
		player.getTournamentDefeats().put(TournamentFightType.F5X5, 0);
		player.getTournamentDefeats().put(TournamentFightType.F9X9, 0);
		
		player.getTournamentVictories().put(TournamentFightType.F1X1, 0);
		player.getTournamentVictories().put(TournamentFightType.F2X2, 0);
		player.getTournamentVictories().put(TournamentFightType.F3X3, 0);
		player.getTournamentVictories().put(TournamentFightType.F4X4, 0);
		player.getTournamentVictories().put(TournamentFightType.F5X5, 0);
		player.getTournamentVictories().put(TournamentFightType.F9X9, 0);
		
		player.getTournamentTies().put(TournamentFightType.F1X1, 0);
		player.getTournamentTies().put(TournamentFightType.F2X2, 0);
		player.getTournamentTies().put(TournamentFightType.F3X3, 0);
		player.getTournamentTies().put(TournamentFightType.F4X4, 0);
		player.getTournamentTies().put(TournamentFightType.F5X5, 0);
		player.getTournamentTies().put(TournamentFightType.F9X9, 0);
		
		player.getTournamentKills().put(TournamentFightType.F1X1, 0);
		player.getTournamentKills().put(TournamentFightType.F2X2, 0);
		player.getTournamentKills().put(TournamentFightType.F3X3, 0);
		player.getTournamentKills().put(TournamentFightType.F4X4, 0);
		player.getTournamentKills().put(TournamentFightType.F5X5, 0);
		player.getTournamentKills().put(TournamentFightType.F9X9, 0);
	}
	
	// store/load fights methods
	public void loadTournamentData(Player player)
	{
		initializeTournamentMaps(player);
		Connection con = null;
		PreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = ConnectionPool.getConnection();
			offline = con.prepareStatement("SELECT * FROM character_memo_alt WHERE obj_id =?");
			offline.setInt(1, player.getObjectId());
			rs = offline.executeQuery();
			
			while (rs.next())
			{
				if (rs.getString("name").startsWith("Tournament"))
				{
					StringTokenizer st = new StringTokenizer(rs.getString("name"), "-");
					st.nextToken(); // "Tournament"
					switch (st.nextToken())
					{
						case "Victories":
							if (st.hasMoreTokens())
							{
								player.getTournamentVictories().put(TournamentFightType.valueOf(st.nextToken()), rs.getInt("value"));
							}
							break;
						case "Defeats":
							if (st.hasMoreTokens())
							{
								player.getTournamentDefeats().put(TournamentFightType.valueOf(st.nextToken()), rs.getInt("value"));
							}
							break;
						case "Ties":
							if (st.hasMoreTokens())
							{
								player.getTournamentTies().put(TournamentFightType.valueOf(st.nextToken()), rs.getInt("value"));
							}
							break;
						case "Kills":
							if (st.hasMoreTokens())
							{
								player.getTournamentKills().put(TournamentFightType.valueOf(st.nextToken()), rs.getInt("value"));
							}
							break;
						
						case "Damage":
							if (st.hasMoreTokens())
							{
								player.getTournamentDamage().put(TournamentFightType.valueOf(st.nextToken()), rs.getInt("value"));
							}
							break;
						case "Points":
							player.setTournamentPoints(rs.getInt("value"));
							break;
					}
					
				}
				
			}
			
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			Mysql.closeQuietly(con, offline, rs);
		}
	}
	
	public int getTournamentPlayerFightsDone(int objectId, TournamentFightType type)
	{
		Connection con = null;
		PreparedStatement offline = null;
		ResultSet rs = null;
		int fights = 0;
		try
		{
			con = ConnectionPool.getConnection();
			offline = con.prepareStatement("SELECT * FROM character_memo_alt WHERE obj_id =?");
			offline.setInt(1, objectId);
			rs = offline.executeQuery();
			
			while (rs.next())
			{
				if (rs.getString("name").startsWith("Tournament"))
				{
					StringTokenizer st = new StringTokenizer(rs.getString("name"), "-");
					st.nextToken(); // "Tournament"
					switch (st.nextToken())
					{
						case "Victories":
						case "Defeats":
						case "Ties":
							if (st.hasMoreTokens() && st.nextToken().startsWith(type.name()))
							{
								fights += rs.getInt("value");
							}
							break;
						
					}
					
				}
				
			}
			
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			Mysql.closeQuietly(con, offline, rs);
		}
		return fights;
	}
	
	public void showPlayerTournamentData(Player player, int targetObjectId, TournamentFightType type, Map<TournamentFightType, Integer> tournamentKills, Map<TournamentFightType, Integer> tournamentVictories, Map<TournamentFightType, Integer> tournamentDefeats, Map<TournamentFightType, Integer> tournamentTies, Map<TournamentFightType, Integer> tournamentDamage)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setFile("data/html/mods/tournament/ranking/info/playerInfo" + type.name() + ".htm");
		
		// Fight Data
		for (Map.Entry<TournamentFightType, Integer> entry : tournamentVictories.entrySet())
		{
			htm.replace("%victories" + entry.getKey().name() + "%", tournamentVictories.get(entry.getKey()));
		}
		for (Map.Entry<TournamentFightType, Integer> entry : tournamentDefeats.entrySet())
		{
			htm.replace("%defeats" + entry.getKey().name() + "%", tournamentDefeats.get(entry.getKey()));
		}
		for (Map.Entry<TournamentFightType, Integer> entry : tournamentTies.entrySet())
		{
			htm.replace("%ties" + entry.getKey().name() + "%", tournamentTies.get(entry.getKey()));
		}
		for (Map.Entry<TournamentFightType, Integer> entry : tournamentKills.entrySet())
		{
			htm.replace("%kills" + entry.getKey().name() + "%", tournamentKills.get(entry.getKey()));
		}
		for (Map.Entry<TournamentFightType, Integer> entry : tournamentDamage.entrySet())
		{
			htm.replace("%damage" + entry.getKey().name() + "%", tournamentDamage.get(entry.getKey()));
		}
		for (Map.Entry<TournamentFightType, Integer> entry : tournamentDamage.entrySet())
		{
			htm.replace("%dpf" + entry.getKey().name() + "%", "Not Showing");
		}
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentVictories().entrySet())
		{
			htm.replace("%fightsDone" + entry.getKey().name() + "%", getTournamentPlayerFightsDone(targetObjectId, type));
		}
		
		htm.replace("%tourPoints%", player.getTournamentPoints());
		htm.replace("%killstotal%", player.getTotalTournamentKills());
		htm.replace("%totalDmg%", player.getTournamentTotalDamage());
		htm.replace("%playerName%", player.getName());
		htm.replace("%dpfTotal%", getDamagePerFight(player));
		htm.replace("%wdt%", getWinDefeatTie(player));
		htm.replace("%totalFights%", player.getTotalTournamentFightsDone());
		
		player.sendPacket(htm);
	}
	
	public void showPlayerRankingData(Player player, int targetObjectId, TournamentFightType type)
	{
		Map<TournamentFightType, Integer> tournamentKills = new HashMap<>();
		Map<TournamentFightType, Integer> tournamentVictories = new HashMap<>();
		Map<TournamentFightType, Integer> tournamentDefeats = new HashMap<>();
		Map<TournamentFightType, Integer> tournamentTies = new HashMap<>();
		Map<TournamentFightType, Integer> tournamentDamage = new HashMap<>();
		
		Connection con = null;
		PreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = ConnectionPool.getConnection();
			offline = con.prepareStatement("SELECT * FROM character_memo_alt WHERE obj_id =?");
			offline.setInt(1, targetObjectId);
			rs = offline.executeQuery();
			
			while (rs.next())
			{
				if (rs.getString("name").startsWith("Tournament"))
				{
					StringTokenizer st = new StringTokenizer(rs.getString("name"), "-");
					st.nextToken(); // "Tournament"
					switch (st.nextToken())
					{
						case "Victories":
							if (st.hasMoreTokens())
							{
								tournamentVictories.put(TournamentFightType.valueOf(st.nextToken()), rs.getInt("value"));
							}
							break;
						case "Defeats":
							if (st.hasMoreTokens())
							{
								tournamentDefeats.put(TournamentFightType.valueOf(st.nextToken()), rs.getInt("value"));
							}
							break;
						case "Ties":
							if (st.hasMoreTokens())
							{
								tournamentTies.put(TournamentFightType.valueOf(st.nextToken()), rs.getInt("value"));
							}
							break;
						case "Kills":
							if (st.hasMoreTokens())
							{
								tournamentKills.put(TournamentFightType.valueOf(st.nextToken()), rs.getInt("value"));
							}
							break;
						
						case "Damage":
							if (st.hasMoreTokens())
							{
								tournamentDamage.put(TournamentFightType.valueOf(st.nextToken()), rs.getInt("value"));
							}
							break;
						
					}
					
				}
				
			}
			
			con.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			Mysql.closeQuietly(con, offline, rs);
		}
		
		showPlayerTournamentData(player, targetObjectId, type, tournamentKills, tournamentVictories, tournamentDefeats, tournamentTies, tournamentDamage);
	}
	
	class WDTRecord
	{
		String playerName;
		
	}
	
	public void storeTournamentData(Player player)
	{
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentVictories().entrySet())
		{
			PlayerVariables.setVar(player, "Tournament-Victories-" + entry.getKey().name(), entry.getValue(), -1);
		}
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentDefeats().entrySet())
		{
			PlayerVariables.setVar(player, "Tournament-Defeats-" + entry.getKey().name(), entry.getValue(), -1);
		}
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentTies().entrySet())
		{
			PlayerVariables.setVar(player, "Tournament-Ties-" + entry.getKey().name(), entry.getValue(), -1);
		}
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentKills().entrySet())
		{
			PlayerVariables.setVar(player, "Tournament-Kills-" + entry.getKey().name(), entry.getValue(), -1);
		}
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentDamage().entrySet())
		{
			PlayerVariables.setVar(player, "Tournament-Damage-" + entry.getKey().name(), entry.getValue(), -1);
		}
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentDamage().entrySet())
		{
			PlayerVariables.setVar(player, "Tournament-WDT-" + entry.getKey().name(), String.valueOf(getWinDefeatTie(player, entry.getKey())), -1);
		}
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentDamage().entrySet())
		{
			PlayerVariables.setVar(player, "Tournament-DPF-" + entry.getKey().name(), String.valueOf(getDamagePerFight(player, entry.getKey())), -1);
		}
		
		PlayerVariables.setVar(player, "Tournament-Points", player.getTournamentPoints(), -1);
		
	}
	
	/**
	 * @return the started
	 */
	public boolean isRunning()
	{
		return running;
	}
	
	/**
	 * @param started the started to set
	 */
	public void setRunning(boolean started)
	{
		this.running = started;
	}
	
	/**
	 * @return the finishEvent
	 */
	public ScheduledFuture<?> getFinishEventTask()
	{
		return finishEventTask;
	}
	
	/**
	 * @param finishEvent the finishEvent to set
	 */
	public void setFinishEventTask(ScheduledFuture<?> finishEvent)
	{
		this.finishEventTask = finishEvent;
	}
	
	/**
	 * @return the tournamentTeleporting
	 */
	public boolean isTournamentTeleporting()
	{
		return tournamentTeleporting;
	}
	
	/**
	 * @param tournamentTeleporting the tournamentTeleporting to set
	 */
	public void setTournamentTeleporting(boolean tournamentTeleporting)
	{
		this.tournamentTeleporting = tournamentTeleporting;
	}
	
	public double getDamagePerFight(int totalDamage, int totalFightsDone)
	{
		double dpf = 0;
		if (totalFightsDone == 0)
		{
			return 0;
		}
		dpf = (totalDamage / totalFightsDone * 1000);
		return dpf;
	}
	
	public int getDamagePerFight(Player player, TournamentFightType type)
	{
		int dpf = 0;
		int totalDamage = player.getTournamentTotalDamage();
		int totalFightsDone = player.getTournamentFightsDone(type);
		if (totalFightsDone == 0)
		{
			return 0;
		}
		dpf = (totalDamage / totalFightsDone);
		return dpf;
	}
	
	public double getWinDefeatTie(int totalFightsDone, int totalVictories, int totalDefeats, int totalTies)
	{
		int ratioByFight = 1;
		double playerWDT = 0;
		if (totalFightsDone == 0)
		{
			return 0;
		}
		playerWDT = ratioByFight * (((3) * totalVictories) + ((-3) * totalDefeats) + (totalTies)) / totalFightsDone;
		return playerWDT;
	}
	
	public double getDamagePerFight(Player player)
	{
		double dpf = 0;
		int totalDamage = player.getTournamentTotalDamage();
		int totalFightsDone = player.getTotalTournamentFightsDone();
		if (totalFightsDone == 0)
		{
			return 0;
		}
		dpf = (totalDamage / totalFightsDone);
		return dpf;
	}
	
	public double getWinDefeatTie(Player player)
	{
		int ratioByFight = 1;
		double playerWDT = 0;
		int totalFightsDone = player.getTotalTournamentFightsDone();
		int totalVictories = player.getTotalVictories();
		int totalDefeats = player.getTotalDefeats();
		int totalTies = player.getTotalTies();
		if (totalFightsDone == 0)
		{
			return 0;
		}
		playerWDT = ratioByFight * (((3) * totalVictories) + ((-3) * totalDefeats) + (totalTies)) / totalFightsDone;
		return playerWDT;
	}
	
	public double getWinDefeatTie(Player player, TournamentFightType type)
	{
		int ratioByFight = 1;
		double playerWDT = 0;
		int totalFightsDone = player.getTournamentFightsDone(type);
		int totalVictories = player.getTournamentVictories().get(type);
		int totalDefeats = player.getTournamentDefeats().get(type);
		int totalTies = player.getTournamentTies().get(type);
		if (totalFightsDone == 0)
		{
			return 0;
		}
		playerWDT = ratioByFight * (((3) * totalVictories) + ((-3) * totalDefeats) + (totalTies)) / totalFightsDone;
		return playerWDT;
	}
	
	// RANKING
	class TourRankRecord
	{
		int pos;
		String playerName;
		String recordVal;
		
		public TourRankRecord(int pos, String playerName, String recordVal)
		{
			this.pos = pos + 1;
			this.playerName = playerName;
			this.recordVal = recordVal;
		}
	}
	
	public String generateRankingRecords(Player player, TournamentFightType type, LinkedList<TourRankRecord> records, String rankType)
	{
		StringBuilder sb = new StringBuilder();
		int bgColor = 1;
		for (TourRankRecord record : records)
		{
			if (record == null)
				continue;
			if (bgColor % 2 == 0)
				sb.append("<table width=300 bgcolor=000000>");
			else
				sb.append("<table width=300>");
			sb.append("<tr>");
			sb.append("<td align=center fixwidth=20>");
			sb.append(record.pos);
			sb.append("</td>");
			sb.append("<td fixwidth=5></td>");
			sb.append("<td align=center fixwidth=75>");
			sb.append(record.playerName);
			sb.append("</td>");
			sb.append("<td align=center fixwidth=50>");
			sb.append(record.recordVal);
			sb.append("</td>");
			sb.append("<td align=center fixwidth=50>");
			sb.append("<a action=\"bypass bp_checkTournamentPlayer " + record.playerName + " " + type.name() + "\"><font color=LEVEL>Check</font></a>");
			sb.append("</td>");
			sb.append("</tr>");
			sb.append("</table>");
			
		}
		return sb.toString();
	}
	
	public void showRanking(Player player, TournamentFightType fightType, String rankType)
	{
		NpcHtmlMessage htm = new NpcHtmlMessage(0);
		htm.setFile("data/html/mods/tournament/ranking/" + rankType + "/" + fightType.name() + ".htm");
		
		LinkedList<TourRankRecord> records = new LinkedList<>();
		int pos = 0;
		Connection con = null;
		PreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = ConnectionPool.getConnection();
			offline = con.prepareStatement("SELECT * FROM tournament_player_data WHERE fight_type=? ORDER BY " + rankType + " DESC LIMIT 10");
			offline.setString(1, fightType.name());
			rs = offline.executeQuery();
			while (rs.next())
			{
				
				records.add(new TourRankRecord(pos, PlayerInfoTable.getInstance().getPlayerName(rs.getInt("obj_id")), String.valueOf(rs.getInt(rankType))));
				pos++;
				
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			Mysql.closeQuietly(con, offline, rs);
		}
		for (Map.Entry<TournamentFightType, Integer> entry : player.getTournamentVictories().entrySet())
			htm.replace("%ranking-" + rankType + entry.getKey() + "%", generateRankingRecords(player, fightType, records, rankType));
		player.sendPacket(htm);
	}
	
	/**
	 * @return the allTimeFights
	 */
	public int getAllTimeFights()
	{
		return allTimeFights;
	}
	
	/**
	 * @param allTimeFights the allTimeFights to set
	 */
	public void setAllTimeFights(int allTimeFights)
	{
		this.allTimeFights = allTimeFights;
	}
}
