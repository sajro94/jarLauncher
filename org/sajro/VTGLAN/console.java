package org.sajro.VTGLAN;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import javax.swing.JTextArea;
import javax.swing.BoxLayout;

@SuppressWarnings("serial")
public class console extends JFrame {

	private JPanel contentPane;
	private JFrame frame;
	JTextArea consoleArea;
	org.sajro.VTGLAN.console.minecraftWorker minecraft = new minecraftWorker();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					console frame = new console();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public console() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane);
		
		consoleArea = new JTextArea();
		consoleArea.setLineWrap(true);
		scrollPane.setViewportView(consoleArea);
		
		System.out.println("test test test");
			new minecraftWorker().execute();
			//System.out.println("Launching Minecraft 2"); //For debugging
	}
	private class minecraftWorker extends SwingWorker<String, String>
	{

		@Override
		protected String doInBackground() throws Exception 
		{
			consoleArea.setText("");
			//System.out.println("Launching Minecraft"); //For debugging
			String user = null;
			String token = null;
			String ramUsing = null;
			String dirCurrent = System.getProperty("user.dir");
			InputStream input = null;
			Properties prop = new Properties();
			String propFile = "launcherConfig.properties";
			input = getClass().getClassLoader().getResourceAsStream(propFile);
			if (input == null) {
				System.out.println("Sorry, unable to find " + propFile);
			}
			prop.load(input);
			user = prop.getProperty("sessionUser");
			token = prop.getProperty("session");
			ramUsing = prop.getProperty("Ram");
			
			System.out.println("Here I am going to launch the Modpack later");
			String dir = "\""+dirCurrent+"\"";
			try {
				String arguments = "-Xmx"
						+ ramUsing
						+ "m -XX:MaxPermSize=256m -Djava.library.path="
						+ dir
						+ "\\bin\\natives -Dminecraft.applet.Targetdirectory="
						+ dir
						+ " -cp "
						+ dir
						+ "\\natives\\net\\minecraft\\launchwrapper\\1.8\\launchwrapper-1.8.jar;"
						+ dir
						+ "\\natives\\org\\ow2\\asm\\asm-all\\4.1\\asm-all-4.1.jar;"
						+ dir
						+ "\\natives\\org\\scala-lang\\scala-library\\2.10.2\\scala-library-2.10.2.jar;"
						+ dir
						+ "\\natives\\org\\scala-lang\\scala-compiler\\2.10.2\\scala-compiler-2.10.2.jar;"
						+ dir
						+ "\\natives\\lzma\\lzma\\0.0.1\\lzma-0.0.1.jar;"
						+ dir
						+ "\\natives\\net\\sf\\jopt-simple\\jopt-simple\\4.5\\jopt-simple-4.5.jar;"
						+ dir
						+ "\\natives\\com\\paulscode\\codecjorbis\\20101023\\codecjorbis-20101023.jar;"
						+ dir
						+ "\\natives\\com\\paulscode\\codecwav\\20101023\\codecwav-20101023.jar;"
						+ dir
						+ "\\natives\\com\\paulscode\\libraryjavasound\\20101123\\libraryjavasound-20101123.jar;"
						+ dir
						+ "\\natives\\com\\paulscode\\librarylwjglopenal\\20100824\\librarylwjglopenal-20100824.jar;"
						+ dir
						+ "\\natives\\com\\paulscode\\soundsystem\\20120107\\soundsystem-20120107.jar;"
						+ dir
						+ "\\natives\\argo\\argo\\2.25_fixed\\argo-2.25_fixed.jar;"
						+ dir
						+ "\\natives\\org\\bouncycastle\\bcprov-jdk15on\\1.47\\bcprov-jdk15on-1.47.jar;"
						+ dir
						+ "\\natives\\com\\google\\guava\\guava\\14.0\\guava-14.0.jar;"
						+ dir
						+ "\\natives\\org\\apache\\commons\\commons-lang3\\3.1\\commons-lang3-3.1.jar;"
						+ dir
						+ "\\natives\\commons-io\\commons-io\\2.4\\commons-io-2.4.jar;"
						+ dir
						+ "\\natives\\net\\java\\jinput\\jinput\\2.0.5\\jinput-2.0.5.jar;"
						+ dir
						+ "\\natives\\net\\java\\jutils\\jutils\\1.0.0\\jutils-1.0.0.jar;"
						+ dir
						+ "\\natives\\com\\google\\code\\gson\\gson\\2.2.2\\gson-2.2.2.jar;"
						+ dir
						+ "\\natives\\org\\lwjgl\\lwjgl\\lwjgl\\2.9.0\\lwjgl-2.9.0.jar;"
						+ dir
						+ "\\natives\\org\\lwjgl\\lwjgl\\lwjgl_util\\2.9.0\\lwjgl_util-2.9.0.jar;"
						+ dir
						+ "\\bin\\modpack.jar;"
						+ dir
						+ "\\bin\\minecraft.jar net.minecraft.launchwrapper.Launch --username "
						+ user
						+ " --session token:"
						+ token
						+ " --version 1.6.4-Forge9.11.1.965 --gamedir "
						+ dir
						+ " --assetsdir "
						+ dir
						+ "\\assets --tweakClass cpw.mods.fml.common.launcher.FMLTweaker";
				System.out.println(arguments);
				Process minecraft = Runtime.getRuntime().exec("java "+arguments);
				//JOptionPane.showConfirmDialog(frame,"Launching Minecraft.", "Warning",-1); //For debugging
				String line;
				BufferedReader input1 = new BufferedReader(new InputStreamReader(minecraft.getErrorStream()));
				  while ((line = input1.readLine()) != null) {
				    publish(line);
				  }
				  input.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void process(List<String> chunks)
		{
			consoleArea.append("\n"+chunks.get(0));
		}
		
		@Override
		protected void done()
		{
		}
		
	}
}
