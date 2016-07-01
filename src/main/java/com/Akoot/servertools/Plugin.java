package com.Akoot.servertools;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class Plugin
{
	private Server server;
	private String name, URL;
	private boolean enabled;

	public Plugin(Server server, String name, String URL)
	{
		this.server = server;
		this.name = name;
		this.URL = URL;
	}

	public URL getURL()
	{
		try
		{
			return new URL(URL);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public String getName()
	{
		return name;
	}

	public void download()
	{
		File saveLocation = server.getPluginsFolder();
		if(!saveLocation.exists()) saveLocation.mkdir();
		try
		{
			FileUtils.copyURLToFile(getURL(), new File(saveLocation, name + ".jar"));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void update()
	{
		if(isOutdated())
		{
			unload();
			download();
			load();
		}
		else
		{
		}
	}
	
	public boolean isOutdated()
	{
		return false;
	}
	
	public void setEnabled(boolean enable)
	{
		this.enabled = enable;
	}
	
	public void unload()
	{
		//TODO: Unload the plugin
	}
	
	public void load()
	{
		//TODO: Load the plugin
	}
	
	public boolean isEnabled()
	{
		return enabled;
	}
}
