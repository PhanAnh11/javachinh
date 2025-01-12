package cuoikijv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginForm extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/test";
    private static final String USER = "root";
    private static final String PASSWORD = "513426";

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private JLabel statusLabel;
//cung 
    public LoginForm() {
        setTitle("Đăng Nhập");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Mật khẩu:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(passwordField, gbc);

        loginButton = new JButton("Đăng Nhập");
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(loginButton, gbc);

        registerButton = new JButton("Đăng Ký");
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(registerButton, gbc);

        statusLabel = new JLabel();
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(statusLabel, gbc);

        // Xử lý sự kiện nút Đăng Nhập
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (checkLogin(username, password)) {
                    statusLabel.setForeground(Color.GREEN);
                    statusLabel.setText("Đăng nhập thành công!");
                    // Mở giao diện tiếp theo
                    dispose(); // Đóng cửa sổ đăng nhập
                    SwingUtilities.invokeLater(() -> new cuoiki1	()); // Hiển thị giao diện Quản lý giao thông
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Sai tên đăng nhập hoặc mật khẩu.");
                }
            }
        });

        // Xử lý sự kiện nút Đăng Ký
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> new RegisterForm().setVisible(true));
                dispose();
            }
        });
    }

    private boolean checkLogin(String username, String password) {
        String sql = "SELECT * FROM login WHERE username = ? AND password = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}
