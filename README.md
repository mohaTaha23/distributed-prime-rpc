# 🧠 Distributed Computing Project – RPC with Directory, Server, and Client

This is my **Distributed Computing** project for Spring 2023/2024  
This simple but nicely done implementation demonstrates how Remote Procedure Calls (RPC) work using raw **sockets** in Java, with three core components working together like an orchestra 🎻:

---

## ✨ Project Components

### 🗂️ Directory
- Acts as a central **registry**.
- Maintains a list of available servers.
- Shares server details with any client that asks nicely.
- Keeps checking the connected server using heartbeats. 

### 🖥️ Server
- Handles actual **computation** (is this number prime?).
- Registers itself with the Directory.
- Accepts multiple client connections using **threads**.
  
### 🗣 Client 
- Connects to the Directory to find an available and make connection with the Server.
- Sends a number to the Server.
- Receives a "prime or not" with the next prime number if it wasn`t.

---

## 🔄 Remote Procedure Call (RPC)

Mimic RPC behavior over sockets!  
Here’s how it flows:

1. Client asks Directory: *"Hey, got any servers?"*
2. Directory replies with: *"Yes! Try this one: 127.0.0.1:8888"*
3. Client connects to Server.
4. Client sends number.
5. Server checks if it’s prime 💡 and responds.
6. Client goes: *"Cute. Thanks!"*

---

## 🔗 Architecture Diagram (Conceptual)

