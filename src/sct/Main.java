package sct;
import java.io.File;

import javax.swing.*;
//import java.awt.Dimension;

public class Main{
	public static void main(String[] args) {
		new File("record").mkdirs();
		JFrame frame = new JFrame("Ontogenesis");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new World());
		frame.setSize(1920, 1080);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		frame.setVisible(true);
	}
}