package escape.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegisterJFrame extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/userinformation";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JLabel statusLabel;

    public RegisterJFrame() {
        // 设置窗口标题
        setTitle("注册页面");
        // 设置窗口大小
        setSize(300, 250);
        // 设置窗口关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口居中
        setLocationRelativeTo(null);
        // 设置布局管理器
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 添加组件
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("用户名:"), gbc);
        usernameField = new JTextField();
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(new JLabel("密码:"), gbc);
        passwordField = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(new JLabel("确认密码:"), gbc);
        confirmPasswordField = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(confirmPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(new JLabel("电子邮件:"), gbc);
        emailField = new JTextField();
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(emailField, gbc);

        JButton registerButton = new JButton("注册");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        registerButton.addActionListener(new RegisterButtonListener());
        add(registerButton, gbc);

        JButton cancelButton = new JButton("取消");
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        cancelButton.addActionListener(new CancelButtonListener());
        add(cancelButton, gbc);

        statusLabel = new JLabel("");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        add(statusLabel, gbc);

        setVisible(true);
    }

    private class CancelButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose(); // 关闭注册窗口
            new LoginJFrame(); // 返回登录窗口
        }
    }

    private class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String email = emailField.getText();

            if (!password.equals(confirmPassword)) {
                statusLabel.setText("密码不匹配！");
                return;
            }

            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("用户名或密码为空！");
                return;
            }

            if (!isValidEmail(email)) {
                statusLabel.setText("电子邮件格式无效！");
                return;
            }

            if (registerUser(username, password, email)) {
                statusLabel.setText("注册成功！");
                dispose();
                new LoginJFrame();
            } else {
                statusLabel.setText("注册失败，用户名可能已存在！");
            }
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean registerUser(String username, String password, String email) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // 检查用户名是否已存在
            String checkQuery = "SELECT uid FROM 用户信息 WHERE uid = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setString(1, username);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                // 用户名已存在
                return false;
            }

            // 插入新用户
            String query = "INSERT INTO 用户信息 (uid, password, email) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, email);

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}