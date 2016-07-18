package com.Akoot.servertools;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class Builder
{	
	private ServerType type;
	private String minecraftVersion;
	private File workDirectory;
	private File jarsDirectory;
	private File buildToolsDirectory;

	public Builder(ServerType type, String minecraftVersion)
	{
		this.type = type;
		this.minecraftVersion = minecraftVersion;
		this.workDirectory = new File(".work");
		this.buildToolsDirectory = new File(workDirectory, "BuildTools");
		this.jarsDirectory = new File(workDirectory, "jars");
		if(!jarsDirectory.exists()) jarsDirectory.mkdirs();
		if(type != ServerType.VANILLA) if(!buildToolsDirectory.exists()) buildToolsDirectory.mkdirs();
	}

	private void setupBukkit()
	{
		try
		{
			File buildTools = new File(buildToolsDirectory, "BuildTools.jar");
			if(!buildTools.exists())
			{
				URL downloadURL = new URL("https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar");
				FileUtils.copyURLToFile(downloadURL, buildTools);
			}
			//			CthFile buildToolsLauncher = new CthFile(buildToolsDirectory, "BuildTools.sh");
			//			buildToolsLauncher.create();
			//			buildToolsLauncher.addLine("java -jar BuildTools.jar --rev " + minecraftVersion);
			try
			{
				if(ServerTools.runProcess(buildToolsDirectory, new String[] {"java", "-jar", buildTools.getName()}) == 0)
				{
					System.out.println("BuildTools has finished building " + getJar().getName());
					File craftbukkit = new File(buildToolsDirectory, "craftbukkit-" + minecraftVersion + ".jar");
					File spigot = new File(buildToolsDirectory, "spigot-" + minecraftVersion + ".jar");
					System.out.println("Moving source jars...");
					if(craftbukkit.exists()) FileUtils.moveFileToDirectory(craftbukkit, jarsDirectory, true);
					if(spigot.exists()) FileUtils.moveFileToDirectory(spigot, jarsDirectory, true);
					System.out.println("Cleaning up...");
					for(File file: buildToolsDirectory.listFiles()) file.delete();
				}
			}
			catch(Exception e)
			{
				System.out.println("There was an error with BuildTools.jar, exiting...");
				e.printStackTrace();
				System.exit(1);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void setupVanilla()
	{
		try
		{
			URL downloadURL = new URL(String.format("https://s3.amazonaws.com/Minecraft.Download/versions/%s/minecraft_server.%s.jar", minecraftVersion, minecraftVersion));
			FileUtils.copyURLToFile(downloadURL, getJar());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public File getJar()
	{
		return new File(jarsDirectory, type.value + "-" + minecraftVersion + ".jar");
	}

	public void buildServer()
	{
		if(type == ServerType.SPIGOT || type == ServerType.BUKKIT) setupBukkit();
		else setupVanilla();
	}

	public void buildPlugins()
	{

	}
}
