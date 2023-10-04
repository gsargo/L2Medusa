package net.sf.l2j.gameserver.communitybbs.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.sql.ClanTable;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.pledge.Clan;

 
public class TopClansBBSManager extends BaseBBSManager
{
	private static final String LOAD_CLAN_HTML = "SELECT * FROM clan_data WHERE reputation_score > 0 ORDER BY reputation_score DESC LIMIT 10";

    StringBuilder _topClansHtml = new StringBuilder();
	Clan clan;
	String leader_name;

    private long _nextUpdate_clan;

    
    protected TopClansBBSManager()
    {
    	_nextUpdate_clan = 0;
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
    	if (command.equals("_bbstopclan"))
    		showTopClanRankWindow(player);
    	else if(command.equals("_bbsloc"))
    		showTopClanRankWindow(player);
    	else
    		super.parseCmd(command, player);
	}
    
    private void updateClansFromDB()
    {

    	
    	String clanListTemplate = "<table %color% cellpadding=0 cellspacing=0 border=0><tr>"
    		+ "<td width=44 align=center>%placement%<img height=6></td>"
    		+ "<td width=10 align=center><img height=6></td>"
    		+ "<td width=8>%ally_crest%</td>"
    		+ "<td width=16>%clan_crest%</td>"
    		+ "<td width=130 align=center>%name%</td>"
    		+ "<td width=145 align=center>%leader%</td>"
    		+ "<td width=40 align=center>%level%</td>"
    		+ "<td width=60 align=center>%points%</td>"
    		+ "<td width=90 align=center>%castle%</td>"
    		+ "<td width=80 align=center>%clan_hall%</td>"
    		+ "</tr></table>"
    		+ "<img src=l2ui.squaregray width=630 height=1>";
	
    	
    	var topClans = new ArrayList<Integer>();
    	
    	var sbClansListHtml = new StringBuilder();
    	
		try (Connection con = ConnectionPool.getConnection();
			PreparedStatement ps = con.prepareStatement(LOAD_CLAN_HTML);
			ResultSet rSet = ps.executeQuery())
		{
			while (rSet.next())
				topClans.add(rSet.getInt("clan_id"));
		}
		catch (Exception e)
		{
			LOGGER.error("Error while selecting top 10 Clans from database.", e);
		}
	    
    
    	for (int i = 0 ; i < 10; i++)
    	{
    		
    		var clanListHtml = clanListTemplate;
    		
    		
    		if (i < topClans.size())
    		{
			    final Clan clanData = ClanTable.getInstance().getClan(topClans.get(i));
			    
			    String ally_crest = clanData.getAllyCrestId()>0 ? "<img height=-10><img height=16 width=8 src=Crest.crest_1_" + clanData.getAllyCrestId()+"><img height=-16><img width=8 height=4 src=><img height=12>" : "<img height=-10><img height=17 width=8 src=L2UI.SquareBlack>";
			    String clan_crest = clanData.getCrestId()>0 ? "<img height=-10><img height=16 width=16 src=Crest.crest_1_" + clanData.getCrestId()+"><img height=-16><img width=16 height=4 src=><img height=12>" : "<img height=-10><img height=17 width=16 src=L2UI.SquareBlack>";
			    
			 
			    String clan_hall = clanData.hasClanHall()? "<img src=CustomIconPack02.clanhall width=32 height=32>" : "-";
			    String _castle = clanData.hasCastle()? "<img src=CustomIconPack02.clancastle width=40 height=32>" : "-";
			  
									  
			    clanListHtml = clanListHtml.replace("%placement%", clan_getPlacement(i + 1));
			    clanListHtml = clanListHtml.replace("%ally_crest%", ally_crest);
			    clanListHtml = clanListHtml.replace("%clan_crest%", clan_crest);
			    clanListHtml = clanListHtml.replace("%name%", clanData.getName());
			    clanListHtml = clanListHtml.replace("%leader%", clanData.getLeaderName());
			    clanListHtml = clanListHtml.replace("%level%", Integer.toString(clanData.getLevel()));
			    clanListHtml = clanListHtml.replace("%points%", Integer.toString(clanData.getReputationScore()));
			    clanListHtml = clanListHtml.replace("%castle%", _castle);
			    clanListHtml = clanListHtml.replace("%clan_hall%", clan_hall);
			    clanListHtml = clanListHtml.replace("%color%", "bgcolor=000000");

			    
	    	}
    		else
    		{
			    clanListHtml = "<img height=" + (i < 3 ? 42 : 38) +"><img src=l2ui.squaregray width=630 height=1>";
    		}
    		
    		sbClansListHtml.append(clanListHtml);
	    }
    
    	String content_clan = HtmCache.getInstance().getHtm("data/html/CommunityBoard/ranking/clan_ranklist.htm");
    	content_clan = content_clan.replaceAll("%topclan%", sbClansListHtml.toString());
    	
    	
    	//clean clans Html cache
    	_topClansHtml.setLength(0);
    	_topClansHtml.append(content_clan);
    }
    
    public void showTopClanRankWindow(Player player)
    { 
    	if (_nextUpdate_clan < System.currentTimeMillis())
    	{
    		//_nextUpdate_clan = System.currentTimeMillis() + (1000L); // for test purposes only one second
    		_nextUpdate_clan = System.currentTimeMillis() + (60000L);
    		updateClansFromDB();
    	}
    	
    	separateAndSend(_topClansHtml.toString(), player);
	    //separateAndSend(_pvp.toString(), player);
    }
    	
    
    public void showHome(Player player)
    { 
    	String content_home = HtmCache.getInstance().getHtm("data/html/CommunityBoard/ranking/home.htm");
	
    	separateAndSend(content_home, player);
    }
    
    
    protected String clan_getPlacement(int index)
       {
           switch (index)
           {
               case 1:
                   return "<img src=fButtons.RankingWnd_1st width=34 height=35>";
               case 2:
                   return "<img src=fButtons.RankingWnd_2nd width=34 height=35>";
               case 3:
                   return "<img src=fButtons.RankingWnd_3rd width=34 height=35>";
               case 4:
                   return "<img src=UpdUI1.num4 width=24 height=35>";   
               case 5:
                   return "<img src=UpdUI1.num5 width=24 height=35>";  
               case 6:
                   return "<img src=UpdUI1.num6 width=24 height=35>";
               case 7:
                   return "<img src=UpdUI1.num7 width=24 height=35>";
               case 8:
                   return "<img src=UpdUI1.num8 width=24 height=35>";
               case 9:
                   return "<img src=UpdUI1.num9 width=24 height=35>";
               case 10:
                   return "<img src=UpdUI1.num10 width=24 height=35>";

                
               default:
            	   return "<font color=FFFFFF>"+ index ;
           }
           
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
           sb.append("<td align=center><font color=B09878>--</font></td>");
           sb.append("<td  align=left><font color=B09878></font></td>");
           sb.append("<td  align=left><font color=B09878></font></td>");
           sb.append("<td  align=center><font color=B09878>---</font></td>");
           sb.append("<td  align=center><font color=B09878>---</font></td>");
           sb.append("<td  align=center><font color=B09878>-</font></td>");
           sb.append("<td  align=center><font color=B09878>-</font></td>");
           sb.append("<td  align=center><font color=B09878>-</font></td>");
           sb.append("<td  align=center><font color=B09878>-</font></td>");
           sb.append("</tr>");
       }
    
    public static TopClansBBSManager getInstance()
    {
        return SingletonHolder.INSTANCE;
    }
    
    private static class SingletonHolder
    {
        protected static final TopClansBBSManager INSTANCE = new TopClansBBSManager();
    }
    
}