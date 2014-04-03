package org.sajro.VTGLAN;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class gui {

	private JFrame frame;
	private JTextField user;
	private JTextField ram;
	String maxRamStr;
	private JButton launchButton;
	long maxRam;
	String dir;
	private JPasswordField pass;
	String installed = "no";
	Boolean autoLaunch = true;
	org.sajro.VTGLAN.gui.delayWorker delayWorker = new delayWorker();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					gui window = new gui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws InterruptedException
	 */
	public gui() throws InterruptedException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		if (!System.getProperty("java.version").contains("1.7")) {
			int wrongJavaResponse = JOptionPane.showConfirmDialog(frame,
					"Please use Java 7 otherwise Minecraft wont run.",
					"Warning", -1);
			System.out.println("button: " + wrongJavaResponse);
			if (wrongJavaResponse == 0) {
				System.exit(0);
			}
		}
		String propUser = null;
		String propPassword = null;
		String propRam = null;
		dir = System.getProperty("user.dir");
		InputStream input = null;
		Properties prop = new Properties();
		System.out.println(dir);
		int count = new File(dir).listFiles().length;

		try {
			if(new File("launcherConfig.properties").exists())
			{
			String propFile = "launcherConfig.properties";
			input = getClass().getClassLoader().getResourceAsStream(propFile);
			if (input == null) {
				System.out.println("Sorry, unable to find " + propFile);
			}
			prop.load(input);
			propUser = prop.getProperty("Username");
			propPassword = prop.getProperty("Password");
			propRam = prop.getProperty("Ram");
			installed = prop.getProperty("Installed");
			if(installed == null)
			{
				installed = "no";
			}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println(installed);
		if (count > 1 && !installed.equals("yes")) {
			int response = JOptionPane
					.showConfirmDialog(
							frame,
							"The Launcher should be alone in the folder\nWant to move it?",
							"Warning", 0);
			if (response == 0) {
				System.exit(0);
			}
		}

		com.sun.management.OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) java.lang.management.ManagementFactory
				.getOperatingSystemMXBean();
		long maxRam = bean.getTotalPhysicalMemorySize() / 1024 / 1024;
		maxRamStr = String.valueOf(maxRam - 1024);
		System.out.println("string" + maxRamStr);

		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 160, 221);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		final JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.addChangeListener(new ChangeListener() { // add the Listener

					@Override
					public void stateChanged(ChangeEvent e) {

						System.out.println("Pane"
								+ tabbedPane.getSelectedIndex());

						if (tabbedPane.getSelectedIndex() == 1) // Index starts at 0, so Index 1 = second tab which is the settings tab
						{
							delayWorker.cancel(true);
							autoLaunch = false;
							System.out.println("Settings!");
							launchButton.setText("Click to Launch");

						}
					}
				});
		tabbedPane.setBounds(0, 0, 154, 193);
		frame.getContentPane().add(tabbedPane);

		JPanel panel = new JPanel();
		tabbedPane.addTab("Main", null, panel, null);
		panel.setLayout(null);

		launchButton = new JButton("Launching in 10");
		launchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String ramUsing = ram.getText();
				String username = user.getText();
				String password = String.valueOf(pass.getPassword());
				try {
					new LauncherClass().main(username, password, ramUsing, dir);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				delayWorker.cancel(true);
				autoLaunch = false;
				delayWorker.cancel(false);
				launchButton
						.setText("Launching Minecraft\nOr downloading Modpack");
			}
		});
		launchButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		launchButton.setBounds(0, 0, 149, 165);
		panel.add(launchButton);

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Settings", null, panel_1, null);
		panel_1.setLayout(null);

		JLabel lblUser = new JLabel("User");
		lblUser.setBounds(5, 11, 32, 14);
		panel_1.add(lblUser);

		JLabel lblPass = new JLabel("Pass");
		lblPass.setBounds(5, 36, 32, 14);
		panel_1.add(lblPass);

		user = new JTextField();
		user.setBounds(43, 8, 76, 20);
		panel_1.add(user);
		user.setText(propUser);
		user.setColumns(10);

		JLabel label = new JLabel("/");
		label.setBounds(57, 67, 5, 14);
		panel_1.add(label);

		JLabel lblMaxram = new JLabel(maxRamStr + "MB RAM");
		lblMaxram.setBounds(63, 67, 46, 14);
		panel_1.add(lblMaxram);

		ram = new JTextField();
		ram.setBounds(5, 64, 46, 20);
		if (propRam == null) {
			ram.setText("2048");
		} else {
			ram.setText(propRam);
		}
		panel_1.add(ram);
		ram.setColumns(10);

		JCheckBox saveLogin = new JCheckBox("Save Login");
		saveLogin.setSelected(true);
		saveLogin.setBounds(12, 88, 97, 23);
		panel_1.add(saveLogin);

		JCheckBox useConsole = new JCheckBox("Use Console");
		useConsole.setBounds(12, 109, 97, 23);
		panel_1.add(useConsole);

		JCheckBox autoStart = new JCheckBox("Auto Start");
		autoStart.setSelected(true);
		autoStart.setBounds(12, 130, 97, 23);
		panel_1.add(autoStart);

		pass = new JPasswordField();
		pass.setBounds(43, 33, 76, 20);
		pass.setText(propPassword);
		panel_1.add(pass);

		if (autoLaunch = true) {
			delayWorker.execute();
		}

	}

	private class delayWorker extends SwingWorker<String, Integer> {

		@Override
		protected String doInBackground() throws Exception {
			int delay = 10;
			do {
				publish(delay);
				System.out.println(delay);
				Thread.sleep(1000);
				delay--;
			} while (delay >= 0);
			return null;
		}

		@Override
		protected void process(List<Integer> chunks) {
			int timeToLaunch = chunks.get(0);
			launchButton.setText("Launching in " + timeToLaunch);
			if (timeToLaunch == 0) {
				launchButton
						.setText("Launching Minecraft\nOr downloading Modpack");
				String ramUsing = ram.getText();
				String username = user.getText();
				String password = String.valueOf(pass.getPassword());
				try {
					new LauncherClass().main(username, password, ramUsing, dir);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		@Override
		protected void done() {

		}

	}
}
