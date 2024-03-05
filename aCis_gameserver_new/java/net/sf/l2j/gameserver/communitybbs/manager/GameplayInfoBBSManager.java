package net.sf.l2j.gameserver.communitybbs.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.stream.IntStream;
import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;

 
public class GameplayInfoBBSManager extends BaseBBSManager
{
  
    protected GameplayInfoBBSManager()
    {
    }
    
    @Override
       public void parseCmd(String command, Player player)
       {
           if(command.equals("_friend")  || command.equals("_friendlist_0_"))
        	   showInfo(player);
           else
               super.parseCmd(command, player);
       }
    
  
    
    public void showInfo(Player player)
    { 
    	String content_home = HtmCache.getInstance().getHtm("data/html/CommunityBoard/ranking/gameinfo.htm");
	
    	separateAndSend(content_home, player);
    }
    
  
    
    public static GameplayInfoBBSManager getInstance()
    {
        return SingletonHolder.INSTANCE;
    }
    
    private static class SingletonHolder
    {
        protected static final GameplayInfoBBSManager INSTANCE = new GameplayInfoBBSManager();
    }
    
}