package com.Akoot.cthulhu.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CthFile extends File
{
	private static final long serialVersionUID = 1L;

	public CthFile(String fileName)
	{
		super(fileName);
	}

	public CthFile(File parent, String fileName)
	{
		super(parent, fileName);
	}

	private void trace(String msg, Object... format)
	{
		System.out.println(String.format(msg, format));
	}

	public void create()
	{
		try
		{
			this.createNewFile();
		} 
		catch (IOException e)
		{
			error(e);
		}
	}

	public String getString(String key)
	{
		String s = String.valueOf(get(key));
		return s;
	}

	public Boolean getBoolean(String key)
	{
		Boolean b = Boolean.valueOf(getString(key));
		return b;
	}

	public double getDouble(String key)
	{
		double d = Double.valueOf(getString(key));
		return d;
	}

	public int getInt(String key)
	{
		int i = Integer.valueOf(getString(key));
		if(Double.isNaN(i)) trace("key " + key + " is not a number");
		return i;
	}

	private void write(List<String> lines)
	{
		try
		{
			PrintWriter pw = new PrintWriter(this);
			for(String ln: lines)
			{
				pw.println(ln);
			}
			pw.close();
		}
		catch (FileNotFoundException e)
		{
			error(e);
		}
	}

	private List<String> read()
	{
		return read("");
	}

	private List<String> read(String exclude)
	{
		List<String> lines = new ArrayList<String>();
		try
		{
			Scanner in = new Scanner(this);
			while(in.hasNextLine())
			{
				String ln = in.nextLine();
				if(!ln.matches(exclude) || exclude.isEmpty())
				{
					lines.add(ln);
				}
			}
			in.close();
		}
		catch (FileNotFoundException e)
		{
			error(e);
		}
		return lines;
	}

	public void set(String key, Object data)
	{
		String line = key + ": ";
		if(data instanceof String)
		{
			String s = data.toString();
			line += "\"" + s + "\"";
		}
		else
		{
			line += data;
		}
		List<String> lines = read(key + ".*");

		lines.add(line);
		write(lines);
	}

	public boolean has(String key)
	{
		return get(key) != null;
	}

	public void addComment(String comment)
	{
		addLine("# " + comment);
	}
	public void setComment(String key, String comment)
	{
		String line = key + ": ";
		String data = String.valueOf(get(key));
		if(data instanceof String)
		{
			String s = data.toString();
			line += "\"" + s + "\"";
		}
		else
		{
			line += data;
		}
		line += " #" + comment;
		try 
		{
			List<String> lines = new ArrayList<String>();
			lines.add(line);
			Scanner in = new Scanner(this);
			while(in.hasNextLine())
			{
				String ln = in.nextLine();
				if(!ln.startsWith(key))
				{
					lines.add(ln);
				}
			}
			in.close();
			write(lines);
		} 
		catch (FileNotFoundException e)
		{
			error(e);
		}
	}

	public void copyFromFile(File file)
	{
		try 
		{
			List<String> lines = new ArrayList<String>();
			Scanner in = new Scanner(file);
			while(in.hasNextLine())
			{
				String ln = in.nextLine();
				lines.add(ln);
			}
			in.close();
			write(lines);
		} 
		catch (FileNotFoundException e)
		{
			error(e);
		}
	}

	public void addLine(String line)
	{
		if(!this.exists()) this.create();
		List<String> lines = new ArrayList<String>();
		lines = read();

		lines.add(line);
		write(lines);
	}

	public Object get(String key)
	{
		Object line = null;
		try 
		{
			Scanner in = new Scanner(this);
			while(in.hasNextLine())
			{
				String ln = in.nextLine();
				if(!ln.startsWith("#") && ln.contains(":"))
				{
					String obj = ln.substring(0, ln.indexOf(":"));
					if(obj.equalsIgnoreCase(key))
					{
						String temp = ln.substring(ln.indexOf(":") + 2, ln.length());
						if(temp.contains("\""))
						{
							line = ln.substring(ln.indexOf(":") + 3, ln.lastIndexOf("\""));
						}
						else
						{
							line = ln.substring(ln.indexOf(":") + 2, ln.length());
						}
					}
				}
			}
			in.close();
		}
		catch (FileNotFoundException e)
		{
			error(e);
		}
		return line;
	}

	public void remove(String key)
	{
		try
		{
			List<String> lines = new ArrayList<String>();
			Scanner in = new Scanner(this);

			lines = read(key + ".*");

			in.close();
			write(lines);
		} 
		catch (FileNotFoundException e)
		{
			error(e);
		}
	}

	private void error(Exception e)
	{
		System.out.println("Error: " + e.getMessage());
	}
}
