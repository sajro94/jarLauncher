package org.sajro.VTGLAN;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.swing.*;

public class downloadWorker extends SwingWorker<String, Long> {

	@Override
	protected String doInBackground() throws Exception {
		URL website = new URL("http://www.sajro.org/VTGLAN/VTGLAN/VTGLAN.zip");
		ReadableByteChannel rbc = Channels.newChannel(website.openStream());
		@SuppressWarnings("resource")
		FileOutputStream fos = new FileOutputStream("VTGLAN.zip");
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		return null;
	}
	
}
