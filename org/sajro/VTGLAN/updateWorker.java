package org.sajro.VTGLAN;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

import javax.swing.*;

public class updateWorker extends SwingWorker<String, Long> {

	@Override
	protected String doInBackground() throws Exception {
		@SuppressWarnings("resource")
		String updates = new Scanner(new URL("http://www.sajro.org/VTGLAN/VTGLAN/updates.html").openStream(),"UTF-8").useDelimiter("\\A").next();
		String[] updatesArray = updates.split(";");
		int updatesArrayNumber = updatesArray.length-1;
		int arrayIndex = 0;
		while(arrayIndex < updatesArrayNumber)
		{
			if(updatesArray[arrayIndex].toString().trim().startsWith("-"))
			{
				String dir = System.getProperty("user.dir");
				File f = new File(dir+ "\\" + updatesArray[arrayIndex].toString().trim().replace("-http://www.sajro.org/VTGLAN/VTGLAN/", ""));
				System.out.println("File: "+f);
				if (f.exists()) {
					System.out.println("Delete file: "+f);
					LauncherClass.delete(f);
				}
			}
			else
			{
				System.out.println("(start)"+updatesArray[arrayIndex].toString().trim()+"(end)");
				System.out.println("Downloading");
				System.out.println(updatesArray[arrayIndex].toString().trim().replace("http://www.sajro.org/VTGLAN/VTGLAN/", ""));
				URL website = new URL(updatesArray[arrayIndex].toString().trim());
				ReadableByteChannel rbc = Channels.newChannel(website.openStream());
				@SuppressWarnings("resource")
				FileOutputStream fos = new FileOutputStream(updatesArray[arrayIndex].toString().trim().replace("http://www.sajro.org/VTGLAN/VTGLAN/", ""));
				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			}
			arrayIndex++;
		}
		System.out.println("done updating");
		return null;
	}

}
