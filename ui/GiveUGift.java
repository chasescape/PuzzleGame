package escape.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GiveUGift {
    public static void main(String[] args) {
        // 创建主窗口
        JFrame frame = new JFrame("hello");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // 创建面板
        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        // 显示窗口
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        //设置成null手动控制组件位置和大小
        panel.setLayout(null);

        // 创建标签
        JLabel userLabel = new JLabel("hi!");
        userLabel.setBounds(10, 20, 100, 25);
        panel.add(userLabel);

        // 创建按钮
        JButton button = new JButton("Click Me");
        button.setBounds(10, 80, 100, 25);
        panel.add(button);

        // 添加按钮点击事件
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userLabel.setText("I luv u!");
            }
        });
    }
}

