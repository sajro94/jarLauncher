package org.sajro.VTGLAN;

import javax.swing.JFrame;

import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

@SuppressWarnings("serial")
public class progressBar extends JFrame {

	JFrame frm = new JFrame();
	JProgressBar downloadProgress;
	int maxDownloadSize;

	public void main() {
		URLConnection connection = null;
		try {
			connection = new URL(
					"http://www.sajro.org/VTGLAN/VTGLAN/VTGLAN.zip")
					.openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		maxDownloadSize = connection.getContentLength();

		downloadProgress = new JProgressBar(0, maxDownloadSize);
		downloadProgress.setValue(43);
		downloadProgress.setStringPainted(true);
		downloadProgress.setSize(250, 60);
		JLabel downloadLabel = new JLabel("Downloading Modpack");
		frm.getContentPane().add(downloadLabel);
		System.out.println("Value" + downloadProgress.getValue());
		frm.getContentPane().add(downloadProgress);
		frm.setVisible(true);
		frm.getContentPane().setLayout(new FlowLayout());
		frm.setSize(200, 100);
		System.out.println("Downloading");
		new downloadWorker().execute();
		System.out.println("Progressbar");
		new progressWorker().execute();
	}

	private class progressWorker extends SwingWorker<String, Integer> {

		@Override
		protected String doInBackground() throws Exception {
			String dir = System.getProperty("user.dir");
			final File file = new File(dir + "\\VTGLAN.zip");
			long localFileSize;
			do {
				Thread.sleep(1000);
				localFileSize = file.length();
				publish((int) localFileSize);
				// System.out.println("Current"+localFileSize);
			} while (localFileSize < maxDownloadSize);
			return "Done";
		}

		@Override
		protected void process(List<Integer> chunks) {
			downloadProgress.setValue(chunks.get(0));
			downloadProgress.setString(chunks.get(0) / 1024 + "/"
					+ maxDownloadSize / 1024 + " kB");
		}

		@Override
		protected void done() {
			System.out.println("Done Downloading");
			try {
				new LauncherClass().downloadDone();
			} catch (Exception e) {
				e.printStackTrace();
			}
			frm.dispose();
		}

	}

}
