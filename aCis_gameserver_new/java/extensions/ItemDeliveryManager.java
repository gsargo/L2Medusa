package extensions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.stream.IntStream;


import net.sf.l2j.commons.pool.ThreadPool;  
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;

import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.ItemList;

public class ItemDeliveryManager
{
	private static Logger _log = Logger.getLogger(ItemDeliveryManager.class.getName());

	private final static String UPDATE = "UPDATE user_item_delivery SET status=1 WHERE id=?;";
	private final static String SELECT = "SELECT id, item_id, item_count, char_name FROM user_item_delivery WHERE status=0;";

	public static ItemDeliveryManager getInstance()
	{
		return SingletonHolder._instance;
	}
	
	private static class SingletonHolder
	{
		protected static final ItemDeliveryManager _instance = new ItemDeliveryManager();
	}
	
	protected ItemDeliveryManager()
	{
		ThreadPool.scheduleAtFixedRate(() -> start(), 45000, 45000);
		_log.info("Item Delivery Manager: started.");
	}
	
	private static void start()
	{
		int id = 0;
		int item_id = 0;
		int item_count = 0;
		String char_name = "";
		///////////////
		 try (Connection con = ConnectionPool.getConnection())
		    {
		        try(PreparedStatement stm = con.prepareStatement(SELECT); 
		    		ResultSet rset = stm.executeQuery())
					{
						while (rset.next())
						{
							id = rset.getInt("id");
							item_id = rset.getInt("item_id");
							item_count = rset.getInt("item_count");
							char_name = rset.getString("char_name");
							if (item_id > 0 && item_count > 0 && char_name != "")
							{
								for (Player activeChar : World.getInstance().getPlayers())
								{
									if (activeChar == null || activeChar.isOnline() == false)
									{
										continue;
									}
									if (activeChar.getName().toLowerCase().equals(char_name.toLowerCase()))
									{
										activeChar.getInventory().addItem("Delivery", item_id, item_count, activeChar, null);
										activeChar.getInventory().updateDatabase();
										activeChar.sendPacket(new ItemList(activeChar, true));
										activeChar.sendMessage("Delivery of " + item_count + " coins.");
										UpdateDelivery(id);
										activeChar.sendPacket(ActionFailed.STATIC_PACKET);
									}
								}
						}
					}
		        
			    }
		    }
		    catch (Exception e)
		    {
		    	_log.warning("Check delivery items failed. " + e.getMessage());
		    }
		///////
	
		
	}

	private static void UpdateDelivery(int id)
	{
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement statement = con.prepareStatement(UPDATE))
		{
			statement.setInt(1, id);
			statement.execute();
		}
		catch (SQLException e)
		{
			_log.warning("Failed to update item delivery id: " + id);
		}
	}
}