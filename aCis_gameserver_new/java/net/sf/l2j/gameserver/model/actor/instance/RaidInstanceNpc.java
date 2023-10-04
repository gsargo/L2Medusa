package net.sf.l2j.gameserver.model.actor.instance;

import java.util.StringTokenizer;

import net.sf.l2j.gameserver.instance.AbstractInstance;
import net.sf.l2j.gameserver.instance.InstanceFactory;
import net.sf.l2j.gameserver.instance.InstanceType;
import net.sf.l2j.gameserver.instance.actor.PlayerAdapter;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.ActionFailed;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class RaidInstanceNpc extends Folk
{
	private static final InstanceFactory INSTANCE_FACTORY = new InstanceFactory();
	
	public RaidInstanceNpc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		final StringTokenizer tokenizer = new StringTokenizer(command, " ");
		final String parameter = tokenizer.nextToken().toLowerCase();
		
		switch (parameter)
		{
			case "create" ->
			{
				final InstanceType instanceType = Enum.valueOf(InstanceType.class, tokenizer.nextToken());
				handleCreateRequest(player, instanceType);
			}
		}
	}
	
	private final void handleCreateRequest(Player player, InstanceType instanceType)
	{
		if (!canJoin(player, instanceType))
			return;
		
		player.setInstanceJoins(instanceType, player.getInstanceJoins(instanceType) + 1);
		final AbstractInstance instance = INSTANCE_FACTORY.createNewInstance(instanceType);
		player.setGameInstance(instance);
		instance.getPlayerGroup().add(new PlayerAdapter(player));
		instance.start();
	}
	
	private final boolean canJoin(Player player, InstanceType instanceType)
	{
		if (player.getInstanceJoins(instanceType) >= getMaximumInstanceJoins(player))
		{
			player.sendMessage("You have exceeded your daily limit.");
			return false;
		}
		
		return true;
	}
	
	private final int getMaximumInstanceJoins(Player player)
	{
		if (player.isPremium())
			return 3;
		
		return 1;
	}
	
	@Override
	public void showChatWindow(Player player, String filename)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		html.replace("%joinsLeft%", getMaximumInstanceJoins(player) - player.getInstanceJoins(InstanceType.RAID));
		player.sendPacket(html);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		String filename = "";
		if (val == 0)
			filename = "" + npcId;
		else
			filename = npcId + "-" + val;
		
		return "data/html/mods/instance/" + filename + ".htm";
	}
}