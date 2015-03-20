
import java.awt.Container;
import java.io.FileInputStream;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class FileContent extends JFrame {

    private JFrame frame = new JFrame();
    private Container container = frame.getContentPane();
    private JTextArea ta = new JTextArea();

    public void main(String FilePath) throws Exception {
        try {
            Scanner input = new Scanner(new FileInputStream(FilePath));
            String info = "";
            frame.setTitle("Read File");
            frame.setSize(400, 600);
            frame.setLocation(200, 100);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            container.setLayout(null);
            ta.setBounds(0, 0, 400, 600);

            while (input.hasNext()) {
                info += input.nextLine() + "\n";
                ta.setText(info);
                ta.setEditable(false);
                container.add(ta);
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
