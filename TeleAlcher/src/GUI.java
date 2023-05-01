import org.dreambot.api.Client;
import org.dreambot.api.utilities.Logger;

import javax.swing.*;
import java.awt.*;

public class GUI {
    public static void createGUI(Main main) {
        JFrame frame = new JFrame("Nyan Alch Teler");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(Client.getCanvas());
        frame.setPreferredSize(new Dimension(300, 200));
        frame.getContentPane().setLayout(new BorderLayout());

        JLabel itemToAlchLabel = new JLabel("Item to alch: ");
        JTextField itemToAlchField = new JTextField();
        itemToAlchField.setPreferredSize(new Dimension(100, 20));
        JPanel itemToAlchPanel = new JPanel();
        itemToAlchField.setText(String.valueOf(main.getItemToAlch()));

        itemToAlchPanel.add(itemToAlchLabel);
        itemToAlchPanel.add(itemToAlchField);
        frame.getContentPane().add(itemToAlchPanel, BorderLayout.NORTH);

        itemToAlchField.addActionListener(e -> {
            try {
                main.setItemToAlch(Integer.parseInt(itemToAlchField.getText()));
            } catch (NumberFormatException ex) {
                Logger.log("Invalid item ID");
                main.setItemToAlch(0);
            }
        });

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            main.setIsRunning(true);
            frame.dispose();
        });
        frame.getContentPane().add(startButton, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }
}