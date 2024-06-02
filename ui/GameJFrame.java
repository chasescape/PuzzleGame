package escape.ui;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Random;

public class GameJFrame extends JFrame implements KeyListener, ActionListener {
    // 游戏数据
    private int[][] data;
    private int x = 0; // 空白块的位置
    private int y = 0; // 空白块的位置
    private int step = 0; // 步数计数器
    private int currentBackgroundIndex = 0; // 当前背景索引
    private int timeRemaining = 600; // 倒计时时间，单位为秒（300秒 = 5分钟）
    private int currentDifficulty = 4; // 默认难度为4x4

    // UI组件
    private JLabel timerLabel = new JLabel("剩余时间: 05:00"); // 显示倒计时的标签
    private Timer timer; // 定时器对象

    // 菜单项
    private JMenuItem replayItem = new JMenuItem("重新游戏");
    private JMenu difficultyMenu = new JMenu("难度");
    private JMenuItem easyItem = new JMenuItem("简单 (3x3)");
    private JMenuItem hardItem = new JMenuItem("困难 (4x4)");
    private JMenuItem reLoginItem = new JMenuItem("重新登录");
    private JMenuItem rebackgroundItem = new JMenuItem("更换背景图片");
    private JMenuItem closeItem = new JMenuItem("关闭游戏");
    private JMenuItem accountItem = new JMenuItem("一键通关");
    private JMenuItem aboutItem = new JMenuItem("开发人员信息");

    // 图片路径
    private final String IMAGE_DIR_3X3 = "\\Puzzle\\PuzzleGame\\src\\main\\java\\escape\\photo\\picture2\\";
    private final String IMAGE_DIR_4X4 = "\\Puzzle\\PuzzleGame\\src\\main\\java\\escape\\photo\\picture1\\";
    private final String[] backgroundPaths = {
            "\\Puzzle\\PuzzleGame\\src\\main\\java\\escape\\photo\\background\\1.jpg",
            "\\Puzzle\\PuzzleGame\\src\\main\\java\\escape\\photo\\background\\2.jpg",
            "\\Puzzle\\PuzzleGame\\src\\main\\java\\escape\\photo\\background\\3.jpg"
    };

    // 构造方法，初始化界面
    public GameJFrame() {
        initJFrame();
        initJMenubar();
        initData();
        initImage();
        startTimer();
        this.setVisible(true);
    }

    // 初始化主界面
    private void initJFrame() {
        this.setSize(820, 900);
        this.setTitle("拼图单机1.0");
        this.setAlwaysOnTop(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.addKeyListener(this);
    }

    // 初始化菜单栏
    private void initJMenubar() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu functionJMenu = new JMenu("功能");
        JMenu helpJMenu = new JMenu("外挂");
        JMenu aboutJMenu = new JMenu("关于我们");

        functionJMenu.add(replayItem);
        functionJMenu.add(difficultyMenu);
        functionJMenu.add(reLoginItem);
        functionJMenu.add(closeItem);
        functionJMenu.add(rebackgroundItem);

        difficultyMenu.add(easyItem);
        difficultyMenu.add(hardItem);

        helpJMenu.add(accountItem);

        aboutJMenu.add(aboutItem);

        replayItem.addActionListener(this);
        easyItem.addActionListener(this);
        hardItem.addActionListener(this);
        reLoginItem.addActionListener(this);
        closeItem.addActionListener(this);
        accountItem.addActionListener(this);
        aboutItem.addActionListener(this);
        rebackgroundItem.addActionListener(this);

        jMenuBar.add(functionJMenu);
        jMenuBar.add(helpJMenu);
        jMenuBar.add(aboutJMenu);
        this.setJMenuBar(jMenuBar);
    }

    // 初始化数据
    private void initData() {
        int size = currentDifficulty;
        data = new int[size][size];
        int[] tempArr = new int[size * size];
        for (int i = 0; i < tempArr.length; i++) {
            tempArr[i] = i;
        }

        do {
            shuffleArray(tempArr);
        } while (!isSolvable(tempArr));

        for (int i = 0; i < tempArr.length; i++) {
            if (tempArr[i] == 0) {
                x = i / size;
                y = i % size;
            }
            data[i / size][i % size] = tempArr[i];
        }
    }

    // 随机打乱数组
    private void shuffleArray(int[] array) {
        Random r = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int j = r.nextInt(i + 1);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    // 判断是否可解
    private boolean isSolvable(int[] array) {
        int inversions = 0;
        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if (array[i] > array[j] && array[i] != 0 && array[j] != 0) {
                    inversions++;
                }
            }
        }
        int size = currentDifficulty;
        int row = (findZeroPosition(array) / size) + 1;
        return (inversions % 2 == 0) == (row % 2 != 0);
    }

    // 找到数组中0的位置
    private int findZeroPosition(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0) {
                return i;
            }
        }
        return -1;
    }

    // 初始化图片界面
    private void initImage() {
        this.getContentPane().removeAll();
        if (victory()) {
            showVictoryDialog();
            return;
        }

        JLabel stepCount = new JLabel("步数: " + step);
        stepCount.setBounds(50, 30, 100, 20);
        this.getContentPane().add(stepCount);

        int size = currentDifficulty;
        int tileSize = (currentDifficulty == 3) ? 160 : 178;
        String imageDir = (currentDifficulty == 3) ? IMAGE_DIR_3X3 : IMAGE_DIR_4X4;

        timerLabel.setBounds(150, 30, 200, 20);
        this.getContentPane().add(timerLabel);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int number = data[i][j];
                JLabel jLabel = new JLabel(new ImageIcon(imageDir + number + ".jpg"));
                jLabel.setBounds(tileSize * j + 70, tileSize * i + 85, tileSize, tileSize);
                jLabel.setBorder(new BevelBorder(0));
                this.getContentPane().add(jLabel);
            }
        }

        JLabel backgroundJLabel = new JLabel(new ImageIcon(backgroundPaths[currentBackgroundIndex]));
        backgroundJLabel.setBounds(0, 0, 820, 900);
        this.getContentPane().add(backgroundJLabel);
        this.getContentPane().revalidate();
        this.getContentPane().repaint();
    }

    // 开始计时器
    private void startTimer() {
        //动作监视ActionListener()用于处理定时器触发的事件
        // 创建一个timer对象，每隔1000毫秒触发一次事件
        timer = new Timer(1000, new ActionListener() {
            @Override
            //当定时器触发，调用该方法
            public void actionPerformed(ActionEvent e) {
                //剩余时间减少一秒
                timeRemaining--;
                // 更新倒计时标签的显示内容
                updateTimerLabel();
                //判断剩余时间是否小于等于0
                if (timeRemaining <= 0) {
                    timer.stop(); //计时停止
                    showGameOverDialog();//显示游戏结束对话框
                }
            }
        });
        //启用计时器
        timer.start();
    }

    // 更新倒计时标签的显示内容
    private void updateTimerLabel() {
        //使用整除（/）将剩余毫秒转换为分钟
        int minutes = timeRemaining / 60;
        //使用取模（%）运算符来获取分钟后的秒数
        int seconds = timeRemaining % 60;
        // 使用String.format方法将分钟和秒数格式化为一个字符串
        // "%02d"是一个格式说明符，它表示一个整数，如果不足两位则在前面补0
        // 例如，如果分钟是5，则会被格式化为"05"；如果秒数是7，则保持为"07"
        // 将格式化后的字符串设置为timerLabel的文本内容
        timerLabel.setText(String.format("剩余时间: %02d:%02d", minutes, seconds));
    }

    // 显示胜利对话框
    private void showVictoryDialog() {

        JDialog aboutDialog = new JDialog(this, "开发人员信息", true);

        JOptionPane.showMessageDialog(this, "胜利！", "胜利", JOptionPane.INFORMATION_MESSAGE);
        step = 0;
        timer.stop();
        initData();
        initImage();
        timeRemaining = 600;
        startTimer();
    }

    private void showHelpDialog() {
        JDialog aboutDialog = new JDialog(this, "一键通关", true);
        aboutDialog.setSize(250, 250);
        aboutDialog.setLocationRelativeTo(this);
        JLabel jLabel = new JLabel("扫码一键通关");
        jLabel.setBounds(30, 0, 100, 50);
        aboutDialog.getContentPane().add(jLabel);
        JLabel QRcodeJLabel = new JLabel(new ImageIcon("\\Puzzle\\PuzzleGame\\src\\main\\java\\escape\\photo\\QRcode\\QRcode.jpg"));
        aboutDialog.getContentPane().add(QRcodeJLabel);
        aboutDialog.setVisible(true);
    }

    // 显示游戏结束对话框
    private void showGameOverDialog() {

        JOptionPane.showMessageDialog(this, "时间到了，游戏结束！", "游戏结束", JOptionPane.INFORMATION_MESSAGE);
        step = 0;
        initData();
        initImage();
        timeRemaining = 600;
        startTimer();
    }

    // 显示开发人员信息对话框
    private void showAboutDialog() {
        // 创建一个新的JDialog对象，用于显示开发人员信息
        // 第一个参数是父组件（this，即当前类的实例）
        // 第二个参数是对话框的标题，这里为"开发人员信息"
        // 第三个参数是布尔值，表示对话框是否应该被模态显示（true表示模态显示，此时用户必须先关闭此对话框才能与其他窗口交互）
        JDialog aboutDialog = new JDialog(this, "开发人员信息", true);
        //设置对话框大小
        aboutDialog.setSize(300, 200);
        // 设置对话框的位置，使其相对于父组件居中显示
        aboutDialog.setLocationRelativeTo(this);
        //创建一个JTextArea对象显示开发人员信息
        JTextArea aboutText = new JTextArea("开发人员: \n组长:王艺琏\n组员：彭伟杰、黄明伟、莫泽辉、王靖\n感谢您的使用!");
        //设置JTextArea对象为不可编辑状态
        aboutText.setEditable(false);
        //将JTextArea对象添加到JDialog中，使开发人员信息显示在对话框中
        aboutDialog.add(aboutText);
        //设置JDialog为可见状态
        aboutDialog.setVisible(true);
    }

    // 判断是否胜利
    private boolean victory() {
        int count = 1;
        for (int i = 0; i < currentDifficulty; i++) {
            for (int j = 0; j < currentDifficulty; j++) {
                if (i == currentDifficulty - 1 && j == currentDifficulty - 1) {
                    return data[i][j] == 0;
                } else if (data[i][j] != count++) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //    按下A时触发显示全部图片
        // 从KeyEvent对象中获取按键的编码
        int code = e.getKeyCode();
        // 判断按键编码是否为A
        if (code == 65) {
            // 清除当前窗口内容面板上的所有组件
            this.getContentPane().removeAll();
            String imageDir = (currentDifficulty == 3) ? IMAGE_DIR_3X3 : IMAGE_DIR_4X4;
            // 创建一个新的JLabel对象，并为其设置一个ImageIcon作为图标
            JLabel all = new JLabel(new ImageIcon(imageDir + "all.jpg"));

            // 设置JLabel的位置和大小
            // 这里使用了setBounds方法，该方法需要四个参数：x坐标，y坐标，宽度，高度
            all.setBounds(70, 85, 710, 710);

            // 将新创建的JLabel添加到当前窗口的内容面板上
            this.getContentPane().add(all);
            // 添加背景图片
            JLabel backgroundJLabel = new JLabel(new ImageIcon(backgroundPaths[currentBackgroundIndex]));
            backgroundJLabel.setBounds(0, 0, 820, 900);
            this.getContentPane().add(backgroundJLabel);
            // 强制内容面板重绘，以显示新添加的JLabel
            this.getContentPane().repaint();
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == 37 && y < currentDifficulty - 1) {
            swap(x, y, x, y + 1);
            y++;
        } else if (code == 38 && x < currentDifficulty - 1) {
            swap(x, y, x + 1, y);
            x++;
        } else if (code == 39 && y > 0) {
            swap(x, y, x, y - 1);
            y--;
        } else if (code == 40 && x > 0) {
            swap(x, y, x - 1, y);
            x--;
        }else if (code == 87) { // 按下W键直接进入胜利状态
            data = new int[][]{
                    {1, 2, 3, 4},
                    {5, 6, 7, 8},
                    {9, 10, 11, 12},
                    {13, 14, 15, 0}
            };
            initImage();
        }
        step++;
        initImage();
    }

    // 交换两个位置的值
    private void swap(int x1, int y1, int x2, int y2) {
        int temp = data[x1][y1];
        data[x1][y1] = data[x2][y2];
        data[x2][y2] = temp;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == replayItem) {
            //重置步数
            step = 0;
            //初始化数据
            initData();
            //初始化图片
            initImage();
            //将时间重置为600s
            timeRemaining = 600;
            //调用startTimer()方法重新开始倒计时
            startTimer();
        } else if (source == easyItem) {
            currentDifficulty = 3;
            step = 0;
            initData();
            initImage();
            timeRemaining = 600;
            startTimer();
        } else if (source == hardItem) {
            currentDifficulty = 4;
            step = 0;
            initData();
            initImage();
            timeRemaining = 600;
            startTimer();
        } else if (source == reLoginItem) {
            // 重新登录逻辑
            dispose();
            new LoginJFrame();
        } else if (source == closeItem) {
            //System.exit(0)是Java中用来终止JAVA虚拟机的方法
            System.exit(0);
            //判断是否一键通关
        } else if (source == accountItem) {
            //如果是，则调用showAboutDialog()方法
            showHelpDialog();
            //判断触发事情对象是否是“关于我们”
        } else if (source == aboutItem) {
            //如果是，则调用showAboutDialog()方法
            showAboutDialog();
        } else if (source == rebackgroundItem) {
            currentBackgroundIndex = (currentBackgroundIndex + 1) % backgroundPaths.length;
            initImage();
        }
    }

    public static void main(String[] args) {
        new GameJFrame();
    }
}