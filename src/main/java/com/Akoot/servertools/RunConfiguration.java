package com.Akoot.servertools;

public class RunConfiguration
{
	private String name;
	private String minecraftVersion;
	private String serverType;
	private int minRam, maxRam;
	private boolean start;

	public RunConfiguration(String[] args)
	{
		this.name = "Minecraft Server";
		this.minecraftVersion = "1.10.2";
		this.serverType = "VANILLA";
		this.minRam = 1024;
		this.maxRam = 1024;

		for(int i = 0; i < args.length; i++)
		{
			if(args[i].equalsIgnoreCase("-start")) start= true;
			if(i + 1 < args.length)
			{
				if(args[i].equalsIgnoreCase("-name")) name = args[i + 1];
				if(args[i].equalsIgnoreCase("-version")) minecraftVersion = args[i + 1];
				if(args[i].equalsIgnoreCase("-type")) serverType = args[i + 1];
				if(args[i].equalsIgnoreCase("-minRam")) minRam = Integer.parseInt(args[i + 1]);
				if(args[i].equalsIgnoreCase("-maxRam")) maxRam = Integer.parseInt(args[i + 1]);
			}
		}
	}

	public String getMinecraftVersion()
	{
		return minecraftVersion;
	}

	public String getName()
	{
		return name;
	}

	public ServerType getServerType()
	{
		return ServerType.valueOf(serverType.toUpperCase());
	}

	public int getMinRam()
	{
		return minRam;
	}

	public int getMaxRam()
	{
		return maxRam;
	}
	
	public boolean startServer()
	{
		return start;
	}
	
	public String serverFileName()
	{
		return getServerType().value + "-" + minecraftVersion + ".jar";
	}
}
