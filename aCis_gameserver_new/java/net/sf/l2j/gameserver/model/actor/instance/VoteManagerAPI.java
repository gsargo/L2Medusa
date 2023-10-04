package net.sf.l2j.gameserver.model.actor.instance;

import net.sf.l2j.gameserver.model.actor.Player;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

import c1c0s.VoteManagerAPI.VoteSites;

public class VoteManagerAPI extends Folk
{
	public VoteManagerAPI(int objectId, NpcTemplate template)
	{
		super(objectId, template);
	}
	
	@Override
	public void onBypassFeedback(final Player player, String command)
	{
		if (player == null)
			return;
	}
	
	@Override
	public void showChatWindow(Player player)
	{
		NpcHtmlMessage html = new NpcHtmlMessage(0);
		final StringBuilder sb1 = new StringBuilder();
		final StringBuilder sb2 = new StringBuilder();
		final StringBuilder sb3 = new StringBuilder();
		final StringBuilder sb4 = new StringBuilder();
		final StringBuilder sb5 = new StringBuilder();
		final StringBuilder sb6 = new StringBuilder();
		final StringBuilder sb7 = new StringBuilder();
		final StringBuilder sb8 = new StringBuilder();
		final StringBuilder sb9 = new StringBuilder();
		final StringBuilder sb10 = new StringBuilder();
		final StringBuilder sb11 = new StringBuilder();
		final StringBuilder sb12 = new StringBuilder();
		final StringBuilder sb13 = new StringBuilder();
		final StringBuilder sb14 = new StringBuilder();
		final StringBuilder sb15 = new StringBuilder();
		
		if (player.isEligibleToVote(VoteSites.L2JBRASIL))
			sb1.append("<button value=\"L2jBrasil\" action=\"bypass -h voiced_VoteL2JBRASIL\" width=256 height=32 back=\"fButtons.UnSelected\" fore=\"fButtons.Left_Selected\">");
		else
			sb1.append(String.format("L2JBRASIL: %s <br1>", player.getVoteCountdown(VoteSites.L2JBRASIL)));
		
		if (player.isEligibleToVote(VoteSites.L2NETWORK))
			sb2.append("<button value=\"L2Network\" action=\"bypass -h voiced_VoteL2NETWORK\" width=256 height=32 back=\"fButtons.UnSelected\" fore=\"fButtons.Left_Selected\">");
		else
			sb2.append(String.format("L2NETWORK: %s <br1>", player.getVoteCountdown(VoteSites.L2NETWORK)));
		
		if (player.isEligibleToVote(VoteSites.L2TOPCO))
			sb3.append("<button value=\"Reward of L2TOPCO\" action=\"bypass -h voiced_VoteL2TOPCO\" width=256 height=32 back=\"fButtons.Left_UnSelected\" fore=\"fButtons.Left_Selected\">");
		else
			sb3.append(String.format("L2TOPCO: %s <br1>", player.getVoteCountdown(VoteSites.L2TOPCO)));
		
		if (player.isEligibleToVote(VoteSites.L2TOPZONE))
			sb4.append("<button value=\"L2Topzone\" action=\"bypass -h voiced_VoteL2TOPZONE\" width=256 height=32 back=\"fButtons.Left_UnSelected\" fore=\"fButtons.Left_Selected\">");
		else
			sb4.append(String.format("L2TOPZONE: %s <br1>", player.getVoteCountdown(VoteSites.L2TOPZONE)));
		
		if (player.isEligibleToVote(VoteSites.L2HOPZONE))
			sb5.append("<button value=\"L2Hopzone\" action=\"bypass -h voiced_VoteL2HOPZONE\" width=256 height=32 back=\"fButtons.Left_UnSelected\" fore=\"fButtons.Left_Selected\">");
		else
			sb5.append(String.format("L2HOPZONE: %s <br1>", player.getVoteCountdown(VoteSites.L2HOPZONE)));
		
		if (player.isEligibleToVote(VoteSites.L2TOPSERVERS))
			sb6.append("<button value=\"Reward of L2TOPSERVERS\" action=\"bypass -h voiced_VoteL2TOPSERVERS\" width=256 height=32 back=\"fButtons.Left_Unselected\" fore=\"fButtons.Left_Selected\">");
		else
			sb6.append(String.format("L2TOPSERVERS: %s <br1>", player.getVoteCountdown(VoteSites.L2TOPSERVERS)));
		
		if (player.isEligibleToVote(VoteSites.L2TOPGAMESERVER))
			sb7.append("<button value=\"Reward of L2TOPGAMESERVER\" action=\"bypass -h voiced_VoteL2TOPGAMESERVER\" width=256 height=32 back=\"fButtons.Left_Unselected\" fore=\"fButtons.Left_Selected\">");
		else
			sb7.append(String.format("L2TOPGAMESERVER: %s <br1>", player.getVoteCountdown(VoteSites.L2TOPGAMESERVER)));
		
		if (player.isEligibleToVote(VoteSites.L2VOTESCOM))
			sb8.append("<button value=\"Reward of L2VOTESCOM\" action=\"bypass -h voiced_VoteL2VOTESCOM\" width=256 height=32 back=\"server.btn_over\" fore=\"server.btn\">");
		else
			sb8.append(String.format("L2VOTESCOM: %s <br1>", player.getVoteCountdown(VoteSites.L2VOTESCOM)));
		
		if (player.isEligibleToVote(VoteSites.L2SERVERSCOM))
			sb9.append("<button value=\"Reward of L2SERVERSCOM\" action=\"bypass -h voiced_VoteL2SERVERSCOM\" width=256 height=32 back=\"server.btn_over\" fore=\"server.btn\">");
		else
			sb9.append(String.format("L2SERVERSCOM: %s <br1>", player.getVoteCountdown(VoteSites.L2SERVERSCOM)));
		
		if (player.isEligibleToVote(VoteSites.L2ITOPZ))
			sb10.append("<button value=\"Reward of L2ITOPZ\" action=\"bypass -h voiced_VoteL2ITOPZ\" width=256 height=32 back=\"fButtons.Left_Unselected\" fore=\"fButtons.Left_Selected\">");
		else
			sb10.append(String.format("L2ITOPZ: %s <br1>", player.getVoteCountdown(VoteSites.L2ITOPZ)));
		
		if (player.isEligibleToVote(VoteSites.L2JTOPCOM))
			sb11.append("<button value=\"Reward of L2JTOPCOM\" action=\"bypass -h voiced_VoteL2JTOPCOM\" width=256 height=32 back=\"server.btn_over\" fore=\"server.btn\">");
		else
			sb11.append(String.format("L2JTOPCOM: %s <br1>", player.getVoteCountdown(VoteSites.L2JTOPCOM)));
		
		if (player.isEligibleToVote(VoteSites.GAMETOPSEU))
			sb12.append("<button value=\"Reward of GAMETOPSEU\" action=\"bypass -h voiced_VoteGAMETOPSEU\" width=256 height=32 back=\"server.btn_over\" fore=\"server.btn\">");
		else
			sb12.append(String.format("GAMETOPSEU: %s <br1>", player.getVoteCountdown(VoteSites.GAMETOPSEU)));
		
		if (player.isEligibleToVote(VoteSites.TOP100ARENA))
			sb13.append("<button value=\"Reward of TOP100ARENA\" action=\"bypass -h voiced_VoteTOP100ARENA\" width=256 height=32 back=\"fButtons.Left_Unselected\" fore=\"fButtons.Selected\">");
		else
			sb13.append(String.format("TOP100ARENA: %s <br1>", player.getVoteCountdown(VoteSites.TOP100ARENA)));
		
		if (player.isEligibleToVote(VoteSites.L2NET))
			sb14.append("<button value=\"Reward of L2NET\" action=\"bypass -h voiced_VoteL2NET\" width=256 height=32 back=\"fButtons.Left_Unselected\" fore=\"fButtons.Left_Selected\">");
		else
			sb14.append(String.format("L2NET: %s <br1>", player.getVoteCountdown(VoteSites.L2NET)));
		
		if (player.isEligibleToVote(VoteSites.L2TOPEU))
			sb15.append("<button value=\"Reward of L2TOPEU\" action=\"bypass -h voiced_VoteL2TOPEU\" width=256 height=32 back=\"fButtons.Left_Unselected\" fore=\"fButtons.Left_Selected\">");
		else
			sb15.append(String.format("L2TOPEU: %s <br1>", player.getVoteCountdown(VoteSites.L2TOPEU)));
		
		html.setFile(getHtmlPath(getNpcId(), 0));
		html.replace("%VoteL2JBRASIL%", sb1.toString());
		html.replace("%VoteL2NETWORK%", sb2.toString());
		html.replace("%VoteL2TOPCO%", sb3.toString());
		html.replace("%VoteL2TOPZONE%", sb4.toString());
		html.replace("%VoteL2HOPZONE%", sb5.toString());
		html.replace("%VoteL2TOPSERVERS%", sb6.toString());
		html.replace("%VoteL2TOPGAMESERVER%", sb7.toString());
		html.replace("%VoteL2VOTESCOM%", sb8.toString());
		html.replace("%VoteL2SERVERSCOM%", sb9.toString());
		html.replace("%VoteL2ITOPZ%", sb10.toString());
		html.replace("%VoteL2JTOPCOM%", sb11.toString());
		html.replace("%VoteGAMETOPSEU%", sb12.toString());
		html.replace("%VoteTOP100ARENA%", sb13.toString());
		html.replace("%VoteL2NET%", sb14.toString());
		html.replace("%VoteL2TOPEU%", sb15.toString());
		player.sendPacket(html);
	}
	
	@Override
	public String getHtmlPath(int npcId, int val)
	{
		return "data/html/c1c0s/votemanager/votemanager.htm";
	}
}