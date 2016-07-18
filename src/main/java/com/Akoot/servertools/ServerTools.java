package com.Akoot.servertools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

public class ServerTools
{
	private static ServerTools instance;
	private Server server;
	public static boolean isBash;

	public ServerTools(RunConfiguration config)
	{
		testBash();
		if(!config.serversDir.exists()) config.serversDir.mkdir();
		this.server = new Server(config);
		if(!server.getServerJar().exists())
		{
			Builder builder = new Builder(server.getServerType(), server.getMinecraftVersion());
			if(!builder.getJar().exists())
			{
				System.out.println("Preparing " + server.getServerJar().getName() + "...");
				if(server.getServerType() != ServerType.VANILLA)
				{
					try
					{
						runProcess(config.serversDir, new String[] {"bash", "-c", "exit"});
						builder.buildServer();
					}
					catch (Exception e)
					{
						System.out.println("To download bukkit/spigot jars, you must run this through bash (msysgit (sorry!))");
						try
						{
							if(!askYesOrNo("Download Git for Windows?")) System.exit(0);
							FileUtils.copyURLToFile(new URL("https://github.com/git-for-windows/git/releases/download/v2.9.0.windows.1/Git-2.9.0-64-bit.exe"), new File("Git-2.9.0-64-bit.exe"));
							System.out.println("Successfully downloaded the installer for Git!");
							System.out.println("Now run it to install git for Windows...");
						}
						catch (IOException e1)
						{
							e1.printStackTrace();
						}
						System.exit(1);
					}
				}
				else
				{
					builder.buildServer();
				}
			}

			System.out.println("Copying " + server.getServerJar() + " to " +  server.getServerFolder() + "...");

			try
			{
				FileUtils.copyFileToDirectory(builder.getJar(), server.getServerFolder(), false);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		System.out.println("Everything is up to date.");
		if(!config.start) if(!askYesOrNo("Start server now?")) System.exit(0);
		server.start();
	}

	public Server getServer()
	{
		return server;
	}

	public static ServerTools getInstance()
	{
		return instance;
	}

	public void testBash()
	{
		try
		{
			runProcess(new File("."), new String[] {"bash", "-c", "exit"});
			isBash = true;
		}
		catch (Exception e)
		{
			isBash =  false;
		}
	}

	public boolean askYesOrNo(String msg)
	{
		@SuppressWarnings("resource")
		Scanner in  = new Scanner(System.in);
		System.out.print(msg + " (y/n): ");
		String input = in.nextLine();
		if(input.startsWith("y")) return true;
		return false;
	}

	public static int runProcess(File workDir, String... command) throws Exception
	{
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.directory(workDir);
		pb.environment().put("JAVA_HOME", System.getProperty("java.home"));
		pb.redirectErrorStream(true);
		Process p = pb.start();
		
		InputStream out = p.getInputStream();
		OutputStream in = p.getOutputStream();

		byte[] buffer = new byte[4000];
		
		while (isAlive(p))
		{
			int no = out.available();
			int ni = System.in.available();
			if (no > 0)
			{
				int n = out.read(buffer, 0, Math.min(no, buffer.length));
				System.out.print(new String(buffer, 0, n));
			}
			if (ni > 0)
			{
				int n = System.in.read(buffer, 0, Math.min(ni, buffer.length));
				in.write(buffer, 0, n);
				in.flush();
			}

			try {Thread.sleep(10);}
			catch (InterruptedException e) {}
		}

		int status = p.waitFor();

		if (status != 0)
		{
			throw new RuntimeException("Error running command, return status != 0: " + Arrays.toString(command));
		}
		return status;
	}

	public static boolean isAlive(Process p)
	{
		try 
		{
			p.exitValue();
			return false;
		}
		catch (IllegalThreadStateException e)
		{
			return true;
		}
	}

	public static void main(String[] args)
	{
//		System.out.println("e");
//		Builder b = new Builder(ServerType.VANILLA, "1.10.2");
//		b.buildServer();
//		try
//		{
//			if(runProcess(new File("."), new String[] {"java", "-jar", b.getJar().getPath(), "nogui"}) == 0) System.out.println("done.");
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
		instance = new ServerTools(new RunConfiguration(args));
	}
}
