package Base.Dungeon;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.olympiad.OlympiadManager;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import Base.XML.XMLDocumentFactory;

public class DungeonManager
{
	private enum RestrictionType
	{
		HWID,
		IP,
		PLAYER_ID
	}
	
	private static final String INSERT_DUNGEON = "INSERT INTO dungeon VALUES (?,?,?,?,?)";
	private static final String DELETE_DUNGEON = "DELETE FROM dungeon";
	private static final String RESTORE_DUNGEON = "	SELECT * FROM dungeon";

	
	private static Logger log = Logger.getLogger(DungeonManager.class.getName());
	
	private Map<Integer, DungeonTemplate> templates;
	private List<Dungeon> running;
	private List<Integer> dungeonParticipants;
	private boolean reloading = false;
	private Map<String, Long[]> _hwidRestrictions;
	private Map<String, Long[]> _ipRestrictions;
	private Map<Integer, Long[]> _playerIdRestrictions;
	
	
	protected DungeonManager()
	{
		templates = new ConcurrentHashMap<>();
		running = new CopyOnWriteArrayList<>();
		dungeonParticipants = new CopyOnWriteArrayList<>();
		_hwidRestrictions = new ConcurrentHashMap<>();
		_playerIdRestrictions = new ConcurrentHashMap<>();
		_ipRestrictions = new ConcurrentHashMap<>();
		
		load();
		// ThreadPool.scheduleAtFixedRate(() -> updateDatabase(), 1000*60*30, 1000*60*60);
		ThreadPool.scheduleAtFixedRate(() -> updateDatabase(), 1000 * 60 * 1, 1000 * 60 * 2);
		
	}
	
	@SuppressWarnings("resource")
	private void updateDatabase()
	{
		try (Connection con = ConnectionPool.getConnection())
		{
			
			PreparedStatement stm = con.prepareStatement(DELETE_DUNGEON);
			stm.execute();
			
			
			try (PreparedStatement ps = con.prepareStatement(INSERT_DUNGEON))
			{
				
				for (String hwid : _hwidRestrictions.keySet())
				{
					for (int i = 1; i < _hwidRestrictions.get(hwid).length; i++)
					{
						ps.setInt(1, i);
						ps.setString(2, hwid);
						ps.setLong(3, _hwidRestrictions.get(hwid)[i]);
						ps.setLong(4, 0);
						ps.setInt(5, RestrictionType.HWID.ordinal());
						ps.addBatch();
					}
				}
				
				for (String ip : _ipRestrictions.keySet())
				{
					for (int i = 1; i < _ipRestrictions.get(ip).length; i++)
					{
						ps.setInt(1, i);
						ps.setString(2, ip);
						ps.setLong(3, _ipRestrictions.get(ip)[i]);
						ps.setLong(4, 0);
						ps.setInt(5, RestrictionType.IP.ordinal());
						ps.addBatch();
					}
				}
				
				for (Integer id : _playerIdRestrictions.keySet())
				{
					for (int i = 1; i < _playerIdRestrictions.get(id).length; i++)
					{
						ps.setInt(1, i);
						ps.setString(2, "");
						ps.setLong(3, _playerIdRestrictions.get(id)[i]);
						ps.setLong(4, id);
						ps.setInt(5, RestrictionType.PLAYER_ID.ordinal());
						ps.addBatch();
					}
				}
				
				ps.executeBatch();
			}
			
			
			stm.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean reload()
	{
		if (!running.isEmpty())
		{
			reloading = true;
			return false;
		}
		
		templates.clear();
		load();
		return true;
	}
	
	@SuppressWarnings("resource")
	private void load()
	{
		try
		{
			File f = new File("./events/Dungeon.xml");
			Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
			
			Node n = doc.getFirstChild();
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (d.getNodeName().equals("dungeon"))
				{
					int id = Integer.parseInt(d.getAttributes().getNamedItem("id").getNodeValue());
					String name = d.getAttributes().getNamedItem("name").getNodeValue();
					int players = Integer.parseInt(d.getAttributes().getNamedItem("players").getNodeValue());
					Map<Integer, Integer> rewards = new HashMap<>();
					String rewardHtm = d.getAttributes().getNamedItem("rewardHtm").getNodeValue();
					Map<Integer, DungeonStage> stages = new HashMap<>();
					
					String rewards_data = d.getAttributes().getNamedItem("rewards").getNodeValue();
					if (!rewards_data.isEmpty()) // If config is empty do not feed the rewards
					{
						String[] rewards_data_split = rewards_data.split(";");
						for (String reward : rewards_data_split)
						{
							String[] reward_split = reward.split(",");
							rewards.put(Integer.parseInt(reward_split[0]), Integer.parseInt(reward_split[1]));
						}
					}
					
					for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling())
					{
						NamedNodeMap attrs = cd.getAttributes();
						
						if (cd.getNodeName().equals("stage"))
						{
							int order = Integer.parseInt(attrs.getNamedItem("order").getNodeValue());
							String loc_data = attrs.getNamedItem("loc").getNodeValue();
							String[] loc_data_split = loc_data.split(",");
							Location loc = new Location(Integer.parseInt(loc_data_split[0]), Integer.parseInt(loc_data_split[1]), Integer.parseInt(loc_data_split[2]));
							boolean teleport = Boolean.parseBoolean(attrs.getNamedItem("teleport").getNodeValue());
							int minutes = Integer.parseInt(attrs.getNamedItem("minutes").getNodeValue());
							Map<Integer, List<Location>> mobs = new HashMap<>();
							
							for (Node ccd = cd.getFirstChild(); ccd != null; ccd = ccd.getNextSibling())
							{
								NamedNodeMap attrs2 = ccd.getAttributes();
								
								if (ccd.getNodeName().equals("mob"))
								{
									int npcId = Integer.parseInt(attrs2.getNamedItem("npcId").getNodeValue());
									List<Location> locs = new ArrayList<>();
									
									String locs_data = attrs2.getNamedItem("locs").getNodeValue();
									String[] locs_data_split = locs_data.split(";");
									for (String locc : locs_data_split)
									{
										String[] locc_data = locc.split(",");
										locs.add(new Location(Integer.parseInt(locc_data[0]), Integer.parseInt(locc_data[1]), Integer.parseInt(locc_data[2])));
									}
									
									mobs.put(npcId, locs);
								}
							}
							
							stages.put(order, new DungeonStage(order, loc, teleport, minutes, mobs));
						}
					}
					
					templates.put(id, new DungeonTemplate(id, name, players, rewards, rewardHtm, stages));
				}
			}
		}
		catch (Exception e)
		{
			log.log(Level.WARNING, "DungeonManager: Error loading dungeons.xml", e);
			e.printStackTrace();
		}
		
		try (Connection con = ConnectionPool.getConnection())
		{
			PreparedStatement stm = con.prepareStatement(RESTORE_DUNGEON);
			ResultSet rset = stm.executeQuery();
			
			while (rset.next())
			{
				int dungid = rset.getInt("dungid");
				String ipaddr = rset.getString("ipaddr");
				long lastjoin = rset.getLong("lastjoin");
				int playerId = rset.getInt("playerId");
				int restriction = rset.getInt("restriction");
				
				var restrictionType = RestrictionType.values()[restriction];
				
				if (restrictionType == RestrictionType.HWID)
				{
					if (!_hwidRestrictions.containsKey(ipaddr))
					{
						Long[] times = new Long[templates.size() + 1];
						for (int i = 0; i < times.length; i++)
							times[i] = 0L;
						times[dungid] = lastjoin;
						
						_hwidRestrictions.put(ipaddr, times);
					}
					else
						_hwidRestrictions.get(ipaddr)[dungid] = lastjoin;
				}
				else if (restrictionType == RestrictionType.IP) 
				{
					if (!_ipRestrictions.containsKey(ipaddr))
					{
						Long[] times = new Long[templates.size() + 1];
						for (int i = 0; i < times.length; i++)
							times[i] = 0L;
						times[dungid] = lastjoin;
						
						_ipRestrictions.put(ipaddr, times);
					}
					else
						_ipRestrictions.get(ipaddr)[dungid] = lastjoin;
				}
				else if (restrictionType == RestrictionType.PLAYER_ID) 
				{
					if (!_playerIdRestrictions.containsKey(playerId))
					{
						Long[] times = new Long[templates.size() + 1];
						for (int i = 0; i < times.length; i++)
							times[i] = 0L;
						times[dungid] = lastjoin;
						
						_playerIdRestrictions.put(playerId, times);
					}
					else
						_playerIdRestrictions.get(playerId)[dungid] = lastjoin;
				}
			}
			
			rset.close();
			stm.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		log.info("DungeonManager: Loaded " + templates.size() + " dungeon templates");
	}
	
	public synchronized void removeDungeon(Dungeon dungeon)
	{
		running.remove(dungeon);
		
		if (reloading && running.isEmpty())
		{
			reloading = false;
			reload();
		}
	}
	
	
	public boolean validateSoloDungeon(Player player, int templateId) 
	{
		var template = templates.get(templateId);
		return validateSoloDungeon(player, template);
	}
	
	public boolean validateSoloDungeon(Player player, DungeonTemplate template)
	{
		if (player.isInParty())
		{
			player.getParty().disband();
		}
		
		 //Uncomment when players will piss you off !!
		  
		// ----- Checking: IP ------
		String playerIP = player.getIP();
		if (_ipRestrictions.containsKey(playerIP) && (System.currentTimeMillis() - _ipRestrictions.get(playerIP)[template.getId()] < 1000 * 60 * 60 * 12))
		{
			player.sendMessage("12 hours have not passed since you last entered this Dungeon.");
			//return false;
		} 
		
		
		// ----- Checking: HWID ------
		/*String playerHWID = player.getHWID();
		if (_hwidRestrictions.containsKey(playerHWID) && (System.currentTimeMillis() - _hwidRestrictions.get(playerHWID)[template.getId()] < 1000 * 60 * 60 * 12))
		{
			player.sendMessage("12 hours have not passed since you last entered this Dungeon.");
			return false;
		} 
		*/
		
		
		// ----- Checking: Player ID ------
		if (_playerIdRestrictions.containsKey(player.getObjectId()) && (System.currentTimeMillis() - _playerIdRestrictions.get(player.getObjectId())[template.getId()] < 1000 * 60 * 60 * 12))
		{
			player.sendMessage("12 hours have not passed since you last entered this Dungeon.");
			//return false;
		} 
		
		
		if (player.getStatus().getLevel() < 76)
		{
			player.sendMessage("Your level is less than 76.");
			//return false;
		}
		
		if (player.isInTournamentMatch() || player.isInTournamentMode()|| player.getTournamentTeam() != null) 
		{
			player.sendMessage("You are already participating in tournament.");
			return false;
		} 

		if(player.getOlympiadGameId() != -1 || player.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(player))
		{
			player.sendMessage("You can not proceed because you are participating in Olympiad Games!");
			return false;
		}
		
		
		return true;
	}
	
	public boolean validatePartyDungeon(Player player, DungeonTemplate template) 
	{
		if (!player.isInParty() || player.getParty().getMembersCount() < template.getPlayers())
		{
			player.sendMessage("You need a party of " + template.getPlayers() + " or more players to enter this Dungeon.");
			//return false;
		}
		
		if (!player.getParty().isLeader(player))
		{
			player.sendMessage("The party leader can only apply entrance.");
			//return false;
		}
		
		for (Player pm : player.getParty().getMembers())
		{
			if (pm.getStatus().getLevel() < 76)
			{
				player.sendMessage("Player " + pm.getName() + " level is less than 76.");
				//return false;
			}
			
			if (pm.isInTournamentMatch() || pm.isInTournamentMode() || pm.getTournamentTeam() != null) 
			{
				player.sendMessage("Player "+ pm.getName() + " is participating in tournament.");
				return false;
			} 

			if(pm.getOlympiadGameId() != -1 ||pm.isInOlympiadMode() || OlympiadManager.getInstance().isRegistered(pm))
			{
				player.sendMessage("You can not participate because player "  + pm.getName() + " of your team is participating in Olympiad Games!");
				return false;
			}
			

			
			// ----- Checking: Party Member IP ------
			String partyMemberIP = pm.getIP();
			if (_ipRestrictions.containsKey(partyMemberIP) && (System.currentTimeMillis() - _ipRestrictions.get(partyMemberIP)[template.getId()] < 1000 * 60 * 60 * 12))
			{
				player.sendMessage("Player " + pm.getName() + " cannot join this Dungeon because 12 hours have not passed since he last joined.");
				//return false;
			} 
			

			// ----- Checking: Party Member HWID ------
			/*String partyMemeberHWID = pm.getHWID();
			if (_hwidRestrictions.containsKey(partyMemeberHWID) && (System.currentTimeMillis() - _hwidRestrictions.get(partyMemeberHWID)[template.getId()] < 1000 * 60 * 60 * 12))
			{
				player.sendMessage("Player " + pm.getName() + " cannot join this Dungeon because 12 hours have not passed since he last joined.");
				return false;
			} 
			*/
			// ----- Checking: Party Member ID ------
			if (_playerIdRestrictions.containsKey(pm.getObjectId()) && (System.currentTimeMillis() - _playerIdRestrictions.get(pm.getObjectId())[template.getId()] < 1000 * 60 * 60 * 12))
			{
				player.sendMessage("Player " + pm.getName() + " cannot join this Dungeon because 12 hours have not passed since he last joined.");
				//return false;
			} 
		}
		

		
		return true;
	}
	
	public synchronized void enterDungeon(int id, Player player)
	{
		if (reloading)
		{
			player.sendMessage("The Dungeon system is reloading, please try again in a few minutes.");
			return;
		}
		
		
		DungeonTemplate template = templates.get(id);
		
		if (template == null)
			return; //should never happen
		
		var canProceed = false;
		
		if (template.getPlayers() == 1) 
		{
			canProceed = validateSoloDungeon(player, template);
		}
		else 
		{
			canProceed = validatePartyDungeon(player, template);
		}
		
		
		if (!canProceed) 
			return;
		
		List<Player> players = new ArrayList<>();
		if (player.isInParty())
		{
			for (Player pm : player.getParty().getMembers())
			{
				String pmIP = pm.getIP();
				String pmHWID = pm.getHWID();
				
				dungeonParticipants.add(pm.getObjectId());
				players.add(pm);
				
				//------ Player ID -------
				if (_playerIdRestrictions.containsKey(pm.getObjectId())) 
				{
					_playerIdRestrictions.get(pm.getObjectId())[template.getId()] = System.currentTimeMillis(); 
				}
				else 
				{
					Long[] times = new Long[templates.size() + 1];
					for (int i = 0; i < times.length; i++)
						times[i] = 0L;
					times[template.getId()] = System.currentTimeMillis();
					_playerIdRestrictions.put(pm.getObjectId(), times);
				}
				
				//------ HWID -------
				if (_hwidRestrictions.containsKey(pmHWID)) 
				{
					_hwidRestrictions.get(pmHWID)[template.getId()] = System.currentTimeMillis();
				}
				else
				{
					Long[] times = new Long[templates.size() + 1];
					for (int i = 0; i < times.length; i++)
						times[i] = 0L;
					times[template.getId()] = System.currentTimeMillis();
					_hwidRestrictions.put(pmHWID, times);
				}
				
				//------ IP -------
				if (_ipRestrictions.containsKey(pmIP)) 
				{
					_ipRestrictions.get(pmIP)[template.getId()] = System.currentTimeMillis();
				}
				else
				{
					Long[] times = new Long[templates.size() + 1];
					for (int i = 0; i < times.length; i++)
						times[i] = 0L;
					times[template.getId()] = System.currentTimeMillis();
					_ipRestrictions.put(pmIP, times);
				}
			}
		}
		else
		{
			String pIP = player.getIP();
			String pHwid = player.getHWID();

			dungeonParticipants.add(player.getObjectId());
			players.add(player);
			
			//------ Player ID -------
			if (_playerIdRestrictions.containsKey(player.getObjectId()))
				_playerIdRestrictions.get(player.getObjectId())[template.getId()] = System.currentTimeMillis();
			else
			{
				Long[] times = new Long[templates.size() + 1];
				for (int i = 0; i < times.length; i++)
					times[i] = 0L;
				times[template.getId()] = System.currentTimeMillis();
				_playerIdRestrictions.put(player.getObjectId(), times);
			}
			
			//------ IP -------
			if (_ipRestrictions.containsKey(pIP))
				_ipRestrictions.get(pIP)[template.getId()] = System.currentTimeMillis();
			else
			{
				Long[] times = new Long[templates.size() + 1];
				for (int i = 0; i < times.length; i++)
					times[i] = 0L;
				times[template.getId()] = System.currentTimeMillis();
				_ipRestrictions.put(pIP, times);
			}
			
			//------ HWID -------
			if (_hwidRestrictions.containsKey(pHwid))
				_hwidRestrictions.get(pHwid)[template.getId()] = System.currentTimeMillis();
			else
			{
				Long[] times = new Long[templates.size() + 1];
				for (int i = 0; i < times.length; i++)
					times[i] = 0L;
				times[template.getId()] = System.currentTimeMillis();
				_hwidRestrictions.put(pHwid, times);
			}
		}
		
		running.add(new Dungeon(template, players));
	}
	
	public boolean isReloading()
	{
		return reloading;
	}
	
	public boolean isInDungeon(Player player)
	{
		for (Dungeon dungeon : running)
			for (Player p : dungeon.getPlayers())
				if (p == player)
					return true;
				
		return false;
	}
	
	public Map<String, Long[]> getPlayerData()
	{
		return _hwidRestrictions;
	}
	
	public List<Integer> getDungeonParticipants()
	{
		return dungeonParticipants;
	}
	
	public static DungeonManager getInstance()
	{
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder
	{
		protected static final DungeonManager instance = new DungeonManager();
	}
}