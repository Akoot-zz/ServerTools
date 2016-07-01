package com.Akoot.servertools;

import java.beans.ConstructorProperties;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

import com.Akoot.cthulhu.util.CthFile;

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
			Scanner in  = new Scanner(System.in);
			System.out.print("Start the server now? (yes/no): ");
			String input = in.next();
			in.close();
			if(input.equalsIgnoreCase("no")) System.exit(0);
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

	public static int runProcess(File workDir, String... command) throws Exception
	{
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.directory(workDir);
		pb.environment().put("JAVA_HOME", System.getProperty("java.home"));
		Process ps = pb.start();

		new Thread(new StreamRedirector(ps.getInputStream(), System.out)).start();
		new Thread(new StreamRedirector(System.in, new PrintStream(ps.getOutputStream()))).start();
		new Thread(new StreamRedirector(ps.getErrorStream(), System.err)).start();

		int status = ps.waitFor();
		if (status != 0)
		{
			throw new RuntimeException("Error running command, return status !=0: " + Arrays.toString(command));
		}
		return status;
	}
	
	public static boolean isWindows()
	{
		return System.getProperty("os.name").toLowerCase().contains("windows");
	}

	public static void main(String[] args)
	{
		instance = new ServerTools(new RunConfiguration(args));
	}

	private static class StreamRedirector
	implements Runnable
	{
		private final InputStream in;
		private final PrintStream out;

		@ConstructorProperties({"in", "out"})
		public StreamRedirector(InputStream in, PrintStream out)
		{
			this.in = in;
			this.out = out;
		}

		@Override
		public void run()
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(this.in));
			try
			{
				String line;
				while ((line = br.readLine()) != null)
				{
					this.out.println(line);
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
