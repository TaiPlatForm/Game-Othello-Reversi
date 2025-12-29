package model;

import java.util.ArrayList;
import java.util.List;

public class ReversiAI {
    public static final int EMPTY = 0;
    public static final int BLACK = 1;
    public static final int WHITE = 2;

    private int aiPlayer; // AI chơi màu nào
    private int[] bestMove; // Lưu nước đi tốt nhất [row, col]
    private int maxDepth = 5; // Độ sâu tìm kiếm

    // 8 hướng di chuyển
    private final int[] dx = { 0, 0, 1, -1, -1, 1, 1, -1 };
    private final int[] dy = { 1, -1, 0, 0, 1, 1, -1, -1 };

    public ReversiAI(int aiPlayer) {
        this.aiPlayer = aiPlayer;
        this.bestMove = new int[2];
    }

    // MINIMAX VỚI ALPHA-BETA PRUNING
    public int minimax(boolean maxmin, int[][] state, int depth, int player, int alpha, int beta) {
        // CƠ SỞ - base case
        if (depth == 0 || isOver(state)) {
            return heuristic(state);
        }

        // Lấy danh sách các nước đi hợp lệ
        List<int[]> validMoves = getValidMoves(state, player);

        // Nếu không có nước đi hợp lệ, chuyển lượt
        if (validMoves.isEmpty()) {
            return minimax(!maxmin, state, depth - 1, getOpponent(player), alpha, beta);
        }

        // ĐỆ QUY
        if (maxmin == true) { // MAX
            int temp = -999999999;

            // foreach (các node con newState hợp lệ)
            for (int[] move : validMoves) {
                // Tạo newState từ nước đi
                int[][] newState = copyBoard(state);
                makeMove(newState, move[0], move[1], player);

                int value = minimax(false, newState, depth - 1, getOpponent(player), alpha, beta);

                if (value > temp) {
                    temp = value;
                    // Ghi lại node/state đang xét (chỉ ở depth gốc)
                    if (depth == maxDepth) {
                        bestMove[0] = move[0];
                        bestMove[1] = move[1];
                    }
                }
                // ALPHA-BETA: Cập nhật alpha và cắt tỉa
                alpha = Math.max(alpha, temp);
                if (beta <= alpha)
                    break; // Cắt tỉa beta
            }
            return temp;
        }

        if (maxmin == false) { // MIN
            int temp = 999999999;

            // foreach (các node con newState hợp lệ)
            for (int[] move : validMoves) {
                int[][] newState = copyBoard(state);
                makeMove(newState, move[0], move[1], player);

                int value = minimax(true, newState, depth - 1, getOpponent(player), alpha, beta);

                if (value < temp) {
                    temp = value;
                    // Ghi lại node/state đang xét
                }
                // ALPHA-BETA: Cập nhật beta và cắt tỉa
                beta = Math.min(beta, temp);
                if (alpha >= beta)
                    break; // Cắt tỉa alpha (không phải beta)

            }
            return temp;
        }

    }

    // HÀM TÌM NƯỚC ĐI TỐT NHẤT
    public int[] findBestMove(int[][] board) {
        bestMove = new int[2];
        // Gọi minimax với alpha = -∞, beta = +∞
        minimax(true, board, maxDepth, aiPlayer, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return bestMove;
    }

    // HEURISTIC - Đánh giá trạng thái bàn cờ
    private int heuristic(int[][] state) {
        int opponent = getOpponent(aiPlayer);
        int score = 0;

        // Bảng trọng số vị trí (góc quan trọng nhất)
        int[][] positionWeight = {
                { 100, -20, 10, 5, 5, 10, -20, 100 },
                { -20, -50, -2, -2, -2, -2, -50, -20 },
                { 10, -2, 1, 1, 1, 1, -2, 10 },
                { 5, -2, 1, 0, 0, 1, -2, 5 },
                { 5, -2, 1, 0, 0, 1, -2, 5 },
                { 10, -2, 1, 1, 1, 1, -2, 10 },
                { -20, -50, -2, -2, -2, -2, -50, -20 },
                { 100, -20, 10, 5, 5, 10, -20, 100 }
        };

        // Tính điểm dựa trên vị trí
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (state[i][j] == aiPlayer) {
                    score += positionWeight[i][j];
                } else if (state[i][j] == opponent) {
                    score -= positionWeight[i][j];
                }
            }
        }

        // Thêm điểm cho số nước đi có thể (mobility)
        int aiMoves = getValidMoves(state, aiPlayer).size();
        int opponentMoves = getValidMoves(state, opponent).size();
        score += (aiMoves - opponentMoves) * 5;

        return score;
    }

    // isOver - Kiểm tra game kết thúc
    private boolean isOver(int[][] state) {
        // Game kết thúc khi cả 2 người chơi đều không có nước đi
        boolean blackCanMove = !getValidMoves(state, BLACK).isEmpty();
        boolean whiteCanMove = !getValidMoves(state, WHITE).isEmpty();
        return !blackCanMove && !whiteCanMove;
    }

    // getValidMoves - Lấy danh sách nước đi hợp lệ
    private List<int[]> getValidMoves(int[][] state, int player) {
        List<int[]> moves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (isValidMove(state, i, j, player)) {
                    moves.add(new int[] { i, j });
                }
            }
        }
        return moves;
    }

    // Kiểm tra nước đi hợp lệ
    private boolean isValidMove(int[][] state, int row, int col, int player) {
        if (row < 0 || row >= 8 || col < 0 || col >= 8 || state[row][col] != EMPTY) {
            return false;
        }

        int opponent = getOpponent(player);

        for (int i = 0; i < 8; i++) {
            int r = row + dx[i];
            int c = col + dy[i];
            int count = 0;

            while (r >= 0 && r < 8 && c >= 0 && c < 8 && state[r][c] == opponent) {
                r += dx[i];
                c += dy[i];
                count++;
            }

            if (count > 0 && r >= 0 && r < 8 && c >= 0 && c < 8 && state[r][c] == player) {
                return true;
            }
        }

        return false;
    }

    // copyBoard - Sao chép bàn cờ
    private int[][] copyBoard(int[][] board) {
        int[][] copy = new int[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                copy[i][j] = board[i][j];
            }
        }
        return copy;
    }

    // makeMove - Thực hiện nước đi (đặt quân + lật)
    private void makeMove(int[][] state, int row, int col, int player) {
        state[row][col] = player;
        int opponent = getOpponent(player);

        // Lật các quân cờ theo 8 hướng
        for (int i = 0; i < 8; i++) {
            int r = row + dx[i];
            int c = col + dy[i];
            int count = 0;

            while (r >= 0 && r < 8 && c >= 0 && c < 8 && state[r][c] == opponent) {
                r += dx[i];
                c += dy[i];
                count++;
            }

            if (count > 0 && r >= 0 && r < 8 && c >= 0 && c < 8 && state[r][c] == player) {
                int flipR = row + dx[i];
                int flipC = col + dy[i];
                while (flipR != r || flipC != c) {
                    state[flipR][flipC] = player;
                    flipR += dx[i];
                    flipC += dy[i];
                }
            }
        }
    }

    // getOpponent - Lấy đối thủ
    private int getOpponent(int player) {
        return (player == BLACK) ? WHITE : BLACK;
    }

    // Getter/Setter
    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int depth) {
        this.maxDepth = depth;
    }

    public int getAiPlayer() {
        return aiPlayer;
    }

    public void setAiPlayer(int player) {
        this.aiPlayer = player;
    }
}
