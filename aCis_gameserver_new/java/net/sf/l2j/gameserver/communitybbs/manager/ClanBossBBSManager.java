package net.sf.l2j.gameserver.communitybbs.manager;



import net.sf.l2j.commons.lang.StringUtil;
import net.sf.l2j.gameserver.data.cache.HtmCache;
import net.sf.l2j.gameserver.data.manager.RaidBossManager;
import net.sf.l2j.gameserver.data.xml.NpcData;
import net.sf.l2j.gameserver.enums.BossStatus;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.spawn.BossSpawn;

 
public class ClanBossBBSManager extends BaseBBSManager
{
	final StringBuilder sb = new StringBuilder("");
	 int[] rbosses_id =
		{
			25325,//Barakiel
			62342,//High Dryad
			10013,//Earth Golem
			10012,//Albion
			60008,//Dryad
			62354,//Talos
			60005,//Magma Golem
			62348,//Ocnus
			10016,//Blazing Salamander
			62358,//Ismenian
			60034//Colchian
		};// desired ids to display as a list on bbs
	 
	  
    protected ClanBossBBSManager()
    {
    }
     
    @Override
       public void parseCmd(String command, Player player)
       {	    
    	   if (command.equals("_bbsmemo"))
        	   showBossWindow(player);
           else
               super.parseCmd(command, player);
       }
    
    public void showBossWindow(Player player)
    { 
    	for (int boss : rbosses_id) // if boss id exists on the desired list.
		{
			// final BossSpawn bs = RaidBossManager.getInstance().getBossSpawn(boss);
			final BossSpawn bs = RaidBossManager.getInstance().getBossSpawn(boss);
			
			if (bs == null)
			{
				System.out.println("npe error:" + boss);
				continue;
			}
			
			final BossStatus status = bs.getStatus();
			String boss_status_icon = status == BossStatus.ALIVE ? "<img src=\"CustomIconPack01.boss-alive\" width=20 height=20>" : "<img src=\"UpdUI.fishing_clockicon\" width=16 height=16>";
				
						
			final String name = NpcData.getInstance().getTemplate(boss).getName();
			// System.out.println("String value for ID: " + boss + "= " + name + ", time= " + time);
			
							
				StringUtil.append(sb, "<table  align=center width=690 height=48 border=0  >" + "<tr>"
				+ "<td align=center width=60>" +get_ClanBossIcon(boss)+ "</td>" 
				+ "<td width=90 align=center><font color=D4D477>" + name +"</font></td>" 
				+ "<td width=45 align=center><font color=F1C232>" + NpcData.getInstance().getTemplate(boss).getLevel() +"</font></td>"
				+"<td width=100>"+getLocation(boss)+"</font></td>"
				+"<td width=70>"+boss_status_icon +"</td>"
				+"</tr></table>");
			    
	    }   	  
    	String rb_content = HtmCache.getInstance().getHtm("data/html/CommunityBoard/ranking/clanrblist.htm");
    	rb_content = rb_content.replaceAll("%bbs_bosses%", sb.toString());
    	
    	separateAndSend(rb_content, player);
    	sb.setLength(0);
	    //separateAndSend(_pvp.toString(), player);
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
    
    protected String get_ClanBossIcon(int clanrb_id)
    {
        switch (clanrb_id)
        {
            case 25325:
         	  return  "<img src=CustomIconPack01.rb_barakiel width=56 height=51>";
            case 62342:
          	   return  "<img src=CustomIconPack01.rb_highdryad width=52 height=51>";
            case 10013:
         	   return  "<img src=CustomIconPack01.rb_highearthgolem width=52 height=51>";         
            case 10012:
          	   return  "<img src=CustomIconPack01.rb_albion width=60 height=51>";
            case 60008:
          	   return  "<img src=CustomIconPack01.rb_dryad width=52 height=51>";
            case 62354:
          	   return  "<img src=CustomIconPack01.rb_talos width=52 height=51>";
            case 60005:
          	   return  "<img src=CustomIconPack01.rb_magma-golem width=52 height=50>";
            case 62348:
          	   return  "<img src=CustomIconPack01.rb_ocnus width=55 height=50>";
            case 10016:
           	   return  "<img src=CustomIconPack01.rb_salamander width=52 height=50>";
            case 62358:
           	   return  "<img src=CustomIconPack01.rb_ismenian width=52 height=50>";
            case 60034:
           	   return  "<img src=CustomIconPack01.rb_colchian width=52 height=50>";
        }
      
        return "" ;
    }
    
    protected String getLocation(int boss_id)
    {
        switch (boss_id)
        {
            case 25325:
            case 62342:
            case 10013:
            case 10012:
            case 60008:
                return "<font color=B6D7A8>"+"Parnassus";
            case 60034:
            	return "<font color=B6D7A8>"+"Elysian Fields";
            case 62354:
                return "<font color=B6D7A8>"+"Asphodel Meadows";
            default:
            	return "<font color=B6D7A8>"+"Tartarus";
        }

    }
    public static ClanBossBBSManager getInstance()
    {
        return SingletonHolder.INSTANCE;
    }
    
    private static class SingletonHolder
    {
        protected static final ClanBossBBSManager INSTANCE = new ClanBossBBSManager();
    }
    
}