package net.sf.l2j.gameserver.data.xml;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.l2j.commons.data.StatSet;
import net.sf.l2j.commons.data.xml.IXmlReader;

import org.w3c.dom.Document;

public class IconsData implements IXmlReader
{
	private final Map<Integer, String> _icons = new ConcurrentHashMap<>();
	
	public void reload()
	{
		_icons.clear();
		load();
	}
	
	@Override
	public void load()
	{
		parseFile("./data/xml/icons.xml");
		LOGGER.info("Loaded {} icons.", _icons.size());
	}
	
	@Override
	public void parseDocument(Document doc, Path path)
	{
		forEach(doc, "list", listNode -> forEach(listNode, "icon", iconNode ->
		{
			final StatSet set = parseAttributes(iconNode);
			_icons.put(set.getInteger("Id"), set.getString("value"));
		}));
	}
	
	public IconsData()
	{
	load();
	}
	
	public String getIcon(int id)
	{
		return _icons.getOrDefault(id, "icon.noimage");
	}
	
	public static IconsData getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final IconsData INSTANCE = new IconsData();
	}
}