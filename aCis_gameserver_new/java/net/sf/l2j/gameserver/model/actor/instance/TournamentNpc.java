package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.entity.Tournament.TournamentManager;
import net.sf.l2j.gameserver.model.entity.Tournament.enums.TournamentFightType;

/**
 * @author Rouxy
 */
public class TournamentNpc extends Folk
{
	public TournamentNpc(final int objectId, final NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		TournamentManager.getInstance().showHtml(player, "main", TournamentFightType.NONE);
	}
	
	@Override
	public String getHtmlPath(final int npcId, final int val)
	{
		String pom = "";
		if (val == 0)
		{
			pom = "" + npcId;
		}
		else
		{
			pom = npcId + "-" + val;
		}
		return "data/html/mods/tournament/" + pom + ".htm";
	}
}
