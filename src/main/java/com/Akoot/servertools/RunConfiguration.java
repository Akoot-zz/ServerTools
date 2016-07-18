package com.Akoot.servertools;

import java.io.File;

import com.Akoot.util.StringUtil;

public class RunConfiguration
{
	public File serversDir;
	public String name;
	public String minecraftVersion;
	public String serverType;
	public int minRam, maxRam;
	public boolean onlineMode;
	public String ip;
	public int port;
	public int maxPlayers;
	public String worldsDir;
	public String pluginsDir;
	public String worldName;
	public boolean start;

	public RunConfiguration(String[] args)
	{
		this.serversDir = new File("servers");
		this.name = "Minecraft Server";
		this.minecraftVersion = "1.10.2";
		this.serverType = "VANILLA";
		this.minRam = 1024;
		this.maxRam = 1024;
		this.onlineMode = true;
		this.ip = "localhost";
		this.port = 25565;
		this.maxPlayers = 20;
		this.worldName = "world";
		this.start = false;

		for(int i = 0; i < args.length; i++)
		{
			if(args[i].equalsIgnoreCase("-cracked")) onlineMode = false;
			if(args[i].equalsIgnoreCase("-start")) start = true;
			if(i + 1 < args.length)
			{
				if(args[i].equalsIgnoreCase("-dir")) serversDir = new File(StringUtil.getArgFor("-dir", args));
				if(args[i].equalsIgnoreCase("-name")) name = StringUtil.getArgFor("-name", args);
				if(args[i].equalsIgnoreCase("-ip")) ip = args[i + 1];
				if(args[i].equalsIgnoreCase("-port")) port = Integer.parseInt(args[i + 1]);
				if(args[i].equalsIgnoreCase("-max-players")) maxPlayers = Integer.parseInt(args[i + 1]);
				if(args[i].equalsIgnoreCase("-plugins-folder")) pluginsDir = StringUtil.getArgFor("-plugins-folder", args);
				if(args[i].equalsIgnoreCase("-worlds-folder")) worldsDir = StringUtil.getArgFor("-worlds-folder", args);
				if(args[i].equalsIgnoreCase("-world")) worldName = StringUtil.getArgFor("-world", args);
				if(args[i].equalsIgnoreCase("-version")) minecraftVersion = args[i + 1];
				if(args[i].equalsIgnoreCase("-type")) serverType = args[i + 1];
				if(args[i].equalsIgnoreCase("-min-ram")) minRam = Integer.parseInt(args[i + 1]);
				if(args[i].equalsIgnoreCase("-max-ram")) maxRam = Integer.parseInt(args[i + 1]);
			}
		}
	}

	public ServerType getServerType()
	{
		return ServerType.valueOf(serverType.toUpperCase());
	}

	public String serverFileName()
	{
		return getServerType().value + "-" + minecraftVersion + ".jar";
	}
}
