package net.sf.l2j.gameserver.model.clanhall;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.logging.CLogger;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.pool.ThreadPool;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.data.sql.ClanTable;
import net.sf.l2j.gameserver.enums.SpawnType;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.instance.Door;
import net.sf.l2j.gameserver.model.location.Location;
import net.sf.l2j.gameserver.model.pledge.Clan;
import net.sf.l2j.gameserver.model.zone.type.ClanHallZone;
import net.sf.l2j.gameserver.network.SystemMessageId;
import net.sf.l2j.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import net.sf.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * In Lineage 2, there are special building for clans: clan halls.<br>
 * <br>
 * Clan halls give the owning clan some useful benefits. There are 2 types of clan halls: auctionable and contestable. A clan can own only 1 hall at the same time.
 * <ul>
 * <li>Auctionable clan halls can be found in any big township, excluding starting villages, Oren and Heine. Any clan can purchase a hall via auction if they can afford it.</li>
 * <li>Some clan halls come into players possession only once they're conquered. Just like clan halls available via purchase, they are used for making items, teleportation, casting auras etc.</li>
 * </ul>
 */
public class ClanHall
{
	protected static final CLogger LOGGER = new CLogger(ClanHall.class.getName());
	
	private static final String DELETE_FUNCTIONS = "DELETE FROM clanhall_functions WHERE hall_id=?";
	private static final String UPDATE_CH = "UPDATE clanhall SET ownerId=?, paidUntil=?, paid=?, sellerBid=?, sellerName=?, sellerClanName=?, endDate=? WHERE id=?";
	
	private static final int ONE_WEEK = 604800000; // One week
	
	public static final int FUNC_TELEPORT = 1;
	public static final int FUNC_ITEM_CREATE = 2;
	public static final int FUNC_RESTORE_HP = 3;
	public static final int FUNC_RESTORE_MP = 4;
	public static final int FUNC_RESTORE_EXP = 5;
	public static final int FUNC_SUPPORT = 6;
	public static final int FUNC_DECO_FRONTPLATEFORM = 7;
	public static final int FUNC_DECO_CURTAINS = 8;
	
	private final Map<Integer, ClanHallFunction> _functions = new ConcurrentHashMap<>();
	private final Map<SpawnType, List<Location>> _spawns = new HashMap<>();
	
	private final List<Door> _doors = new ArrayList<>();
	private final int[] _relatedNpcIds;
	
	private final int _id;
	private final String _name;
	private final String _desc;
	private final String _location;
	private final int _grade;
	private final int _lease;
	private final int _defaultBid;
	
	private ScheduledFuture<?> _feeTask;
	private Auction _auction;
	private int _ownerId;
	private ClanHallZone _zone;
	private long _paidUntil;
	private boolean _isPaid;
	
	public ClanHall(StatSet set)
	{
		_id = set.getInteger("id");
		_name = set.getString("name");
		_desc = set.getString("desc");
		_location = set.getString("loc");
		_grade = set.getInteger("grade", 0);
		_lease = set.getInteger("lease", 0);
		_defaultBid = set.getInteger("defaultBid", 0);
		_relatedNpcIds = set.getIntegerArray("relatedNpcIds");
	}
	
	public final String Auction_custom(int ch_id)
	{
		switch(ch_id)
		{
			case 22://Gludio
			case 23:
			case 24:
			case 25:
				return "<font color=\"93e697\">Gludio";
			
			case 36://Aden
			case 37:
			case 38:
			case 41:
				return "<font color=\"fad991\">Aden";
				
			case 42://Giran
			case 43:
			case 44:
			case 45:
				return "<font color=\"768fcf\">Giran";
				
			case 51://Rune
			case 52:
			case 53:
			case 56:
				return "<font color=\"e89e76\">Rune";
				
			default:
				return "<font color=\"aaaaff\">";
		}
	}
	
	public final int getId()
	{
		return _id;
	}
	
	public final String getName()
	{
		return _name;
	}
	
	public final String getDesc()
	{
		return _desc;
	}
	
	public final String getLocation()
	{
		return _location;
	}
	
	public final int getGrade()
	{
		return _grade;
	}
	
	public final int getLease()
	{
		return _lease;
	}
	
	public final int getDefaultBid()
	{
		return _defaultBid;
	}
	
	public int[] getRelatedNpcIds()
	{
		return _relatedNpcIds;
	}
	
	public final Auction getAuction()
	{
		return _auction;
	}
	
	public final void setAuction(Auction auction)
	{
		_auction = auction;
	}
	
	public final int getOwnerId()
	{
		return _ownerId;
	}
	
	public void setOwnerId(int ownerId)
	{
		_ownerId = ownerId;
	}
	
	public final long getPaidUntil()
	{
		return _paidUntil;
	}
	
	public void setPaidUntil(long paidUntil)
	{
		_paidUntil = paidUntil;
	}
	
	public final boolean getPaid()
	{
		return _isPaid;
	}
	
	public void setPaid(boolean isPaid)
	{
		_isPaid = isPaid;
	}
	
	public ClanHallZone getZone()
	{
		return _zone;
	}
	
	public void setZone(ClanHallZone zone)
	{
		_zone = zone;
	}
	
	/**
	 * @return true if this {@link ClanHall} is free.
	 */
	public boolean isFree()
	{
		return _ownerId == 0;
	}
	
	/**
	 * @return the {@link List} of all {@link ClanHallFunction}s this {@link ClanHall} owns.
	 */
	public final Map<Integer, ClanHallFunction> getFunctions()
	{
		return _functions;
	}
	
	/**
	 * Add a {@link Location} into the dedicated {@link SpawnType} {@link List}.<br>
	 * <br>
	 * If the key doesn't exist, generate a new {@link ArrayList}.
	 * @param type : The {@link SpawnType} to test.
	 * @param loc : The {@link Location} to add.
	 */
	public final void addSpawn(SpawnType type, Location loc)
	{
		_spawns.computeIfAbsent(type, k -> new ArrayList<>()).add(loc);
	}
	
	/**
	 * @param type : The {@link SpawnType} to test.
	 * @return the {@link List} of {@link Location}s based on {@link SpawnType} parameter. If that {@link SpawnType} doesn't exist, return the OWNER {@link List} of {@link Location}.
	 */
	public final List<Location> getSpawns(SpawnType type)
	{
		return _spawns.getOrDefault(type, _spawns.get(SpawnType.OWNER));
	}
	
	/**
	 * @param type : The {@link SpawnType} to test.
	 * @return a random {@link Location} based on {@link SpawnType} parameter. If that {@link SpawnType} doesn't exist, return a NORMAL random {@link Location}.
	 */
	public final Location getRndSpawn(SpawnType type)
	{
		return Rnd.get(getSpawns(type));
	}
	
	/**
	 * @return the {@link List} of all {@link Door}s this {@link ClanHall} owns.
	 */
	public final List<Door> getDoors()
	{
		return _doors;
	}
	
	/**
	 * @param doorId : The id to test.
	 * @return the {@link Door} based on a doorId.
	 */
	public final Door getDoor(int doorId)
	{
		return _doors.stream().filter(d -> d.getDoorId() == doorId).findFirst().orElse(null);
	}
	
	/**
	 * @param type : The type of {@link ClanHallFunction} we search.
	 * @return the {@link ClanHallFunction} associated to the type.
	 */
	public ClanHallFunction getFunction(int type)
	{
		return _functions.get(type);
	}
	
	/**
	 * Free this {@link ClanHall}.
	 * <ul>
	 * <li>Remove the {@link ClanHall} from the {@link Clan}.</li>
	 * <li>Reset all variables to default.</li>
	 * <li>Delete {@link ClanHallFunction}s, and update the database.</li>
	 * </ul>
	 */
	public void free()
	{
		// Cancel fee task, if existing.
		if (_feeTask != null)
		{
			_feeTask.cancel(false);
			_feeTask = null;
		}
		
		// Do some actions on previous owner, if any.
		final Clan clan = ClanTable.getInstance().getClan(_ownerId);
		if (clan != null)
		{
			// Set the clan hall id back to 0.
			clan.setClanHallId(0);
			
			// Refresh Clan Action panel.
			clan.broadcastToMembers(new PledgeShowInfoUpdate(clan));
		}
		
		_ownerId = 0;
		_paidUntil = 0;
		_isPaid = false;
		
		// Remove all related functions.
		removeAllFunctions();
		
		// Close all doors.
		openCloseDoors(false);
		
		if (_auction != null)
		{
			// Remove existing bids.
			_auction.removeBids(null);
			
			// Reset auction to initial values if existing.
			_auction.reset(true);
			
			// Launch the auction task.
			_auction.startAutoTask();
		}
		
		// Update dabase.
		updateDb();
	}
	
	/**
	 * Set {@link ClanHall} {@link Clan} owner. If previous owner was existing, do some actions on it.
	 * @param clan : The new {@link ClanHall} owner.
	 */
	public void setOwner(Clan clan)
	{
		if (_auction != null)
		{
			// Send back all losers bids, clear the Bidders Map.
			_auction.removeBids(clan);
			
			// Reset variables.
			_auction.reset(false);
		}
		
		// Verify that Clan isn't null.
		if (clan == null)
		{
			if (_auction != null)
				_auction.startAutoTask();
			
			return;
		}
		
		// Do some actions on previous owner, if any.
		final Clan owner = ClanTable.getInstance().getClan(_ownerId);
		if (owner != null)
		{
			// Set the clan hall id back to 0.
			owner.setClanHallId(0);
			
			// Refresh Clan Action panel.
			owner.broadcastToMembers(new PledgeShowInfoUpdate(owner));
		}
		
		// Remove all related functions.
		removeAllFunctions();
		
		// Close all doors.
		openCloseDoors(false);
		
		clan.setClanHallId(_id);
		
		_ownerId = clan.getClanId();
		_paidUntil = System.currentTimeMillis() + ONE_WEEK;
		_isPaid = true;
		
		// Initialize the Fee task for this Clan. The previous Fee task is dropped.
		initializeFeeTask();
		
		// Refresh Clan Action panel.
		clan.broadcastToMembers(new PledgeShowInfoUpdate(clan));
		
		// Teleport out all outsiders (potential previous owners).
		banishForeigners();
		
		// Save all informations into database.
		updateDb();
	}
	
	/**
	 * Open or close a {@link ClanHall} {@link Door} by a {@link Player}. The {@link Player} must be the owner of this {@link ClanHall}.
	 * @param player : The {@link Player} who requested to open/close the door.
	 * @param doorId : The affected doorId.
	 * @param open : If set to true the {@link Door} will open, false will close it.
	 */
	public void openCloseDoor(Player player, int doorId, boolean open)
	{
		if (player != null && player.getClanId() == getOwnerId())
			openCloseDoor(doorId, open);
	}
	
	/**
	 * Open or close a {@link ClanHall} {@link Door} based on its doorId.
	 * @param doorId : The affected doorId.
	 * @param open : If set to true the {@link Door} will open, false will close it.
	 */
	public void openCloseDoor(int doorId, boolean open)
	{
		openCloseDoor(getDoor(doorId), open);
	}
	
	/**
	 * Open or close a {@link ClanHall} {@link Door}.
	 * @param door : The affected {@link Door}.
	 * @param open : If set to true the {@link Door} will open, false will close it.
	 */
	public static void openCloseDoor(Door door, boolean open)
	{
		if (door != null)
		{
			if (open)
				door.openMe();
			else
				door.closeMe();
		}
	}
	
	/**
	 * Open or close all {@link ClanHall} related {@link Door}s by a {@link Player}. The {@link Player} must be the owner of this {@link ClanHall}.
	 * @param player : The {@link Player} who requested to open/close doors.
	 * @param open : If set to true the {@link Door} will open, false will close it.
	 */
	public void openCloseDoors(Player player, boolean open)
	{
		if (player != null && player.getClanId() == getOwnerId())
			openCloseDoors(open);
	}
	
	/**
	 * Open or close all {@link ClanHall} related {@link Door}s.
	 * @param open : If set to true the {@link Door} will open, false will close it.
	 */
	public void openCloseDoors(boolean open)
	{
		for (Door door : _doors)
		{
			if (open)
				door.openMe();
			else
				door.closeMe();
		}
	}
	
	/**
	 * Banish all {@link Player}s from that {@link ClanHall} zone.
	 */
	public void banishForeigners()
	{
		if (_zone != null)
			_zone.banishForeigners(getOwnerId());
	}
	
	/**
	 * Remove all {@link ClanHallFunction}s linked to this {@link ClanHall}.
	 */
	public void removeAllFunctions()
	{
		// Remove all ClanHallFunctions from memory.
		_functions.clear();
		
		// Update db.
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(DELETE_FUNCTIONS))
		{
			ps.setInt(1, getId());
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't delete all clan hall functions.", e);
		}
	}
	
	/**
	 * Update a {@link ClanHallFunction} linked to this {@link ClanHall}. If it doesn't exist, generate it and save it on database.
	 * @param player : The {@link Player} who requested the change.
	 * @param type : The type of {@link ClanHallFunction} to update.
	 * @param lvl : The new level to set.
	 * @param lease : The associated lease taken from {@link Player} inventory.
	 * @param rate : The new rate to set.
	 * @return True if the {@link ClanHallFunction} has been successfully updated.
	 */
	public boolean updateFunction(Player player, int type, int lvl, int lease, long rate)
	{
		// Player doesn't exist.
		if (player == null)
			return false;
		
		// A lease exists, but the Player can't pay it using its inventory adena.
		if (lease > 0 && !player.destroyItemByItemId("Consume", 57, lease, null, true))
			return false;
		
		ClanHallFunction chf = _functions.get(type);
		if (chf == null)
		{
			// Generate a ClanHallFunction and save it on the database.
			chf = new ClanHallFunction(this, type, lvl, lease, rate, System.currentTimeMillis() + rate);
			chf.dbSave();
			
			// Add the ClanHallFunction on memory.
			_functions.put(type, chf);
			
			return true;
		}
		
		// Both lease and level are set to 0, we remove the function.
		if (lvl == 0 && lease == 0)
			chf.removeFunction();
		// Refresh the function.
		else
			chf.refreshFunction(lease, lvl);
		
		return true;
	}
	
	/**
	 * Save all related informations of this {@link ClanHall} into database.
	 */
	public void updateDb()
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(UPDATE_CH))
		{
			ps.setInt(1, _ownerId);
			ps.setLong(2, _paidUntil);
			ps.setInt(3, (_isPaid) ? 1 : 0);
			
			if (_auction != null)
			{
				if (_auction.getSeller() != null)
				{
					ps.setInt(4, _auction.getSeller().getBid());
					ps.setString(5, _auction.getSeller().getName());
					ps.setString(6, _auction.getSeller().getClanName());
				}
				else
				{
					ps.setInt(4, 0);
					ps.setString(5, "");
					ps.setString(6, "");
				}
				ps.setLong(7, _auction.getEndDate());
			}
			else
			{
				ps.setInt(4, 0);
				ps.setString(5, "");
				ps.setString(6, "");
				ps.setLong(7, 0);
			}
			ps.setInt(8, _id);
			ps.execute();
		}
		catch (Exception e)
		{
			LOGGER.error("Couldn't update clan hall.", e);
		}
	}
	
	/**
	 * Initialize Fee Task.
	 */
	public void initializeFeeTask()
	{
		// Cancel fee task, if existing. We don't care setting it to null, since it is fed just after.
		if (_feeTask != null)
			_feeTask.cancel(false);
		
		// Take current time.
		long time = System.currentTimeMillis();
		
		// If time didn't past yet, calculate the difference and apply it on the Fee task. Otherwise, run it instantly.
		time = (_paidUntil > time) ? _paidUntil - time : 0;
		
		// Run the Fee task with the given, calculated, time.
		_feeTask = ThreadPool.schedule(new FeeTask(), time);
	}
	
	private class FeeTask implements Runnable
	{
		public FeeTask()
		{
		}
		
		@Override
		public void run()
		{
			// Don't bother if ClanHall is already free.
			if (isFree())
				return;
			
			// Clan can't be retrieved, we free the ClanHall.
			final Clan clan = ClanTable.getInstance().getClan(getOwnerId());
			if (clan == null)
			{
				free();
				return;
			}
			
			// We got enough adena, mark the ClanHall as being paid, send back the task one week later.
			if (clan.getWarehouse().getAdena() >= getLease())
			{
				// Delete the adena.
				clan.getWarehouse().destroyItemByItemId("CH_rental_fee", 57, getLease(), null, null);
				
				// Run the task one week later.
				_feeTask = ThreadPool.schedule(new FeeTask(), ONE_WEEK);
				
				// Refresh variables. Force _isPaid to be set on true, in case we return from grace period.
				_paidUntil += ONE_WEEK;
				_isPaid = true;
				
				// Save all informations into database.
				updateDb();
			}
			// Not enough adena.
			else
			{
				// The ClanHall was already under failed payment ; we free the ClanHall immediately.
				if (!_isPaid)
				{
					// Free the ClanHall.
					free();
					
					// Send message to all Clan members.
					clan.broadcastToMembers(SystemMessage.getSystemMessage(SystemMessageId.THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED));
				}
				// Grace period, we will retest it one day later.
				else
				{
					// Run the task one day later.
					_feeTask = ThreadPool.schedule(new FeeTask(), ONE_WEEK);
					
					// Refresh variables.
					_paidUntil += ONE_WEEK;
					_isPaid = false;
					
					// Save all informations into database.
					updateDb();
					
					// Send message to all Clan members.
					clan.broadcastToMembers(SystemMessage.getSystemMessage(SystemMessageId.PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW).addNumber(getLease()));
				}
			}
		}
	}
}