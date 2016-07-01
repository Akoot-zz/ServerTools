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
		this.workDirectory = new File(ServerTools.CWD, ".work");
		this.buildToolsDirectory = new File(workDirectory, "BuildTools");
		this.jarsDirectory = new File(workDirectory, "jars");
		if(!jarsDirectory.exists()) jarsDirectory.mkdirs();
		if(!buildToolsDirectory.exists()) buildToolsDirectory.mkdirs();
	}
	
	private void setupBukkit()
	{
		try
		{
			URL downloadURL = new URL("https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar");
			FileUtils.copyURLToFile(downloadURL, new File(buildToolsDirectory, "BuildTools.jar"));
			if(ServerTools.runProcess(buildToolsDirectory, new String[] { "java", "-jar", "BuildTools.jar", "--rev", minecraftVersion }) == 0)
			{
				System.out.println("Moving source jars...");
				FileUtils.moveFileToDirectory(new File(buildToolsDirectory, "craftbukkit-" + minecraftVersion + ".jar"), jarsDirectory, true);
				FileUtils.moveFileToDirectory(new File(buildToolsDirectory, "spigot-" + minecraftVersion + ".jar"), jarsDirectory, true);
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
