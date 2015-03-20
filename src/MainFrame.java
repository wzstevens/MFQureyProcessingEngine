import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * 
 * @author apple
 */

// Main Window of the Program
public class MainFrame extends JFrame implements ActionListener {
	// Initialize parameters
	private JLabel label1 = new JLabel(
			"Welcom to SSD Database Management System", JLabel.CENTER);
	private JLabel label2 = new JLabel("Please Choose A Method to Input",
			JLabel.CENTER);
	private JFrame mainframe = new JFrame();
	private Container container = mainframe.getContentPane();
	private JButton jbtbutton1 = new JButton("Choose a File");
	private JButton jbtbutton2 = new JButton("Type-in");

	public MainFrame() {
		// Main Window setup
		mainframe.setTitle("WELCOME");
		mainframe.setSize(550, 250);
		mainframe.setLocationRelativeTo(null);
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainframe.setVisible(true);

		container.setLayout(null);

		label1.setSize(550, 25);
		label1.setLocation(0, 32);
		label2.setSize(550, 25);
		label2.setLocation(0, 72);

		jbtbutton1.setSize(148, 23);
		jbtbutton1.setLocation(200, 122);
		jbtbutton2.setSize(148, 23);
		jbtbutton2.setLocation(200, 162);

		setLayout(new GridLayout());

		container.add(label1);
		container.add(label2);
		container.add(jbtbutton1);
		container.add(jbtbutton2);

		jbtbutton1.addActionListener(this);
		jbtbutton2.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// To change body of generated methods, choose Tools | Templates.
		// Button "Choose a file"
		if (e.getSource().equals(jbtbutton1)) {
			try {
				ChooseFileFrame frame = new ChooseFileFrame();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		// Button "Type in "
		else if (e.getSource().equals(jbtbutton2)) {
			try {
				TypeInFrame frame = new TypeInFrame();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
