package net.sf.l2j.gameserver.model.entity.Tournament.Data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.data.xml.IXmlReader;
import net.sf.l2j.commons.random.Rnd;

import net.sf.l2j.gameserver.model.entity.Tournament.enums.TournamentFightType;
import net.sf.l2j.gameserver.model.entity.Tournament.model.TournamentArena;
import net.sf.l2j.gameserver.model.location.Location;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

public class TournamentArenaParser implements IXmlReader
{
	
	private List<TournamentArena> _arenas = new ArrayList<>();
	// private int _lastDynamicId = 0;
	
	public TournamentArenaParser()
	{
		load();
	}
	
	public void reload()
	{
		// Reset dynamic id.
		// _lastDynamicId = 0;
		_arenas.clear();
		load();
	}
	
	public static TournamentArenaParser getInstance()
	{
		return SingleTonHolder._instance;
	}
	
	private static class SingleTonHolder
	{
		protected static TournamentArenaParser _instance = new TournamentArenaParser();
	}
	
	@Override
	public void load()
	{
		parseFile("./data/xml/TournamentData/Arenas.xml");
		LOGGER.info("Loaded {} Tournament Arenas.", _arenas.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		
		// StatsSet used to feed informations. Cleaned on every entry.
		final StatSet set = new StatSet();
		forEach(doc, "list", listNode -> forEach(listNode, "arena", arenaNode ->
		{
			
			final NamedNodeMap attrs = arenaNode.getAttributes();
			// final Node attribute = attrs.getNamedItem("id");
			// final int arenaId = attribute == null ? _lastDynamicId++ : Integer.parseInt(attribute.getNodeValue());
			
			// parse and feed attributes
			parseAndFeed(attrs, set);
			
			final List<Location> teamOneLocs = new ArrayList<>();
			forEach(arenaNode, "team_one_loc", nodeNode ->
			{
				final NamedNodeMap nodeAttrs = nodeNode.getAttributes();
				teamOneLocs.add(new Location(parseInteger(nodeAttrs, "x"), parseInteger(nodeAttrs, "y"), parseInteger(nodeAttrs, "z")));
			});
			
			final List<Location> teamTwoLocs = new ArrayList<>();
			forEach(arenaNode, "team_two_loc", nodeNode ->
			{
				final NamedNodeMap nodeAttrs = nodeNode.getAttributes();
				teamTwoLocs.add(new Location(parseInteger(nodeAttrs, "x"), parseInteger(nodeAttrs, "y"), parseInteger(nodeAttrs, "z")));
			});
			TournamentArena arena = new TournamentArena(set, teamOneLocs, teamTwoLocs);
			_arenas.add(arena);
			
		}));
		
	}
	
	public List<TournamentArena> getArenasForType(TournamentFightType type)
	{
		List<TournamentArena> list = new ArrayList<>();
		for (TournamentArena arena : _arenas)
		{
			if (arena.getTypes().contains(type))
			{
				list.add(arena);
			}
		}
		return list;
	}
	
	public TournamentArena getRandomArenaForType(TournamentFightType type)
	{
		return getArenasForType(type).get(Rnd.get(getArenasForType(type).size()));
	}
	
	public TournamentArena getRandomArena()
	{
		return _arenas.get(Rnd.get(_arenas.size()));
	}
	
	public TournamentArena getArena(int id)
	{
		return _arenas.get(id);
	}
	
	/**
	 * @return the arenas
	 */
	public List<TournamentArena> getArenas()
	{
		return _arenas;
	}
	
	/**
	 * @param arenas the arenas to set
	 */
	public void setArenas(List<TournamentArena> arenas)
	{
		this._arenas = arenas;
	}
	
}
