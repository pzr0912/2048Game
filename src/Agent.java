package board;
//AI to play 2048

public class Agent {
    static int board_length = 4;
    static int Max_Depth = 3;
    class SimuBoard { // a board that simulate Class Board, but enabled with all kinds of utility functions
        double EmptyScore = 1, MergeScore = 2, SumScore = 0.01, MonoScore = 10, FixedScore = 5;
        int[][] board;
        SimuBoard(int[][] board_) {
            board = new int[board_length + 1][board_length + 1];
            for (int i = 1; i <= board_length; i++) {
                for (int j = 1; j <= board_length; j++) {
                    board[i][j] = board_[i][j];
                }
            }
        }
        int sumRow(int index) {
            int sum = 0;
            for (int i = 1; i <= board_length; i++) {
                sum += board[index][i];
            }
            return sum;
        }
        int sumColumn(int index) {
            int sum = 0;
            for (int i = 1; i <= board_length; i++) {
                sum += board[i][index];
            }
            return sum;
        }
        int emptyCellsInRow(int row) {
            int sum = 0;
            for (int i = 1; i <= board_length; i++) {
                if (board[row][i] == 0)
                    sum += 1;
            }
            return sum;
        }
        int emptyCellsInColumn(int column) {
            int sum = 0;
            for (int i = 1; i <= board_length; i++) {
                if (board[i][column] == 0)
                    sum += 1;
            }
            return sum;
        }
        int mergeInRow(int row) { // potential merges in a row
            int sum = 0;
            for (int i = 1; i < board_length; i++) {
                if (board[row][i] == board[row][i + 1])
                    sum += 1;
            }
            return sum;
        }
        int mergeInColumn(int column) { // potential merges in a column
            int sum = 0;
            for (int i = 1; i < board_length; i++) {
                if (board[i][column] == board[i + 1][column])
                    sum += 1;
            }
            return sum;
        }
        int monoInColumn(int column) { // measure the monotony in a column
            int sum = 0;
            boolean flag_1 = false, flag_2 = false;
            if ((board[1][column] - board[2][column]) * (board[2][column] - board[3][column]) >= 0) {
                sum = 2;
                flag_1 = true;
            }
            if ((board[2][column] - board[3][column]) * (board[3][column] - board[4][column]) >= 0) {
                sum = 2;
                flag_2 = true;
            }
            if (flag_1 && flag_2) {
                sum = 3;
            }
            return sum;
        }
        int monoInRow(int row) { // measure the monotony in a row
            int sum = 0;
            boolean flag_1 = false, flag_2 = false;
            if ((board[row][1] - board[row][2]) * (board[row][2] - board[row][3]) >= 0) {
                sum = 2;
                flag_1 = true;
            }
            if ((board[row][2] - board[row][3]) * (board[row][3] - board[row][4]) >= 0) {
                sum = 2;
                flag_2 = true;
            }
            if (flag_1 && flag_2) {
                sum = 4;
            }
            return sum;
        }
        double finalScore() {
            double sum = 0;
            for (int row = 1; row <= board_length; row++) {
                sum = sum + EmptyScore * emptyCellsInRow(row)
                          + MergeScore * mergeInRow(row)
                          + MonoScore * monoInRow(row)
                          + SumScore * sumRow(row);
            }
            for (int column = 1; column <= board_length; column++) {
                sum = sum + EmptyScore * emptyCellsInColumn(column)
                          + MergeScore * mergeInColumn(column)
                          + MonoScore * monoInColumn(column)
                          + SumScore * sumColumn(column);
            }
            return sum;
        }
        boolean equal(SimuBoard other) {
            for (int i = 1; i <= board_length; i++) {
                for (int j = 1; j <= board_length; j++) {
                    if (board[i][j] != other.board[i][j])
                        return false;
                }
            }
            return true;
        }
    }
    Agent() {}
    Operation decide(Board board) {
        double best_score = 0;
        Operation best_move = Operation.UP;
        for (Operation move: Operation.values()) {
            double score = calculateScore(board, move);
            System.out.println(move);
            if (score > best_score) {
                best_score = score;
                best_move = move;
            }
        }
        return best_move;
    }
    double calculateScore(Board board_, Operation move) {
        SimuBoard board = new SimuBoard(board_.getTrue_number());
        Board operate_board = new Board(board_.getTrue_number());
        SimuBoard next_board = new SimuBoard(operate_board.simuRun(move));
        if (next_board.equal(board)) {
            System.out.println("***");
            return 0;
        }
        return board.FixedScore + generateScore(next_board, 0);
    }
    double generateScore(SimuBoard board, int depth) {
        if (depth >= Max_Depth)
            return board.finalScore();
        double score = 0;
        for (int i = 1; i <= board_length; i++) {
            for (int j = 1; j <= board_length; j++) {
                if (board.board[i][j] == 0) {
                    SimuBoard new_board_2 = new SimuBoard(board.board);
                    new_board_2.board[i][j] = 2;
                    double score_2 = calculateMoveScore(new_board_2, depth);
                    score += 0.5 * score_2;
                    SimuBoard new_board_4 = new SimuBoard(board.board);
                    new_board_4.board[i][j] = 4;
                    double score_4 = calculateMoveScore(new_board_4, depth);
                    score += 0.5 * score_4;
                }
            }
        }
        return score;
    }
    double calculateMoveScore(SimuBoard board, int depth) {
        double best_score = 0;
        for (Operation type: Operation.values()) {
            Board simu_board = new Board(board.board);
            SimuBoard new_board = new SimuBoard(simu_board.simuRun(type));
            if (!new_board.equal(board)) {
                double score = generateScore(new_board, depth + 1);
                best_score = Math.max(best_score, score);
            }
        }
        return best_score;
    }
}
