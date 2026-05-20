# 🎤 Script Thuyết Trình — Duy (Supporter 1)

> **Thời lượng:** 3 – 4 phút  
> **Vai trò:** Hỗ trợ 1 — trình bày sau Thanh Tuấn (Main Captain)  
> **Nội dung:** 2 chế độ chơi, thuật toán máy tự điền số, undo, xử lý sự kiện

---

## 🇻🇳 LỜI NÓI TỪNG PHẦN (dễ học thuộc)

---

### 🟢 MỞ ĐẦU — nhận bàn giao từ Thanh Tuấn

> *Thanh Tuấn vừa nói xong phần Backtracking + Warnsdorff xong, giờ Duy đứng lên:*

**Duy nói:**

> *"Cảm ơn Thanh Tuấn đã trình bày rất kỹ về thuật toán backtracking và heuristic Warnsdorff. Bây giờ mình — Duy — sẽ đi vào phần tiếp theo: cách mà trò chơi vận hành khi người dùng tương tác, cụ thể là **hai chế độ chơi**, cách máy tính tự động điền số, cơ chế **hoàn tác**, và toàn bộ luồng **xử lý sự kiện** phía sau mỗi lần người chơi click chuột."*

---

### 🟡 PHẦN 1 — Hai chế độ chơi (30 giây)

> *Chiếu bảng so sánh lên màn hình, vừa chỉ vừa nói:*

**Duy nói:**

> *"Trò chơi của tụi mình có **2 chế độ**: **Dễ** và **Khó**."*
>
> *"Điểm chung là ở cả 2 chế độ, **người chơi đều tự chọn nước đi** — mình click vô ô mình muốn quân mã nhảy tới. Cái khác nằm ở chỗ **điền số**."*
>
> *"Ở chế độ **Dễ**, sau khi mình click, **máy tính sẽ tự động tìm và điền** một con số từ 1 đến 8, miễn là số đó không bị trùng trong hàng và cột hiện tại. Máy lo hết, mình chỉ cần lo chọn nước đi đúng."*
>
> *"Còn chế độ **Khó** thì ngược lại — sau khi click, một cái popup hiện ra, mình phải **tự gõ số 1→8 từ bàn phím**. Mình phải tự suy nghĩ xem số nào mới không vi phạm Latin Square. Sai là bị báo lỗi liền."*

---

### 🟠 PHẦN 2 — Thuật toán auto-digit (máy tự điền số) (1 phút)

> *Chiếu code hoặc pseudo-code:*

**Duy nói:**

> *"Bây giờ mình đi sâu hơn vô thuật toán phía sau chế độ Dễ — cái hàm tên là `makeMoveAutoDigit`."*
>
> *"Cách nó hoạt động rất đơn giản. Khi người chơi click 1 ô, đầu tiên nó kiểm tra nước đi đó có đúng luật quân mã không — tức là đi theo hình chữ L. Nếu không đúng thì thôi, không làm gì hết."*
>
> *"Nếu đúng rồi, nó chạy 1 vòng lặp từ **số 1 đến số 8**. Với mỗi số, nó kiểm tra: số này đã xuất hiện trong cùng hàng hoặc cùng cột chưa? Nếu chưa — thì chọn luôn, điền vô ô đó. Còn nếu tới số 8 luôn rồi mà vẫn không tìm ra số nào hợp lệ — thì báo lỗi cho người chơi, kiểu 'không điền được đâu, chọn ô khác đi'."*
>
> *"Đây là 1 thuật toán **greedy** — tham lam, chọn số đầu tiên thỏa điều kiện. Độ phức tạp chỉ là **O(8)** cho mỗi nước đi, tức là duyệt tối đa 8 lần. Gần như tức thì."*

---

### 🔴 PHẦN 3 — Thuật toán nhập tay (Hard mode) (30 giây)

**Duy nói:**

> *"Còn `makeMoveWithDigit` cho chế độ Khó cũng tương tự, nhưng thay vì máy tự tìm số, thì **người chơi tự nhập số từ bàn phím** thông qua 1 hộp thoại `JOptionPane`."*
>
> *"Máy sẽ kiểm tra 3 thứ: một là số nhập vô có đúng định dạng không, hai là có nằm trong khoảng 1→8 không, ba là có bị trùng hàng trùng cột không. Nếu vi phạm cái nào là báo lỗi cái đó ngay."*

---

### 🟣 PHẦN 4 — Cơ chế Hoàn tác / Undo (30 giây)

**Duy nói:**

> *"Một tính năng quan trọng nữa là **Undo — Hoàn tác**. Khi người chơi bấm nút 'Hoàn tác', chương trình sẽ tìm ô có số thứ tự nước đi gần nhất — tức là `currentNumber - 1`, xóa dữ liệu trong ô đó, rồi dò lại vị trí hiện tại của quân mã."*
>
> *"Lưu ý là không thể undo nếu chưa đi nước nào — tức `currentNumber <= 1` — và nếu đang chạy animation auto-solve thì bấm Hoàn tác sẽ dừng animation luôn."*

---

### 🔵 PHẦN 5 — Luồng xử lý sự kiện (30 giây)

> *Chiếu sơ đồ hoặc pseudo-code `handleClick`:*

**Duy nói:**

> *"Cuối cùng là tổng quan luồng xử lý khi người chơi click 1 ô — hàm `handleClick`. Có 3 lớp kiểm tra:"*
>
> *"**Thứ nhất**: nếu đang chạy animation auto-solve thì bỏ qua, không cho click đè."*
> *"**Thứ hai**: nếu chế độ Dễ — gọi `makeMoveAutoDigit`, thành công thì tô vàng các nước đi tiếp theo."*
> *"**Thứ ba**: nếu chế độ Khó — kiểm tra nước đi hợp lệ, hiện popup cho nhập số, validate số đó, rồi gọi `makeMoveWithDigit`."*
>
> *"Và sau mỗi nước đi thành công, đều gọi `checkWin` để kiểm tra xem thắng chưa."*

---

### 🟢 KẾT THÚC — chuyển cho An

**Duy nói:**

> *"Đó là toàn bộ phần của mình về 2 chế độ chơi và luồng tương tác người dùng. Tiếp theo, mình xin mời bạn **An** lên trình bày về phần kiểm tra Latin Square, tính năng tự giải, và demo trực tiếp."*

---

---

## 🇬🇧 ENGLISH VERSION

---

### 🟢 OPENING — handover from Thanh Tuấn

> *"Thank you Thanh Tuấn for the detailed explanation of backtracking and the Warnsdorff heuristic. Now I — Duy — will continue with how the game operates from the user's perspective: our **two game modes**, the **auto-digit algorithm**, the **undo mechanism**, and the **event handling flow** behind every mouse click."*

---

### 🟡 PART 1 — Two Game Modes

> *"Our game has **two modes**: **Easy** and **Hard**."*
>
> *"In both modes, **the player chooses the move** — they click the cell where they want the knight to jump. The difference is in **who picks the digit**."*
>
> *"In **Easy mode**, after you click, the **computer automatically finds and assigns** a digit from 1 to 8 — as long as it doesn't conflict with the current row and column. The computer handles everything; you just focus on choosing the right knight move."*
>
> *"In **Hard mode**, after clicking, a popup dialog appears and **you have to type a digit 1→8 yourself**. You must think about which number won't violate the Latin Square. If you pick a conflicting digit, you get an error immediately."*

---

### 🟠 PART 2 — Auto-Digit Algorithm

> *"Let me walk you through the auto-digit function — `makeMoveAutoDigit`."*
>
> *"Here's how it works. When the player clicks a cell, it first validates the knight move — does it follow the L-shape pattern? If not, nothing happens."*
>
> *"If the move is valid, it runs a loop from **1 to 8**. For each digit, it checks: has this digit already appeared in the same row or column? If not — it picks it immediately and assigns it to the cell. If it reaches 8 and still can't find a valid digit — it shows an error popup telling the player to try a different cell."*
>
> *"This is a **greedy algorithm** — it picks the first digit that satisfies the constraint. The time complexity is just **O(8)** per move — essentially instant."*

---

### 🔴 PART 3 — Manual-Digit Algorithm

> *"The `makeMoveWithDigit` function for Hard mode is similar, but instead of the computer finding the digit, **the player types it through a `JOptionPane` input dialog**."*
>
> *"The computer then validates three things: one, is the input a valid integer? Two, is it in the range 1→8? Three, does it conflict with the row or column? If any check fails, the player gets an error immediately."*

---

### 🟣 PART 4 — Undo Mechanism

> *"Another important feature is **Undo**. When the player clicks the Undo button, the program finds the cell with the most recent move number — that's `currentNumber - 1` — clears its data, decrements the counter, and re-locates the knight's position."*
>
> *"You can't undo if no moves have been made — when `currentNumber <= 1` — and if the auto-solve animation is running, pressing Undo will stop the animation."*

---

### 🔵 PART 5 — Event Handling Flow

> *"Finally, here's the overall flow when the player clicks a cell — the `handleClick` function. There are three layers of checks:"*
>
> *"**First**: if an auto-solve animation is running, ignore the click."*
> *"**Second**: if we're in Easy mode — call `makeMoveAutoDigit`, and if successful, highlight the valid next moves."*
> *"**Third**: if we're in Hard mode — validate the knight move, show the input popup, validate the digit, then call `makeMoveWithDigit`."*
>
> *"After every successful move, `checkWin` is called to see if the player has won."*

---

### 🟢 CLOSING — handover to An

> *"That covers everything about our two game modes and the user interaction flow. Next, I'd like to invite **An** to present the Latin Square validation, the auto-solve feature, and a live demo. Over to you, An!"*

---

---

## 📋 TÓM TẮT NHANH — Duy cần nhớ

| Thứ tự | Nội dung | Thời gian |
|---|---|---|
| 1 | Mở đầu — giới thiệu mình + chủ đề | 15 giây |
| 2 | 2 chế độ chơi (có bảng so sánh) | 30 giây |
| 3 | `makeMoveAutoDigit` — greedy O(8) | 1 phút |
| 4 | `makeMoveWithDigit` — người chơi nhập | 30 giây |
| 5 | Undo — cách hoạt động + giới hạn | 30 giây |
| 6 | `handleClick` — luồng xử lý sự kiện | 30 giây |
| 7 | Kết thúc — chuyển cho An | 10 giây |

---

## 💡 MẸO KHI TRÌNH BÀY

- **Không đọc nguyên văn** — đây chỉ là gợi ý, nói tự nhiên như đang giải thích cho bạn.
- **Chỉ tay vào màn hình** khi nói về bảng so sánh 2 chế độ.
- **Nói chậm lại** ở phần pseudo-code `makeMoveAutoDigit` — đó là phần quan trọng nhất.
- **Tập trước 2-3 lần** cho quen miệng, canh đúng ~3 phút.
- Nếu run quá thì cứ bám theo phần **TÓM TẮT NHANH** ở trên, nhìn bullet point mà nói.
