package net.sf.l2j.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import net.sf.l2j.gameserver.data.SkillTable;
import net.sf.l2j.gameserver.model.actor.Npc;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.MagicSkillUse;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

/**
 * @author KDerD64
 */
public final class L2BufferInstance extends Npc
{
	public L2BufferInstance(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		StringTokenizer st = new StringTokenizer(command, " ");
		String actualCommand = st.nextToken();
		
		int buffid = 0;
		int bufflevel = 1;
		String nextWindow = null;
		if (st.countTokens() == 3)
		{
			buffid = Integer.valueOf(st.nextToken());
			bufflevel = Integer.valueOf(st.nextToken());
			nextWindow = st.nextToken();
		}
		else if (st.countTokens() == 1)
			buffid = Integer.valueOf(st.nextToken());
		
		if (actualCommand.equalsIgnoreCase("getbuff"))
		{
			if (buffid != 0)
			{
				MagicSkillUse mgc = new MagicSkillUse(this, player, buffid, bufflevel, -1, 0);
				
				SkillTable.getInstance().getInfo(buffid, bufflevel).getEffects(this, player);
				showMessageWindow(player);
				player.broadcastPacket(mgc);
				showChatWindow(player, nextWindow);
			}
		}
		
		else if (actualCommand.equalsIgnoreCase("restore"))
		{
			player.getStatus().setCpHpMp(player.getStatus().getMaxCp(), getStatus().getMaxHp(), player.getStatus().getMaxMp());
			showMessageWindow(player);
		}
		else if (actualCommand.equalsIgnoreCase("cancel"))
		{
			player.stopAllEffects();
			showMessageWindow(player);
		}
		else
			super.onBypassFeedback(player, command);
	}
	
	private void showMessageWindow(Player player)
	{
		String filename = "data/html/buffer/" + getNpcId() + ".htm";
		
		filename = getHtmlPath(getNpcId(), 0);
		NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", String.valueOf(getObjectId()));
		html.replace("%npcname%", getName());
		player.sendPacket(html);
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String pom = "";
		if (val == 0)
			pom = "" + npcId;
		else
			pom = npcId + "-" + val;
		
		return "data/html/buffer/" + pom + ".htm";
	}
}