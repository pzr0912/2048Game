package board;
// Main Frame, with many actionListeners

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Play extends JFrame{
    static int board_length = 4;
    static int[][] last_number = new int[board_length + 1][board_length + 1];
    static int last_point, best_point = 0;
    static Board chess_board;
    static ChessPanel panel;
    static RankTable rankTable;
    static HelpPanel helpPanel;
    static JLabel title_label, content_label, current_score, best_score, score_label, best_score_label, rank_title;
    static JButton new_game, cancel;
    static int epoch = 0;
    static long pressed_time = 0;
    static boolean AI = false, click_new_game = true;
    static Agent agent;
    static AITimerListener aiTimerListener;
    public Play() {
    }
    static void setup() {
        agent = new Agent();
        Font font = new Font("arial", Font.BOLD, 15);
        JFrame frame = new JFrame();
        frame.setSize(470, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setTitle("Fun with 2048");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(250, 248, 239));
        JMenuBar menuBar = new JMenuBar();
        menuBar.setLayout(null);
        JMenu menu = new JMenu("File");
        menu.setForeground(new Color(119, 110, 101));
        JMenuItem item_load = new JMenuItem("load");
        JMenuItem item_save = new JMenuItem("save");
        JMenuItem item_rank = new JMenuItem("Rank");
        JMenuItem item_play = new JMenuItem("Play");
        JMenuItem item_help = new JMenuItem("Help");
        JMenuItem item_AI = new JMenuItem("AI");
        item_load.setForeground(new Color(119, 110, 101));
        item_save.setForeground(new Color(119, 110, 101));
        item_rank.setForeground(new Color(119, 110, 101));
        item_play.setForeground(new Color(119, 110, 101));
        item_help.setForeground(new Color(119, 110, 101));
        item_AI.setForeground(new Color(119, 110, 101));
        item_play.setPreferredSize(new Dimension(10, item_play.getPreferredSize().height));
        item_rank.setPreferredSize(new Dimension(10, item_rank.getPreferredSize().height));
        item_help.setPreferredSize(new Dimension(10, item_help.getPreferredSize().height));
        item_AI.setPreferredSize(new Dimension(10, item_help.getPreferredSize().height));

        menu.add(item_load);
        menu.add(item_save);
        menuBar.add(menu);
        menuBar.add(item_play);
        menuBar.add(item_rank);
        menuBar.add(item_AI);
        menuBar.add(item_help);
        item_play.setHorizontalAlignment(SwingConstants.LEFT);
        item_rank.setHorizontalAlignment(SwingConstants.LEFT);
        item_help.setHorizontalAlignment(SwingConstants.LEFT);
        item_AI.setHorizontalAlignment(SwingConstants.LEFT);
        frame.setJMenuBar(menuBar);
        rankTable = new RankTable();
        rankTable.setVisible(false);
        frame.add(rankTable);
        best_point = rankTable.getHighestRank();
        helpPanel = new HelpPanel();
        helpPanel.setVisible(false);
        frame.add(helpPanel);

        content_label = new JLabel("<html><body>Join the numbers and get to the 2048 tile!</body></html>");
        content_label.setBounds(8, 50, 350, 40);
        content_label.setFont(new Font("arial", Font.ITALIC, 15));
        content_label.setForeground(new Color(119, 110, 101));
        content_label.setFocusable(false);
        content_label.setVisible(true);
        frame.add(content_label);
        title_label = new JLabel("Fun with 2048");
        title_label.setBounds(8, 0, 200, 50);
        title_label.setFont(new Font("arial", Font.BOLD, 30));
        title_label.setForeground(new Color(119, 110, 101));
        title_label.setFocusable(false);
        title_label.setVisible(true);
        frame.add(title_label);

        score_label = new JLabel("Score", JLabel.CENTER);
        score_label.setForeground(new Color(187, 173, 160));
        score_label.setBounds(210, 0, 70, 30);
        score_label.setFont(new Font("arial", Font.BOLD, 15));
        score_label.setFocusable(false);
        score_label.setVisible(true);
        frame.add(score_label);
        current_score = new JLabel(String.valueOf(0), JLabel.CENTER);
        current_score.setForeground(new Color(187, 173, 160));
        current_score.setBounds(210, 30, 70, 20);
        current_score.setFont(new Font("arial", Font.BOLD, 15));
        current_score.setFocusable(false);
        current_score.setVisible(true);
        frame.add(current_score);

        best_score_label = new JLabel("Best", JLabel.CENTER);
        best_score_label.setForeground(new Color(187, 173, 160));
        best_score_label.setBounds(260, 0, 100, 30);
        best_score_label.setFont(new Font("arial", Font.BOLD, 15));
        best_score_label.setFocusable(false);
        best_score_label.setVisible(true);
        frame.add(best_score_label);
        best_score = new JLabel(String.valueOf(best_point), JLabel.CENTER);
        best_score.setForeground(new Color(187, 173, 160));
        best_score.setBounds(260, 30, 100, 20);
        best_score.setFont(new Font("arial", Font.BOLD, 15));
        best_score.setFocusable(false);
        best_score.setVisible(true);
        frame.add(best_score);

        rank_title = new JLabel("Rank Table");
        rank_title.setForeground(new Color(119, 110, 101));
        rank_title.setBounds(8, 90, 150, 30);
        rank_title.setFont(new Font("arial", Font.BOLD, 20));
        rank_title.setFocusable(false);
        rank_title.setVisible(false);
        frame.add(rank_title);

        panel = new ChessPanel();
        frame.add(panel);
        panel.set_number_link_merged(chess_board.getTrue_number(), chess_board.get_link(), chess_board.get_merged());
        panel.execute();

        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) { // watch the key press event, enable arrow keys to operate the board
                if (AI || !click_new_game)
                    return;
                int keyCode = e.getKeyCode();
                Operation type = switch (keyCode) {
                    case KeyEvent.VK_UP -> Operation.UP;
                    case KeyEvent.VK_DOWN -> Operation.DOWN;
                    case KeyEvent.VK_LEFT -> Operation.LEFT;
                    case KeyEvent.VK_RIGHT -> Operation.RIGHT;
                    default -> null;
                };
                if (type != null)
                    operate(type);
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        // use a file chooser to load and save file
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilter() {
            @Override
            public String getDescription() {
                return "*.txt";
            }

            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".txt");
            }
        });

        item_load.addActionListener(event->{
            int result = chooser.showOpenDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = new File(chooser.getSelectedFile().getPath());
                chess_board.load(file);
                set_score();
                set_last();
                panel.reset();
                panel.set_number_link_merged(chess_board.getTrue_number(), chess_board.get_link(), chess_board.get_merged());
                panel.execute();
                System.out.println("Successfully load");
                chess_board.print();
            }
        });
        item_save.addActionListener(event->{
            int result = chooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = new File(chooser.getSelectedFile().getPath());
                chess_board.save(file);
                rankTable.operate_rank(chess_board.getPoints());
                System.out.println("Successfully saved");
            }
        });

        item_play.addActionListener(event->{
            System.out.println("PLAY");
            if (AI) { // When currently AI plays the game, change it back to human mode
                click_new_game = false;
                reset();
            }
            AI = false;
            aiTimerListener.performed = false;
            panel.setVisible(true);
            rank_title.setVisible(false);
            rankTable.setVisible(false);
            helpPanel.setVisible(false);
            new_game.setEnabled(true);
            cancel.setEnabled(true);
            content_label.setText("<html><body>Join the numbers and get to the 2048 tile!</body></html>");
            item_help.setEnabled(true);
            menu.setEnabled(true);
            item_rank.setEnabled(true);
        });
        item_rank.addActionListener(event->{
            if (rankTable.isVisible()) {
                panel.setVisible(true);
                new_game.setEnabled(true);
                cancel.setEnabled(true);
                rank_title.setVisible(false);
                rankTable.setVisible(false);
                helpPanel.setVisible(false);
            } else {
                panel.setVisible(false);
                new_game.setEnabled(false);
                cancel.setEnabled(false);
                rank_title.setVisible(true);
                rankTable.setVisible(true);
                helpPanel.setVisible(false);
            }
        });
        item_AI.addActionListener(event->{
            if (AI)
                return;
            reset();
            AI = true; // change it to AI mode
            aiTimerListener.performed = true;
            panel.setVisible(true);
            rank_title.setVisible(false);
            rankTable.setVisible(false);
            helpPanel.setVisible(false);
            new_game.setEnabled(true);
            cancel.setEnabled(true);
            new_game.setEnabled(false);
            cancel.setEnabled(false);
            content_label.setText("AI mode. Enjoy!");
            item_help.setEnabled(false);
            menu.setEnabled(false);
            item_rank.setEnabled(false);
        });
        item_help.addActionListener(event->{
            if (helpPanel.isVisible()) {
                panel.setVisible(true);
                new_game.setEnabled(true);
                cancel.setEnabled(true);
                rank_title.setVisible(false);
                rankTable.setVisible(false);
                helpPanel.setVisible(false);
            } else {
                panel.setVisible(false);
                new_game.setEnabled(false);
                cancel.setEnabled(false);
                rank_title.setVisible(false);
                rankTable.setVisible(false);
                helpPanel.setVisible(true);
            }
        });

        new_game = new JButton("New Game");
        new_game.setBackground(new Color(143, 122, 102));
        new_game.setForeground(Color.WHITE);
        new_game.setFont(font);
        new_game.setBounds(350, 20, 100, 30);
        new_game.setVisible(true);
        new_game.setFocusPainted(false);
        new_game.setFocusable(false);
        new_game.setBorder(new RoundBtn(5));
        cancel = new JButton("Cancel");
        cancel.setBackground(new Color(143, 122, 102));
        cancel.setForeground(Color.WHITE);
        cancel.setFont(font);
        cancel.setBounds(350, 50, 100, 30);
        cancel.setVisible(true);
        cancel.setFocusPainted(false);
        cancel.setFocusable(false);
        cancel.setBorder(new RoundBtn(5));
        frame.add(new_game);
        frame.add(cancel);

        new_game.addActionListener(event -> {
            System.out.println("Press new game");
            rankTable.operate_rank(chess_board.getPoints());
            click_new_game = true;
            reset();
        });
        cancel.addActionListener(event -> {
            System.out.println("Press cancel");
            for (int i = 1; i <= board_length; i++) {
                for (int j = 1; j <= board_length; j++) {
                    System.out.printf("%d ", last_number[i][j]);
                }
            }
            System.out.println("\n");
            chess_board.reload(last_number, last_point);
            set_score();
            chess_board.print();
            panel.reset();
            panel.set_number_link_merged(chess_board.getTrue_number(), chess_board.get_link(), chess_board.get_merged());
            panel.execute();
        });
        aiTimerListener = new AITimerListener();
        new Timer(500, aiTimerListener).start(); // AI make a decision in every 500 ms(When AI mode is enabled)

        frame.setVisible(true);
    }

    static void operate(Operation type) { // operate the board according to moving type
        System.out.printf("Operate %s\n", type);
        long current_pressed_time = System.currentTimeMillis();
        if (current_pressed_time - pressed_time < 300) {
            return;
        }
        pressed_time = current_pressed_time;
        set_last();
        chess_board.copy_link(panel.getLink());
        epoch += 1;
        System.out.printf("Epoch: %d\n", epoch);
        boolean can_continue = chess_board.run(type);
        chess_board.print();
        set_score();
        panel.set_number_link_merged(chess_board.getTrue_number(), chess_board.get_link(), chess_board.get_merged());
        panel.execute();
        if (!can_continue) {
            int option = JOptionPane.showConfirmDialog(null, "Game is over.\nStart again?", "Info", JOptionPane.YES_NO_OPTION);
            rankTable.operate_rank(chess_board.getPoints());
            if (option == JOptionPane.OK_OPTION) {
                reset();
            } else if (option == JOptionPane.NO_OPTION) {
                System.exit(0);
            }
        }
    }

    static void set_last() { // save the number and points in last step
        int[][] board_last_number = chess_board.getTrue_number();
        for (int i = 1; i <= board_length; i++) {
            for (int j = 1; j <= board_length; j++) {
                last_number[i][j] = board_last_number[i][j];
            }
        }
        last_point = chess_board.getPoints();
    }

    static void reset() { // reset the board
        System.out.println("RESET");
        chess_board = new Board();
        set_score();
        set_last();
        chess_board.print();
        panel.reset();
        panel.set_number_link_merged(chess_board.getTrue_number(), chess_board.get_link(), chess_board.get_merged());
        panel.execute();
    }

    static void set_score() {
        current_score.setText(String.valueOf(chess_board.getPoints()));
        best_point = Math.max(best_point, chess_board.getPoints());
        best_score.setText(String.valueOf(best_point));
    }

    public static void main(String[] args) {
        chess_board = new Board();
        set_last();
        chess_board.print();
        setup();
    }
}

class RoundBtn implements Border // beautify the button(With round border)
{
    private int r;
    RoundBtn(int r) {
        this.r = r;
    }
    public Insets getBorderInsets(Component c) {
        return new Insets(this.r+1, this.r+1, this.r+2, this.r);
    }
    public boolean isBorderOpaque() {
        return true;
    }
    public void paintBorder(Component c, Graphics g, int x, int y,
                            int width, int height) {
        g.drawRoundRect(x, y, width-1, height-1, r, r);
    }
}
