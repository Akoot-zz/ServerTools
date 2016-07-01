package com.Akoot.servertools;

public enum ServerType
{
	SPIGOT("spigot"),
	BUKKIT("craftbukkit"),
	VANILLA("minecraft_server");
	
	public String value;
	
	private ServerType(String value)
	{
		this.value = value;
	}
}