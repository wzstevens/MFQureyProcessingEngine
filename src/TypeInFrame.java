import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.SwingConstants;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * 
 * @author apple
 */
public class TypeInFrame extends JFrame implements ActionListener {

	// Initialize parameters and setup window
	private JFrame frame = new JFrame();
	private Container container = frame.getContentPane();
	private JLabel label1 = new JLabel("Select Attributes(S):", JLabel.LEFT);
	private JTextField text1 = new JTextField();
	private JLabel label2 = new JLabel("Number of Grouping Variables(n):",
			JLabel.LEFT);
	private JTextField text2 = new JTextField();
	private JLabel label3 = new JLabel(
			"Where Clause: (Please input 'null' if none)", JLabel.LEFT);
	private JTextField text3 = new JTextField();
	private JLabel label4 = new JLabel("Grouping Attributes(V):", JLabel.LEFT);
	private JTextField text4 = new JTextField();
	private JLabel label5 = new JLabel("Aggregate Function Vector([F]):",
			JLabel.LEFT);
	private JTextField text5 = new JTextField();
	private JLabel label6 = new JLabel("Select Condition Vector([σ]):",
			JLabel.LEFT);
	private JTextArea text6 = new JTextArea();
	private JLabel label7 = new JLabel(
			"Having Clause(G): (Please type in 'null' if none)", JLabel.LEFT);
	private JTextArea text7 = new JTextArea();
	private JButton button1 = new JButton("OK");
	private JButton button2 = new JButton("Cancel");

	private final int frameHeight = 450;
	private final int frameWidth = 600;
	private final int textboxLength = 400;
	private final int textboxWidth = 20;

	public TypeInFrame() {
		frame.setTitle("TYPE IN");
		frame.setSize(frameHeight, frameWidth);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);

		container.setLayout(null);

		// Select Attributes
		label1.setSize(600, 20);
		label1.setLocation(25, 22);
		text1.setSize(textboxLength, textboxWidth);
		text1.setLocation(25, 42);

		// Number of Grouping Variables
		label2.setSize(600, 20);
		label2.setLocation(25, 62);
		text2.setSize(textboxLength, textboxWidth);
		text2.setLocation(25, 82);

		// Where Clause
		label3.setSize(600, 20);
		label3.setLocation(25, 102);
		text3.setSize(textboxLength, textboxWidth);
		text3.setLocation(25, 122);

		// Grouping Attributes
		label4.setSize(600, 20);
		label4.setLocation(25, 142);
		text4.setSize(textboxLength, textboxWidth);
		text4.setLocation(25, 162);

		// Aggregate Functions
		label5.setSize(600, 20);
		label5.setLocation(25, 182);
		text5.setSize(textboxLength, textboxWidth);
		text5.setLocation(25, 202);

		// Select Condition
		label6.setSize(600, 20);
		label6.setLocation(25, 222);
		text6.setSize(textboxLength, 100);
		text6.setLocation(25, 242);

		// Having Clause
		label7.setSize(600, 20);
		label7.setLocation(25, 342);
		text7.setSize(textboxLength, 100);
		text7.setLocation(25, 362);

		button1.setSize(100, 20);
		button1.setLocation(180, 492);
		button2.setSize(100, 20);
		button2.setLocation(180, 522);

		container.add(label1);
		container.add(text1);
		container.add(label2);
		container.add(text2);
		container.add(label3);
		container.add(text3);
		container.add(label4);
		container.add(text4);
		container.add(label5);
		container.add(text5);
		container.add(label6);
		container.add(text6);
		container.add(label7);
		container.add(text7);
		container.add(button1);
		container.add(button2);

		button1.addActionListener(this);
		button2.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// To change body of generated methods, choose Tools | Templates.
		if (e.getSource().equals(button1)) {
			// get phi operator parameters
			Phi_Operator pho = new Phi_Operator();
			try {
				pho.setSelect(Insert_Keywords(text1.getText(), ","));
				pho.setN_Group_(Integer.parseInt(text2.getText()));
				pho.setWhere_Con(text3.getText());
				pho.setGroup_Attr(Insert_Keywords(text4.getText(), ","));
				pho.setFunctions(Insert_Keywords(text5.getText(), ","));
				pho.setCondition_Vect(Insert_Keywords(text6.getText(), ";"));
				pho.setHaving(Insert_Keywords(text7.getText(), ";"));
				pho.Print_Int(pho.getCondition_Vect().size());

				// check validation of contents
				pho.Check_Content(pho.Get_Having(pho.getHaving(), " ", "."),
						pho.getSelect());
				pho.Check_Content(pho.getGroup_Attr(), pho.getSelect());
				pho.Remove_Content(pho.getSelect(), pho.getGroup_Attr());
				// pho.Check_Number(pho.getN_Group_());
				pho.setWhere_Con(pho.Null_To_True(pho.getWhere_Con()));
				pho.setCondition_Vect(pho.Null_To_True(pho.getCondition_Vect()));
				if (pho.getError_Num() == 0) {
					Mfquery mfq = new Mfquery();
					mfq.setSelectedOutput(pho.getSelect());
					mfq.setGroupingVarNumber(pho.getN_Group_());
					mfq.setWhere(pho.getWhere_Con());
					mfq.setGroupingAttributes(transferAttr(pho.getGroup_Attr()));
					mfq.setSuchthat(pho.getCondition_Vect());
					mfq.setHaving(pho.getHaving());
					mfq.main();
				}

				JOptionPane.showMessageDialog(null,
						"Done!\nThe input has been stored!", "Congratulations",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception ex) {
				int num = pho.getError_Num();
				num++;
				pho.setError_Num(num);
				JOptionPane.showMessageDialog(null, "Error", "Error",
						JOptionPane.ERROR_MESSAGE);
				ex.printStackTrace();
			}
		} else if (e.getSource().equals(button2)) {
			frame.dispose();
		}
	}

	// get contents according to keywords
	private String Get_Value(String Key, String Filepath)
			throws FileNotFoundException {
		String Last = "";
		try {
			Scanner scan = new Scanner(new FileInputStream(Filepath));
			while (scan.hasNextLine()) {
				String next = scan.nextLine();
				if (next.length() != 0 && next.equals(Key)) {
					next = scan.nextLine();
					while (next.length() == 0) {
						next = scan.nextLine();
						continue;
					}
					Last = next;
					break;
				} else
					continue;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Last;
	}

	// insert keywords into arraylists
	private ArrayList Insert_Keywords(String Str, String Split) {
		Scanner scan = new Scanner(Str);
		scan.useDelimiter(Split);
		ArrayList list = new ArrayList();
		while (scan.hasNext()) {
			list.add(scan.next().trim());
		}
		return list;
	}

	// get having and such that contents
	private void Execuate(ArrayList list, String Filepath)
			throws FileNotFoundException {
		Scanner scan = new Scanner(new FileInputStream(Filepath));
		String Str = "";
		while (!Str.equals(list.get(list.size() - 1))) {
			Str = scan.nextLine();
			continue;
		}
		while (Str.length() != 0 && !Str.contentEquals("HAVING CLAUSE(G):")) {
			if (scan.hasNextLine()) {
				Str = scan.nextLine();
				list.add(Str);
			} else
				break;
		}
		if (list.get(list.size() - 1).toString().length() == 0
				|| list.get(list.size() - 1).toString()
						.equals("HAVING CLAUSE(G):"))
			list.remove(list.size() - 1);
	}

	// transform the Arraylist form of grouping attributes to strings
	static String transferAttr(ArrayList outAttr) {

		String temp = "";
		for (int i = 0; i < outAttr.size(); i++) {
			temp += (outAttr.get(i) + "|");
		}

		temp = temp.substring(0, temp.lastIndexOf("|"));

		return temp;
	}

}
