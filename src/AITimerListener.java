package board;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.locks.ReentrantLock;


class AITimerListener implements ActionListener {
    boolean performed = false;
    private static ReentrantLock lock = new ReentrantLock();
    public AITimerListener() {
    }
    public void actionPerformed(ActionEvent e) {
        if (Play.panel.running)
            System.out.println("Paint running!");
        if (!performed || Play.panel.running)
            return;
        System.out.println("ActionPerformed");
        lock.lock();
        try {
            Operation type = Play.agent.decide(Play.chess_board);
            Play.operate(type);
        } finally {
            lock.unlock();
        }
    }
}