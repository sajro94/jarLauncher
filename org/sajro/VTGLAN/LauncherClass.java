package org.sajro.VTGLAN;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class LauncherClass {
	String dir;
	String user;
	String session;
	String ramUsing;
	private JFrame frame;

	public void main(String username, String password, String ramUsing, String dir) throws IOException 
	{
		Properties prop = new Properties();
		OutputStream propOut = null;
		try {
			propOut = new FileOutputStream("LauncherConfig.properties");
			//Sets the values I want to store
			prop.setProperty("Username", username);
			prop.setProperty("Password", password);
			prop.setProperty("Ram", ramUsing);
			prop.setProperty("Installed", "yes");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String valid = null;
		String webData = null;
		System.out.println("Username:"+username+" ,password:"+password+" ,rams:"+ramUsing+" ,directory:"+dir);
		try {
			webData = new Scanner(new URL("http://login.minecraft.net/?user="+username+"&password="+password+"&version=13").openStream(), "UTF-8").useDelimiter("\\A").next();
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		System.out.println(webData);
		
		String[] sessions = webData.split(":");
		if (sessions.length > 1)
		{
			session = sessions[3];
			user = sessions[2];
			prop.setProperty("sessionUser", user);
			prop.setProperty("session", session);
			prop.store(propOut, null);
		}
		else
		{
			session = "badLogin";
		}

		try 
		{
			valid = new Scanner(new URL("http://session.minecraft.net/game/joinserver.jsp?user="+user+"&sessionId="+session+"&serverId=1").openStream(), "UTF-8").useDelimiter("\\A").next();
			
		} 
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		System.out.println("5"+valid+"5");
		if (valid.equals("OK"))
		{
			try {
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			File zipPath = new File(dir + "\\VTGLAN.zip");
			System.out.println(zipPath);
			if(zipPath.exists())
			{
				URLConnection connection = null;
				try 
				{
					connection = new URL("http://www.sajro.org/VTGLAN/VTGLAN/VTGLAN.zip").openConnection();
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				int maxDownloadSize = connection.getContentLength();
				int zipSize = (int) zipPath.length();
				if(zipSize == maxDownloadSize)
				{
					if(new File(dir+"\\version.html").exists())
					{
						String localVersion = null;
						String modPackVersion = null;
						try
						{
							modPackVersion = new Scanner(new URL("http://www.sajro.org/VTGLAN/VTGLAN/version.html").openStream(), "UTF-8").useDelimiter("\\A").next();
							System.out.println(modPackVersion);
							System.out.println(dir+"\\version.html");
							//List<String> lines = Files.readAllLines(Paths.get(dir+"version.html"), null);
							localVersion = new Scanner(new URL("file:///"+dir+"\\version.html").openStream(), "UTF-8").useDelimiter("\\A").next();
							System.out.println(localVersion);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						if(localVersion.equals(modPackVersion))
						{
							launchModPack(ramUsing, dir, user, session);
						}
						else
						{
							clean_folder(dir);
							new progressBar().main();
						}
					}
					else
					{
						clean_folder(dir);
						new progressBar().main();
					}
				}
				else
				{
					clean_folder(dir);
					new progressBar().main();
				}
			}
			else
			{
				clean_folder(dir);
				new progressBar().main();
			}
			
		}
		else
		{
			JOptionPane.showOptionDialog(null, "Bad login", "BAD LOGIN", -1, 0, null, null, null);
		}
		
	}
	
	public static void clean_folder(String dir)
	{
		File f = new File(dir+"//server.jar");
		if(f.exists())
		{
			f.delete();
		}
		f = new File(dir+"//server_run.bat");
		if(f.exists())
		{
			f.delete();
		}
		f = new File(dir+"//version.html");
		if(f.exists())
		{
			f.delete();
		}
		f = new File(dir+"//mods");
		if(f.exists())
		{
			delete(f);
		}
		f = new File(dir+"//assets");
		if(f.exists())
		{
			delete(f);
		}
		f = new File(dir+"//bin");
		if(f.exists())
		{
			delete(f);
		}
		f = new File(dir+"//config");
		if(f.exists())
		{
			delete(f);
		}
		f = new File(dir+"//libraries");
		if(f.exists())
		{
			delete(f);
		}
		f = new File(dir+"//natives");
		if(f.exists())
		{
			delete(f);
		}
		
	}
	
	public void extractModPack(String ramUsing, String user, String token) throws Exception
	{
		dir = System.getProperty("user.dir");
		String zipFile = dir+"\\VTGLAN.zip";
        String outputFolder = dir;
 
        System.out.println("Begin unzip "+ zipFile + " into "+outputFolder);
        ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry ze = zis.getNextEntry();
        while(ze!=null){
            String entryName = ze.getName();
            System.out.print("Extracting " + entryName + " -> " + outputFolder + File.separator +  entryName + "...");
            File f = new File(outputFolder + File.separator +  entryName);
            System.out.println(entryName);
            if(entryName.endsWith("/"))
            {
            	f.mkdir();
            	System.out.println(f);
            }
            else
            {
            	FileOutputStream fos = new FileOutputStream(f);
            	int len;
            	byte buffer[] = new byte[1024];
            	while ((len = zis.read(buffer)) > 0) 
            	{
            		fos.write(buffer, 0, len);
            	}
            	fos.close();   
            }
            System.out.println("OK!");
            ze = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
 
        System.out.println( zipFile + " unzipped successfully");
        launchModPack(ramUsing, dir, user, token);
	}
	
	public void launchModPack(String ramUsing, String dir, String user, String token)
	{
		System.out.println("Here I am going to launch the Modpack later");
		try {
			String arguments = "-Xmx" + ramUsing + "m -XX:MaxPermSize=256m -Djava.library.path=" + dir + "\\bin\\natives -Dminecraft.applet.Targetdirectory=" + dir + " -cp " + dir + "\\natives\\net\\minecraft\\launchwrapper\\1.8\\launchwrapper-1.8.jar;" + dir + "\\natives\\org\\ow2\\asm\\asm-all\\4.1\\asm-all-4.1.jar;" + dir + "\\natives\\org\\scala-lang\\scala-library\\2.10.2\\scala-library-2.10.2.jar;" + dir + "\\natives\\org\\scala-lang\\scala-compiler\\2.10.2\\scala-compiler-2.10.2.jar;" + dir + "\\natives\\lzma\\lzma\\0.0.1\\lzma-0.0.1.jar;" + dir + "\\natives\\net\\sf\\jopt-simple\\jopt-simple\\4.5\\jopt-simple-4.5.jar;" + dir + "\\natives\\com\\paulscode\\codecjorbis\\20101023\\codecjorbis-20101023.jar;" + dir + "\\natives\\com\\paulscode\\codecwav\\20101023\\codecwav-20101023.jar;" + dir + "\\natives\\com\\paulscode\\libraryjavasound\\20101123\\libraryjavasound-20101123.jar;" + dir + "\\natives\\com\\paulscode\\librarylwjglopenal\\20100824\\librarylwjglopenal-20100824.jar;" + dir + "\\natives\\com\\paulscode\\soundsystem\\20120107\\soundsystem-20120107.jar;" + dir + "\\natives\\argo\\argo\\2.25_fixed\\argo-2.25_fixed.jar;" + dir + "\\natives\\org\\bouncycastle\\bcprov-jdk15on\\1.47\\bcprov-jdk15on-1.47.jar;" + dir + "\\natives\\com\\google\\guava\\guava\\14.0\\guava-14.0.jar;" + dir + "\\natives\\org\\apache\\commons\\commons-lang3\\3.1\\commons-lang3-3.1.jar;" + dir + "\\natives\\commons-io\\commons-io\\2.4\\commons-io-2.4.jar;" + dir + "\\natives\\net\\java\\jinput\\jinput\\2.0.5\\jinput-2.0.5.jar;" + dir + "\\natives\\net\\java\\jutils\\jutils\\1.0.0\\jutils-1.0.0.jar;" + dir + "\\natives\\com\\google\\code\\gson\\gson\\2.2.2\\gson-2.2.2.jar;" + dir + "\\natives\\org\\lwjgl\\lwjgl\\lwjgl\\2.9.0\\lwjgl-2.9.0.jar;" + dir + "\\natives\\org\\lwjgl\\lwjgl\\lwjgl_util\\2.9.0\\lwjgl_util-2.9.0.jar;" + dir + "\\bin\\modpack.jar;" + dir + "\\bin\\minecraft.jar net.minecraft.launchwrapper.Launch --username " + user + " --session token:" + token + " --version 1.6.4-Forge9.11.1.965 --gamedir " + dir + " --assetsdir " + dir + "\\assets --tweakClass cpw.mods.fml.common.launcher.FMLTweaker";
			System.out.println(arguments);
			Runtime.getRuntime().exec("java "+arguments);
		} catch (IOException e) {
			e.printStackTrace();
		}
		JOptionPane.showConfirmDialog(frame, "Please use Java 7 otherwise Minecraft wont run.", "Warning", -1);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
		
	}
	
	static void delete(File f)
	{
		if (f.isDirectory()) 
		{
			for (File c : f.listFiles())
			{
				delete(c);
			}
		}
		if (!f.delete())
		{
			try 
			{
				throw new FileNotFoundException("Failed to delete file: " + f);
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
		}
	}
	public void downloadDone() throws Exception {
		try
		{
			InputStream input = null;
			Properties prop = new Properties();
			String propFile = "launcherConfig.properties";
			input = getClass().getClassLoader().getResourceAsStream(propFile);
			if(input==null)
			{
			System.out.println("Sorry, unable to find " + propFile);
			}
			prop.load(input);
			String session = prop.getProperty("session");
			String username = prop.getProperty("sessionUser");
			String ramUsing = prop.getProperty("Ram");
			new LauncherClass().extractModPack(ramUsing, username, session);
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
		
