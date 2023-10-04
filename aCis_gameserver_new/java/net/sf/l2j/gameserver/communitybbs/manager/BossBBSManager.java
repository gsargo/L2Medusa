package net.sf.l2j.gameserver.communitybbs.manager;



import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.manager.RaidBossManager;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.enums.BossStatus;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.spawn.BossSpawn;

 
public class BossBBSManager extends BaseBBSManager
{
	final StringBuilder rb_sb = new StringBuilder("");
	 int[] rbosses_id =
		{
			60010,
			60007,
			25019,
			25126,
			25163,
			60036,
			60050,
			62359// Laestrygon
		};// desired ids to display as a list on bbs
	 
	  
    protected BossBBSManager()
    {
    }
     
    @Override
       public void parseCmd(String command, Player player)
       {	    
    	   if (command.equals("_bbsclan"))
        	   showBossWindow(player);
           else
               super.parseCmd(command, player);
       }
    
    public void showBossWindow(Player player)
    { 
    	for (int boss : rbosses_id) // if boss id exists on the desired list.
		{
			// final BossSpawn bs = RaidBossManager.getInstance().getBossSpawn(boss);
			final BossSpawn top_bs = RaidBossManager.getInstance().getBossSpawn(boss);
			
			if (top_bs == null)
			{
				System.out.println("npe error:" + boss);
				continue;
			}
			
			final BossStatus status =top_bs.getStatus();
			String boss_status_icon = status == BossStatus.ALIVE ? "<img src=\"CustomIconPack01.boss-alive\" width=20 height=20>" : top_bs.getTimeLeft(); //show active icon or time left to respawn.
				
						
			final String name = NpcData.getInstance().getTemplate(boss).getName();
			// System.out.println("String value for ID: " + boss + "= " + name + ", time= " + time);
			
							
				StringUtil.append(rb_sb, "<table  align=center width=690 height=48 border=0  >" + "<tr>"
				+ "<td align=center width=60>" +get_BossIcon(boss) +"</td>" 
				+ "<td width=70 align=center><font color=F48787>" + name +"</font></td>" 
				+ "<td width=45 align=center><font color=D02A2A>" + NpcData.getInstance().getTemplate(boss).getLevel() +"</font></td>"
				+"<td width=100>"+getLocation(boss)+"</td>"
				+"<td width=70>"+boss_status_icon +"</td>"
				+"</tr></table>");
			    
	    }   	  
    	String rb_content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/ranking/rblist.htm");
    	rb_content = rb_content.replaceAll("%bbs_bosses%", rb_sb.toString());
    	
    	separateAndSend(rb_content, player);
    	rb_sb.setLength(0);
	    //separateAndSend(_pvp.toString(), player);
    }
    
    protected String get_BossIcon(int rb_id)
    {
        switch (rb_id)
        {
            case 60010:
         	  return  "<img src=CustomIconPack01.toprb_medusa width=50 height=51>";
            case 60007:
          	   return  "<img src=CustomIconPack01.toprb_zeus width=50 height=51>";
            case 25019:
         	   return  "<img src=CustomIconPack01.toprb_poseidon width=50 height=51>";         
            case 25126:
          	   return  "<img src=CustomIconPack01.toprb_minotaur width=53 height=51>";
            case 25163:
          	   return  "<img src=CustomIconPack01.toprb_typhon width=50 height=51>";
            case 60036:
          	   return  "<img src=CustomIconPack01.toprb_hades width=50 height=51>";
            case 60050:
          	   return  "<img src=CustomIconPack01.toprb_cerberus width=50 height=50>";
            case 62359:
          	   return  "<img src=CustomIconPack01.toprb_laestrygon width=50 height=50>";
        }
      
        return "" ;
    }
      
    protected String board_getColor(int boss_id)
       {
           switch (boss_id)
           {
               case 1:
                   return "<img src=CustomIconPack01.mini_wooden width=24 height=26>";
               case 2:
                   return "<img src=CustomIconPack01.mini_wooden width=24 height=26>";
               case 3:
                   return "<img src=CustomIconPack01.mini_wooden width=24 height=26>";
           }
           return " ";
       }
    
    protected String getLocation(int boss_id)
    {
        switch (boss_id)
        {
            case 60010:return "<font color=B6D7A8>"+"Medusa's Lair";
            case 60007:return "<font color=B6D7A8>"+"Olympus";
            case 25019:return "<font color=B6D7A8>"+"Underwater Kingdom";
            case 25126:return "<font color=B6D7A8>"+"Labyrinth of Minotaur";
            case 25163:return "<font color=B6D7A8>"+"Tartarus";
            case 60036:
            case 60050:return "<font color=B6D7A8>"+"Hades' Palace";
            case 62359:return "<font color=B6D7A8>"+"Parnasus";
            	
            default:
            	return "";
        }

    }
 
    
    public static BossBBSManager getInstance()
    {
        return SingletonHolder.INSTANCE;
    }
    
    private static class SingletonHolder
    {
        protected static final BossBBSManager INSTANCE = new BossBBSManager();
    }
    
}