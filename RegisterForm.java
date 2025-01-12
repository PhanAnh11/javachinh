package cuoikijv;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class RegisterForm extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/test";
    private static final String USER = "root";
    private static final String PASSWORD = "513426";

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField verificationCodeField;
    private JButton registerButton, backButton;
    private JLabel statusLabel;

    public RegisterForm() {
        setTitle("Đăng Ký");
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

        JLabel verificationCodeLabel = new JLabel("Mã xác thực:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(verificationCodeLabel, gbc);

        verificationCodeField = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(verificationCodeField, gbc);

        registerButton = new JButton("Đăng Ký");
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(registerButton, gbc);

        backButton = new JButton("Quay Lại");
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(backButton, gbc);

        statusLabel = new JLabel();
        gbc.gridx = 1;
        gbc.gridy = 5;
        add(statusLabel, gbc);

        // Xử lý sự kiện nút Đăng Ký
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String verificationCode = verificationCodeField.getText();

                if (verificationCode.equals("jvsw2024")) {
                    if (registerUser(username, password)) {
                        statusLabel.setForeground(Color.GREEN);
                        statusLabel.setText("Đăng ký thành công!");
                    } else {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("Đăng ký thất bại. Thử lại sau.");
                    }
                } else {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Mã xác thực không chính xác.");
                }
            }
        });

        // Xử lý sự kiện nút Quay Lại
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
                dispose();
            }
        });
    }

    private boolean registerUser(String username, String password) {
        String sql = "INSERT INTO login (username, password) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            int rowsInserted = preparedStatement.executeUpdate();
            return rowsInserted > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RegisterForm registerForm = new RegisterForm();
            registerForm.setVisible(true);
        });
    }
}
