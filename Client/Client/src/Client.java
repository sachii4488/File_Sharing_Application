import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        final File[] fileToSend = new File[1];

        // Load modern UI theme
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf().toString());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        // Create main frame
        JFrame jFrame = new JFrame("File Transfer Client");
        jFrame.setSize(400, 250);

        // ImageIcon icon = new ImageIcon("logo.png");  // Make sure logo.png is in the correct location
        //jFrame.setIconImage(icon.getImage());

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLayout(new BorderLayout());

        jFrame.getContentPane().setBackground(new Color(255, 255, 255));

        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(106, 190, 199));

        JLabel jlTitle = new JLabel("📂 File Transfer Client");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 22));
        jlTitle.setForeground(Color.WHITE);
        headerPanel.add(jlTitle);
        jFrame.add(headerPanel, BorderLayout.NORTH);


        // File selection and status panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        JLabel jlFileName = new JLabel("📁 Choose a file to send");
        jlFileName.setFont(new Font("Arial", Font.PLAIN, 16));
        jlFileName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton jbChooseFile = new JButton("Choose File");
        jbChooseFile.setAlignmentX(Component.CENTER_ALIGNMENT);
        jbChooseFile.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setDialogTitle("Select File");
            if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                fileToSend[0] = jFileChooser.getSelectedFile();
                jlFileName.setText("Selected: " + fileToSend[0].getName());
            }
        });

        // Send button
        JButton jbSendFile = new JButton("Send File");
        jbSendFile.setAlignmentX(Component.CENTER_ALIGNMENT);
        jbSendFile.addActionListener(e -> {
            if (fileToSend[0] == null) {
                JOptionPane.showMessageDialog(jFrame, "⚠ Please select a file first!", "Warning", JOptionPane.WARNING_MESSAGE);
            } else {
                sendFile(fileToSend[0], jlFileName, jFrame);
            }
        });

        // Adding components
        centerPanel.add(jlFileName);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(jbChooseFile);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        centerPanel.add(jbSendFile);

        jFrame.add(centerPanel, BorderLayout.CENTER);

        // Make frame visible
        jFrame.setVisible(true);
    }


    private static void sendFile(File file, JLabel jlFileName, JFrame jFrame) {
        try (FileInputStream fileInputStream = new FileInputStream(file);
             Socket socket = new Socket("172.20.10.3", 1234);  // Change IP as needed
             DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {

            String filename = file.getName();
            byte[] fileNameBytes = filename.getBytes();
            byte[] fileContentBytes = new byte[(int) file.length()];
            fileInputStream.read(fileContentBytes);

            // Sending file data
            dataOutputStream.writeInt(fileNameBytes.length);
            dataOutputStream.write(fileNameBytes);
            dataOutputStream.writeInt(fileContentBytes.length);
            dataOutputStream.write(fileContentBytes);

            jlFileName.setText("✅ File Sent: " + filename);
            JOptionPane.showMessageDialog(jFrame, "✔ File sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            jlFileName.setText("❌ File Sending Failed");
            JOptionPane.showMessageDialog(jFrame, "⚠ File sending failed!", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
