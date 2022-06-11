package board;
//panel to show the help page

import javax.swing.*;
import java.awt.*;

public class HelpPanel extends JPanel {
    JLabel help_label;
    public HelpPanel() {
        this.setBounds(8, 90, 440, 440);
        this.setLayout(null);
        this.setBackground(new Color(250, 248, 239));
        help_label = new JLabel();
//        Border blackline = BorderFactory.createLineBorder(Color.red);
//        help_label.setBorder(blackline);
        help_label.setForeground(new Color(119, 110, 101));
        help_label.setBounds(0, 0, 440, 200);
        help_label.setFont(new Font("arial", Font.BOLD, 15));
        help_label.setFocusable(false);
        help_label.setVisible(true);
        String help_message = "<html><body>This is a demo of 2048 game.<br>To play, you need to press your arrow keys to move the tiles.<br> When two tiles with the same number touch, they merge into one!<br><br><br>Have fun!<br><br>For advice, please contact panzr20@mails.tsinghua.edu.cn<br>Reference:https://2048game.com/</body></html>";
        help_label.setText(help_message);
        help_label.setVerticalTextPosition(JLabel.NORTH);
        this.add(help_label);
    }
}
