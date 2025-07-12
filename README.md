# TetrECS - A JavaFX Grid-Based Puzzle Game

**TetrECS** is a JavaFX-based tile placement game inspired by *Tetris*. Unlike traditional Tetris, players can freely place incoming game pieces onto a fixed-size grid using keyboard controls. Strategically arrange blocks to clear lines, maximize scores, and test your planning under pressure.

---

## 🎮 Features

- Built with JavaFX (desktop application)
- Place blocks with precision using keyboard input (WASD + Enter)
- Real-time piece validation to prevent overlaps
- Scoring and line-clearing mechanics
- Sound effects for piece placement and error handling
- Modular architecture using `Game`, `Grid`, `GamePiece`, and `ChallengeScene` classes

---

## 🧩 Controls

| Key      | Action                    |
|----------|---------------------------|
| `W`      | Move piece up             |
| `A`      | Move piece left           |
| `S`      | Move piece down           |
| `D`      | Move piece right          |
| `Enter`  | Place piece on the grid   |

---

## 🧱 How to Play

1. Pieces appear at a given position on the grid.
2. Use the `W`, `A`, `S`, `D` keys to move the piece to your desired location.
3. Press `Enter` to place the piece.
4. Filled lines are cleared automatically and score is awarded.
5. Game ends when no valid placements remain.

---

## 🛠️ Requirements

- Java 17+ (or compatible with JavaFX SDK used)
- JavaFX 17+ (modular or non-modular)
- Maven or Gradle for build (if using dependencies like FXGL or third-party media tools)

---

## 🧪 Running the Game

### Option 1: From an IDE

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/tetrcs-javafx.git
   cd tetrcs-javafx
   ```
