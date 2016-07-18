package com.Akoot.util;

import java.util.Arrays;
import java.util.List;

public class StringUtil
{
	public static String toListString(List<String> args)
	{
		String msg = "";
		for(String s: args) msg += s + "\n";
		msg = msg.trim();
		return msg;
	}

	public static String toString(Object[] args)
	{
		String msg = "";
		for(Object s: args) msg += s + " ";
		msg = msg.trim();
		return msg;
	}

	public static String toPlural(int amt, String msg)
	{
		return msg + (amt == 1 ? "" : "s");
	}

	public static String toString(List<String> args)
	{
		String msg = "";
		for(String s: args) msg += s + " ";
		msg = msg.trim();
		return msg;
	}

	public static String[] replace(String[] a, int index, String replace)
	{
		String[] arr = new String[a.length];
		for(int i = 0; i < a.length; i++)
		{
			if(i == index) arr[i] = replace;
			else arr[i] = a[i];
		}
		return arr;
	}

	public static String getArgFor(String arg, String[] args)
	{
		for(int i = 0; i < args.length; i++)
		{
			String result = args[i];
			if(i + 1 < args.length)
			{
				if(args[i].equalsIgnoreCase(arg))
				{
					result = args[i + 1];
					if(result.startsWith("\""))
					{
						int k = i + 2;
						for(int j = i + 2; j < args.length; j++)
						{
							if(args[j].endsWith("\""))
							{
								k = j;
								break;
							}
						}
						result = toString(substr(args, i + 1, k + 1));
					}
					return result.replaceAll("\"", "");
				}
			}
		}
		return "";
	}

	public static boolean hasArgFor(String arg, String[] args)
	{
		for(int i = 0; i < args.length; i++)
		{
			if(i + 1 < args.length)
			{
				if(args[i].equalsIgnoreCase(arg)) return true;
			}
		}
		return false;
	}

	public static boolean contains(String arg, String[] args)
	{
		for(int i = 0; i < args.length; i++) if(args[i].equalsIgnoreCase(arg)) return true;
		return false;
	}

	public static String[] removeFirst(String[] a)
	{
		return Arrays.copyOfRange(a, 1, a.length);
	}

	public static String[] substr(String[] a, int startIndex, int endIndex)
	{
		return Arrays.copyOfRange(a, startIndex, endIndex);
	}

	public static String[] substr(String[] a, int startIndex)
	{
		return Arrays.copyOfRange(a, startIndex, a.length);
	}
}
