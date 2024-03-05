package net.sf.l2j.gameserver.communitybbs.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.IntStream;
import net.sf.l2j.commons.pool.ConnectionPool;

import net.sf.l2j.Config;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;

 
public class HomeBBSManager extends BaseBBSManager
{

    private List<String> _news;
    
    protected HomeBBSManager()
    {
    	_news = new ArrayList<>();
    	_news.add(Config.News_1);
    	_news.add(Config.News_2);
    	_news.add(Config.News_3);
    	_news.add(Config.News_4);
    	_news.add(Config.News_5);
    	_news.add(Config.News_6);
    	_news.add(Config.News_7);
    	_news.add(Config.News_8);
    	_news.add(Config.News_9);
    	_news.add(Config.News_10);
    	_news.add(Config.News_11);
    	_news.add(Config.News_12);
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
           if(command.equals("_bbshome"))
        	   showHome(player, 0);
           else if (command.startsWith("_bbshome"))
           {
	   			final StringTokenizer st = new StringTokenizer(command, ";");
	   			st.nextToken();
	   			
	   			final String action = st.nextToken();
	        	switch (action)
	        	{
	        		case "news_page":
	        		{
	        			final String strPage = st.nextToken();
	        			int newsPage = 0;
	        			if (strPage != null && !strPage.isBlank())
	        			{
	        				newsPage = Integer.parseInt(strPage);
	        			}
	        			showHome(player, newsPage);
	        		}
	        		break;
	        	}
           }
           else
               super.parseCmd(command, player);
       }
    
  
    public void showHome(Player player, int newsPage)
    { 
    	String content_home = HtmCache.getInstance().getHtm("data/html/CommunityBoard/ranking/home_news.htm");

    	if (newsPage < 0)
    		newsPage = 0;
    	
    	if (newsPage >= _news.size())
    		newsPage = _news.size() - 1;
    	
    	
    	content_home = content_home.replace("%prev_page%", Integer.toString(newsPage - 1));
    	content_home = content_home.replace("%next_page%", Integer.toString(newsPage + 1));
    	content_home = content_home.replace("%news_msg%", _news.get(newsPage));
    	
    		//

    	separateAndSend(content_home, player);
    }
    
    protected String board_getColor(int index)
       {
           switch (index)
           {
               case 1:
                   return "<font color=FFFF00>" +String.format("%02d", index);
               case 2:
                   return "<font color=CCC1C1>" +String.format("%02d", index);
               case 3:
                   return "<font color=E9967A>" +String.format("%02d", index);
           }
           return "<font color=FFFFFF>"+String.format("%02d", index) ;
       }
    
    public String getRank_miniicons(int pvps)
	{
		if(pvps>=0 && pvps <500)
			return 
				"<img src=\"CustomIconPack01.mini_wooden\" width=24 height=26>";
		else if(pvps>=500 && pvps <1500)
			return 
				"<img src=\"CustomIconPack01.mini_iron\" width=24 height=26>";
		else if(pvps>=1500 && pvps <2500)
			return 
				 "<img src=\"CustomIconPack01.mini_bronze\" width=24 height=26>";
		else if(pvps>=2500 && pvps <3500)
			return 
				 "<img src=\"CustomIconPack01.mini_silver\" width=24 height=26>";			
		else if(pvps>=3500 && pvps <4500)
			return 
				"<img src=\"CustomIconPack01.mini_gold\" width=24 height=26";
		else if(pvps>=4500 && pvps <10000)
			return 
				 "<img src=\"CustomIconPack01.mini_platinum\" width=24 height=26>";
		else if(pvps>=10000 && pvps<20000)
			return 
				"<img src=\"CustomIconPack01.mini_diamond\" width=24 height=26>";
		
			return 
			"<img src=\"CustomIconPack01.mini_legendary\" width=24 height=26>";
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
    
    public static HomeBBSManager getInstance()
    {
        return SingletonHolder.INSTANCE;
    }
    
    private static class SingletonHolder
    {
        protected static final HomeBBSManager INSTANCE = new HomeBBSManager();
    }
    
}