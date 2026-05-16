import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class KnightSudokuGUI extends JFrame {

    private static final int N = KnightSudoku.N;

    private static final Color CLR_LIGHT     = new Color(240, 240, 240);
    private static final Color CLR_DARK      = Color.WHITE;
    private static final Color CLR_HIGHLIGHT = new Color(255, 255, 100);  // yellow — valid moves
    private static final Color CLR_CURRENT   = new Color(255, 200,  80);  // gold   — knight position
    private static final Color CLR_PREFILL   = new Color(173, 216, 230);  // blue   — pre-filled (easy)
    private static final Color CLR_CONFLICT  = new Color(255, 130, 130);  // red    — latin square error

    private static final int[] DR = {-2, -2, -1, -1,  1,  1,  2,  2};
    private static final int[] DC = {-1,  1, -2,  2, -2,  2, -1,  1};

    private final CellButton[][] cells = new CellButton[N][N];
    private KnightSudoku game;
    private JLabel statusLabel;
    private Timer solveTimer;
    private SwingWorker<int[], Void> activeWorker = null;  // track background solver

    // ─── constructor ─────────────────────────────────────────────────────────

    public KnightSudokuGUI(boolean easyMode) {
        game = new KnightSudoku(easyMode);
        buildUI();
        refreshBoard();
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ─── UI construction ─────────────────────────────────────────────────────

    private void buildUI() {
        updateTitle();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (activeWorker != null) activeWorker.cancel(true);
                if (solveTimer != null)   solveTimer.stop();
                dispose();
                System.exit(0);
            }
        });
        setLayout(new BorderLayout(0, 4));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        // Board
        JPanel board = new JPanel(new GridLayout(N, N, 2, 2));
        board.setBackground(Color.DARK_GRAY);
        board.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                cells[r][c] = new CellButton();
                cells[r][c].setFont(new Font("Arial", Font.BOLD, 24));
                cells[r][c].setPreferredSize(new Dimension(72, 72));
                cells[r][c].setFocusPainted(false);
                final int row = r, col = c;
                cells[r][c].addActionListener(e -> handleClick(row, col));
                board.add(cells[r][c]);
            }
        }

        // Controls
        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        ctrl.setBackground(new Color(248, 248, 248));
        ctrl.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        statusLabel = new JLabel("Đã đi: 0 / 64");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        JButton undoBtn  = makeBtn("Hoàn tác", e -> handleUndo());
        JButton solveBtn = makeBtn("Tự giải",  e -> handleAutoSolve());
        JButton newBtn   = makeBtn("Ván mới",  e -> handleNewGame());

        ctrl.add(statusLabel);
        ctrl.add(undoBtn);
        ctrl.add(solveBtn);
        ctrl.add(newBtn);

        add(board, BorderLayout.CENTER);
        add(ctrl,  BorderLayout.SOUTH);
    }

    private JButton makeBtn(String text, ActionListener al) {
        JButton b = new JButton(text);
        b.addActionListener(al);
        return b;
    }

    private void updateTitle() {
        setTitle("Độc Mã Sudoku" + (game.isEasyMode()
            ? "  —  Chế độ Dễ  (máy tự điền số)"
            : "  —  Chế độ Khó  (tự nhập số)"));
    }

    // ─── custom cell button ──────────────────────────────────────────────────

    private static class CellButton extends JButton {
        boolean knightHere = false;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (knightHere) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(160, 0, 0));
                g2.fillOval(getWidth() - 15, 4, 11, 11);
                g2.dispose();
            }
        }
    }

    // ─── display helpers ─────────────────────────────────────────────────────

    private void refreshBoard() {
        int lr = game.getLastRow(), lc = game.getLastCol();
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < N; c++) {
                CellButton btn = cells[r][c];
                int d = game.getDigitAt(r, c);

                btn.setText(d > 0 ? String.valueOf(d) : "");
                btn.knightHere = (r == lr && c == lc);

                Color bg;
                if      (r == lr && c == lc)               bg = CLR_CURRENT;
                else if (game.isPreFilled(r, c))           bg = CLR_PREFILL;
                else if (d > 0 && game.isConflictAt(r, c)) bg = CLR_CONFLICT;
                else                                        bg = (r + c) % 2 == 0 ? CLR_LIGHT : CLR_DARK;

                btn.setBackground(bg);
                btn.setForeground(game.isPreFilled(r, c) ? new Color(0, 70, 140) : Color.BLACK);
                btn.repaint();
            }
        }
        updateStatus();
    }

    /** Refresh board then highlight valid knight moves from (row, col). */
    private void showValidMoves(int row, int col) {
        refreshBoard();
        if (row < 0 || col < 0) return;
        cells[row][col].knightHere = true;
        cells[row][col].setBackground(CLR_CURRENT);
        cells[row][col].repaint();
        for (int i = 0; i < 8; i++) {
            int nr = row + DR[i], nc = col + DC[i];
            if (nr >= 0 && nr < N && nc >= 0 && nc < N && game.isValidMove(nr, nc)) {
                cells[nr][nc].setBackground(CLR_HIGHLIGHT);
                cells[nr][nc].repaint();
            }
        }
    }

    private void updateStatus() {
        int done = game.getMovesDone();
        String next = "";
        if (done < 64 && game.isEasyMode()) {
            int lastR = game.getLastRow(), lastC = game.getLastCol();
            if (lastR >= 0 && lastC >= 0) {
                int lastDigit = game.getDigitAt(lastR, lastC);
                next = "   |   Máy đã điền số: " + lastDigit;
            }
        }
        statusLabel.setText("Đã đi: " + done + " / 64" + next);
    }

    // ─── event handlers ──────────────────────────────────────────────────────

    private void handleClick(int row, int col) {
        if (solveTimer != null && solveTimer.isRunning()) return;
        if (game.isPreFilled(row, col)) return;

        if (game.isEasyMode()) {
            int result = game.makeMoveAutoDigit(row, col);
            if (result == 1) return;
            if (result == 2) {
                JOptionPane.showMessageDialog(this,
                    "Không tìm được số 1-8 hợp lệ cho ô này!\nHãy thử nước đi khác.",
                    "Không thể điền", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showValidMoves(row, col);
            checkWin();
        } else {
            if (!game.isValidMove(row, col)) return;
            String input = JOptionPane.showInputDialog(this,
                "Nhập số từ 1 đến 8:", "Chọn số",
                JOptionPane.QUESTION_MESSAGE);
            if (input == null || input.trim().isEmpty()) return;
            int digit;
            try {
                digit = Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập số từ 1 đến 8!", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (digit < 1 || digit > 8) {
                JOptionPane.showMessageDialog(this,
                    "Số phải nằm trong khoảng 1-8!", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            int result = game.makeMoveWithDigit(row, col, digit);
            if (result == 2) {
                JOptionPane.showMessageDialog(this,
                    "Số " + digit + " đã có trong hàng hoặc cột này!\nHãy chọn số khác.",
                    "Không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (result != 0) return;
            showValidMoves(row, col);
            checkWin();
        }
    }

    private void handleUndo() {
        if (solveTimer != null && solveTimer.isRunning()) {
            solveTimer.stop();
            statusLabel.setText("Đã dừng tự giải — bấm 'Ván mới' để chơi lại.");
            return;
        }
        int[] pos = game.undoMove();
        if (pos != null) {
            showValidMoves(pos[0], pos[1]);
        } else {
            statusLabel.setText("Không có nước nào để hoàn tác.");
        }
    }

    private void handleAutoSolve() {
        if (solveTimer != null && solveTimer.isRunning()) {
            solveTimer.stop();
            statusLabel.setText("Đã dừng tự giải.");
            return;
        }
        statusLabel.setText("Đang tìm lời giải…");
        activeWorker = new SwingWorker<int[], Void>() {
            @Override protected int[] doInBackground() { return KnightSudoku.solveComplete(); }
            @Override protected void done() {
                if (isCancelled()) return;
                activeWorker = null;
                try {
                    int[] path = get();
                    if (path == null) {
                        JOptionPane.showMessageDialog(KnightSudokuGUI.this,
                            "Không tìm được lời giải!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    animateSolution(path);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        };
        activeWorker.execute();
    }

    private void animateSolution(int[] path) {
        game = new KnightSudoku(false);
        setTitle("Độc Mã Sudoku  —  Đang tự giải…");
        refreshBoard();

        final int[] step = {0};
        solveTimer = new Timer(120, null);
        solveTimer.addActionListener(e -> {
            if (step[0] >= 64) {
                solveTimer.stop();
                setTitle("Độc Mã Sudoku  —  Tự giải xong!");
                checkWin();
                return;
            }
            int r = path[step[0]] / N, c = path[step[0]] % N;
            game.makeMove(r, c);
            showValidMoves(r, c);
            step[0]++;
        });
        solveTimer.start();
    }

    private void handleNewGame() {
        if (solveTimer != null && solveTimer.isRunning()) solveTimer.stop();

        String[] opts = {"Dễ  (máy tự điền số)", "Khó  (tự nhập số)"};
        int choice = JOptionPane.showOptionDialog(this,
            "Chọn chế độ cho ván mới:", "Ván mới",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, opts, opts[1]);
        if (choice < 0) return;

        boolean easy = (choice == 0);
        game = new KnightSudoku(easy);
        refreshBoard();
        updateTitle();
    }

    private void checkWin() {
        if (!game.allCellsFilled()) return;
        if (game.isGameComplete()) {
            JOptionPane.showMessageDialog(this,
                "Chúc mừng! Bạn thắng!\n\nKnight's Tour hoàn chỉnh  +  Latin Square hợp lệ!",
                "Thắng!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Bạn đã đi hết 64 ô,\nnhưng có hàng hoặc cột bị trùng số (Latin Square chưa đúng).\n\nHãy thử ván mới!",
                "Chưa thắng", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ─── main ─────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String[] opts = {"Dễ  (máy tự điền số)", "Khó  (tự nhập số)"};
            int choice = JOptionPane.showOptionDialog(null,
                "Chào mừng đến với Độc Mã Sudoku!\n\n" +
                "Luật chơi:\n" +
                "  • Quân mã đi theo nước L như cờ vua\n" +
                "  • Số hiển thị từ 1 đến 8\n" +
                "  • Mỗi hàng & cột phải có đủ các số 1-8  (Latin Square)\n" +
                "  • Đi hết 64 ô + Latin Square đúng = THẮNG\n\n" +
                "Chế độ Dễ: bạn chọn nước đi, máy tự tính và điền số 1-8.\n" +
                "Chế độ Khó: bạn tự nhập số 1-8 sau mỗi nước đi.",
                "Độc Mã Sudoku",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, opts, opts[1]);
            if (choice < 0) choice = 1;
            new KnightSudokuGUI(choice == 0);
        });
    }
}
