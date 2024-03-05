package net.sf.l2j.gameserver.communitybbs.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.stream.IntStream;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;

 
public class TopBBSManager extends BaseBBSManager
{
	private static final String LOAD_PVP_HTML = "SELECT * FROM characters WHERE pvpkills > 0 AND accesslevel = 0 AND vip >=0 ORDER BY pvpkills DESC LIMIT 15";
    //private static final String LOAD_PVP_HTML = "SELECT char_name,pvpkills,vip,online FROM characters WHERE pvpkills > 0 ORDER BY pvpkills";
    private static final String LOAD_PK_HTML = "SELECT * FROM characters WHERE pkkills > 0 AND accesslevel = 0 AND vip >=0 ORDER BY pkkills DESC LIMIT 15";
    StringBuilder _pvp = new StringBuilder();
    StringBuilder _pk = new StringBuilder();
    
    private long _nextUpdate;
    private long _nextUpdate_pk;
    
    protected TopBBSManager()
    {
    }
    
   /* @Override
    public void parseCmd(String command, Player activeChar)
    {
        if (command.startsWith("_bbspvp"))
        {
            showChatWindow(activeChar, "_bbspvp");
        }
    }
    */ 
    @Override
       public void parseCmd(String command, Player player)
       {
           if (command.equals("_bbspvp"))
        	   showRankWindow(player);
           else if(command.equals("_bbspk"))
        	   showPKRankWindow(player);
           else if(command.equals("_bbsgetfav"))
        	   showRankWindow(player);
           else
               super.parseCmd(command, player);
       }
    
    public void showRankWindow(Player player)
    { 
    if (_nextUpdate < System.currentTimeMillis())
     {
    	_nextUpdate = System.currentTimeMillis() + (60000L); 
    	//_nextUpdate = System.currentTimeMillis() + (100L); //for test purposes
    	_pvp.setLength(0);
    	
		    try (Connection con = ConnectionPool.getConnection())
		    {
		        try(PreparedStatement stm = con.prepareStatement(LOAD_PVP_HTML); // PVP QUERY
		    		ResultSet rSet = stm.executeQuery())
					{
			        	int index = 1;
						while (rSet.next())
						{
						    String pl = rSet.getString("char_name");
						    int vip = rSet.getInt("vip");
						    int pvpKills = rSet.getInt("pvpkills");						                    						    
						    String Vip_status = vip == 1 ? "<img src=CustomIconPack01.premium-member-extra-mini width=16 height=16>" : "<font color=B09878>"+ "-" +"</font>";  					    					    
						    int online = rSet.getInt("online");             
						    String status = online == 1 ? "<img src=CustomIconPack01.active_status32 width=8 height=29>" : "<img src=CustomIconPack01.deactive_status32 width=8 height=29>";
						    
						  	_pvp.append("<tr><td align=\"center\" width=15>"+ board_getColor(index) +"</font>" +"</td>"+"<td align=\"center\" width=120>"+ board_getRankColor(pvpKills)+pl+"</font>"+"</td>"+ "<td align=\"center\" width=25>"+pvpKills+"</td>" + "<td align=\"center\" width=30>" + getRank_miniicons(pvpKills) + " " + "</td>" + "<td align=\"center\" width=20>" + Vip_status+" " +"</td>" +"<td align=\"center\" width=20>" + status+"</td></tr>");
						    index++;
						}
						IntStream.range(index - 1, 15).forEach(x -> Fullfill_Empty_positions(_pvp));
					}
		        
			    }
		    
		    catch (Exception e)
		    {
		        System.out.println("Error while selecting top 15 pvp from database." +e);
		    }
		    
	      }   	  
    	String content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/ranking/ranklist.htm");
    	content = content.replaceAll("%pvp%", _pvp.toString());
    	
    	separateAndSend(content, player);
	    //separateAndSend(_pvp.toString(), player);
    }
    
    public void showPKRankWindow(Player player)
    { 
    if (_nextUpdate_pk < System.currentTimeMillis())
     {
    	_nextUpdate_pk = System.currentTimeMillis() + (60000L); 
    	_pk.setLength(0);
    	
		    try (Connection con = ConnectionPool.getConnection())
		    {
		    	try(PreparedStatement stm = con.prepareStatement(LOAD_PK_HTML);// PK QUERY
			    		ResultSet rSet = stm.executeQuery())
						{
				        	int index = 1;
							while (rSet.next())
							{
							    String pl = rSet.getString("char_name");
							    //final Player databasePlayer = World.getInstance().getPlayer(pl);
							    int pvpKills = rSet.getInt("pvpkills");
							    int pkKills = rSet.getInt("pkkills");					                    
							    int vip = rSet.getInt("vip");
							    String Vip_status = vip == 1 ? "<img src=CustomIconPack01.premium-member-extra-mini width=16 height=16>" : "<font color=B09878>"+ "-" +"</font>";  					    						    
							    int online = rSet.getInt("online");             
							    String status = online == 1 ? "<img src=CustomIconPack01.active_status32x32 width=8 height=29>" : "<img src=CustomIconPack01.deactive_status32 width=8 height=29>";
							    
							  	_pk.append("<tr><td align=\"center\" width=15>"+ board_getColor(index) +"</font>" +"</td>"+"<td align=\"center\" width=120>"+ board_getRankColor(pvpKills)+pl+"</font>"+"</td>"+ "<td align=\"center\" width=25>"+pkKills+"</td>" + "<td align=\"center\" width=30>" + getRank_miniicons(pvpKills) + " " + "</td>" + "<td align=\"center\" width=20>" + Vip_status+" " +"</td>" +"<td align=\"center\" width=20>" + status+"</td></tr>");
							    index++;
							}
							IntStream.range(index - 1, 15).forEach(x -> Fullfill_Empty_positions(_pk));
						}
			    }
		    
		    catch (Exception e)
		    {
		        System.out.println("Error while selecting top 15 pvp/pk from database." +e);
		    }
		    
	      }   	  
    	String content_pk = HtmCache.getInstance().getHtm("data/html/CommunityBoard/ranking/ranklist-1.htm");
    	content_pk = content_pk.replaceAll("%pk%", _pk.toString());
    	
    	separateAndSend(content_pk, player);
	    //separateAndSend(_pvp.toString(), player);
    }
    
    public void showHome(Player player)
    { 
    	String content_home = HtmCache.getInstance().getHtm("data/html/CommunityBoard/ranking/home.htm");
	
    	separateAndSend(content_home, player);
    }
    
    protected String board_getColor(int index)
       {
           switch (index)
           {
               case 1:
            	  return  "<img src=CustomIconPack01.topbbrank1 width=34 height=28>";
               case 2:
            	   return  "<img src=CustomIconPack01.topbbrank2 width=34 height=28>";
               case 3:
            	   return  "<img src=CustomIconPack01.topbbrank3 width=34 height=28>";
           }
          // return "<font color=FFFFFF>"+String.format("%02d", index) ;
           return "<font color=FFFFFF>" +index;
       }
    
    public String getRank_miniicons(int pvps)
	{
		if(pvps>=0 && pvps <500)
			return 
				"<img src=\"CustomIconPack01.mini_wooden\" width=24 height=24>";
		else if(pvps>=500 && pvps <1500)
			return 
				"<img src=\"CustomIconPack01.mini_iron\" width=24 height=24>";
		else if(pvps>=1500 && pvps <2500)
			return 
				 "<img src=\"CustomIconPack01.mini_bronze\" width=24 height=24>";
		else if(pvps>=2500 && pvps <3500)
			return 
				 "<img src=\"CustomIconPack01.mini_silver\" width=24 height=24>";			
		else if(pvps>=3500 && pvps <4500)
			return 
				"<img src=\"CustomIconPack01.mini_gold\" width=24 height=24";
		else if(pvps>=4500 && pvps <10000)
			return 
				 "<img src=\"CustomIconPack01.mini_platinum\" width=24 height=24>";
		else if(pvps>=10000 && pvps<20000)
			return 
				"<img src=\"CustomIconPack01.mini_diamond\" width=24 height=24>";
		
			return 
			"<img src=\"CustomIconPack01.mini_legendary\" width=24 height=24>";
// more than 20K PvPs
		
	}
    
    public String board_getRankColor(int pvps)
	{
		if(pvps>=0 && pvps <500)
			return 
				"<font color=\"FFFFFF\">";

		else if(pvps>=500 && pvps <1500)
			return 
				"<font color=\"C9C4A2\">";

		else if(pvps>=1500 && pvps <2500)
			return 
				"<font color=\"47B976\">";

		else if(pvps>=2500 && pvps <3500)
			return 
				"<font color=\"FFFF00\">";
				
		else if(pvps>=3500 && pvps <4500)
			return 
				"<font color=\"F1461C\">";

		else if(pvps>=4500 && pvps <10000)
			return 
				"<font color=\"26DFD0\">";

		else if(pvps>=10000 && pvps <20000)
			return 
				"<font color=\"853537\">";

			return 
				"<font color=\"B46AA0\">";
// more than 20K PvPs
	}
    
    protected void Fullfill_Empty_positions(StringBuilder sb)
       {
           sb.append("<tr>");
           sb.append("<td width=15 align=center><font color=B09878>--</font></td>");
           sb.append("<td width=40 align=center><font color=B09878>---------</font></td>");
           sb.append("<td width=25 align=center><font color=B09878>----</font></td>");
           sb.append("<td width=30 align=center><font color=B09878>---</font></td>");
           sb.append("<td width=20 align=center><font color=B09878>----</font></td>");
           sb.append("<td width=20 align=center><font color=B09878>-</font></td>");
           sb.append("</tr>");
       }
    
    public static TopBBSManager getInstance()
    {
        return SingletonHolder.INSTANCE;
    }
    
    private static class SingletonHolder
    {
        protected static final TopBBSManager INSTANCE = new TopBBSManager();
    }
    
}