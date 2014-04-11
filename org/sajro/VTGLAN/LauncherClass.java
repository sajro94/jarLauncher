package org.sajro.VTGLAN;

import java.io.File;
import java.net.URL;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLConnection;
//import java.util.Dictionary;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.Properties;

//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;








import javax.net.ssl.HttpsURLConnection;
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
	int responseCode;

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
			if(new File(propFile).exists())
			{
			prop.load(input);
			installedModpack = prop.getProperty("InstalledModpack");
			if(installedModpack == null)
			{
				installedModpack = "no";
			}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		String valid = "yes";
		System.out.println("Username:" + username + " ,password:" + password
				+ " ,rams:" + ramUsing + " ,directory:" + dir);
		String webDataLogin = null;
		try
		{
			webDataLogin = login();
		}
		catch(Exception e)
		{
			JOptionPane.showOptionDialog(null, "Check network and or minecraft login servers.", "Network Error", -1, 0,null, null, null);
		}
		System.out.println(webDataLogin);
		int tokenNum = webDataLogin.indexOf("\"accessToken\":")+14;
		String sessionTemp = webDataLogin.substring(tokenNum, tokenNum+34);
		session = sessionTemp.replaceAll("\"", "");
		int userNumStart = webDataLogin.indexOf("\"name\":", webDataLogin.indexOf("\"selectedProfile\""))+8;
		int userNumEnd = webDataLogin.indexOf(",",userNumStart);
		String userTemp = webDataLogin.substring(userNumStart, userNumEnd);
		user = userTemp.replaceAll("\"", "");
		
		System.out.println("session:"+session);
		System.out.println("temp:"+sessionTemp);
		System.out.println("name:"+user);
		System.out.println("temp:"+userTemp);
		propOut = new FileOutputStream("LauncherConfig.properties");
		try {
			prop.setProperty("sessionUser", user);
			prop.setProperty("Password", pass);
			prop.setProperty("Ram", ram);
			prop.setProperty("Installed", "yes");
			prop.setProperty("session", session);
		prop.setProperty("InstalledModpack", "yes");
		prop.store(propOut, null);
		}
		catch(Exception e)
		{
			
		}
		if(responseCode == 200)
		{
			valid = "OK";
		}
		
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
				} catch (IOException e) {
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
							localVersion = new Scanner(new URL("file:///"+dir+"\\version.html").openStream(),"UTF-8").useDelimiter("\\A").next();
							System.out.println(localVersion);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (localVersion.equals(modPackVersion)) {
							if(new File(dir+"\\bin\\minecraft.jar").exists())
							{
								try {
								launchModPack(ramUsing, dir, user, session);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							else
							{
								try {
									new LauncherClass().extractModPack(ramUsing, user, session);
								} catch (Exception e) {
									e.printStackTrace();
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
		new LauncherClass().launchModPack(ramUsing, dir, user, token);
		
	}

	public void launchModPack(String ramUsing, String dirCurrent, String user,String token) throws Exception 
	{
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
			if(new File(propFile).exists())
			{
			prop.load(input);
			session = prop.getProperty("session");
			user = prop.getProperty("sessionUser");
			ramUsing = prop.getProperty("Ram");
			}
			new LauncherClass().extractModPack(ramUsing, user, session);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String login() throws IOException
	{
		
		String loginUrl = "https://authserver.mojang.com/authenticate";
		String payload = "{\"agent\": {\"name\": \"Minecraft\",\"version\": 1},\"username\":\"" + user + "\",\"password\":\"" + pass + "\",\"clientToken\": \"omitted\"}";
		
		URL login = new URL(loginUrl);
		HttpsURLConnection con = (HttpsURLConnection) login.openConnection();
		
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type","application/json");
				
		System.out.println(payload);
		
		//Dictionary<String, String> jsonPayload = new Gson().fromJson(payload, new TypeToken<Dictionary<String, Integer>>() {}.getType());
		
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());	
		wr.writeBytes(payload);
		wr.flush();
		wr.close();
		
		responseCode = con.getResponseCode();
		String responseMsg = con.getResponseMessage();
		System.out.println("Use this number to check for sucess:"+responseCode);
		System.out.println(responseMsg);
		
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
			
		return response.toString();
	}
}
