package com.Akoot.servertools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.Akoot.util.CthFile;

public class ServerTools
{
	private static ServerTools instance;
	static final File CWD = new File("run");
	private Server server;

	public ServerTools(RunConfiguration config)
	{
		this.server = new Server(config);
		if(!server.getServerJar().exists())
		{
			Builder builder = new Builder(config.getServerType(), config.getMinecraftVersion());
			if(!builder.getJar().exists())
			{
				System.out.println("Downloading " + config.serverFileName() + "...");
				if(config.getServerType() != ServerType.VANILLA)
				{
					try
					{
						runProcess(CWD, new String[] {"bash", "-c", "exit"});
						builder.buildServer();
					}
					catch (Exception e)
					{
						System.out.println("To download bukkit/spigot jars, you must run this through bash (msysgit (sorry!))");
						try
						{
							if(!askYesOrNo("Download Git for Windows?")) System.exit(0);
							FileUtils.copyURLToFile(new URL("https://github.com/git-for-windows/git/releases/download/v2.9.0.windows.1/Git-2.9.0-64-bit.exe"), new File(CWD, "Git-2.9.0-64-bit.exe"));
							System.out.println("Successfully downloaded the installer for Git! Please install it manually");
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

			System.out.println("Moving " + config.serverFileName() + " to " +  server.getServerFolder() + "...");

			try
			{
				FileUtils.moveFileToDirectory(builder.getJar(), server.getServerFolder(), false);
				new CthFile(server.getServerFolder(), "eula.txt").addLine("eula=true");
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			System.out.println("Done with everything b0ss!");
		}
		if(!config.startServer())
		{
			if(!askYesOrNo("Start server now?")) System.exit(0);
		}
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

	public static boolean isWindows()
	{
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}

	public static void main(String[] args)
	{
		instance = new ServerTools(new RunConfiguration(args));
	}

	public boolean askYesOrNo(String msg)
	{
		Scanner in  = new Scanner(System.in);
		System.out.print(msg + " (yes/no): ");
		String input = in.nextLine();
		in.close();
		if(input.startsWith("y")) return true;
		return false;
	}

	public static int runProcess(File workDir, String... command) throws Exception
	{
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.directory(workDir);
		pb.environment().put("JAVA_HOME", System.getProperty("java.home"));
		Process ps = pb.start();

		IOUtils.copy(ps.getInputStream(), System.out);

		int status = ps.waitFor();

		if (status != 0)
		{
			throw new RuntimeException("Error running command, return status != 0: " + Arrays.toString(command));
		}
		return status;
	}
}
