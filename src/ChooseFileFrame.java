
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author apple
 */
public class ChooseFileFrame extends JFrame implements ActionListener {

    // Initialize parameters
    private JFrame frame = new JFrame();
    private Container container = frame.getContentPane();
    private JLabel label1 = new JLabel("File Name:", JLabel.LEFT);
    private JLabel label2 = new JLabel("File Path:", JLabel.LEFT);
    private JLabel label3 = new JLabel("", JLabel.LEFT);
    private JLabel label4 = new JLabel("", JLabel.LEFT);
    private JButton button1 = new JButton("Choose File");
    private JButton button2 = new JButton("Check File Content");
    private JButton button3 = new JButton("Continue");
    private String Filepath = null;

    // Get file path String
    public String getFilepath() {
        return Filepath;
    }

    // set file path
    public void setFilepath(String filepath) {
        Filepath = filepath;
    }

    public ChooseFileFrame() {
        // Choose file window setup
        frame.setTitle("CHOOSE A FILE");
        frame.setSize(750, 250);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);

        container.setLayout(null);

        label1.setSize(65, 12);
        label1.setLocation(25, 22);

        label2.setSize(65, 12);
        label2.setLocation(25, 55);

        label3.setSize(580, 12);
        label3.setLocation(100, 22);

        label4.setSize(580, 12);
        label4.setLocation(100, 55);

        button1.setSize(148, 23);
        button1.setLocation(25, 102);

        button2.setSize(148, 23);
        button2.setLocation(25, 132);
        button2.setEnabled(false);

        button3.setSize(148, 23);
        button3.setLocation(25, 162);
        button3.setEnabled(false);

        container.add(label1);
        container.add(label2);
        container.add(label3);
        container.add(label4);
        container.add(button1);
        container.add(button2);
        container.add(button3);

        button1.addActionListener(this);
        button2.addActionListener(this);
        button3.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // To change body of generated methods, choose Tools | Templates.

        if (e.getSource().equals(button1)) {
            // Choose a file button action
            ReadFileUsingJFileChooser choose = new ReadFileUsingJFileChooser();
            try {
                choose.main();
                setFilepath(choose.getFilepath());
                label3.setText(choose.getFilename());
                label4.setText(choose.getFilepath());
                label3.repaint();
                label4.repaint();
                button2.setEnabled(true);
                button3.setEnabled(true);
                button1.setText("Change File");
                frame.repaint();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource().equals(button2)) {
            // Show file contents button action
            FileContent filecontent = new FileContent();
            try {
                filecontent.main(getFilepath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource().equals(button3)) {
            // Continue button action
            Analysis analysis = new Analysis();
            try {
                analysis.setFilepath(getFilepath());
                analysis.main();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
