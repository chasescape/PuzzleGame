package escape.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Random;

public class LoginJFrame extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/userinformation";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField captchaField;
    private JLabel captchaLabel;
    private JLabel statusLabel;
    private String generatedCaptcha;

    public LoginJFrame() {
        // 设置窗口标题
        setTitle("登录页面");
        // 设置窗口大小
        setSize(300, 200);
        // 设置窗口关闭操作
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 设置窗口居中
        setLocationRelativeTo(null);
        // 设置布局管理器
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
//        这行代码设置了 gbc 对象的 fill
//        属性为 GridBagConstraints.HORIZONTAL。
//        这意味着当组件的宽度小于其单元格的宽度时，
//        组件将水平扩展以填充其单元格的可用空间。如果单元格的高度大于组件的高度，
//        并且没有设置垂直填充（即 fill 没有设置为 VERTICAL 或 BOTH），
//        那么组件在垂直方向上不会扩展。
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
        captchaField = new JTextField();
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        add(captchaField, gbc);

        captchaLabel = new JLabel();
        captchaLabel.setForeground(Color.BLUE);
        captchaLabel.setFont(new Font("Arial", Font.TYPE1_FONT, 18));
        captchaLabel.setHorizontalAlignment(SwingConstants.CENTER);
        generatedCaptcha = generateCaptcha();
        captchaLabel.setText(generatedCaptcha);
        captchaLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                generatedCaptcha = generateCaptcha();
                captchaLabel.setText(generatedCaptcha);
            }
        });
        gbc.gridx = 2;
        add(captchaLabel, gbc);

        JButton loginButton = new JButton("登录");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(loginButton, gbc);
        loginButton.addActionListener(new LoginButtonListener());

        JButton registerButton = new JButton("注册");
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(registerButton, gbc);
        registerButton.addActionListener(new RegisterButtonListener());

        statusLabel = new JLabel("");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        add(statusLabel, gbc);

        // 显示窗口
        setVisible(true);
    }

    private String generateCaptcha() {
        Random random = new Random();
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            captcha.append((char) (random.nextInt(26) + 'A'));
        }
        return captcha.toString();
    }

    private class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
            new RegisterJFrame();
        }
    }

    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String enteredCaptcha = captchaField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("用户名或密码为空！");
                return;
            }
            if (enteredCaptcha.isEmpty()){
                statusLabel.setText("验证码为空");
                return;
            }
            if (!enteredCaptcha.equalsIgnoreCase(generatedCaptcha)) {
                statusLabel.setText("验证码错误！");
                return;
            }

            if (authenticateUser(username, password)) {
                statusLabel.setText("登录成功！");
                dispose();
                new GameJFrame();
            } else {
                statusLabel.setText("用户名或密码错误！");
            }
        }

        private boolean authenticateUser(String username, String password) {
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT * FROM 用户信息 WHERE uid = ? AND password = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);
                statement.setString(2, password);
                ResultSet resultSet = statement.executeQuery();

                return resultSet.next();
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }
}