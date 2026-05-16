# Độc Mã Sudoku — Kịch bản Thuyết trình

> **Nhóm 3 thành viên:** Anh Tuấn (Main Captain), Duy, An

---

## Phân công

| Thành viên | Vai trò | Nội dung phụ trách |
|---|---|---|
| **Anh Tuấn** | Main Captain | Giới thiệu tổng quan, luật chơi, thuật toán cốt lõi: Backtracking + Warnsdorff |
| **Duy** | Hỗ trợ 1 | Hai chế độ chơi, thuật toán máy tự điền số, undo, xử lý sự kiện |
| **An** | Hỗ trợ 2 | Kiểm tra Latin Square, auto-solve bằng SwingWorker, GUI, demo |

---

## PHIÊN BẢN TIẾNG VIỆT

---

### I. Anh Tuấn — Main Captain (5-7 phút)

#### 1. Giới thiệu dự án
- **Tên dự án:** Độc Mã Sudoku (Knight Sudoku)
- **Ý tưởng:** Kết hợp 2 bài toán cổ điển:
  - **Knight's Tour (Mã đi tuần):** Quân mã trên bàn cờ 8×8 đi qua tất cả 64 ô, mỗi ô đúng 1 lần, di chuyển theo hình chữ L.
  - **Latin Square:** Bảng 8×8, mỗi hàng và mỗi cột chứa đúng các số 1→8, không trùng lặp.
- **Mục tiêu:** Đi hết 64 ô theo nước mã + đảm bảo Latin Square → THẮNG.

#### 2. Luật chơi
- Bàn cờ 8×8, đánh số từ 1 đến 64 theo thứ tự nước đi.
- Số hiển thị trên mỗi ô = `(moveNumber - 1) % 8 + 1` → ra số 1→8.
- Mỗi hàng và mỗi cột phải có đủ 8 số 1→8 (Latin Square).
- Nước đi hợp lệ: quân mã di chuyển hình chữ L `(±2, ±1)` hoặc `(±1, ±2)`.

#### 3. Cấu trúc dữ liệu chính (KnightSudoku.java)
```
moveBoard[8][8]   → lưu số thứ tự nước đi (1→64), 0 = ô trống
digitBoard[8][8]  → lưu chữ số Sudoku (1→8), 0 = ô trống
preFilled[8][8]   → đánh dấu ô đã điền trước (hiện không dùng)
lastRow, lastCol  → vị trí quân mã hiện tại
currentNumber     → số thứ tự nước đi tiếp theo
```

#### 4. Thuật toán cốt lõi: Knight's Tour + Backtracking + Warnsdorff

**Bài toán:** Tìm đường đi cho quân mã qua 64 ô, đồng thời thỏa Latin Square.

**Backtracking (Quay lui):**
- Bắt đầu từ 1 ô, thử tất cả 8 nước đi khả thi.
- Đệ quy đi tiếp. Nếu gặp ngõ cụt → lùi lại (undo) và thử hướng khác.
- Điều kiện dừng: đi hết 64 ô (thành công) hoặc hết tất cả lựa chọn (thất bại).

**Heuristic Warnsdorff (cải tiến):**
- Tại mỗi bước, thay vì thử ngẫu nhiên, ta tính **bậc Warnsdorff** của mỗi ô ứng viên.
- Bậc Warnsdorff = số ô chưa đi mà quân mã có thể nhảy tới từ ô ứng viên đó (có xét ràng buộc Latin Square).
- **Ưu tiên ô có bậc nhỏ nhất** → giảm phân nhánh, tăng tốc độ tìm kiếm.

**Ràng buộc Latin Square trong backtracking:**
- `rowColConflict()`: kiểm tra số dự định điền đã tồn tại trong hàng/cột chưa.
- Nếu có xung đột → bỏ qua nhánh đó (pruning).

```
╔══════════════════════════════════════════════╗
║               BẮT ĐẦU                        ║
║         Chọn ô xuất phát (sr, sc)            ║
║                 │                             ║
║    ┌────────────▼────────────┐               ║
║    │  backtrack(b,path,r,c,m)│               ║
║    │  Kiểm tra Latin Square  │               ║
║    │  Nếu xung đột → false   │               ║
║    └────────────┬────────────┘               ║
║                 │                             ║
║       Đánh dấu ô hiện tại                    ║
║       Nếu m == 64 → return true ✓            ║
║                 │                             ║
║    ┌────────────▼────────────┐               ║
║    │ Tính Warnsdorff Degree   │               ║
║    │ cho 8 ô lân cận          │               ║
║    │ Sắp xếp tăng dần         │               ║
║    └────────────┬────────────┘               ║
║                 │                             ║
║    ┌────────────▼────────────┐               ║
║    │ Duyệt từng ô ứng viên   │               ║
║    │ Gọi đệ quy backtrack()  │               ║
║    │ Nếu true → lan truyền   │               ║
║    └────────────┬────────────┘               ║
║                 │                             ║
║       Nếu tất cả thất bại:                    ║
║       Undo ô hiện tại, return false           ║
╚══════════════════════════════════════════════╝
```

#### 5. Độ phức tạp
- Không có Warnsdorff: O(8^64) — bùng nổ tổ hợp.
- Có Warnsdorff + Latin Square pruning: giảm mạnh, tìm lời giải trong vài giây.
- Kết quả: 64 ô × kiểm tra 8 hướng × ràng buộc Latin → thực tế rất nhanh.

---

### II. Duy — Hỗ trợ 1 (3-4 phút)

#### 1. Hai chế độ chơi

| | Chế độ Dễ | Chế độ Khó |
|---|---|---|
| Chọn ô | Người chơi click | Người chơi click |
| Điền số | **Máy tự tìm** số 1→8 hợp lệ | **Người chơi tự nhập** số 1→8 |
| Lỗi | Báo nếu không tìm được số | Báo nếu trùng hàng/cột |

#### 2. Thuật toán `makeMoveAutoDigit()` (máy tự điền số)

```
Input:  row, col (ô người chơi vừa click)

1. Kiểm tra nước đi có hợp lệ không (knight jump)
2. Duyệt d = 1→8:
     Nếu d chưa xuất hiện trong hàng row VÀ cột col
         → Chọn d, điền vào ô
         → Trả về 0 (thành công)
3. Nếu không tìm được d nào → Trả về 2 (không có số hợp lệ)
```

- `hasDigitConflict(row, col, d)`: kiểm tra số `d` đã có trong cùng hàng hoặc cùng cột chưa.
- Đây là thuật toán **greedy đơn giản** — chọn số đầu tiên hợp lệ.

#### 3. Thuật toán `makeMoveWithDigit()` (người chơi tự nhập)

```
Input:  row, col, digit (1→8 do người chơi nhập)

1. Kiểm tra nước đi hợp lệ (knight jump)
2. Kiểm tra digit có trùng hàng/cột không
     Nếu có → Trả về 2 (báo lỗi cho người chơi)
3. Điền digit vào ô → Trả về 0 (thành công)
```

#### 4. Cơ chế Hoàn tác (Undo)

- Tìm ô có `moveBoard = currentNumber - 1` (không phải preFilled).
- Xóa dữ liệu ô đó, giảm `currentNumber`.
- Tìm lại vị trí quân mã mới (`lastRow`, `lastCol`).
- Giới hạn: không thể undo nếu `currentNumber <= 1`.

#### 5. Xử lý sự kiện người dùng

```
handleClick(row, col):
  - Nếu đang animation → bỏ qua
  - Nếu ô preFilled → bỏ qua
  - Nếu chế độ Dễ:
      gọi makeMoveAutoDigit()
      nếu thành công → highlight nước đi tiếp theo
      nếu thất bại → hiện popup "không tìm được số"
  - Nếu chế độ Khó:
      kiểm tra isValidMove()
      hiện JOptionPane.showInputDialog nhập số 1→8
      kiểm tra số hợp lệ (parseInt, range, conflict)
      gọi makeMoveWithDigit()
```

---

### III. An — Hỗ trợ 2 (3-4 phút)

#### 1. Kiểm tra Latin Square

**`isLatinSquareValid()`:**
- Duyệt từng hàng: kiểm tra có đủ 1→8, không trùng.
- Duyệt từng cột: kiểm tra có đủ 1→8, không trùng.
- Dùng mảng boolean `seen[9]` để đánh dấu.

**`isConflictAt(row, col)`:**
- Kiểm tra 1 ô cụ thể có bị trùng số với ô khác trong cùng hàng/cột không.
- Dùng để tô màu đỏ (CLR_CONFLICT) trên GUI.

```
isLatinSquareValid():
  for mỗi hàng r (0→7):
      boolean seen[9] = {false}
      for mỗi cột c (0→7):
          d = digitBoard[r][c]
          if d == 0 hoặc seen[d] → return false
          seen[d] = true
  for mỗi cột c (0→7):
      boolean seen[9] = {false}
      for mỗi hàng r (0→7):
          d = digitBoard[r][c]
          if d == 0 hoặc seen[d] → return false
          seen[d] = true
  return true ✓
```

#### 2. Auto-Solve (Tự giải)

- Khi người chơi bấm nút "Tự giải":
  - Gọi `KnightSudoku.solveComplete()` trong **SwingWorker** (luồng nền).
  - Không làm treo giao diện (EDT không bị block).
  - Nếu tìm thấy lời giải (`int[64] path`) → `animateSolution(path)`.

**`animateSolution()`:**
- Dùng `javax.swing.Timer` (120ms/bước).
- Mỗi tick: điền 1 ô từ path, cập nhật giao diện.
- Khi hết 64 bước → kiểm tra thắng.

#### 3. Giao diện người dùng (GUI)

| Thành phần | Mô tả |
|---|---|
| `GridLayout(8,8)` | Bàn cờ 8×8 |
| `CellButton` | Custom JButton, vẽ thêm chấm đỏ khi quân mã đang đứng |
| Màu vàng `CLR_HIGHLIGHT` | Các ô nước đi hợp lệ |
| Màu cam `CLR_CURRENT` | Vị trí quân mã |
| Màu đỏ `CLR_CONFLICT` | Ô bị trùng Latin Square |
| `statusLabel` | Hiển thị số nước đã đi |
| Nút "Hoàn tác" | Undo nước đi trước |
| Nút "Tự giải" | Auto-solve animation |
| Nút "Ván mới" | Chọn lại chế độ |

#### 4. Demo & Tổng kết
- Demo chế độ Dễ: click → máy tự điền số.
- Demo chế độ Khó: click → nhập số → kiểm tra.
- Kết luận: Dự án kết hợp thành công 2 thuật toán cổ điển (Backtracking + Latin Square), có GUI trực quan.

---

---

## ENGLISH VERSION

---

### I. Tuấn — Main Captain (5-7 minutes)

#### 1. Project Introduction
- **Project Name:** Knight Sudoku (Độc Mã Sudoku)
- **Core Idea:** A fusion of two classic algorithmic problems:
  - **Knight's Tour:** A knight on an 8×8 board must visit all 64 squares exactly once, moving in an L-shape.
  - **Latin Square:** An 8×8 grid where each row and column contains digits 1→8 exactly once.
- **Goal:** Complete the Knight's Tour while maintaining a valid Latin Square → WIN.

#### 2. Game Rules
- 8×8 board, move numbers from 1 to 64.
- Displayed digit per cell = `(moveNumber - 1) % 8 + 1` → yields 1→8.
- Each row and column must contain all digits 1→8 (Latin Square constraint).
- Valid moves: knight moves in L-shape `(±2, ±1)` or `(±1, ±2)`.

#### 3. Core Data Structures (KnightSudoku.java)
```
moveBoard[8][8]   → stores move numbers (1→64), 0 = empty
digitBoard[8][8]  → stores Sudoku digits (1→8), 0 = empty
preFilled[8][8]   → marks pre-filled cells (currently unused)
lastRow, lastCol  → current knight position
currentNumber     → next move number
```

#### 4. Core Algorithm: Knight's Tour via Backtracking + Warnsdorff

**The Problem:** Find a knight's path covering 64 cells while satisfying the Latin Square constraint.

**Backtracking:**
- Start from a cell, try all 8 possible knight moves.
- Recursively proceed. If dead end → backtrack (undo) and try another direction.
- Base case: all 64 cells filled (success) or all options exhausted (failure).

**Warnsdorff Heuristic (optimization):**
- At each step, compute the **Warnsdorff degree** of each candidate cell.
- Warnsdorff degree = number of unvisited cells reachable from that candidate (with Latin Square constraint applied).
- **Prioritize cells with the smallest degree** → reduces branching, speeds up search.

**Latin Square Pruning:**
- `rowColConflict()`: checks if the prospective digit already exists in the same row or column.
- Conflict → prune that branch immediately.

```
╔══════════════════════════════════════════════╗
║                 START                        ║
║        Pick starting cell (sr, sc)           ║
║                   │                           ║
║    ┌──────────────▼──────────────┐           ║
║    │ backtrack(b,path,r,c,m)     │           ║
║    │ Check Latin Square conflict │           ║
║    │ If conflict → return false  │           ║
║    └──────────────┬──────────────┘           ║
║                   │                           ║
║         Mark current cell                    ║
║         If m == 64 → return true ✓           ║
║                   │                           ║
║    ┌──────────────▼──────────────┐           ║
║    │ Compute Warnsdorff Degree    │           ║
║    │ for 8 neighboring cells     │           ║
║    │ Sort ascending              │           ║
║    └──────────────┬──────────────┘           ║
║                   │                           ║
║    ┌──────────────▼──────────────┐           ║
║    │ Iterate each candidate cell │           ║
║    │ Recursively call backtrack()│           ║
║    │ If true → propagate up      │           ║
║    └──────────────┬──────────────┘           ║
║                   │                           ║
║     If all fail:                             ║
║     Undo current cell, return false          ║
╚══════════════════════════════════════════════╝
```

#### 5. Complexity Analysis
- Without Warnsdorff: O(8^64) — combinatorial explosion.
- With Warnsdorff + Latin Square pruning: significantly reduced, finds solutions in seconds.
- In practice: 64 cells × 8 directions × Latin check → very fast.

---

### II. Duy — Supporter 1 (3-4 minutes)

#### 1. Two Game Modes

| | Easy Mode | Hard Mode |
|---|---|---|
| Move selection | Player clicks | Player clicks |
| Digit entry | **Computer auto-assigns** a valid digit 1→8 | **Player manually inputs** digit 1→8 |
| Error | Alerts if no valid digit found | Alerts if digit conflicts with row/column |

#### 2. `makeMoveAutoDigit()` Algorithm (computer auto-assign)

```
Input:  row, col (cell the player just clicked)

1. Validate the knight move is legal
2. Iterate d = 1→8:
     If d does NOT appear in row AND column
         → Assign d to the cell
         → Return 0 (success)
3. If no valid d found → Return 2 (no valid digit available)
```

- `hasDigitConflict(row, col, d)`: checks if digit `d` already exists in the same row or column.
- This is a **simple greedy algorithm** — picks the first valid digit.

#### 3. `makeMoveWithDigit()` Algorithm (player input)

```
Input:  row, col, digit (1→8 from player)

1. Validate the knight move is legal
2. Check if digit conflicts with row/column
     If yes → Return 2 (alert the player)
3. Assign digit to cell → Return 0 (success)
```

#### 4. Undo Mechanism

- Find the cell where `moveBoard = currentNumber - 1` (non-preFilled).
- Clear that cell's data, decrement `currentNumber`.
- Re-locate the knight's position (`lastRow`, `lastCol`).
- Constraint: cannot undo if `currentNumber <= 1`.

#### 5. User Event Handling

```
handleClick(row, col):
  - If animation running → skip
  - If cell is preFilled → skip
  - If Easy mode:
      call makeMoveAutoDigit()
      if success → highlight valid next moves
      if failure → show popup "no valid digit found"
  - If Hard mode:
      check isValidMove()
      show JOptionPane.showInputDialog for digit 1→8
      validate digit (parseInt, range 1-8, row/col conflict)
      call makeMoveWithDigit()
```

---

### III. An — Supporter 2 (3-4 minutes)

#### 1. Latin Square Validation

**`isLatinSquareValid()`:**
- For each row: verify all digits 1→8 present, no duplicates.
- For each column: verify all digits 1→8 present, no duplicates.
- Uses `boolean seen[9]` array for efficient checking.

**`isConflictAt(row, col)`:**
- Checks if a specific cell conflicts with another cell in its row or column.
- Used to color cells red (CLR_CONFLICT) on the GUI.

```
isLatinSquareValid():
  for each row r (0→7):
      boolean seen[9] = {false}
      for each column c (0→7):
          d = digitBoard[r][c]
          if d == 0 or seen[d] → return false
          seen[d] = true
  for each column c (0→7):
      boolean seen[9] = {false}
      for each row r (0→7):
          d = digitBoard[r][c]
          if d == 0 or seen[d] → return false
          seen[d] = true
  return true ✓
```

#### 2. Auto-Solve Feature

- On "Tự giải" button click:
  - Runs `KnightSudoku.solveComplete()` inside a **SwingWorker** (background thread).
  - EDT (UI thread) is never blocked — game remains responsive.
  - If solution found (`int[64] path`) → `animateSolution(path)`.

**`animateSolution()`:**
- Uses `javax.swing.Timer` (120ms per step).
- Each tick: fills one cell from the solution path, updates the GUI.
- After 64 steps → checks win condition.

#### 3. GUI Overview

| Component | Description |
|---|---|
| `GridLayout(8,8)` | 8×8 chessboard |
| `CellButton` | Custom JButton, draws a red dot when knight is present |
| Yellow `CLR_HIGHLIGHT` | Highlighted valid move cells |
| Orange `CLR_CURRENT` | Current knight position |
| Red `CLR_CONFLICT` | Latin Square conflict cells |
| `statusLabel` | Shows move count |
| "Hoàn tác" button | Undo previous move |
| "Tự giải" button | Auto-solve animation |
| "Ván mới" button | New game with mode selection |

#### 4. Demo & Conclusion
- Demo Easy mode: click → computer auto-assigns digit.
- Demo Hard mode: click → input digit → validation.
- Conclusion: Successfully combines 2 classic algorithms (Backtracking + Latin Square) with an interactive GUI.
