package com.Akoot.servertools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.Akoot.util.CthFile;

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
	private String worldName;
	private int maxPlayers;
	private boolean onlineMode;
	private String ip;
	private CthFile eula;

	public Server(RunConfiguration run)
	{
		boolean vanilla = run.getServerType() == ServerType.VANILLA;
		this.displayname = run.name;
		this.config = new CthFile(run.serversDir, displayname + ".serverconfig");
		this.serverFolder = new File(run.serversDir, displayname);
		if(!config.exists())
		{
			config.create();
			config.addComment("Available types: VANILLA, SPIGOT, BUKKIT");
			config.set("server-type", run.getServerType());
			config.set("minecraft-version", run.minecraftVersion);
			config.addLine("");
			config.addComment("Change the directories in which worlds and plugins will be saved and read from");
			config.addComment("(Some plugins don't like this, Use this at your own risk!)");
			config.set("plugins-directory", "plugins");
			config.set("worlds-directory", ".");
			config.addLine("");
			config.addComment("Change the name of the default world (default is \"world\")");
			config.set("world-name", run.worldName);
			config.addComment("Server IP address");
			config.set("ip", run.ip + ":" + run.port);
			config.addComment("online-mode: false = cracked");
			config.set("online-mode", run.onlineMode);
			config.set("max-players", run.maxPlayers);
			config.addLine("");
			config.addComment("The minimum (starting) amount of ram for the server");
			config.set("minimum-ram", run.minRam);
			config.addComment("The maximum amount of ram the server is allowed to use");
			config.set("maximum-ram", run.maxRam);
			config.addComment("Multiply ram in GB by 1024 to get ram in MB");
		}
		if(!serverFolder.exists()) serverFolder.mkdir();		
		this.plugins = new ArrayList<Plugin>();	
		this.worldsFolder = new File(serverFolder, config.getString("worlds-directory"));
		if(!worldsFolder.exists()) worldsFolder.mkdir();		
		this.pluginsFolder = new File(serverFolder, config.getString("plugins-directory"));
		if(!pluginsFolder.exists() && !vanilla) pluginsFolder.mkdir();		
		this.type = ServerType.valueOf(config.getString("server-type").toUpperCase());
		this.minecraftVersion = config.getString("minecraft-version");
		this.minRam = config.getInt("minimum-ram");
		this.maxRam = config.getInt("maximum-ram");
		this.maxPlayers = config.getInt("max-players");
		this.worldName = config.getString("world-name");
		this.onlineMode = config.getBoolean("online-mode");
		this.ip = config.getString("ip");
		this.serverJar = new File(serverFolder, type.value + "-" + minecraftVersion + ".jar");
		this.serverLauncher = new CthFile(serverFolder, ServerTools.isBash ? "launch.bat" : "launch.sh");
		buildLauncher();
		this.eula = new CthFile(this.serverFolder, "eula.txt");
		if(!eula.exists()) eula.addLine("eula=true");
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

	public void buildLauncher()
	{
		serverLauncher.delete();
		serverLauncher.create();

		if(ServerTools.isBash) serverLauncher.addLine("#Feel free to edit this generated launch file!");
		else serverLauncher.addLine("::Feel free to edit this generated launch file!");

		if(ServerTools.isBash)
		{
			serverLauncher.addLine("@echo off");
			serverLauncher.addLine("title " + displayname + "-" + minecraftVersion);
		}

		String command = "java -jar -Xmx" + maxRam + "M -Xms" + minRam + "M " + serverJar.getName();
		if(type == ServerType.VANILLA) command += " nogui";
		else
		{
			command += "--plugins " + this.pluginsFolder;
			command += "--universe " + this.worldsFolder;
			command += "--world " + this.worldName;
			command += "--size " + this.maxPlayers;
			command += "--online-mode " + this.onlineMode;
			command += "--host " + this.ip;
			command += "--port " + (this.ip.contains(":") ? this.ip.substring(ip.indexOf(":")) : 25565);
		}

		serverLauncher.addLine(command);
	}

	public String[] getProgramArgs()
	{
		//if(ServerTools.isBash) return new String[] {serverLauncher.getName()};
		//else return new String[] {"cmd", "/c", "start", serverLauncher.getName()};
		if(this.type != ServerType.VANILLA) return new String[] {"java", "-jar", serverJar.getName()};
		else return new String[] {"java", "-jar", serverJar.getName(), "nogui"};
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
			ServerTools.runProcess(serverFolder, getProgramArgs());
			System.exit(0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
