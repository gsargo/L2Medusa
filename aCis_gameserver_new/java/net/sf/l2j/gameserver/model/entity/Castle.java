package net.sf.l2j.gameserver.model.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentSkipListSet;

import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.manager.CastleManager;
import net.sf.l2j.gameserver.data.manager.CastleManorManager;
import net.sf.l2j.gameserver.data.manager.SevenSignsManager;
import net.sf.l2j.gameserver.data.manager.ZoneManager;
import net.sf.l2j.gameserver.data.sql.ClanTable;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.enums.SealType;
import net.sf.l2j.gameserver.enums.SiegeSide;
import net.sf.l2j.gameserver.enums.SpawnType;
import net.sf.l2j.gameserver.model.WorldObject;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.ControlTower;
import net.sf.l2j.gameserver.model.actor.instance.Door;
import net.sf.l2j.gameserver.model.actor.instance.HolyThing;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.item.MercenaryTicket;
import net.sf.l2j.gameserver.model.item.instance.ItemInstance;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.location.TowerSpawnLocation;
import net.sf.l2j.gameserver.model.pledge.Clan;
import net.sf.l2j.gameserver.model.pledge.ClanMember;
import net.sf.l2j.gameserver.model.spawn.Spawn;
import net.sf.l2j.gameserver.model.zone.type.CastleTeleportZone;
import net.sf.l2j.gameserver.model.zone.type.CastleZone;
import net.sf.l2j.gameserver.model.zone.type.SiegeZone;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.PlaySound;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;


public class Castle
{
	protected static final CLogger LOGGER = new CLogger(Castle.class.getName());
	
	private static final String UPDATE_TREASURY = "UPDATE castle SET treasury = ? WHERE id = ?";
	private static final String UPDATE_CERTIFICATES = "UPDATE castle SET certificates=? WHERE id=?";
	private static final String UPDATE_TAX = "UPDATE castle SET taxPercent = ? WHERE id = ?";
	
	private static final String UPDATE_DOORS = "REPLACE INTO castle_doorupgrade (doorId, hp, castleId) VALUES (?,?,?)";
	private static final String LOAD_DOORS = "SELECT * FROM castle_doorupgrade WHERE castleId=?";
	private static final String DELETE_DOOR = "DELETE FROM castle_doorupgrade WHERE castleId=?";
	
	private static final String DELETE_OWNER = "UPDATE clan_data SET hasCastle=0 WHERE hasCastle=?";
	private static final String UPDATE_OWNER = "UPDATE clan_data SET hasCastle=? WHERE clan_id=?";
	
	private static final String LOAD_GUARDS = "SELECT npcId,x,y,z,heading,respawnDelay FROM castle_siege_guards WHERE castleId=?";
	
	private static final String LOAD_TRAPS = "SELECT * FROM castle_trapupgrade WHERE castleId=?";
	private static final String UPDATE_TRAP = "REPLACE INTO castle_trapupgrade (castleId, towerIndex, level) values (?,?,?)";
	private static final String DELETE_TRAP = "DELETE FROM castle_trapupgrade WHERE castleId=?";
	
	private static final String UPDATE_ITEMS_LOC = "UPDATE items SET loc='INVENTORY' WHERE item_id IN (?, 6841) AND owner_id=? AND loc='PAPERDOLL'";
	
	private final int _castleId;
	private final String _name;
	
	private int _circletId;
	private int _ownerId;
	
	private final List<Door> _doors = new ArrayList<>();
	private final List<MercenaryTicket> _tickets = new ArrayList<>(60);
	private final List<Integer> _artifacts = new ArrayList<>(1);
	private final List<Integer> _relatedNpcIds = new ArrayList<>();
	
	private final Set<ItemInstance> _droppedTickets = new ConcurrentSkipListSet<>();
	private final List<Npc> _siegeGuards = new ArrayList<>();
	
	private final List<TowerSpawnLocation> _controlTowers = new ArrayList<>();
	private final List<TowerSpawnLocation> _flameTowers = new ArrayList<>();
	
	private final Map<SpawnType, List<Location>> _spawns = new HashMap<>();
	
	private Siege _siege;
	private Calendar _siegeDate;
	private boolean _isTimeRegistrationOver = true;
	
	private int _taxPercent;
	private double _taxRate;
	private long _treasury;
	
	private SiegeZone _siegeZone;
	private CastleZone _castleZone;
	private CastleTeleportZone _teleZone;
	
	private int _leftCertificates;
	
	public Castle(int id, String name)
	{
		_castleId = id;
		_name = name;
		
		// Feed _siegeZone.
		for (SiegeZone zone : ZoneManager.getInstance().getAllZones(SiegeZone.class))
		{
			if (zone.getSiegableId() == _castleId)
			{
				_siegeZone = zone;
				break;
			}
		}
		
		// Feed _castleZone.
		for (CastleZone zone : ZoneManager.getInstance().getAllZones(CastleZone.class))
		{
			if (zone.getResidenceId() == _castleId)
			{
				_castleZone = zone;
				break;
			}
		}
		
		// Feed _teleZone.
		for (CastleTeleportZone zone : ZoneManager.getInstance().getAllZones(CastleTeleportZone.class))
		{
			if (zone.getCastleId() == _castleId)
			{
				_teleZone = zone;
				break;
			}
		}
	}
	
	public synchronized void engrave(Clan clan, WorldObject target)
	{
		if (!isGoodArtifact(target))
			return;
		
		// "Clan X engraved the ruler" message.
		getSiege().announce(SystemMessage.getSystemMessage(SystemMessageId.CLAN_S1_ENGRAVED_RULER).addString(clan.getName()), SiegeSide.ATTACKER, SiegeSide.DEFENDER);
		
		setOwner(clan);
	}
	
	/**
	 * Add amount to castle's treasury (warehouse).
	 * @param amount The amount to add.
	 */
	public void addToTreasury(int amount)
	{
		if (_ownerId <= 0)
			return;
		
		if (_name.equalsIgnoreCase("Schuttgart") || _name.equalsIgnoreCase("Goddard"))
		{
			Castle rune = CastleManager.getInstance().getCastleByName("rune");
			if (rune != null)
			{
				int runeTax = (int) (amount * rune._taxRate);
				if (rune._ownerId > 0)
					rune.addToTreasury(runeTax);
				amount -= runeTax;
			}
		}
		
		if (!_name.equalsIgnoreCase("aden") && !_name.equalsIgnoreCase("Rune") && !_name.equalsIgnoreCase("Schuttgart") && !_name.equalsIgnoreCase("Goddard")) // If current castle instance is not Aden, Rune, Goddard or Schuttgart.
		{
			Castle aden = CastleManager.getInstance().getCastleByName("aden");
			if (aden != null)
			{
				int adenTax = (int) (amount * aden._taxRate); // Find out what Aden gets from the current castle instance's income
				if (aden._ownerId > 0)
					aden.addToTreasury(adenTax); // Only bother to really add the tax to the treasury if not npc owned
					
				amount -= adenTax; // Subtract Aden's income from current castle instance's income
			}
		}
		
		addToTreasuryNoTax(amount);
	}
	
	/**
	 * Add amount to castle instance's treasury (warehouse), no tax paying.
	 * @param amount The amount of adenas to add to treasury.
	 * @return true if successful.
	 */
	public boolean addToTreasuryNoTax(long amount)
	{
		if (_ownerId <= 0)
			return false;
		
		if (amount < 0)
		{
			amount *= -1;
			if (_treasury < amount)
				return false;
			_treasury -= amount;
		}
		else
		{
			if (_treasury + amount > Integer.MAX_VALUE)
				_treasury = Integer.MAX_VALUE;
			else
				_treasury += amount;
		}
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_TREASURY))
		{
			ps.setLong(1, _treasury);
			ps.setInt(2, _castleId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't update treasury.", e);
		}
		return true;
	}
	
	/**
	 * Move non clan members off castle area and to nearest town.
	 */
	public void banishForeigners()
	{
		getCastleZone().banishForeigners(_ownerId);
	}
	
	public SiegeZone getSiegeZone()
	{
		return _siegeZone;
	}
	
	public CastleZone getCastleZone()
	{
		return _castleZone;
	}
	
	public CastleTeleportZone getTeleZone()
	{
		return _teleZone;
	}
	
	public void oustAllPlayers()
	{
		getTeleZone().oustAllPlayers();
	}
	
	public int getLeftCertificates()
	{
		return _leftCertificates;
	}
	
	/**
	 * Set (and optionally save on database) left certificates amount.
	 * @param leftCertificates : The amount to save.
	 * @param storeInDb : If true, we store it on database. Basically set to false on server startup.
	 */
	public void setLeftCertificates(int leftCertificates, boolean storeInDb)
	{
		_leftCertificates = leftCertificates;
		
		if (storeInDb)
		{
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement(UPDATE_CERTIFICATES))
			{
				ps.setInt(1, leftCertificates);
				ps.setInt(2, _castleId);
				ps.executeUpdate();
			}
			catch (Exception e)
			{
				LOGGER.error("Couldn't update certificates amount.", e);
			}
		}
	}
	
	public void closeDoor(Player player, int doorId)
	{
		openCloseDoor(player, doorId, false);
	}
	
	public void openDoor(Player player, int doorId)
	{
		openCloseDoor(player, doorId, true);
	}
	
	public void openCloseDoor(Player player, int doorId, boolean open)
	{
		if (player.getClanId() != _ownerId)
			return;
		
		Door door = getDoor(doorId);
		if (door != null)
		{
			if (open)
				door.openMe();
			else
				door.closeMe();
		}
	}
	
	/**
	 * This method setup the castle owner.
	 * @param clan The clan who will own the castle.
	 */
	public void setOwner(Clan clan)
	{
		// Act only if castle owner is different of NPC, or if old owner is different of new owner.
		if (_ownerId > 0 && (clan == null || clan.getClanId() != _ownerId))
		{
			// Try to find clan instance of the old owner.
			Clan oldOwner = ClanTable.getInstance().getClan(_ownerId);
			if (oldOwner != null)
			{
				// Dismount the old leader if he was riding a wyvern.
				Player oldLeader = oldOwner.getLeader().getPlayerInstance();
				if (oldLeader != null && oldLeader.getMountType() == 2)
					oldLeader.dismount();
				
				// Unset castle flag for old owner clan.
				oldOwner.setCastle(0);
			}
		}
		
		// Update database.
		updateOwnerInDB(clan);
		
		// If siege is in progress, mid victory phase of siege.
		if (getSiege().isInProgress())
			getSiege().midVictory();
	}
	
	/**
	 * Remove the castle owner. This method is only used by admin command.
	 **/
	public void removeOwner()
	{
		if (_ownerId <= 0)
			return;
		
		final Clan clan = ClanTable.getInstance().getClan(_ownerId);
		if (clan == null)
			return;
		
		clan.setCastle(0);
		clan.broadcastToMembers(new PledgeShowInfoUpdate(clan));
		
		// Remove clan from siege registered clans (as owners are automatically added).
		getSiege().getRegisteredClans().remove(clan);
		
		// Delete all spawned tickets.
		for (ItemInstance item : _droppedTickets)
			item.decayMe();
		
		// Clear the List.
		_droppedTickets.clear();
		
		// Unspawn Mercenaries, if any.
		for (Npc npc : _siegeGuards)
		{
			final Spawn spawn = npc.getSpawn();
			if (spawn != null)
				spawn.setRespawnState(false);
			
			npc.doDie(npc);
		}
		
		// Clear the List.
		_siegeGuards.clear();
		
		updateOwnerInDB(null);
		
		if (getSiege().isInProgress())
			getSiege().midVictory();
		else
			checkItemsForClan(clan);
	}
	
	/**
	 * This method updates the castle tax rate.
	 * @param player : Sends informative messages to that character (success or fail).
	 * @param taxPercent : The new tax rate to apply.
	 */
	public void setTaxPercent(Player player, int taxPercent)
	{
		int maxTax;
		switch (SevenSignsManager.getInstance().getSealOwner(SealType.STRIFE))
		{
			case DAWN:
				maxTax = 25;
				break;
			
			case DUSK:
				maxTax = 5;
				break;
			
			default:
				maxTax = 15;
		}
		
		if (taxPercent < 0 || taxPercent > maxTax)
		{
			player.sendMessage("Tax value must be between 0 and " + maxTax + ".");
			return;
		}
		
		setTaxPercent(taxPercent, true);
		player.sendMessage(_name + " castle tax changed to " + taxPercent + "%.");
	}
	
	public void setTaxPercent(int taxPercent, boolean save)
	{
		_taxPercent = taxPercent;
		_taxRate = _taxPercent / 100.0;
		
		if (save)
		{
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement(UPDATE_TAX))
			{
				ps.setInt(1, taxPercent);
				ps.setInt(2, _castleId);
				ps.executeUpdate();
			}
			catch (Exception e)
			{
				LOGGER.error("Couldn't update tax amount.", e);
			}
		}
	}
	
	/**
	 * Respawn doors associated to that castle.
	 * @param isDoorWeak if true, spawn doors with 50% max HPs.
	 */
	public void spawnDoors(boolean isDoorWeak)
	{
		for (Door door : _doors)
		{
			if (door.isDead())
				door.doRevive();
			
			door.closeMe();
			door.getStatus().setHp((isDoorWeak) ? door.getStatus().getMaxHp() / 2 : door.getStatus().getMaxHp());
		}
	}
	
	/**
	 * Close doors associated to that castle.
	 */
	public void closeDoors()
	{
		for (Door door : _doors)
			door.closeMe();
	}
	
	/**
	 * Upgrade door.
	 * @param doorId The doorId to affect.
	 * @param hp The hp ratio.
	 * @param db If set to true, save changes on database.
	 */
	public void upgradeDoor(int doorId, int hp, boolean db)
	{
		Door door = getDoor(doorId);
		if (door == null)
			return;
		
		door.getStatus().setUpgradeHpRatio(hp);
		door.getStatus().setMaxHp();
		
		if (db)
		{
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement(UPDATE_DOORS))
			{
				ps.setInt(1, doorId);
				ps.setInt(2, hp);
				ps.setInt(3, _castleId);
				ps.execute();
			}
			catch (Exception e)
			{
				LOGGER.error("Couldn't upgrade castle doors.", e);
			}
		}
	}
	
	/**
	 * This method loads castle door upgrade data from database.
	 */
	public void loadDoorUpgrade()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_DOORS))
		{
			ps.setInt(1, _castleId);
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
					upgradeDoor(rs.getInt("doorId"), rs.getInt("hp"), false);
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't load door upgrades.", e);
		}
	}
	
	/**
	 * This method is only used on siege midVictory.
	 */
	public void removeDoorUpgrade()
	{
		for (Door door : _doors)
			door.getStatus().setUpgradeHpRatio(1);
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_DOOR))
		{
			ps.setInt(1, _castleId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't delete door upgrade.", e);
		}
	}
	
	private void updateOwnerInDB(Clan clan)
	{
		if (clan != null)
			_ownerId = clan.getClanId(); // Update owner id property
		else
		{
			_ownerId = 0; // Remove owner
			CastleManorManager.getInstance().resetManorData(_castleId);
		}
		
		if (clan != null)
		{
			// Set castle for new owner.
			clan.setCastle(_castleId);
			
			// Announce to clan members.
			clan.broadcastToMembers(new PledgeShowInfoUpdate(clan), new PlaySound(1, "Siege_Victory"));
		}
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_OWNER);
			PreparedStatement ps2 = con.prepareStatement(UPDATE_OWNER))
		{
			ps.setInt(1, _castleId);
			ps.executeUpdate();
			
			ps2.setInt(1, _castleId);
			ps2.setInt(2, _ownerId);
			ps2.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't update castle owner.", e);
		}
	}
	
	public int getCastleId()
	{
		return _castleId;
	}
	
	public Door getDoor(int doorId)
	{
		for (Door door : _doors)
		{
			if (door.getDoorId() == doorId)
				return door;
		}
		return null;
	}
	
	public List<Door> getDoors()
	{
		return _doors;
	}
	
	public List<MercenaryTicket> getTickets()
	{
		return _tickets;
	}
	
	public MercenaryTicket getTicket(int itemId)
	{
		return _tickets.stream().filter(t -> t.getItemId() == itemId).findFirst().orElse(null);
	}
	
	public Set<ItemInstance> getDroppedTickets()
	{
		return _droppedTickets;
	}
	
	public void addDroppedTicket(ItemInstance item)
	{
		_droppedTickets.add(item);
	}
	
	public void removeDroppedTicket(ItemInstance item)
	{
		_droppedTickets.remove(item);
	}
	
	public int getDroppedTicketsCount(int itemId)
	{
		return (int) _droppedTickets.stream().filter(t -> t.getItemId() == itemId).count();
	}
	
	public boolean isTooCloseFromDroppedTicket(int x, int y, int z)
	{
		return _droppedTickets.stream().anyMatch(i -> i.isIn3DRadius(x, y, z, 25));
	}
	
	/**
	 * That method is used to spawn NPCs, being neutral guards or player-based mercenaries.
	 * <ul>
	 * <li>If castle got an owner, it spawns mercenaries following tickets. Otherwise it uses SpawnManager territory.</li>
	 * <li>It feeds the nearest Control Tower with the spawn. If tower is broken, associated spawns are removed.</li>
	 * </ul>
	 */
	public void spawnSiegeGuardsOrMercenaries()
	{
		if (_ownerId > 0)
		{
			for (ItemInstance item : _droppedTickets)
			{
				// Retrieve MercenaryTicket information.
				final MercenaryTicket ticket = getTicket(item.getItemId());
				if (ticket == null)
					continue;
				
				try
				{
					final Spawn spawn = new Spawn(ticket.getNpcId());
					spawn.setLoc(item.getPosition());
					spawn.setRespawnState(false);
					
					// Spawn the Npc and associate it to this Castle.
					final Npc guard = spawn.doSpawn(false);
					guard.setCastle(this);
					
					_siegeGuards.add(guard);
				}
				catch (Exception e)
				{
					LOGGER.error("Couldn't spawn npc ticket {}. ", e, ticket.getNpcId());
					continue;
				}
				
				// Delete the ticket item.
				item.decayMe();
			}
			
			_droppedTickets.clear();
		}
		else
		{
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement(LOAD_GUARDS))
			{
				ps.setInt(1, _castleId);
				
				try (ResultSet rs = ps.executeQuery())
				{
					while (rs.next())
					{
						final NpcTemplate template = NpcData.getInstance().getTemplate(rs.getInt("npcId"));
						if (template == null)
							continue;
						
						final Spawn spawn = new Spawn(template);
						spawn.setLoc(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"), rs.getInt("heading"));
						spawn.setRespawnDelay(rs.getInt("respawnDelay"));
						spawn.setRespawnState(true);
						
						double distToTower = Double.MAX_VALUE;
						ControlTower towerToAdd = null;
						
						for (ControlTower tower : _siege.getControlTowers())
						{
							final double distance = tower.distance3D(spawn.getLoc());
							if (distToTower > distance)
							{
								distToTower = distance;
								towerToAdd = tower;
							}
						}
						
						if (towerToAdd != null)
						{
							towerToAdd.addSpawn(spawn);
							
							// Spawn the Npc and associate it to this Castle.
							final Npc guard = spawn.doSpawn(false);
							guard.setCastle(this);
							
							_siegeGuards.add(guard);
						}
					}
				}
			}
			catch (Exception e)
			{
				LOGGER.error("Couldn't load siege guards.", e);
			}
		}
	}
	
	/**
	 * Despawn neutral guards or player-based mercenaries.
	 */
	public void despawnSiegeGuardsOrMercenaries()
	{
		for (Npc npc : _siegeGuards)
		{
			final Spawn spawn = npc.getSpawn();
			if (spawn != null)
				spawn.setRespawnState(false);
			
			npc.doDie(npc);
		}
		
		_siegeGuards.clear();
		
		if (_ownerId <= 0)
		{
			for (ControlTower tower : _siege.getControlTowers())
				tower.getSpawns().clear();
		}
	}
	
	public List<TowerSpawnLocation> getControlTowers()
	{
		return _controlTowers;
	}
	
	public List<TowerSpawnLocation> getFlameTowers()
	{
		return _flameTowers;
	}
	
	public void loadTrapUpgrade()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_TRAPS))
		{
			ps.setInt(1, _castleId);
			
			try (ResultSet rs = ps.executeQuery())
			{
				while (rs.next())
					_flameTowers.get(rs.getInt("towerIndex")).setUpgradeLevel(rs.getInt("level"));
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't load traps.", e);
		}
	}
	
	public List<Integer> getRelatedNpcIds()
	{
		return _relatedNpcIds;
	}
	
	public void setRelatedNpcIds(String idsToSplit)
	{
		for (String splittedId : idsToSplit.split(";"))
			_relatedNpcIds.add(Integer.parseInt(splittedId));
	}
	
	/**
	 * Add a {@link Location} into the dedicated {@link SpawnType} {@link List}.<br>
	 * <br>
	 * If the key doesn't exist, generate a new {@link ArrayList}.
	 * @param type : The SpawnType to test.
	 * @param loc : The Location to add.
	 */
	public final void addSpawn(SpawnType type, Location loc)
	{
		_spawns.computeIfAbsent(type, k -> new ArrayList<>()).add(loc);
	}
	
	/**
	 * @param type : The SpawnType to test.
	 * @return the {@link List} of {@link Location}s based on {@link SpawnType} parameter. If that SpawnType doesn't exist, return the OWNER List of Locations.
	 */
	public final List<Location> getSpawns(SpawnType type)
	{
		return _spawns.getOrDefault(type, _spawns.get(SpawnType.OWNER));
	}
	
	/**
	 * @param type : The SpawnType to test.
	 * @return a random {@link Location} based on {@link SpawnType} parameter. If that SpawnType doesn't exist, return a NORMAL random Location.
	 */
	public final Location getRndSpawn(SpawnType type)
	{
		return Rnd.get(getSpawns(type));
	}
	
	public String getName()
	{
		return _name;
	}
	
	public int getCircletId()
	{
		return _circletId;
	}
	
	public void setCircletId(int circletId)
	{
		_circletId = circletId;
	}
	
	public int getOwnerId()
	{
		return _ownerId;
	}
	
	public void setOwnerId(int ownerId)
	{
		_ownerId = ownerId;
	}
	
	public Siege getSiege()
	{
		return _siege;
	}
	
	public void setSiege(Siege siege)
	{
		_siege = siege;
	}
	
	public Calendar getSiegeDate()
	{
		return _siegeDate;
	}
	
	
	public void setSiegeDate(Calendar siegeDate)
	{
		_siegeDate = siegeDate;
	}
	
	public boolean isTimeRegistrationOver()
	{
		return _isTimeRegistrationOver;
	}
	
	public void setTimeRegistrationOver(boolean val)
	{
		_isTimeRegistrationOver = val;
	}
	
	public int getTaxPercent()
	{
		return _taxPercent;
	}
	
	public double getTaxRate()
	{
		return _taxRate;
	}
	
	public long getTreasury()
	{
		return _treasury;
	}
	
	public void setTreasury(long treasury)
	{
		_treasury = treasury;
	}
	
	public List<Integer> getArtifacts()
	{
		return _artifacts;
	}
	
	public void setArtifacts(String idsToSplit)
	{
		for (String idToSplit : idsToSplit.split(";"))
			_artifacts.add(Integer.parseInt(idToSplit));
	}
	
	public boolean isGoodArtifact(WorldObject object)
	{
		return object instanceof HolyThing && _artifacts.contains(((HolyThing) object).getNpcId());
	}
	
	/**
	 * @param towerIndex : The index to check on.
	 * @return the trap upgrade level for a dedicated tower index.
	 */
	public int getTrapUpgradeLevel(int towerIndex)
	{
		final TowerSpawnLocation spawn = _flameTowers.get(towerIndex);
		return (spawn != null) ? spawn.getUpgradeLevel() : 0;
	}
	
	/**
	 * Save properties of a Flame Tower.
	 * @param towerIndex : The tower to affect.
	 * @param level : The new level of update.
	 * @param save : Should it be saved on database or not.
	 */
	public void setTrapUpgrade(int towerIndex, int level, boolean save)
	{
		if (save)
		{
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement(UPDATE_TRAP))
			{
				ps.setInt(1, _castleId);
				ps.setInt(2, towerIndex);
				ps.setInt(3, level);
				ps.execute();
			}
			catch (Exception e)
			{
				LOGGER.error("Couldn't replace trap upgrade.", e);
			}
		}
		
		final TowerSpawnLocation spawn = _flameTowers.get(towerIndex);
		if (spawn != null)
			spawn.setUpgradeLevel(level);
	}
	
	/**
	 * Delete all traps informations for a single castle.
	 */
	public void removeTrapUpgrade()
	{
		for (TowerSpawnLocation ts : _flameTowers)
			ts.setUpgradeLevel(0);
		
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_TRAP))
		{
			ps.setInt(1, _castleId);
			ps.executeUpdate();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't delete trap upgrade.", e);
		}
	}
	
	public void checkItemsForMember(ClanMember member)
	{
		final Player player = member.getPlayerInstance();
		if (player != null)
			player.checkItemRestriction();
		else
		{
			try (Connection con = ConnectionPool.getConnection();
				PreparedStatement ps = con.prepareStatement(UPDATE_ITEMS_LOC))
			{
				ps.setInt(1, _circletId);
				ps.setInt(2, member.getObjectId());
				ps.executeUpdate();
			}
			catch (Exception e)
			{
				LOGGER.error("Couldn't update items for member.", e);
			}
		}
	}
	
	public void checkItemsForClan(Clan clan)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_ITEMS_LOC))
		{
			ps.setInt(1, _circletId);
			
			for (ClanMember member : clan.getMembers())
			{
				final Player player = member.getPlayerInstance();
				if (player != null)
					player.checkItemRestriction();
				else
				{
					ps.setInt(2, member.getObjectId());
					ps.addBatch();
				}
			}
			ps.executeBatch();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't update items for clan.", e);
		}
	}
}