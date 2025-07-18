package net.sf.l2j.commons.lang;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sf.l2j.commons.logging.CLogger;

public final class StringUtil
{
	public static final String DIGITS = "0123456789";
	public static final String LOWER_CASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
	public static final String UPPER_CASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static final String LETTERS = LOWER_CASE_LETTERS + UPPER_CASE_LETTERS;
	public static final String LETTERS_AND_DIGITS = LETTERS + DIGITS;
	
	private static final CLogger LOGGER = new CLogger(StringUtil.class.getName());
	
	/**
	 * Checks each String passed as parameter. If at least one is empty or null, than return true.
	 * @param strings : The Strings to test.
	 * @return true if at least one String is empty or null.
	 */
	public static boolean isEmpty(String... strings)
	{
		for (String str : strings)
		{
			if (str == null || str.isEmpty())
				return true;
		}
		return false;
	}
	
	/**
	 * Appends objects to an existing StringBuilder.
	 * @param sb : the StringBuilder to edit.
	 * @param content : parameters to append.
	 */
	public static void append(StringBuilder sb, Object... content)
	{
		for (Object obj : content)
			sb.append((obj == null) ? null : obj.toString());
	}
	
	/**
	 * @param text : the String to check.
	 * @return true if the String contains only numbers, false otherwise.
	 */
	public static boolean isDigit(String text)
	{
		if (text == null)
			return false;
		
		return text.matches("[0-9]+");
	}
	
	/**
	 * @param text : the String to check.
	 * @return true if the String contains only numbers and letters, false otherwise.
	 */
	public static boolean isAlphaNumeric(String text)
	{
		if (text == null)
			return false;
		
		for (char chars : text.toCharArray())
		{
			if (!Character.isLetterOrDigit(chars))
				return false;
		}
		return true;
	}
	
	/**
	 * @param value : the number to format.
	 * @return a number formatted with "," delimiter.
	 */
	public static String formatNumber(long value)
	{
		return NumberFormat.getInstance(Locale.ENGLISH).format(value);
	}
	
	/**
	 * @param string : the initial word to scramble.
	 * @return an anagram of the given string.
	 */
	public static String scrambleString(String string)
	{
		final List<String> letters = Arrays.asList(string.split(""));
		Collections.shuffle(letters);
		
		final StringBuilder sb = new StringBuilder(string.length());
		for (String c : letters)
			sb.append(c);
		
		return sb.toString();
	}
	
	/**
	 * Verify if the given text matches with the regex pattern.
	 * @param text : the text to test.
	 * @param regex : the regex pattern to make test with.
	 * @return true if matching.
	 */
	public static boolean isValidString(String text, String regex)
	{
		Pattern pattern;
		try
		{
			pattern = Pattern.compile(regex);
		}
		catch (PatternSyntaxException e) // case of illegal pattern
		{
			pattern = Pattern.compile(".*");
		}
		
		Matcher regexp = pattern.matcher(text);
		
		return regexp.matches();
	}
	
	/**
	 * Format a given text to fit with logging "title" criterias, and send it.
	 * @param text : the String to format.
	 */
	public static void printSection(String text)
	{
		final StringBuilder sb = new StringBuilder(80);
		for (int i = 0; i < (73 - text.length()); i++)
			sb.append("-");
		
		StringUtil.append(sb, "=[ ", text, " ]");
		
		LOGGER.info(sb.toString());
	}
	
	/**
	 * Format a time given in seconds into "h m s" String format.
	 * @param time : a time given in seconds.
	 * @return a "h m s" formated String.
	 */
	public static String getTimeStamp(int time)
	{
		final int hours = time / 3600;
		time %= 3600;
		final int minutes = time / 60;
		time %= 60;
		
		String result = "";
		if (hours > 0)
			result += hours + "h";
		if (minutes > 0)
			result += " " + minutes + "m";
		if (time > 0 || result.length() == 0)
			result += " " + time + "s";
		
		return result;
	}
	
	/**
	 * Format a {@link String} to delete its extension ("castles.xml" > "castles"), if any.
	 * @param fileName : The String to edit, which is a former file name.
	 * @return a left-side truncated String to the first "." encountered.
	 */
	public static String getNameWithoutExtension(String fileName)
	{
		final int pos = fileName.lastIndexOf(".");
		if (pos > 0)
			fileName = fileName.substring(0, pos);
		
		return fileName;
	}
	
	/**
	 * Trim the {@link String} set as parameter to the amount of characters set as second parameter.
	 * @param s : The {@link String} to trim.
	 * @param maxWidth : The maximum length.
	 * @return The {@link String} trimmed to the good format.
	 */
	public static String trim(String s, int maxWidth)
	{
		return (s.length() > maxWidth) ? s.substring(0, maxWidth) : s;
	}
	
	/**
	 * Trim the {@link String} set as parameter to the amount of characters set as second parameter, or return {@link String} defaultValue if {@link String} is null or empty.
	 * @param s : The {@link String} to trim.
	 * @param maxWidth : The maximum length.
	 * @param defaultValue : The default {@link String} to return if {@link String} is null or empty.
	 * @return The {@link String} trimmed to the good format.
	 */
	public static String trim(String s, int maxWidth, String defaultValue)
	{
		if (s == null || s.isEmpty())
			return defaultValue;
		
		return trim(s, maxWidth);
	}
	
	// c1c0s votemanagerAPI
	
	public static String convertLongToCountdown(long timestamp)
	{
		long hours = TimeUnit.MILLISECONDS.toHours(timestamp);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(timestamp) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timestamp));
		
		return hours > 0 ? hours + " hour(s)" : minutes + " minutes(s)";
	}
	
	public static String convertLongToCountdownN(long youCanVote)
	{
		String formattedCountdown = String.format("%d hours, %d mins, %d secs", TimeUnit.MILLISECONDS.toHours(youCanVote), TimeUnit.MILLISECONDS.toMinutes(youCanVote) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(youCanVote)), TimeUnit.MILLISECONDS.toSeconds(youCanVote) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(youCanVote)));
		return formattedCountdown;
	}
}