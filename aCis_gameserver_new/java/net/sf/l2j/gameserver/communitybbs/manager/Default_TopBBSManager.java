package net.sf.l2j.gameserver.communitybbs.manager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringTokenizer;

import net.sf.l2j.commons.pool.ConnectionPool;
import net.sf.l2j.commons.pool.ThreadPool;

import net.sf.l2j.gameserver.model.actor.Player;

public class Default_TopBBSManager extends BaseBBSManager
{
    protected static StringBuilder _pvpHTML = null;
    protected static StringBuilder _pkHTML = null;
 	private static final String LOAD_PVP_HTML = "SELECT * FROM characters ORDER BY pvpkills DESC LIMIT 15";
    private static final String LOAD_PK_HTML = "SELECT * FROM characters ORDER BY pkkills DESC LIMIT 15";
	
	protected Default_TopBBSManager()
	{
		loadHTML(0);
	}
	@SuppressWarnings("resource")
	public void loadHTML(int whichone) {
         StringBuilder HTML = new StringBuilder();

        String info = null;
        String secondInfo = null;
        switch (whichone) {
            default:
                info = "Top PvPers of the server";
                secondInfo = "PvP Ranking";
                break;
            case 1:
                info = "Most famous people of the server";
                secondInfo = "Fame Ranking";
                break;
            case 2:
                info = "The most \"hardcore\" players";
                secondInfo = "PK Ranking";
                break;
        }

        HTML.append("<html>\r\n<title>Global Ranking - " + info + "</title>" + "<body>\r\n" + "<table cellspacing=0 cellpadding=0>\r\n" + "<tr>\r\n" + "<td><img src=\"texture_pack.btns.bg_bg2\" width=905 height=525></td>\r\n" + "</tr>\r\n" 
        + "</table>\r\n" + "<table>\r\n" + "<tr>\r\n" + "<td width=680></td>\r\n" + "<td>\r\n" + "<br><br>\r\n" + "<table cellspacing=-165 cellpadding=-355>\r\n" + "<tr>\r\n" + "<td>\r\n" 
        	+ "<table >\r\n" + "<tr>\r\n" + "<td align=center width=200>" 
        + "<table border=0 cellpadding=0 cellspacing=0 width=769 height=50 background=\"l2ui_ct1.Windows_DF_TooltipBG\">" + "<tr>" + "<td valign=\"top\" align=\"center\" valign=top>" 
        	+ "<table width=755 height=50>" + "	<tr>" + "<td width=755><br>" 
        + "<table border=0 cellspacing=0 cellpadding=0 width=755 height=20>" + "<tr>" + "<td WIDTH=375 align=center valign=top>" + "<font name=hs12 color=AFACEC>" + secondInfo + "</font><br>" + "	</td>" + "</tr>" + "</table>" 
        	+ "<table width=760 cellspacing=0 cellpadding=0 height=50 border=0>" + "<tr>" + "<td align=center>" + "	<table width=740 border=0>" + "<tr>" + "<td width=740 align=center>" 
        + "<table cellspacing=3 cellpadding=4 width=740 height=40>" + "<tr>" + "<td FIXWIDTH=40 align=right valign=top>" 
        	+ "<table border=0 cellspacing=0 cellpadding=0 width=32 height=32 background=\"icon.skill0817\">" + "<tr>" + "<td width=32 align=center valign=top>" + "<img src=\"L2UI_CH3.Minimap.cursedmapicon02\" width=\"56\" height=\"56\">" + "</td>" + "</tr>" + "</table>"
        + "<table cellspacing=3 cellpadding=4 width=740 height=40><td><tr>" + "<td FIXWIDTH=500 align=center valign=top>" + "<br1>" + "<br1> <font name=CreditTextNormal>Compete against other players and make your way to the top!</font></td>" +  "<td WIDTH=40 align=right valign=top></td></tr></table>" 
        	+ "<table border=0 cellspacing=0 cellpadding=0 width=36 height=32 background=\"icon.skill0817\">" + "<tr>" + "<td width=32 align=center valign=top>" + "<img src=\"L2UI_CH3.Minimap.cursedmapicon02\" width=\"56\" height=\"56\">" + "</td>" + "</tr>" + "</table><br><br><br>");

        HTML.append("<table>"); // navigation tab
        HTML.append("<tr>");
        HTML.append("<td>");
        HTML.append("<button value=\"Top Fame\", action=\"bypass _bbsfame\" width=140 height=23 back=\"l2ui_ct1.Tab_DF_Tab_Selected\" fore=\"l2ui_ct1.Tab_DF_Tab_Unselected\">");
        HTML.append("</td>");
        HTML.append("<td>");
        HTML.append("<button value=\"Top PvP\", action=\"bypass _bbspvp\" width=140 height=23 back=\"l2ui_ct1.Tab_DF_Tab_Selected\" fore=\"l2ui_ct1.Tab_DF_Tab_Unselected\">");
        HTML.append("</td>");
        HTML.append("<td>");
        HTML.append("<button value=\"Top PK\", action=\"bypass _bbspk\" width=140 height=23 back=\"l2ui_ct1.Tab_DF_Tab_Selected\" fore=\"l2ui_ct1.Tab_DF_Tab_Unselected\">");
        HTML.append("</td>");
        HTML.append("</tr>");
        HTML.append("</table>"); // navigation tab

        HTML.append("<table width=740 cellspacing=0 cellpadding=0>");
        HTML.append("<tr>");
        HTML.append("<td width=740 align=center>");
        HTML.append("</td>");
        HTML.append("</tr>");
        HTML.append("</table>");
        HTML.append("<table width=740><tr><td width=740 align=center>");
        HTML.append("<table width=740 height=25 bgcolor=000000><tr><td align=center>");
        HTML.append("<table><tr>");
        HTML.append("<td width=50 valign=top>Rank</td>");
        HTML.append("<td width=150 align=center valign=top>Name</td>");
        HTML.append("<td width=150 align=center valign=top>Clan</td>");
        HTML.append("<td width=190 align=center valign=top>Class</td>");
        HTML.append("<td width=70 align=center valign=top>Status</td>");
        HTML.append("<td width=100 align=center valign=top>Count</td>");
        HTML.append("</tr></table>");
        HTML.append("</td></tr></table>");
        HTML.append("</td>");
        HTML.append("</tr>");
        HTML.append("</table>");
        HTML.append("</tr>");


        PreparedStatement statement;
        ResultSet rs;
        Connection con = null;
        try {
            con = ConnectionPool.getConnection();

            switch (whichone) {
                default:
                    statement = con.prepareStatement(LOAD_PVP_HTML);
                    break;
                case 2:
                    statement = con.prepareStatement(LOAD_PK_HTML);
                    break;
            }

            rs = statement.executeQuery();

            int ladder = 0;

            while (rs.next()) {
                ladder++;

                String name = rs.getString("char_name");
                String pvps = String.valueOf(rs.getInt("pvpkills"));
                String pks = String.valueOf(rs.getInt("pkkills"));
                //String baseclass = CharTemplateTable.getInstance().getClassNameById(rs.getInt("base_class"));

                if (rs.getBoolean("online"))
                    name = "<a action=\"bypass _bbsloc;playerinfo;" + name + "\">" + name + "</a>";

                HTML.append("<table width=740>");
                HTML.append("<tr>");
                HTML.append("<td width=740 align=center>");
                if (ladder == 1)
                	 HTML.append("<table width=740 height=25 bgcolor=333606>");
                else if (ladder == 2)
               	 HTML.append("<table width=740 height=25 bgcolor=757373>");
                else if (ladder == 3)
               	 HTML.append("<table width=740 height=25 bgcolor=523c3c>");
                else
                	HTML.append("<table width=740 height=25 bgcolor=0e0d0d>");
                HTML.append("<tr>");
                HTML.append("<td align=center>");
                HTML.append("<table>");
                HTML.append("<tr>");
                while (ladder < 50)
                {
                    if (ladder == 1)
                        HTML.append("<td width=50 valign=top><font color=ffff00>" + ladder + "</font></td>");
                    else if (ladder == 2)
                        HTML.append("<td width=50 valign=top><font color=ada5a5>" + ladder + "</font></td>");
                    else if (ladder == 3)
                        HTML.append("<td width=50 valign=top><font color=8f5454>" + ladder + "</font></td>");
                    else
                        HTML.append("<td width=50 valign=top><font color=bbbbbb>" + ladder + "</font></td>");
                    break;
                }
                HTML.append("<td width=150 align=center valign=top><font color=bbbbbb>" + name + "</font></td>");
                if (rs.getBoolean("online")) {
                    HTML.append("<td width=70 align=center valign=top><font color=669755>Online</font></td>");
                } else {
                    HTML.append("<td width=70 align=center valign=top><font color=DC143C>Offline</font></td>");
                }
                if (whichone == 0)
                    HTML.append("<td width=100  align=center valign=top><font color=bbbbbb>" + pvps + "</font></td>");
                if (whichone == 2)
                    HTML.append("<td width=100  align=center valign=top><font color=bbbbbb>" + pks + "</font></td>");
                HTML.append("</tr>");
                HTML.append("</table></td></tr></table>");
                HTML.append("</td>");
                HTML.append("</tr>");
            }

            rs.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try
            {
            	if (con != null)
            		con.close();
            }
            catch (Exception e)
            {
            }

            HTML.append("</table></center><br></body></html>");
        }

        switch (whichone) {
            default:
                _pvpHTML = HTML;
                break;
            case 2:
                _pkHTML = HTML;
                break;
        }
    }
	
	@Override
	public void parseCmd(String command, Player player)
	{
		if (command.startsWith("_bbspvp")) 
	    {
            String html = _pvpHTML.toString();
            html = html.replace("%PvP%", "PvP");
            html = html.replace("%Fame%", "<a action=\"bypass _bbsfame\">Fame</a>");
            html = html.replace("%PK%", "<a action=\"bypass _bbspk\">PK</a>");

            separateAndSend(html, player);
	    }
	    else if (command.startsWith("_bbspk")) 
        {
            String html = _pkHTML.toString();
            html = html.replace("%PvP%", "<a action=\"bypass _bbspvp\">PvP</a>");
            html = html.replace("%Fame%", "<a action=\"bypass _bbsfame\">Fame</a>");
            html = html.replace("%PK%", "PK");

            separateAndSend(html, player);
        } 
		if (command.equals("_bbshome"))
			loadStaticHtm("index.htm", player);
		else if (command.startsWith("_bbshome;"))
		{
			final StringTokenizer st = new StringTokenizer(command, ";");
			st.nextToken();
			
			loadStaticHtm(st.nextToken(), player);
		}
		else
			super.parseCmd(command, player);
	}
	
	@Override
	protected String getFolder()
	{
		return "top/";
	}
	
	public static Default_TopBBSManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final Default_TopBBSManager INSTANCE = new Default_TopBBSManager();
	}
}