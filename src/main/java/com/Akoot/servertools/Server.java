package com.Akoot.servertools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.Akoot.cthulhu.util.CthFile;

public class Server
{
	private List<Plugin> plugins;
	private String displayname;
	private String minecraftVersion;
	private ServerType type;
	private File serverFolder, worldsFolder, pluginsFolder;
	private CthFile config;
	private File serverJar;
	private CthFile serverLauncher;
	private int minRam, maxRam;

	public Server(RunConfiguration run)
	{
		this.displayname = run.getName();
		this.serverFolder = new File(ServerTools.CWD, displayname);
		if(!serverFolder.exists()) serverFolder.mkdirs();
		this.plugins = new ArrayList<Plugin>();
		this.config = new CthFile(ServerTools.CWD, displayname + ".serverconfig");
		if(!config.exists())
		{
			config.create();
			config.addComment("Available types: VANILLA, SPIGOT, BUKKIT");
			config.set("server-type", run.getServerType());
			config.set("minecraft-version", run.getMinecraftVersion());
			config.addLine("");
			config.addComment("Change the directories in which worlds and plugins will be saved and read from");
			config.addComment("(Some plugins don't like this, Use this at your own risk!)");
			config.set("plugins-directory", serverFolder.getPath());
			config.set("worlds-directory", serverFolder.getPath());
			config.addLine("");
			config.addComment("The minimum (starting) amount of ram for the server");
			config.set("minimum-ram", run.getMinRam());
			config.addComment("The maximum amount of ram the server is allowed to use");
			config.set("maximum-ram", run.getMaxRam());
			config.addComment("(Multiply ram in GB by 1024 to get ram in MB)");
		}
		this.worldsFolder = new File(config.getString("worlds-directory"));
		if(!worldsFolder.exists()) worldsFolder.mkdirs();
		this.pluginsFolder = new File(config.getString("plugins-directory"));
		if(!pluginsFolder.exists()) pluginsFolder.mkdirs();
		this.type = ServerType.valueOf(config.getString("server-type").toUpperCase());
		this.minecraftVersion = config.getString("minecraft-version");
		this.pluginsFolder = new File(config.getString("plugins-directory"));
		if(!pluginsFolder.exists()) pluginsFolder.mkdirs();
		this.worldsFolder = new File(config.getString("worlds-directory"));
		if(!worldsFolder.exists()) worldsFolder.mkdirs();
		this.minRam = config.getInt("minimum-ram");
		this.maxRam = config.getInt("maximum-ram");
		this.serverJar = new File(serverFolder, type.value + "-" + minecraftVersion + ".jar");
		this.serverLauncher = new CthFile(serverFolder, ServerTools.isWindows() ? "launch.bat" : "launch.sh");
		if(!serverLauncher.exists())
		{
			serverLauncher.create();
			if(ServerTools.isWindows()) serverLauncher.addLine("@echo off");
			serverLauncher.addLine(getProgramArgsAsString());
			if(ServerTools.isWindows()) serverLauncher.addLine("pause");
		}
	}

	public String getName()
	{
		return displayname;
	}

	public List<Plugin> getPlugins()
	{
		return plugins;
	}

	public File getPluginsFolder()
	{
		return pluginsFolder;
	}

	public File getWorldsFolder()
	{
		return worldsFolder;
	}

	public File getServerFolder()
	{
		return serverFolder;
	}

	public File getServerJar()
	{
		return serverJar;
	}

	public String getMinecraftVersion()
	{
		return minecraftVersion;
	}

	public ServerType getServerType()
	{
		return type;
	}

	public CthFile getConfig()
	{
		return config;
	}

	public String[] getProgramArgs()
	{
		if(type == ServerType.VANILLA) return new String[] {"java", "-jar", "-Xmx" + maxRam + "M", "-Xms" + minRam + "M", serverJar.getName(), "-nogui"};
		else return new String[] {"java", "-jar", "-Xmx" + maxRam + "M", "-Xms" + minRam + "M", serverJar.getName()};
	}

	public String getProgramArgsAsString()
	{
		String m = "";
		for(String s: getProgramArgs()) m += s + " ";
		return m.trim();
	}

	public void start()
	{
		try
		{
//			String[] args;
//			if(ServerTools.isWindows()) args = new String[] {serverLauncher.getName()};
//			else args = new String[] {"bash", "-c", serverLauncher.getName()};
			if(ServerTools.runProcess(serverFolder, getProgramArgs()) == 0)
			{
				System.out.println("Looks like everything ran smoothly!");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
