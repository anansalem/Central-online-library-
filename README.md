# Central-online-library-
client-server library management application** built in Java for the Advanced Programming final project. Users can remotely browse a catalog, search for items, borrow books and eBooks, and return them — all over a live network socket connection

---

## ✨ Features

- 🔍 **Search** the catalog by title, author, genre, or category
- 📖 **Borrow & Return** books and eBooks remotely
- 👤 **Multi-user** — multiple clients connect simultaneously (multi-threading)
- 💾 **Persistent storage** — data saved to CSV files on the server
- 🖥️ **Two clients** — terminal-based and a full Swing GUI
- 🛡️ **Exception handling** — graceful errors, no crashes

---

## 🗂️ Project Structure

```
LibrarySystem/
├── common/                     # Shared classes (client + server)
│   ├── LibraryItem.java        # Abstract base class
│   ├── Searchable.java         # Interface
│   ├── Borrowable.java         # Interface
│   ├── Book.java               # Extends LibraryItem
│   ├── EBook.java              # Extends Book (multi-level inheritance)
│   ├── Magazine.java           # Extends LibraryItem
│   ├── LibraryRequest.java     # Socket protocol: client → server
│   └── LibraryResponse.java   # Socket protocol: server → client
├── server/
│   ├── LibraryServer.java      # Main server + accept loop
│   ├── ClientHandler.java      # Per-client thread (Runnable)
│   ├── LibraryService.java     # Business logic
│   └── FileManager.java        # CSV file I/O + transaction log
├── client/
│   ├── LibraryClient.java      # Terminal client
│   └── LibraryGUI.java         # Swing GUI client
├── data/                       # Auto-generated on first run
│   ├── library_data.csv
│   └── borrow_log.txt
└── README.md
```

---

## 🧱 OOP Concepts Demonstrated

| Concept | Where |
|---|---|
| **Abstract class** | `LibraryItem` — declares `getType()` and `getDetails()` as abstract |
| **Interfaces** | `Searchable`, `Borrowable` in the `common` package |
| **Inheritance** | `Book` → `LibraryItem`, `EBook` → `Book` → `LibraryItem` |
| **Polymorphism** | `getSummary()` overridden in Book, EBook, Magazine; interface dispatch in LibraryService |
| **Encapsulation** | All `LibraryItem` fields `private` with getters/setters |
| **Sockets** | `ServerSocket` in LibraryServer, `Socket` in LibraryClient/GUI |
| **Multi-threading** | Each client runs in its own `Thread(new ClientHandler(...))` *(bonus)* |
| **File Handling** | CSV read/write in `FileManager` |
| **Data Structures** | `LinkedHashMap<String, LibraryItem>` for O(1) lookup; `ArrayList` for results |
| **Exception Handling** | Try-catch + try-with-resources throughout all network and file code |

---

## 🏗️ Class Hierarchy

```
LibraryItem  (abstract)
├── Book         implements Searchable, Borrowable
│   └── EBook    extends Book
└── Magazine     implements Searchable  (reference only — not borrowable)
```

---

## 🚀 Getting Started

### Prerequisites

- Java 17 or later — download from [https://adoptium.net](https://adoptium.net)
- Verify installation:
  ```bash
  java -version
  ```

### Compile

From the `LibrarySystem/` root directory:

```bash
javac -d out common/*.java server/*.java client/*.java
```

> **Windows CMD** — if wildcards don't work, list each file explicitly or use PowerShell.

### Run the Server

```bash
java -cp out server.LibraryServer
```

Expected output:
```
╔════════════════════════════════════╗
║   Online Library System - Server   ║
║  Listening on port 5000             ║
╚════════════════════════════════════╝
[Server] Loaded 10 items from storage.
```

### Run the GUI Client *(recommended)*

Open a second terminal in the same folder:

```bash
java -cp out client.LibraryGUI
```

### Run the Terminal Client

```bash
java -cp out client.LibraryClient
```

> 💡 You can open multiple client windows at the same time to test multi-client support.

---

## 🖥️ GUI Preview

| Screen | Description |
|---|---|
| **Login** | Enter username → connects to server |
| **Catalog** | Browse all items with filter (All / Book / EBook / Magazine / Available) |
| **Search** | Search by title, author, genre |
| **My Books** | View and return your borrowed items |
| **Status bar** | Live feedback from the server on every action |

---

## 📡 Communication Protocol

Client and server exchange serializable Java objects over TCP on port **5000**:

```
Client  ──── LibraryRequest  ────►  Server
        ◄─── LibraryResponse ────
```

**Request actions:** `SEARCH` · `BORROW` · `RETURN` · `LIST_ALL` · `LIST_MINE` · `EXIT`

---

## 📁 Sample Data

The server auto-generates 10 items on first run:

| ID | Type | Title |
|---|---|---|
| B001 | Book | Clean Code — Robert Martin |
| B002 | Book | The Great Gatsby — F. Scott Fitzgerald |
| B003 | Book | Dune — Frank Herbert |
| B004 | Book | Design Patterns — GoF |
| B005 | Book | 1984 — George Orwell |
| E001 | EBook | Head First Java — Kathy Sierra |
| E002 | EBook | The Pragmatic Programmer — Hunt & Thomas |
| M001 | Magazine | National Geographic |
| M002 | Magazine | Time |
| M003 | Magazine | IEEE Spectrum |

---

## ⚠️ Troubleshooting

| Error | Fix |
|---|---|
| `'java' is not recognized` | Java not installed or not in PATH — reinstall from adoptium.net |
| `Cannot connect to server` | Start the server before the client |
| `Address already in use` | Port 5000 is busy — restart your PC |
| `file not found` when compiling | Make sure you're in the `LibrarySystem/` root folder |

---

## 🛠️ Built With

- **Java 21** — core language
- **Java Sockets** — client-server communication
- **Java Swing** — graphical user interface
- **Java Serialization** — object exchange over sockets
- **CSV files** — lightweight data persistence

---

## 📄 License

This project was developed as a university course project at **EJUST** for the Advanced Programming course — Spring 2026.
