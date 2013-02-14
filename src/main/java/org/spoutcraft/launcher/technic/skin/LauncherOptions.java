/*
 * This file is part of Technic Launcher.
 *
 * Copyright (c) 2013-2013, Technic <http://www.technicpack.net/>
 * Technic Launcher is licensed under the Spout License Version 1.
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spoutcraft.launcher.technic.skin;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import org.spoutcraft.launcher.Memory;
import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.entrypoint.SpoutcraftLauncher;
import org.spoutcraft.launcher.skin.MetroLoginFrame;
import org.spoutcraft.launcher.skin.components.LiteButton;
import org.spoutcraft.launcher.util.Compatibility;
import org.spoutcraft.launcher.util.Utils;

public class LauncherOptions extends JDialog implements ActionListener, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;

	private static final int FRAME_WIDTH = 300;
	private static final int FRAME_HEIGHT = 300;
	private static final String QUIT_ACTION = "quit";
	private static final String SAVE_ACTION = "save";
	private static final String LOGS_ACTION = "logs";
	private static final String CONSOLE_ACTION = "console";

	private JLabel background;
	private JLabel build;
	private LiteButton logs;
	private LiteButton save;
	private LiteButton console;
	private JComboBox memory;
	private JCheckBox permgen;
	private int mouseX = 0, mouseY = 0;

	public LauncherOptions() {
		setTitle("Launcher Options");
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		addMouseListener(this);
		addMouseMotionListener(this);
		setResizable(false);
		setUndecorated(true);
		initComponents();
	}

	private void initComponents() {
		Font minecraft = MetroLoginFrame.getMinecraftFont(12);
		
		background = new JLabel();
		background.setBounds(0,0, FRAME_WIDTH, FRAME_HEIGHT);
		MetroLoginFrame.setIcon(background, "optionsBackground.png", background.getWidth(), background.getHeight());

		ImageButton optionsQuit = new ImageButton(MetroLoginFrame.getIcon("exit.png", 16, 16), MetroLoginFrame.getIcon("exit.png", 16, 16));
		optionsQuit.setRolloverIcon(MetroLoginFrame.getIcon("exit_hover.png", 16, 16));
		optionsQuit.setBounds(FRAME_WIDTH - 10 - 16, 10, 16, 16);
		optionsQuit.setActionCommand(QUIT_ACTION);
		optionsQuit.addActionListener(this);

		JLabel title = new JLabel("Launcher Options");
		title.setFont(minecraft.deriveFont(14F));
		title.setBounds(50, 10, 200, 20);
		title.setForeground(Color.WHITE);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel memoryLabel = new JLabel("Memory: ");
		memoryLabel.setFont(minecraft);
		memoryLabel.setBounds(50, 100, 75, 25);
		memoryLabel.setForeground(Color.WHITE);

		memory = new JComboBox();
		memory.setBounds(150, 100, 100, 25);
		populateMemory(memory);

		permgen = new JCheckBox("Increase PermGen Size");
		permgen.setFont(minecraft);
		permgen.setBounds(50, 150, 200, 25);
		permgen.setSelected(Settings.getPermGen());
		permgen.setBorderPainted(false);
		permgen.setFocusPainted(false);
		permgen.setContentAreaFilled(false);
		permgen.setForeground(Color.WHITE);

		save = new LiteButton("Save", MetroLoginFrame.getIcon("button_launch.png"), MetroLoginFrame.getIcon("button_launch_hover.png"));
		save.setHorizontalTextPosition(SwingConstants.CENTER);
		save.setFont(minecraft.deriveFont(14F));
		save.setBounds(FRAME_WIDTH - 90 - 10, FRAME_HEIGHT - 60, 90, 30);
		save.setActionCommand(SAVE_ACTION);
		save.addActionListener(this);

		logs = new LiteButton("Logs", MetroLoginFrame.getIcon("button_launch.png"), MetroLoginFrame.getIcon("button_launch_hover.png"));
		logs.setFont(minecraft.deriveFont(14F));
		logs.setBounds(FRAME_WIDTH / 2 - 45, FRAME_HEIGHT - 60, 90, 30);
		logs.setForeground(Color.WHITE);
		logs.setActionCommand(LOGS_ACTION);
		logs.addActionListener(this);


		console = new LiteButton("Console", MetroLoginFrame.getIcon("button_launch.png"), MetroLoginFrame.getIcon("button_launch_hover.png"));
		console.setHorizontalTextPosition(SwingConstants.CENTER);
		console.setFont(minecraft.deriveFont(14F));
		console.setBounds(10, FRAME_HEIGHT - 60, 90, 30);
		console.setForeground(Color.WHITE);
		console.setActionCommand(CONSOLE_ACTION);
		console.addActionListener(this);

		build = new JLabel("Launcher Build: " + Settings.getLauncherBuild());
		build.setBounds(10, FRAME_HEIGHT - 25, 150, 20);
		build.setFont(minecraft);
		build.setForeground(Color.WHITE);

		Container contentPane = getContentPane();
		contentPane.add(permgen);
		contentPane.add(build);
		contentPane.add(logs);
		contentPane.add(console);
		contentPane.add(optionsQuit);
		contentPane.add(title);
		contentPane.add(memory);
		contentPane.add(memoryLabel);
		contentPane.add(save);
		contentPane.add(background);

		setLocationRelativeTo(this.getOwner());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JComponent) {
			action(e.getActionCommand(), (JComponent) e.getSource());
		}
	}

	public void action(String action, JComponent c) {
		if (action.equals(QUIT_ACTION)) {
			dispose();
		} else if (action.equals(SAVE_ACTION)) {
			int oldMem = Settings.getMemory();
			int mem = Memory.memoryOptions[memory.getSelectedIndex()].getSettingsId();
			Settings.setMemory(mem);
			boolean oldperm = Settings.getPermGen();
			boolean perm = permgen.isSelected();
			Settings.setPermGen(perm);
			Settings.getYAML().save();
			
			if (mem != oldMem || oldperm != perm) {
				int result = JOptionPane.showConfirmDialog(c, "Restart required for settings to take effect. Would you like to restart?", "Restart Required", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (result == JOptionPane.YES_OPTION) {
					SpoutcraftLauncher.relaunch(true);
				}
			}
			dispose();
		} else if (action.equals(LOGS_ACTION)) {
			File logDirectory = new File(Utils.getLauncherDirectory(), "logs");
			Compatibility.open(logDirectory);
		} else if (action.equals(CONSOLE_ACTION)) {
			SpoutcraftLauncher.setupConsole();
			dispose();
		}
	}

	@SuppressWarnings("restriction")
	private void populateMemory(JComboBox memory) {
		long maxMemory = 1024;
		String architecture = System.getProperty("sun.arch.data.model", "32");
		boolean bit64 = architecture.equals("64");

		try {
			OperatingSystemMXBean osInfo = ManagementFactory.getOperatingSystemMXBean();
			if (osInfo instanceof com.sun.management.OperatingSystemMXBean) {
				maxMemory = ((com.sun.management.OperatingSystemMXBean) osInfo).getTotalPhysicalMemorySize() / 1024 / 1024;
			}
		} catch (Throwable t) {
		}
		maxMemory = Math.max(512, maxMemory);

		if (maxMemory >= Memory.MAX_32_BIT_MEMORY && !bit64) {
			memory.setToolTipText("<html>Sets the amount of memory assigned to Minecraft<br/>" + "You have more than 1.5GB of memory available, but<br/>"
					+ "you must have 64bit java installed to use it.</html>");
		} else {
			memory.setToolTipText("<html>Sets the amount of memory assigned to Minecraft<br/>" + "More memory is not always better.<br/>"
					+ "More memory will also cause your CPU to work more.</html>");
		}

		if (!bit64) {
			maxMemory = Math.min(Memory.MAX_32_BIT_MEMORY, maxMemory);
		}
		System.out.println("Maximum usable memory detected: " + maxMemory + " mb");

		for (Memory mem : Memory.memoryOptions) {
			if (maxMemory >= mem.getMemoryMB()) {
				memory.addItem(mem.getDescription());
			}
		}

		int memoryOption = Settings.getMemory();
		try {
			Settings.setMemory(memoryOption);
			memory.setSelectedIndex(Memory.getMemoryIndexFromId(memoryOption));
		} catch (IllegalArgumentException e) {
			memory.removeAllItems();
			memory.addItem(String.valueOf(Memory.memoryOptions[0]));
			Settings.setMemory(1); // 512 == 1
			memory.setSelectedIndex(0); // 1st element
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
