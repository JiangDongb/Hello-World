import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    private static boolean isDarkMode = false;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Color Mode Toggle Example");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        frame.add(panel);

        SunMoonButton button = new SunMoonButton();
        button.setBounds(10, 10, 30, 30); // 调整按钮大小为 30x30
        panel.add(button);

        JLabel dateLabel = new JLabel();
        dateLabel.setBounds(250, 10, 150, 20); // 日期标签
        updateDate(dateLabel);
        panel.add(dateLabel);

        JLabel timeLabel = new JLabel();
        timeLabel.setBounds(250, 30, 150, 20); // 时间标签
        updateClock(timeLabel);
        panel.add(timeLabel);

        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateClock(timeLabel);
                updateDate(dateLabel);
            }
        });
        timer.start();

        panel.setBackground(Color.WHITE);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleColorMode(panel, button);
            }
        });

        frame.setVisible(true);
    }

    private static void toggleColorMode(JPanel panel, SunMoonButton button) {
        button.setEnabled(false); // 动画开始时禁用按钮
        Timer colorTimer = new Timer(20, new ActionListener() {
            int step = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                float ratio = step / 50.0f;
                if (isDarkMode) {
                    panel.setBackground(interpolateColor(Color.BLACK, Color.WHITE, ratio));
                } else {
                    panel.setBackground(interpolateColor(Color.WHITE, Color.BLACK, ratio));
                }
                step++;
                if (step > 50) {
                    isDarkMode = !isDarkMode;
                    button.animateShapeChange();
                    ((Timer)e.getSource()).stop();
                    button.setEnabled(true); // 动画结束时启用按钮
                }
            }
        });
        colorTimer.start();
    }

    private static Color interpolateColor(Color start, Color end, float ratio) {
        int red = (int) (start.getRed() * (1 - ratio) + end.getRed() * ratio);
        int green = (int) (start.getGreen() * (1 - ratio) + end.getGreen() * ratio);
        int blue = (int) (start.getBlue() * (1 - ratio) + end.getBlue() * ratio);
        return new Color(red, green, blue);
    }

    private static void updateClock(JLabel label) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        label.setText(sdf.format(new Date()));
    }

    private static void updateDate(JLabel label) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        label.setText(sdf.format(new Date()));
    }
}

class SunMoonButton extends JButton {
    private boolean isSun = true;
    private final ImageIcon sunIcon;
    private final ImageIcon moonIcon;
    private Timer animationTimer;

    public SunMoonButton() {
        sunIcon = new ImageIcon(new ImageIcon("path/to/your/sun.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        moonIcon = new ImageIcon(new ImageIcon("path/to/your/moon.png").getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH));
        setIcon(sunIcon);
    }

    public void animateShapeChange() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationTimer = new Timer(20, new ActionListener() {
            int step = 0;
            int totalSteps = 50;

            @Override
            public void actionPerformed(ActionEvent e) {
                step++;
                float ratio = step / (float) totalSteps;
                BufferedImage combinedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = combinedImage.createGraphics();
                // 设置透明背景
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.0f));
                g2d.fillRect(0, 0, getWidth(), getHeight());

                if (isSun) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - ratio));
                    g2d.drawImage(sunIcon.getImage(), 0, 0, getWidth(), getHeight(), null);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ratio));
                    g2d.drawImage(moonIcon.getImage(), 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f - ratio));
                    g2d.drawImage(moonIcon.getImage(), 0, 0, getWidth(), getHeight(), null);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ratio));
                    g2d.drawImage(sunIcon.getImage(), 0, 0, getWidth(), getHeight(), null);
                }
                g2d.dispose();
                setIcon(new ImageIcon(combinedImage));

                if (step >= totalSteps) {
                    isSun = !isSun;
                    setIcon(isSun ? sunIcon : moonIcon);
                    animationTimer.stop();
                }
            }
        });
        animationTimer.start();
    }
}

