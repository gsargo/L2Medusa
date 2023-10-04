package net.sf.l2j.gameserver.handler.admincommandhandlers;

import java.util.StringTokenizer;

import net.sf.l2j.gameserver.handler.IAdminCommandHandler;
import net.sf.l2j.gameserver.model.World;
import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.entity.instance.InstanceManager;

/**
 * @author Rouxy
 */
public class AdminInstance implements IAdminCommandHandler
{
	
	@Override
	public void useAdminCommand(String command, Player activeChar)
	{
		if (command.startsWith("admin_getinstance"))
		{
			StringTokenizer st = new StringTokenizer(command, " ");
			st.nextToken(); // skip command
			
			if (!st.hasMoreTokens())
			{
				activeChar.sendMessage("Write the name.");
				return;
			}
			
			String target_name = st.nextToken();
			Player player = World.getInstance().getPlayer(target_name);
			if (player == null)
			{
				activeChar.sendMessage("Player is offline");
				return;
			}
			
			activeChar.setInstance(player.getInstance(), false);
			activeChar.sendMessage("You are with the same instance of player " + target_name);
		}
		else if (command.startsWith("admin_resetmyinstance"))
		{
			activeChar.setInstance(InstanceManager.getInstance().getInstance(0), false);
			activeChar.sendMessage("Your instance is now default");
		}
		
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		
		return new String[]
		{
			"admin_getinstance",
			"admin_resetmyinstance"
		};
	}
	
}
