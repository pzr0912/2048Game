package board;
import java.util.*;
import java.io.*;
import java.awt.*;
enum Operation {UP, LEFT, DOWN, RIGHT}


public class Board{
    int board_length = 4, margin_length = 1;
    int side_length = board_length + (margin_length << 1); // 6
    int number_length = board_length * board_length; // 16
    int total_length = side_length * side_length; // 36
    private int points;
    private final int[] number = new int[total_length];
    private final int[] simu_number = new int[total_length]; // for simu... functions
    private int[][] true_number = new int[board_length + 1][board_length + 1];
    private int[][] simu_true_number = new int[board_length + 1][board_length + 1];
    private int[][] link = new int[board_length + 1][board_length + 1]; // link connect the number with the label in ChessPanel
    private int[] merged = new int[board_length * board_length * 2 + 1];
    private final Random generator;

    Board() {
        generator = new Random();
        points = 0;
        for (int i = 0; i < total_length; i++) {
            int x = i / side_length, y = i % side_length;
            if (x >= margin_length && x <= (board_length + margin_length - 1) && y >= margin_length && y <= (board_length + margin_length - 1))
                number[i] = simu_number[i] = 0;
            else
                number[i] = simu_number[i] = -1; // margin numbers
        }
        for (int i = 1; i <= board_length; i++) {
            for (int j = 1; j <= board_length; j++) {
                link[i][j] = 0;
            }
        }
        for (int i = 1; i <= board_length * board_length * 2; i++)
            merged[i] = 0;
        int init_1 = generator.nextInt(number_length);
        int init_2 = generator.nextInt(number_length);
        while (init_1 == init_2) {
            init_2 = generator.nextInt(number_length);
        }
        number[getPosition(init_1)] = (generator.nextInt(2) + 1) << 1;
        number[getPosition(init_2)] = (generator.nextInt(2) + 1) << 1;
    }
    Board(int[][] simu_number_) {
        points = 0;
        generator = new Random();
        for (int i = 0; i < total_length; i++) {
            int x = i / side_length, y = i % side_length;
            if (x >= margin_length && x <= (board_length + margin_length - 1) && y >= margin_length && y <= (board_length + margin_length - 1))
                simu_number[i] = 0;
            else
                simu_number[i] = -1; // margin numbers
        }
        for (int i = 1; i <= board_length; i++) {
            for (int j = 1; j <= board_length; j++) {
                simu_number[getPosition(i, j)] = simu_number_[i][j];
            }
        }
    }
    private int transform(Operation type) {
        return switch (type) {
            case UP -> side_length;
            case DOWN -> -side_length;
            case LEFT -> -1;
            case RIGHT -> 1;
        };
    }

    void copy_link(int[][] link_) {
        for (int i = 1; i <= board_length; i++) {
            for (int j = 1; j <= board_length; j++) {
                link[i][j] = link_[i][j];
            }
        }
    }

    int[][] get_link() {
        return link;
    }
    int[] get_merged() {
        return merged;
    }

    private void operate_link(int src, int dst, boolean is_merge) {
        Point point_src = getPoint(src), point_dst = getPoint(dst);
        if (is_merge)
            merged[link[point_dst.x][point_dst.y]] = link[point_src.x][point_src.y];
        link[point_dst.x][point_dst.y] = link[point_src.x][point_src.y];
        link[point_src.x][point_src.y] = 0;
    }

    private boolean move(Operation type, boolean operate) {
        int stride = transform(type);
        boolean has_moved = false;
        for (int i = 0; i < number_length; i++) {
            int index = getPosition(i);
            if ((index + stride >= total_length) || (index + stride < 0))
                continue;
            if (number[index] != 0 && number[index + stride] == 0) {
                if (operate) {
                    number[index + stride] = number[index];
                    number[index] = 0;
                    operate_link(index, index + stride, false);
                }
                has_moved = true;
            }
        }
        return has_moved;
    }

    private boolean merge(Operation type, boolean operate) {
        int stride = transform(type);
        boolean has_merged = false;
        for (int i = 0; i < number_length; i++) {
            int index = getPosition(i);
            if (index + stride >= total_length || index + stride < 0 || number[index] == 0)
                continue;
            if ((number[index] == number[index + stride]) && ((index + stride * 2) >= total_length || ((index + stride * 2) < 0) || (number[index + stride] != number[index + stride * 2]))) {
                if (operate) {
                    number[index + stride] *= 2;
                    number[index] = 0;
                    points += number[index + stride];
                    operate_link(index, index + stride, true);
                }
                has_merged = true;
            }
        }
        return has_merged;
    }

    boolean is_active() {
        boolean move_flag = false, merge_flag = false;
        for (Operation type : Operation.values()) {
            if (move(type, false))
                move_flag = true;
            if (merge(type, false))
                merge_flag = true;
        }
        return move_flag || merge_flag;
    }
    // simu... functions don't actually do the move
     boolean simu_move(Operation type) {
        int stride = transform(type);
        boolean has_move = false;
        for (int i = 0; i < number_length; i++) {
            int index = getPosition(i);
            if ((index + stride >= total_length) || (index + stride < 0))
                continue;
            if (simu_number[index] != 0 && simu_number[index + stride] == 0) {
                simu_number[index + stride] = simu_number[index];
                simu_number[index] = 0;
                has_move = true;
            }
        }
        return has_move;
    }

     boolean simu_merge(Operation type) {
        int stride = transform(type);
        boolean has_merge = false;
        for (int i = 0; i < number_length; i++) {
            int index = getPosition(i);
            if (index + stride >= total_length || index + stride < 0 || simu_number[index] == 0)
                continue;
            if ((simu_number[index] == simu_number[index + stride]) && ((index + stride * 2) >= total_length || ((index + stride * 2) < 0) || (simu_number[index + stride] != simu_number[index + stride * 2]))) {
                simu_number[index + stride] *= 2;
                simu_number[index] = 0;
                has_merge = true;
            }
        }
        return has_merge;
    }

    int[][] simuRun(Operation type) {
        boolean legal_operation = false;
        while (true) {
            boolean has_moved = simu_move(type);
            boolean move_flag = has_moved;
            if (has_moved)
                legal_operation = true;
            while (has_moved) {
                move_flag = true;
                legal_operation = true;
                has_moved = simu_move(type);
            }
            boolean has_merged = simu_merge(type);
            boolean merge_flag = has_merged;
            if (has_merged)
                legal_operation = true;
            while (has_merged) {
                merge_flag = true;
                legal_operation = true;
                has_merged = simu_merge(type);
            }
            if (!move_flag && !merge_flag)
                break;
        }
        return simGetTrue_number();
    }

    boolean run(Operation type) {
        boolean legal_operation = false;
        while (true) {
            boolean has_moved = move(type, true);
            boolean move_flag = has_moved;
            if (has_moved)
                legal_operation = true;
            while (has_moved) {
                move_flag = true;
                legal_operation = true;
                has_moved = move(type, true);
            }
            boolean has_merged = merge(type, true);
            boolean merge_flag = has_merged;
            if (has_merged)
                legal_operation = true;
            while (has_merged) {
                merge_flag = true;
                legal_operation = true;
                has_merged = merge(type, true);
            }
            if (!move_flag && !merge_flag)
                break;
        }
        boolean can_continue;
        int[] index_empty = new int[total_length];
        int total_empty = 0;
        for (int i = 0; i < number_length; i++) {
            int index = getPosition(i);
            if (number[index] == 0) {
                index_empty[total_empty++] = index;
            }
        }
        if (total_empty > 0 && legal_operation) { // has empty block and current operation is legal
            int empty = generator.nextInt(total_empty);
            number[index_empty[empty]] = (generator.nextInt(2) + 1) << 1;
        }
        can_continue = is_active(); // determine whether the game can continue
        return can_continue;
    }
    //Utility functions
    int getPosition(int x, int y) {
        return x * side_length + y + margin_length - 1;
    }
    int getPosition(int x) {
        return getPosition((x / board_length) + 1, (x % board_length) + 1);
    }
    Point getPoint(int x) {
        return new Point(x / side_length, x % side_length);
    }
    int getNumber(int x, int y) {
        return number[getPosition(x, y)];
    }
    int getNumber(int x) {
        return number[getPosition(x)];
    }
    int getPoints() {
        return points;
    }
    int[][] getTrue_number() {
        for (int i = 1; i <= board_length; i++) {
            for (int j = 1; j <= board_length; j++) {
                true_number[i][j] = getNumber(i, j);
            }
        }
        return true_number;
    }

    int[][] simGetTrue_number() {
        for (int i = 1; i <= board_length; i++) {
            for (int j = 1; j <= board_length; j++) {
                simu_true_number[i][j] = simu_number[getPosition(i, j)];
            }
        }
        return simu_true_number;
    }
    void load(File input) {
        int[] temp = new int[total_length];
        for (int i = 0; i < total_length; i++)
            temp[i] = number[i];
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(input)));
            String s = reader.readLine();
            String[] number_array = s.split(" ");
            int len = total_length;
            for (int i = 0; i < len; i++)
                number[i] = Integer.parseInt(number_array[i]);
            s = reader.readLine();
            points = Integer.parseInt(s);
        } catch (IOException e) {
            System.out.println("Error in IO!");
        } catch (Exception e) {
            System.out.println("Error in load!");
            for (int i = 0; i < total_length; i++) // When encounter error, reload the numbers
                number[i] = temp[i];
        }
        for (int i = 1; i <= board_length; i++) {
            for (int j = 1; j <= board_length; j++) {
                link[i][j] = 0;
            }
        }
    }
    void save(File output) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
            for (int i = 0; i < total_length; i++) {
                writer.write(String.valueOf(number[i]));
                writer.write(' ');
            }
            writer.write('\n');
            writer.write(String.valueOf(points));
            writer.close();
        } catch (IOException e) {
            System.out.println("Error in IO!");
        }
    }
    void reload(int[][] new_number, int new_point) {
        for (int i = 1; i <= board_length; i++) {
            for (int j = 1; j <= board_length; j++) {
                number[getPosition(i, j)] = new_number[i][j];
                link[i][j] = 0;
            }
        }
        for (int i = 1; i <= board_length * board_length * 2; i++)
            merged[i] = 0;
        points = new_point;
    }
    //debug
    void print() {
        for (int i = number_length - 1; i >= 0; i -= board_length) {
            for (int j = board_length - 1; j >= 0; j--) {
                System.out.print(padLeft(String.valueOf(number[getPosition(i - j)]), 4, ' '));
                System.out.print(' ');
                if (j == 0) {
                    System.out.print('\n');
                }
            }
        }
        System.out.printf("Point: %d\n\n", getPoints());
    }
    String padLeft(String origin, int length, char ch) {
        while (origin.length() < length) {
            origin = ch + origin;
        }
        return origin;
    }
}