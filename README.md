# Chatty Chat Chat, a Java Chat App
## COSC 150 - Advanced Programming

- [Goals](#goals)
- [Description](#description)
- [Specification and requirements](#specification-and-requirements)
- [Provided files](#provided-files)
- [Grading rubric](#grading-rubric)

## Goals

In order to complete this assignment, I needed to:
- Understand how to model network connections using sequence diagrams
  - Describe the operations required for a complete network interaction.
  - Model the communications between client and server processes, and between the threads supporting those processes.
- Understand how to use Java sockets to create and sustain network connnections.
  - Create a server that accepts multiple simultaneous connections.
  - Implement a design to handle interactions with many clients at once.
- Understand how to use Java threads to allow for nondeterministic execution.
  - Allow for both client and server processes to handle multiple simultaneous tasks.
  - Use proper object locks and/or wait-notify signaling to collaborate on a shared object.

## Description

In this assignment, I implemented my very own internet chat protocol -- the ChattyChatChat protocol (CCC). The protocol governs how a single ChattyChatChat server mediates connections between any number of ChattyChatChat clients as they communicate with each other.

Implementing this protocol required that I create at least two Java classes:
- The ChattyChatChatServer, which is run on a single computer at a specified port and receives connections from clients.
- The ChattyChatChatClient, which is run by each client computer and connects to the server.

#### Boring Historical Context

The idea of text-based messaging has been around (almost) as long as networked computers have, with early bullitin board servers (BBS) and Usenet protocols allowing threaded discussion. The first widely-used real-time chat protocol was [Internet Relay Chat (IRC)](https://en.wikipedia.org/wiki/Internet_Relay_Chat), which was developed in 1988 and [standardized in 1993](https://tools.ietf.org/html/rfc1459).

## Specification and requirements

#### ChattyChatChatServer

The server program is a class named `ChattyChatChatServer`; this program accepts a single command-line argument describing the port for the server to listen on. For example, to start the server and have it listen to port `9876`, the command-line invocation would be:
```
java ChattyChatChatServer 9876
```
A single instance of the ChattyChatChat server serves as the common point of connection for all clients wanting to interact with the chat server.

#### ChattyChatChatClient

The client program is a class named `ChattyChatChatClient`; this program accepts two command-line arguments describing the server name and port to connect to. For example, to start a client and connect to a server running on port `9876` on `cs-class`, the command-line invocation would be:
```
java ChattyChatChatClient cs-class.uis.georgetown.edu 9876
```
Note that the server must be running in order for any client to successfully connect.

#### Chat protocol and commands

The communication protocol for the chat clients and server obeys the following rules:
- A "normal" message is text sent by one client to the server; this message should be relayed to all other clients, who will print it to standard out (e.g., using `System.out.println()`) upon receipt.
- The CCC protocol also provides the following *chat commands* which should be interpreted by the server to perform a special task:
  - `/nick <name>` : Set this client's nickname to be the string `<name>`. For example:
  ```
  /nick cosc150student
  ```
  would set the user's nickname to `cosc150student`.
    - The nickname command may be used more than once per session by any user; the current nickname is retained unless/until a subsequent `/nick` command is received.
    - Nicknames do *not* need to be unique on the server.
    - Nicknames are single-words and do not contain spaces.
    - Any additional characters beyond the first word may be ignored; that is, the above and below commands would have identical effect:
  ```
  /nick cosc150student these words may be discarded
  ```
  - `/dm <name> <msg>` : Send a message to user(s) with the specified nickname. For example:
  ```
  /dm cosc150student This is a "secret" message
  ```
  should deliver the message "This is a "secret" message" only to user(s) who have the nickname "cosc150student".
    - Only clients with the correct nickname should receive this message; nothing should be sent to any other clients.
    - If no client has the specified nickname, this message may be ignored.
    - If multiple clients have the specified nickname, *all* of them should receive the message.
  - `/quit` : Disconnect from the server and end the client program.
    - When a client enters this message, it is still be sent to the server as a notice that the client will disconnect; the server then safely closes this socket connection and clean-up details related to the client.
    - The client program disconnects, cleans up, and ends when this string is entered.
    - Any additional characters after the `/quit` are safely ignored.
- Any other input (including one beginning with a slash, but not exactly matching the above) is considered a regular message.


## Other Files

The following files related to project logistics are present in the repository:
- `README.md` which provides this specification.
- `.gitignore` which specifies files git should NOT track.
- `.travis.yml` which provides instructions to trigger Travis testing.
- `travis` a directory containing the testing scripts.
