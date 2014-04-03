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

import javax.swing.JOptionPane;

public class LauncherClass {
	String dir;
	String user;
	String session;
	String pass;
	String ramUsing;
	String ram;
	Properties prop = new Properties();
	String installedModpack = "no";
	OutputStream propOut = null;
	InputStream input = null;

	public void main(String username, String password, String ramUsing,String dir) throws IOException 
	{
		user = username;
		pass = password;
		ram = ramUsing;
		try {
			String propFile = "launcherConfig.properties";
			input = getClass().getClassLoader().getResourceAsStream(propFile);
			if (input == null) {
				System.out.println("Sorry, unable to find " + propFile);
			}
			prop.load(input);
			installedModpack = prop.getProperty("InstalledModpack");
			if(installedModpack == null)
			{
				installedModpack = "no";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		String valid = null;
		String webData = null;
		System.out.println("Username:" + username + " ,password:" + password
				+ " ,rams:" + ramUsing + " ,directory:" + dir);
		try {
			webData = new Scanner(
					new URL("http://login.minecraft.net/?user=" + username
							+ "&password=" + password + "&version=13")
							.openStream(),
					"UTF-8").useDelimiter("\\A").next();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(webData);

		String[] sessions = webData.split(":");
		if (sessions.length > 1) {
			session = sessions[3];
			user = sessions[2];
			prop.setProperty("sessionUser", user);
			prop.setProperty("session", session);
		} else {
			session = "badLogin";
		}

		try {
			valid = new Scanner(
					new URL(
							"http://session.minecraft.net/game/joinserver.jsp?user="
									+ user + "&sessionId=" + session
									+ "&serverId=1").openStream(), "UTF-8")
					.useDelimiter("\\A").next();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("5" + valid + "5");
		if (valid.equals("OK")) {
			try {
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			File zipPath = new File(dir + "\\VTGLAN.zip");
			System.out.println(zipPath);
			if (zipPath.exists()) {
				URLConnection connection = null;
				try {
					connection = new URL(
							"http://www.sajro.org/VTGLAN/VTGLAN/VTGLAN.zip")
							.openConnection();
				} catch (Exception e) {
					e.printStackTrace();
				}
				int maxDownloadSize = connection.getContentLength();
				int zipSize = (int) zipPath.length();
				if (zipSize == maxDownloadSize) {
					if (new File(dir + "\\version.html").exists()) {
						String localVersion = null;
						String modPackVersion = null;
						try {
							modPackVersion = new Scanner(
									new URL(
											"http://www.sajro.org/VTGLAN/VTGLAN/version.html")
											.openStream(), "UTF-8")
									.useDelimiter("\\A").next();
							System.out.println(modPackVersion);
							System.out.println(dir + "\\version.html");
							// List<String> lines =
							// Files.readAllLines(Paths.get(dir+"version.html"),
							// null);
							localVersion = new Scanner(new URL("file:///" + dir
									+ "\\version.html").openStream(), "UTF-8")
									.useDelimiter("\\A").next();
							System.out.println(localVersion);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (localVersion.equals(modPackVersion)) {
							try {
								launchModPack(ramUsing, dir, user, session);
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							if(!installedModpack.equals("yes"))
							{
							new progressBar().main();
							clean_folder(dir);
							}
							else
							{
								new updateWorker().execute();
							}
						}
					} else {
						if(!installedModpack.equals("yes"))
						{
						new progressBar().main();
						clean_folder(dir);
						}
						else
						{
							new updateWorker().execute();
						}
					}
				} else {
					if(!installedModpack.equals("yes"))
					{
					new progressBar().main();
					clean_folder(dir);
					}
					else
					{
						new updateWorker().execute();
					}
				}
			} else {
				clean_folder(dir);
				new progressBar().main();
			}

		} else {
			JOptionPane.showOptionDialog(null, "Bad login", "BAD LOGIN", -1, 0,
					null, null, null);
		}

	}

	public static void clean_folder(String dir) {
		File f = new File(dir + "//server.jar");
		if (f.exists()) {
			f.delete();
		}
		f = new File(dir + "//server_run.bat");
		if (f.exists()) {
			f.delete();
		}
		f = new File(dir + "//version.html");
		if (f.exists()) {
			f.delete();
		}
		f = new File(dir + "//mods");
		if (f.exists()) {
			delete(f);
		}
		f = new File(dir + "//assets");
		if (f.exists()) {
			delete(f);
		}
		f = new File(dir + "//bin");
		if (f.exists()) {
			delete(f);
		}
		f = new File(dir + "//config");
		if (f.exists()) {
			delete(f);
		}
		f = new File(dir + "//libraries");
		if (f.exists()) {
			delete(f);
		}
		f = new File(dir + "//natives");
		if (f.exists()) {
			delete(f);
		}

	}

	public void extractModPack(String ramUsing, String user, String token)throws Exception 
	{
		dir = System.getProperty("user.dir");
		String zipFile = dir + "\\VTGLAN.zip";
		String outputFolder = dir;

		System.out.println("Begin unzip " + zipFile + " into " + outputFolder);
		ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry ze = zis.getNextEntry();
		while (ze != null) {
			String entryName = ze.getName();
			System.out.print("Extracting " + entryName + " -> " + outputFolder
					+ File.separator + entryName + "...");
			File f = new File(outputFolder + File.separator + entryName);
			System.out.println(entryName);
			if (entryName.endsWith("/")) {
				f.mkdir();
				System.out.println(f);
			} else {
				FileOutputStream fos = new FileOutputStream(f);
				int len;
				byte buffer[] = new byte[1024];
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
			}
			System.out.println("OK!");
			ze = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
		System.out.println(zipFile + " unzipped successfully");
		
	}

	public void launchModPack(String ramUsing, String dirCurrent, String user,String token) throws Exception 
	{
		propOut = new FileOutputStream("LauncherConfig.properties");
		try {
			prop.setProperty("Username", user);
			prop.setProperty("Password", pass);
			prop.setProperty("Ram", ram);
			prop.setProperty("Installed", "yes");
		prop.setProperty("InstalledModpack", "yes");
		prop.store(propOut, null);
		}
		catch(Exception e)
		{
			
		}
		console.main(null);
	}

	static void delete(File f) {
		if (f.isDirectory()) {
			for (File c : f.listFiles()) {
				delete(c);
			}
		}
		if (!f.delete()) {
			try {
				throw new FileNotFoundException("Failed to delete file: " + f);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void downloadDone() throws Exception {
		try {
			InputStream input = null;
			Properties prop = new Properties();
			String propFile = "launcherConfig.properties";
			input = getClass().getClassLoader().getResourceAsStream(propFile);
			if (input == null) {
				System.out.println("Sorry, unable to find " + propFile);
			}
			prop.load(input);
			String session = prop.getProperty("session");
			String username = prop.getProperty("sessionUser");
			String ramUsing = prop.getProperty("Ram");
			new LauncherClass().extractModPack(ramUsing, username, session);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
