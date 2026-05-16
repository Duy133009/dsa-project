<div align="center">

<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
<img src="https://img.shields.io/badge/Swing-GUI-important?style=for-the-badge&logo=java" alt="Swing">
<img src="https://img.shields.io/badge/Algorithm-Backtracking-blueviolet?style=for-the-badge" alt="Backtracking">
<img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="License">

</div>

<br>

<h1 align="center">
  &#9822; Độc Mã Sudoku <br>
  <sup><i>Knight Sudoku</i></sup>
</h1>

<p align="center">
  <b>Knight's Tour</b> &times; <b>Latin Square</b> &mdash; two classic algorithmic challenges fused into one addictive puzzle game.
</p>

<br>

---

## &#128269; Overview

**Knight Sudoku** is a Java desktop game that combines two NP-hard problems:

| Problem | Description |
|---|---|
| &#9822; **Knight's Tour** | A knight must visit all **64 squares** on an 8&times;8 chessboard exactly once, moving in an L-shape. |
| &#128220; **Latin Square** | Each of the **8 rows** and **8 columns** must contain digits **1&rarr;8** exactly once (like Sudoku without sub-grids). |

> **Win condition:** Complete the full Knight's Tour **and** satisfy the Latin Square constraint on all 64 cells.

<br>

---

## &#127918; Screenshots

<p align="center">
  <em><strong>Gameplay — Easy Mode</strong></em><br>
  <sub>Player clicks a cell &rarr; Computer auto-assigns a valid digit 1&dash;8</sub>
</p>

```
  ┌───────┬───────┬───────┬───────┬───────┬───────┬───────┬───────┐
  │  3    │       │  6    │       │  1    │       │  7    │       │
  │       │  5    │       │  2    │       │  8    │       │  4    │
  ├───────┼───────┼───────┼───────┼───────┼───────┼───────┼───────┤
  │       │  6    │       │  1    │       │  7    │       │  3    │
  │  4    │       │  5    │       │  2    │       │  8    │       │
  ├───────┼───────┼───────┼───────┼───────┼───────┼───────┼───────┤
  │  7    │       │  3    │       │  6    │       │  1    │       │
  │       │  8    │       │  4    │       │  5    │       │  2    │
  ├───────┼───────┼───────┼───────┼───────┼───────┼───────┼───────┤
  │       │  1    │       │  7    │       │  3    │       │  6    │
  │  2    │       │  8    │       │  4    │       │  5    │       │
  ├───────┼───────┼───────┼───────┼───────┼───────┼───────┼───────┤
  │  5    │       │  2    │       │  8    │       │  4    │       │
  │       │  7    │       │  3    │       │  6    │       │  1    │
  ├───────┼───────┼───────┼───────┼───────┼───────┼───────┼───────┤
  │       │  3    │       │  6    │       │  1    │       │  7    │
  │  8    │       │  4    │       │  5    │       │  2    │       │
  ├───────┼───────┼───────┼───────┼───────┼───────┼───────┼───────┤
  │  1    │       │  7    │       │  3    │       │  6    │       │
  │       │  2    │       │  8    │       │  4    │       │  5    │
  ├───────┼───────┼───────┼───────┼───────┼───────┼───────┼───────┤
  │       │  4    │       │  5    │       │  2    │       │  8    │
  │  6    │       │  1    │       │  7    │       │  3    │       │
  └───────┴───────┴───────┴───────┴───────┴───────┴───────┴───────┘
```

<br>

---

## &#127922; Game Modes

| | &#128037; Easy Mode | &#128293; Hard Mode |
|---|---|---|
| **Move selection** | Player clicks a cell | Player clicks a cell |
| **Digit entry** | Computer auto-finds a valid digit 1&rarr;8 | Player manually types digit 1&rarr;8 |
| **Conflict check** | Computer ensures no row/column duplicates | Player must reason about Latin Square constraints |
| **Error feedback** | Popup if no digit can fit | Popup if chosen digit conflicts |

<br>

---

## &#128736; Core Algorithms

### 1. Knight's Tour Solver — Backtracking + Warnsdorff

```
┌─────────────────────────────────┐
│           START                 │
│    Pick starting cell (sr,sc)   │
└───────────────┬─────────────────┘
                │
    ┌───────────▼───────────┐
    │ backtrack(b,path,r,c,m) │
    │ Check Latin conflict    │
    │ If conflict → prune     │
    └───────────┬───────────┘
                │
      Mark cell at (r,c)
      If m == 64 → SOLUTION ✓
                │
    ┌───────────▼───────────┐
    │ Compute Warnsdorff      │
    │ degree for 8 neighbors  │
    │ Sort by degree ASC      │
    └───────────┬───────────┘
                │
    ┌───────────▼───────────┐
    │ Recurse each neighbor  │
    │ If success → propagate │
    │ If all fail → UNDO     │
    └───────────────────────┘
```

- **Backtracking:** Depth-first search trying all knight moves; undo on dead ends.
- **Warnsdorff Heuristic:** Prioritize cells with the fewest onward moves &rarr; drastically reduces branching.
- **Latin Square Forward Checking:** Before placing a digit, verify it doesn't already exist in the same row or column &rarr; prune invalid branches early.

### 2. Auto-Digit Assignment (Easy Mode)

A greedy O(8) scan per move: iterate digits 1&rarr;8, pick the first one with no row/column conflict.

### 3. Latin Square Validation

After every move, the game checks:
- All 8 rows contain digits 1&rarr;8 without duplicates
- All 8 columns contain digits 1&rarr;8 without duplicates

Real-time conflict highlighting (red cells) shows violations immediately.

<br>

---

## &#128200; Complexity Analysis

| Approach | Worst-Case | Practical |
|---|---|---|
| Naive backtracking | O(8<sup>64</sup>) | Unusable |
| + Warnsdorff heuristic | O(8<sup>64</sup>) | Much faster |
| + Latin Square pruning | Still exponential | **&lt; 3 seconds** &#9989; |
| Auto-digit (Easy mode) | O(8) per move | Instant |

> The combination of Warnsdorff ordering and Latin Square forward checking makes full 64-move solutions computable in seconds.

<br>

---

## &#128451; Data Structures

```java
moveBoard[8][8]      // int     — stores move numbers 1→64; 0 = empty
digitBoard[8][8]     // int     — stores Sudoku digits 1→8; 0 = empty
preFilled[8][8]      // boolean — marks pre-filled (reserved) cells
lastRow, lastCol     // int     — current knight position
currentNumber        // int     — next move number (1-based)
```

- **Digit mapping:** `digit = (moveNumber - 1) % 8 + 1`
- Knight moves: `DR = {±2, ±2, ±1, ±1}` &times; `DC = {±1, ±1, ±2, ±2}`

<br>

---

## &#128187; GUI Components (Java Swing)

| Component | Description |
|---|---|
| `GridLayout(8,8)` | 8&times;8 board |
| `CellButton` | Custom `JButton` — draws a red dot where the knight stands |
| &#128993; Yellow | Valid knight-move destinations |
| &#129000; Orange | Current knight position |
| &#128308; Red | Latin Square conflict |
| &#128309; Blue | Pre-filled cells (reserved) |
| `statusLabel` | Live move counter |
| Controls | Hoàn tác (Undo), Tự giải (Auto-Solve), Ván mới (New Game) |

<br>

---

## &#128736; Tech Stack

<div align="center">

| Layer | Technology |
|---|---|
| **Language** | Java 17+ |
| **GUI** | Swing (`JFrame`, `JButton`, `GridLayout`) |
| **Concurrency** | `SwingWorker` (background solver) + `javax.swing.Timer` (animation) |
| **Algorithms** | Backtracking, Warnsdorff Heuristic, Latin Square Forward Checking |

</div>

<br>

---

## &#9889; Quick Start

### Prerequisites
- **JDK 17** or later
- Any Java IDE (IntelliJ, Eclipse, VS Code) or terminal

### Compile & Run

```bash
# Clone the repo
git clone https://github.com/Duy133009/dsa-project.git
cd dsa-project

# Compile
javac KnightSudoku.java KnightSudokuGUI.java

# Run
java KnightSudokuGUI
```

### Or open in IDE
Open the `projectDSA` folder as an existing project in your IDE and run `KnightSudokuGUI.main()`.

<br>

---

## &#128194; Project Structure

```
projectDSA/
├── KnightSudoku.java         # Core game logic & solver algorithms
├── KnightSudokuGUI.java      # Swing GUI — board, buttons, event handling
├── PRESENTATION.md           # Presentation script (VI + EN)
├── team_details.html         # Team roles & responsibilities (HTML)
├── README.md                 # You are here
└── .gitignore
```

<br>

---

## &#128101; Team

| Member | Role | Key Responsibility |
|---|---|---|
| **Anh Tuấn** | Main Captain | Project intro, rules, **Backtracking + Warnsdorff** algorithm |
| **Duy** | Supporter 1 | Game modes, **auto-digit algorithm**, undo, event handling |
| **An** | Supporter 2 | **Latin Square validation**, auto-solve, GUI, demo |

> See [`team_details.html`](team_details.html) for detailed role descriptions in English.

<br>

---

## &#128214; License

MIT &mdash; feel free to use, modify, and learn from this project.

<br>

<p align="center">
  <b>Made with &#9749; and &#9822; by Anh Tuấn, Duy &amp; An</b>
</p>
