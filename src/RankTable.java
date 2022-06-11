package board;
//panel to show the rank table page

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;

public class RankTable extends JPanel{
    private int len;
    private int[] rank = new int[6];
    JLabel[] rank_label;
    final private ImageIcon icon;
    final private Image background_image;
    RankTable() {
        Toolkit tk = this.getToolkit();
        URL icon_url = getClass().getResource("/pic/rank_table.jpg");
        icon = new ImageIcon(tk.getImage(icon_url));
        background_image = icon.getImage();
        this.setBounds(8, 120, 440, 410);
        this.setLayout(null);
        for (int i = 1; i <= 5; i++) {

        }
        rank_label = new JLabel[6];
        int start_y = 30, start_x = 130;
        for (int i = 1; i <= 5; i++) {
            rank_label[i] = new JLabel();
            rank_label[i].setForeground(new Color(119, 110, 101));
            rank_label[i].setBounds(start_x, start_y + (i - 1) * 77, 200, 30);
            rank_label[i].setFocusable(false);
            rank_label[i].setVisible(true);
            this.add(rank_label[i]);
        }
        load();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background_image, 0, 0, this.getWidth(), this.getHeight(), this);
        for (int i = 1; i <= len; i++) {
            rank_label[i].setText(String.valueOf(rank[i]));
            rank_label[i].setFont(new Font("arial", Font.BOLD, 25));
        }
        for (int i = len + 1; i <= 5; i++) {
            rank_label[i].setText("None yet");
            rank_label[i].setFont(new Font("arial", Font.BOLD, 20));
        }
    }
    void setup() {
        File dir = new File("config");
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (Exception error) {
                System.out.println("Unknown Error");
            }
        }
        File file = new File("config/config.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception error) {
                System.out.println("Unknown Error");
            }
        }
    }
    void load() {
        setup();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("config/config.txt")));
            String s = reader.readLine();
            String[] number_array = s.split(" ");
            len = number_array.length;
            for (int i = 1; i <= len; i++)
                rank[i] = Integer.parseInt(number_array[i - 1]);
        } catch (IOException e) {
            System.out.println("Error in IO!");
        } catch (Exception e) {
            System.out.println("Error in load!");
            len = 0;
        }
    }
    void save() {
        setup();
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("config/config.txt")));
            for (int i = 1; i <= len; i++) {
                writer.write(String.valueOf(rank[i]));
                writer.write(' ');
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error in IO!");
        }
    }
    int getLen() {
        return len;
    }
    void operate_rank(int point) { // insert a point to the rank table, keep it when it is the top 5
        System.out.printf("Operate: %d\n", point);
        for (int i = 1; i <= len; i++) {
            if (point > rank[i]) {
                int temp = point;
                point = rank[len];
                for (int j = len; j > i; j--) {
                    rank[j] = rank[j - 1];
                }
                rank[i] = temp;
            }
        }
        if (len < 5) {
            rank[++len] = point;
        }
        save();
    }
    int getHighestRank() { // get best score
        if (len == 0) {
            return 0;
        } else {
            return rank[1];
        }
    }
}
