package board;
//panel to show the main page

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.*;

public class ChessPanel extends JPanel{
    private static final int TIMER_DELAY = 10;
    static int board_length = 4;
    final private  int[][] number = new int[board_length + 1][board_length + 1];
    final private  int[][] number_next = new int[board_length + 1][board_length + 1];
    final private ImageIcon icon;
    final private Image background_image;
    private static boolean[] enable = new boolean[board_length * board_length * 2 + 1]; // enable stores the current available and busy numbers(for label)
    private int[][] link = new int[board_length + 1][board_length + 1];
    private int[][] link_next = new int[board_length + 1][board_length + 1];
    private int[] merged = new int[board_length * board_length * 2 + 1];
    private Point[] src = new Point[board_length * board_length * 2 + 1];
    private Point[] dst = new Point[board_length * board_length * 2 + 1];
    private Point[] current = new Point[board_length * board_length * 2 + 1];
    private int[] tag = new int[board_length * board_length * 2 + 1];
    private int[] image_width = new int[board_length * board_length * 2 + 1];
    private int[] image_height = new int[board_length * board_length * 2 + 1];
    static boolean running = false;
    public ChessPanel() {
        Toolkit tk = this.getToolkit();
        URL icon_url = getClass().getResource("/pic/panel.png");
        icon = new ImageIcon(tk.getImage(icon_url));
        background_image = icon.getImage();
        for (int i = 1; i <= board_length; i++) {
            for (int j = 1; j <= board_length; j++) {
                number[i][j] = 0;
                link[i][j] = 0;
                link_next[i][j] = 0;
            }
        }
        for (int i = 1; i <= board_length * board_length * 2; i++) {
            enable[i] = true;
            src[i] = new Point(-1, -1);
            dst[i] = new Point(-1, -1);
            current[i] = new Point(-1, -1);
            merged[i] = 0;
            tag[i] = 0;
            image_height[i] = image_width[i] = 100;
        }
        this.setBounds(8, 90, 440, 440);
        this.setLayout(null);
        MyTimerListener myTimerListener = new MyTimerListener(this);
        new Timer(TIMER_DELAY, myTimerListener).start();// painter's time: paint every TIMER_DELAY
    }

    void reset() {
        for (int i = 1; i <= board_length; i++) {
            for (int j = 1; j <= board_length; j++) {
                number[i][j] = 0;
                link[i][j] = 0;
                link_next[i][j] = 0;
            }
        }
        for (int i = 1; i <= board_length * board_length * 2; i++) {
            enable[i] = true;
            src[i] = new Point(-1, -1);
            dst[i] = new Point(-1, -1);
            current[i] = new Point(-1, -1);
            merged[i] = 0;
            tag[i] = 0;
            image_height[i] = image_width[i] = 100;
        }
    }

    public int[][] getLink() {
        return link;
    }

    static public Point get_location(int i, int j) {
        i = board_length + 1 - i;
        int start_x = 8, start_y = 8;
        return new Point(start_x + (j - 1) * 108, start_y + (i - 1) * 108);
    }

    static int get_available_number() {
        for (int i = 1; i <= board_length * board_length * 2; i++) {
            if (enable[i]) {
                enable[i] = false;
                return i;
            }
        }
        return -1;
    }

    public void paintComponent(Graphics g) {
//        System.out.println("PAINT!");
        super.paintComponent(g);
        g.drawImage(background_image, 0, 0, this.getWidth(), this.getHeight(), this);
        for (int i = 1; i <= board_length * board_length * 2; i++) {
            if (current[i].x == -1)
                continue;
            int x = 1, y = 1;
            boolean flag = false;
            for (x = 1; x <= board_length; x++) {
                for (y = 1; y <= board_length; y++) {
                    if (link[x][y] == i) {
                        flag = true;
                        break;
                    }
                }
                if (flag)
                    break;
            }
            Toolkit tk = this.getToolkit();
            URL icon_url = getClass().getResource(String.format("/pic/%d.png", number[x][y]));
            ImageIcon icon_ = new ImageIcon(tk.getImage(icon_url));
            icon_.setImage(icon_.getImage().getScaledInstance(image_width[i], image_height[i], Image.SCALE_DEFAULT));
            g.drawImage(icon_.getImage(), current[i].x + ((100 - image_width[i]) / 2), current[i].y + ((100 - image_height[i]) / 2), this);
        }
        Toolkit.getDefaultToolkit().sync();
    }

    void move_and_merge() { // get the labels that have been moved, merged, added
        for (int i = 1; i <= board_length; i++) {
            for (int j = 1; j <= board_length; j++) {
                if (number_next[i][j] != 0 && link_next[i][j] == 0) { // label that has been added
                    link_next[i][j] = get_available_number();
                }
            }
        }
        for (int i = 1; i <= board_length * board_length * 2; i++) {
            if (!enable[i]) {
                int origin_x = 0, origin_y = 0, current_x = 0, current_y = 0;
                boolean flag = false;
                for (int j = 1; j <= board_length; j++) {
                    for (int k = 1; k <= board_length; k++) {
                        if (link[j][k] == i) {
                            origin_x = j;
                            origin_y = k;
                            flag = true;
                            break;
                        }
                    }
                    if (flag)
                        break;
                }
                flag = false;
                for (int j = 1; j <= board_length; j++) {
                    for (int k = 1; k <= board_length; k++) {
                        if (link_next[j][k] == i) {
                            current_x = j;
                            current_y = k;
                            flag = true;
                            break;
                        }
                    }
                    if (flag)
                        break;
                }
                assert(current_x != 0 || origin_x != 0);
                if (current_x != 0 && origin_x != 0) { //moved
                    src[i] = get_location(origin_x, origin_y);
                    dst[i] = get_location(current_x, current_y);
                    tag[i] = 1;
                } else if (current_x != 0) { // added
                    dst[i] = src[i] = get_location(current_x, current_y);
                    tag[i] = 2;
                } else if (origin_x != 0) { // merged
                    src[i] = get_location(origin_x, origin_y);
                    tag[i] = 3;
                    enable[i] = true;
                }
            } else { // disabled
                tag[i] = 0;
            }
        }
        for (int i = 1; i <= board_length * board_length * 2; i++) {
            if (tag[i] == 3) { // the tile is merged by merged[i]
                dst[i] = dst[merged[i]];
            }
        }
    }

    //Be careful when copy array
    public void set_number_link_merged(int[][] number_, int[][] link_, int[] merged_) {
        for (int i = 1; i <= board_length; i++) {
            for (int j = 1; j <= board_length; j++) {
                number_next[i][j] = number_[i][j];
                link_next[i][j] = link_[i][j];
            }
        }
        for (int i = 1; i <= board_length * board_length * 2; i++)
            merged[i] = merged_[i];
    }

    public void execute() {
        running = true;
    }
    class MyTimerListener implements ActionListener {
        private int board_length = 4;
        private ChessPanel panel;
        int cnt = 0, total = 8, resize = 13;
        int[] size = new int[6];
        public MyTimerListener(ChessPanel panel_) {
            panel = panel_;
            size[1] = size[4] = 110;
            size[2] = size[3] = 120;
            size[5] = 100;
        }
        public void actionPerformed(ActionEvent e) {
            if (!panel.running)
                return;
            cnt += 1;
            if (cnt == 1)
                panel.move_and_merge();
            if (cnt <= total) { // moving
                for (int i = 1; i <= board_length * board_length * 2; i++)
                    panel.current[i].x = panel.current[i].y = -1;
                //calculate current position based on origin,destination and current cnt
                for (int i = 1; i <= board_length * board_length * 2; i++) {
                    if (panel.tag[i] == 0)
                        continue;
                    panel.current[i].x = (panel.dst[i].x * cnt / total) + (panel.src[i].x * (total - cnt) / total);
                    panel.current[i].y = (panel.dst[i].y * cnt / total) + (panel.src[i].y * (total - cnt) / total);
                    if (panel.tag[i] == 2) { // added
                        panel.current[i].x = panel.current[i].y = -1;
                    }
                    if (cnt == total && panel.tag[i] == 3) { // merged
                        panel.current[i].x = panel.current[i].y = -1;
                    }
                }
            } else { // resize
                for (int i = 1; i <= board_length * board_length * 2; i++) {
                    if (panel.tag[i] == 2) { // added
                        panel.current[i] = panel.dst[i];
                        panel.image_width[i] = 20 * (cnt - total);
                        panel.image_height[i] = 20 * (cnt - total);
                    } else if (panel.tag[i] == 3) { // merged
                        int label = panel.merged[i];
                        panel.image_width[label] = size[cnt - total];
                        panel.image_height[label] = size[cnt - total];
                    }
                }
            }
            panel.repaint();
            if (cnt == total)  {
                for (int i = 1; i <= board_length; i++) {
                    for (int j = 1; j <= board_length; j++) {
                        panel.number[i][j] = panel.number_next[i][j];
                        panel.link[i][j] = panel.link_next[i][j];
                    }
                }
            }
            if (cnt == resize) { // update
                panel.running = false;
                System.out.println("All Paint Done!");
                cnt = 0;
            }
        }
    }
}
