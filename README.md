# 🌊 Submarine (Mindustry Style)

[RU] Простая 2D игра на Java, созданная в VSCodium.
[EN] A simple Java 2D game developed in VSCodium.

---

## 🇷🇺 Описание проекта
Это прототип игры с видом сверху, написанный на **Java**. Игрок управляет подводным аппаратом в процедурно сгенерированном мире.

### Основные фишки:
*   **Генерация карты:** Автоматическое размещение ресурсов (железо, кремний, базальт, водоросли).
*   **Меню построек:** Реализован интерфейс `BuildMenu` для будущего строительства.
*   **Управление:** Плавное перемещение на `WASD` с вращением спрайта за направлением движения.
*   **Графика:** Использование `Graphics2D` с фильтрацией `NEAREST_NEIGHBOR` для сохранения четкости пиксель-арта.

### Как запустить:
1. Скачайте все файлы `.java` и `.png` в одну папку.
2. Откройте проект в **VSCodium** (или любой другой IDE).
3. Запустите файл `play.java`.

---

## 🇺🇸 Project Description
A top-down 2D game prototype built with **Java**. The player controls a submarine exploring a procedurally generated underwater world.

### Key Features:
*   **Map Generation:** Automatic placement of resources like iron, silicon, basalt, and water plants.
*   **Build Menu:** Integrated `BuildMenu` interface for construction mechanics.
*   **Controls:** Smooth `WASD` movement with sprite rotation towards the direction of travel.
*   **Graphics:** Uses `Graphics2D` with `NEAREST_NEIGHBOR` interpolation to keep the pixel art sharp.

### How to Run:
1. Download all `.java` and `.png` files into a single folder.
2. Open the project in **VSCodium** or any Java IDE.
3. Run the `play.java` file.

---

## 🛠 Tech Stack / Технологии
*   **Language:** Java 8+
*   **Graphics:** Swing / AWT
*   **IDE:** VSCodium

## 📂 Files / Файлы
*   `play.java` — Main engine & rendering.
*   `generate.java` — Map generation logic.
*   `BuildMenu.java` — UI & construction logic.
*   `*.png` — Game assets (player, tiles, resources).
