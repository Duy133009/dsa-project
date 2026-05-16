import java.util.*;

public class KnightSudoku {
    public static final int N = 8;

    private int[][] moveBoard;      // move number 1-64, 0 if empty
    private int[][] digitBoard;     // sudoku digit 1-8, 0 if empty
    private boolean[][] preFilled;  // pre-filled cells (Easy mode)

    private int lastRow, lastCol;   // position of last move, -1 if none
    private int currentNumber;      // next move number
    private boolean easyMode;       // non-final: may fall back if solver fails

    private static final int[] DR = {-2, -2, -1, -1,  1,  1,  2,  2};
    private static final int[] DC = {-1,  1, -2,  2, -2,  2, -1,  1};

    /**
     * Constructor. Does NOT run the solver — call applyEasyPreFill() from
     * a background thread after construction to keep the EDT responsive.
     */
    public KnightSudoku(boolean easyMode) {
        this.easyMode = easyMode;
        moveBoard  = new int[N][N];
        digitBoard = new int[N][N];
        preFilled  = new boolean[N][N];
        lastRow = lastCol = -1;
        currentNumber = 1;
    }

    /**
     * Called on the EDT after solveComplete() finishes in a SwingWorker.
     * Applies the first 32 moves as pre-filled cells for Easy mode.
     * If path is null (solver failed) we silently fall back to Normal mode.
     */
    public void applyEasyPreFill(int[] path) {
        if (path == null) {
            easyMode = false;
            return;
        }
        for (int i = 0; i < 32; i++) {
            int r = path[i] / N, c = path[i] % N;
            moveBoard[r][c]  = i + 1;
            digitBoard[r][c] = getSudokuDigit(i + 1);
            preFilled[r][c]  = true;
        }
        lastRow = path[31] / N;
        lastCol = path[31] % N;
        currentNumber = 33;
    }

    // ─── solver (static, runs in SwingWorker) ────────────────────────────────

    /**
     * Backtracking + Warnsdorff heuristic + Latin Square forward checking.
     * Returns path[i] = row*N+col for move i+1, or null if unsolvable.
     */
    public static int[] solveComplete() {
        int[] path  = new int[64];
        int[][] tmp = new int[N][N];
        for (int sr = 0; sr < N; sr++) {
            for (int sc = 0; sc < N; sc++) {
                for (int[] row : tmp) Arrays.fill(row, 0);
                Arrays.fill(path, -1);
                if (backtrack(tmp, path, sr, sc, 1)) return path;
            }
        }
        return null;
    }

    private static boolean backtrack(int[][] b, int[] path, int r, int c, int m) {
        if (Thread.currentThread().isInterrupted()) return false;   // allow cancellation
        int d = getSudokuDigit(m);
        if (rowColConflict(b, r, c, d)) return false;

        b[r][c] = m;
        path[m - 1] = r * N + c;
        if (m == 64) return true;

        int nd = getSudokuDigit(m + 1);
        List<int[]> nexts = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int nr = r + DR[i], nc = c + DC[i];
            if (inBounds(nr, nc) && b[nr][nc] == 0 && !rowColConflict(b, nr, nc, nd)) {
                nexts.add(new int[]{nr, nc, wDegree(b, nr, nc, m + 1)});
            }
        }
        nexts.sort(Comparator.comparingInt(x -> x[2]));

        for (int[] nx : nexts) {
            if (backtrack(b, path, nx[0], nx[1], m + 1)) return true;
        }

        b[r][c] = 0;
        path[m - 1] = -1;
        return false;
    }

    /** Warnsdorff degree: count successors of (r,c) for move m+1 that don't violate Latin Square. */
    private static int wDegree(int[][] b, int r, int c, int m) {
        int cnt = 0;
        int nd = getSudokuDigit(m + 1);
        for (int i = 0; i < 8; i++) {
            int nr = r + DR[i], nc = c + DC[i];
            if (inBounds(nr, nc) && b[nr][nc] == 0 && !rowColConflict(b, nr, nc, nd)) cnt++;
        }
        return cnt;
    }

    private static boolean rowColConflict(int[][] b, int r, int c, int d) {
        for (int i = 0; i < N; i++) {
            if (b[r][i] != 0 && getSudokuDigit(b[r][i]) == d) return true;
            if (b[i][c] != 0 && getSudokuDigit(b[i][c]) == d) return true;
        }
        return false;
    }

    private static boolean inBounds(int r, int c) {
        return r >= 0 && r < N && c >= 0 && c < N;
    }

    // ─── digit mapping ───────────────────────────────────────────────────────

    /** Move number 1-64  →  sudoku digit 1-8. */
    public static int getSudokuDigit(int moveNum) {
        return (moveNum - 1) % 8 + 1;
    }

    // ─── game logic ──────────────────────────────────────────────────────────

    public boolean makeMove(int row, int col) {
        if (!inBounds(row, col) || moveBoard[row][col] != 0 || preFilled[row][col]) return false;
        if (!knightJump(lastRow, lastCol, row, col)) return false;
        moveBoard[row][col]  = currentNumber;
        digitBoard[row][col] = getSudokuDigit(currentNumber);
        lastRow = row; lastCol = col;
        currentNumber++;
        return true;
    }

    /** Easy mode: computer auto-assigns a valid digit 1-8 for the move.
     *  @return 0=success, 1=invalid knight move, 2=no valid digit available. */
    public int makeMoveAutoDigit(int row, int col) {
        if (!inBounds(row, col) || moveBoard[row][col] != 0 || preFilled[row][col]) return 1;
        if (!knightJump(lastRow, lastCol, row, col)) return 1;
        int digit = findValidDigit(row, col);
        if (digit == 0) return 2;
        moveBoard[row][col]  = currentNumber;
        digitBoard[row][col] = digit;
        lastRow = row; lastCol = col;
        currentNumber++;
        return 0;
    }

    private int findValidDigit(int row, int col) {
        for (int d = 1; d <= 8; d++) {
            if (!hasDigitConflict(row, col, d)) return d;
        }
        return 0;
    }

    /** Hard mode: player chooses digit 1-8 manually.
     *  @return 0=success, 1=invalid knight move, 2=digit conflicts row/column. */
    public int makeMoveWithDigit(int row, int col, int digit) {
        if (!inBounds(row, col) || moveBoard[row][col] != 0 || preFilled[row][col]) return 1;
        if (!knightJump(lastRow, lastCol, row, col)) return 1;
        if (digit < 1 || digit > 8) return 1;
        if (hasDigitConflict(row, col, digit)) return 2;
        moveBoard[row][col]  = currentNumber;
        digitBoard[row][col] = digit;
        lastRow = row; lastCol = col;
        currentNumber++;
        return 0;
    }

    private boolean hasDigitConflict(int row, int col, int digit) {
        for (int c = 0; c < N; c++)
            if (c != col && digitBoard[row][c] == digit) return true;
        for (int r = 0; r < N; r++)
            if (r != row && digitBoard[r][col] == digit) return true;
        return false;
    }

    public boolean isValidMove(int row, int col) {
        if (!inBounds(row, col) || moveBoard[row][col] != 0 || preFilled[row][col]) return false;
        return knightJump(lastRow, lastCol, row, col);
    }

    private boolean knightJump(int r1, int c1, int r2, int c2) {
        if (r1 < 0) return true;
        int dr = Math.abs(r2 - r1), dc = Math.abs(c2 - c1);
        return (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
    }

    public int[] undoMove() {
        if (currentNumber <= 1) return null;

        int prev = currentNumber - 1;
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                if (moveBoard[r][c] == prev && !preFilled[r][c]) {
                    moveBoard[r][c]  = 0;
                    digitBoard[r][c] = 0;
                    currentNumber = prev;

                    int p2 = prev - 1;
                    lastRow = lastCol = -1;
                    if (p2 > 0) {
                        outer:
                        for (int r2 = 0; r2 < N; r2++)
                            for (int c2 = 0; c2 < N; c2++)
                                if (moveBoard[r2][c2] == p2) { lastRow = r2; lastCol = c2; break outer; }
                    }
                    return new int[]{lastRow, lastCol};
                }
            }
        }
        return null;
    }

    // ─── validation ──────────────────────────────────────────────────────────

    public boolean isGameComplete()  { return currentNumber > 64 && isLatinSquareValid(); }
    public boolean allCellsFilled()  { return currentNumber > 64; }

    public boolean isLatinSquareValid() {
        for (int r = 0; r < N; r++) {
            boolean[] seen = new boolean[9];
            for (int c = 0; c < N; c++) {
                int d = digitBoard[r][c];
                if (d == 0 || seen[d]) return false;
                seen[d] = true;
            }
        }
        for (int c = 0; c < N; c++) {
            boolean[] seen = new boolean[9];
            for (int r = 0; r < N; r++) {
                int d = digitBoard[r][c];
                if (d == 0 || seen[d]) return false;
                seen[d] = true;
            }
        }
        return true;
    }

    public boolean isConflictAt(int row, int col) {
        int d = digitBoard[row][col];
        if (d == 0) return false;
        for (int c = 0; c < N; c++) if (c != col && digitBoard[row][c] == d) return true;
        for (int r = 0; r < N; r++) if (r != row && digitBoard[r][col]  == d) return true;
        return false;
    }

    // ─── getters ─────────────────────────────────────────────────────────────

    public int     getDigitAt(int r, int c)  { return digitBoard[r][c]; }
    public boolean isPreFilled(int r, int c) { return preFilled[r][c]; }
    public int     getCurrentNumber()        { return currentNumber; }
    public int     getLastRow()              { return lastRow; }
    public int     getLastCol()              { return lastCol; }
    public boolean isEasyMode()             { return easyMode; }
    public int     getMovesDone()            { return currentNumber - 1; }
}
