
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class ReadFileUsingJFileChooser extends JFrame {

    private String filename = null;
    private String filepath = null;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void main() throws Exception {
        try {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                java.io.File file = fileChooser.getSelectedFile();
                setFilepath(file.getAbsolutePath());
                setFilename(file.getName());
            } else {
                System.out.println("No file selected!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
