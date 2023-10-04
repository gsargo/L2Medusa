package net.sf.l2j.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.sf.l2j.commons.pool.ConnectionPool;

import net.sf.l2j.gameserver.model.actor.Player;

/**
 * @author DevKatara
 */
public class PlayerVariables
{
	// When var exist
	public static void changeValue(Player player, String name, String value)
	{
		if (!player.getVariables().containsKey(name))
		{
			player.sendMessage("Variable is not exist...");
			return;
		}
		
		getVarObject(player, name).setValue(value);
		Mysql.set("UPDATE character_memo_alt SET value=? WHERE obj_id=? AND name=?", value, player.getObjectId(), name);
	}
	
	public static void setVar(Player player, String name, String value, long expirationTime)
	{
		if (player.getVariables().containsKey(name))
			getVarObject(player, name).stopExpireTask();
		
		player.getVariables().put(name, new PlayerVar(player, name, value, expirationTime));
		Mysql.set("REPLACE INTO character_memo_alt (obj_id, name, value, expire_time) VALUES (?,?,?,?)", player.getObjectId(), name, value, expirationTime);
	}
	
	public static void setVar(int objId, String name, String value, long expirationTime)
	{
		Mysql.set("REPLACE INTO character_memo_alt (obj_id, name, value, expire_time) VALUES (?,?,?,?)", objId, name, value, expirationTime);
	}
	
	public static void setVar(Player player, String name, int value, long expirationTime)
	{
		setVar(player, name, String.valueOf(value), expirationTime);
	}
	
	public void setVar(Player player, String name, long value, long expirationTime)
	{
		setVar(player, name, String.valueOf(value), expirationTime);
	}
	
	public static PlayerVar getVarObject(Player player, String name)
	{
		if (player.getVariables() == null)
			return null;
		
		return player.getVariables().get(name);
	}
	
	public static long getVarTimeToExpire(Player player, String name)
	{
		try
		{
			return getVarObject(player, name).getTimeToExpire();
		}
		catch (NullPointerException npe)
		{
		}
		
		return 0;
	}
	
	public static void unsetVar(Player player, String name)
	{
		if (name == null)
			return;
		
		// Avoid possible unsetVar that have elements for login
		if (player == null)
			return;
		
		PlayerVar pv = player.getVariables().remove(name);
		
		if (pv != null)
		{
			if (name.contains("delete_temp_item"))
				pv.getOwner().deleteTempItem(Integer.parseInt(pv.getValue()));
			else if (name.contains("solo_hero"))
			{
				pv.getOwner().broadcastCharInfo();
				pv.getOwner().broadcastUserInfo();
			}
			
			Mysql.set("DELETE FROM character_memo_alt WHERE obj_id=? AND name=? LIMIT 1", pv.getOwner().getObjectId(), name);
			
			pv.stopExpireTask();
		}
	}
	
	public static void deleteExpiredVar(Player player, String name, String value)
	{
		if (name == null)
			return;
		
		if (name.contains("delete_temp_item"))
			player.deleteTempItem(Integer.parseInt(value));
		/*
		 * else if(name.contains("solo_hero")) // Useless player.broadcastCharInfo();
		 */
		
		Mysql.set("DELETE FROM character_memo_alt WHERE obj_id=? AND name=? LIMIT 1", player.getObjectId(), name);
	}
	
	public static String getVar(Player player, String name)
	{
		PlayerVar pv = getVarObject(player, name);
		
		if (pv == null)
			return null;
		
		return pv.getValue();
	}
	
	public static long getVarTimeToExpireSQL(Player player, String name)
	{
		long expireTime = 0;
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement statement = con.prepareStatement("SELECT expire_time FROM character_memo_alt WHERE obj_id = ? AND name = ?"))
		{
			statement.setLong(1, player.getObjectId());
			statement.setString(2, name);
			try (ResultSet rset = statement.executeQuery())
			{
				while (rset.next())
					expireTime = rset.getLong("expire_time");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return expireTime;
	}
	
	public static boolean getVarB(Player player, String name, boolean defaultVal)
	{
		PlayerVar pv = getVarObject(player, name);
		
		if (pv == null)
			return defaultVal;
		
		return pv.getValueBoolean();
	}
	
	public static boolean getVarB(Player player, String name)
	{
		return getVarB(player, name, false);
	}
	
	public long getVarLong(Player player, String name)
	{
		return getVarLong(player, name, 0L);
	}
	
	public long getVarLong(Player player, String name, long defaultVal)
	{
		long result = defaultVal;
		String var = getVar(player, name);
		if (var != null)
			result = Long.parseLong(var);
		
		return result;
	}
	
	public static int getVarInt(Player player, String name)
	{
		return getVarInt(player, name, 0);
	}
	
	public static int getVarInt(Player player, String name, int defaultVal)
	{
		int result = defaultVal;
		String var = getVar(player, name);
		if (var != null)
		{
			if (var.equalsIgnoreCase("true"))
				result = 1;
			else if (var.equalsIgnoreCase("false"))
				result = 0;
			else
				result = Integer.parseInt(var);
		}
		return result;
	}
	
	public static void votedResult(Player player)
	{
		Connection con = null;
		PreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = ConnectionPool.getConnection();
			offline = con.prepareStatement("SELECT * FROM character_memo_alt WHERE obj_id=? AND name=?");
			offline.setInt(1, player.getObjectId());
			rs = offline.executeQuery();
			boolean hasResult = rs.next();
			if (!hasResult)
			{
				insertVoteSites(player);
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
	
	public static void insertVoteSites(Player player)
	{
		Connection con = null;
		PreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = ConnectionPool.getConnection();
			offline = con.prepareStatement("INSERT INTO character_memo_alt (obj_id,name,value,expire_time) VALUES (?,?,?,?)");
			offline.setInt(1, player.getObjectId());
			offline.setString(2, "votedSites");
			offline.setString(3, "0");
			offline.setLong(4, 0);
			offline.execute();
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
	
	public static void loadVariables(Player player)
	{
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
				String name = rs.getString("name");
				String value = rs.getString("value");
				long expire_time = rs.getLong("expire_time");
				long curtime = System.currentTimeMillis();
				
				if ((expire_time <= curtime) && (expire_time > 0))
				{
					deleteExpiredVar(player, name, rs.getString("value")); // TODO: Remove the Var
					continue;
				}
				
				player.getVariables().put(name, new PlayerVar(player, name, value, expire_time));
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
	
	public static String getVarValue(Player player, String var, String defaultString)
	{
		String value = null;
		Connection con = null;
		PreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = ConnectionPool.getConnection();
			offline = con.prepareStatement("SELECT value FROM character_memo_alt WHERE obj_id = ? AND name = ?");
			offline.setInt(1, player.getObjectId());
			offline.setString(2, var);
			rs = offline.executeQuery();
			if (rs.next())
				value = rs.getString("value");
			
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
		return value == null ? defaultString : value;
	}
	
	public static String getVarValue(int objectId, String var, String defaultString)
	{
		String value = null;
		Connection con = null;
		PreparedStatement offline = null;
		ResultSet rs = null;
		try
		{
			con = ConnectionPool.getConnection();
			offline = con.prepareStatement("SELECT value FROM character_memo_alt WHERE obj_id = ? AND name = ?");
			offline.setInt(1, objectId);
			offline.setString(2, var);
			rs = offline.executeQuery();
			if (rs.next())
				value = rs.getString("value");
			
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
		return value == null ? defaultString : value;
	}
	
}
