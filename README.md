# PassItOn

A JavaFX desktop application that allows students to share and trade school supplies with one another. Students can manage their personal supplies catalogue, post requests for items they need, claim requests posted by other students, and confirm completed trades through a two-sided verification system.

---

## Authors

- Joshua Howard (STUDENT # 2542214)
- Bradley Balram (STUDENT #2433764)

---

## Git Code

- https://github.com/Wingedcrow/passiton-intelliJ.git

---

## Features

- Student registration and login with session management
- Supplies dashboard with real time search and category filtering
- Manual ownership toggling for catalogue items
- Trade request posting with a three request active limit
- Marketboard displaying all open requests from other students
- Full trade lifecycle : OPEN, CLAIMED, AGREED, SATISFIED
- Two-sided trade confirmation before a trade is marked complete
- Automatic ownership update when a trade is satisfied
- Expiry system for abandoned open requests

---

## Technologies Used

- Java 21
- JavaFX 21.0.2
- MySQL 8.0.41
- MySQL Connector/J 9.6.0
- Maven

---

## Requirements

- Java 21 or higher
- MySQL Server 8.0 or higher
- Maven 3.6 or higher
- An IDE that supports JavaFX (IntelliJ IDEA recommended)

---

## Database Setup

1. Open MySQL Workbench 
2. Run the provided `PassItOnMySqlDatabase.sql` file to create the database and populate it with sample data
3. The database will be created under the name `passiton`

The default connection details in `DatabaseConnection.java` are:

```
URL:      jdbc:mysql://localhost:3306/passiton
Username: root
Password: mysql

```

## Running the Application

1. Clone or download the project
2. Open the project in your IDE
3. Ensure the database is set up and running
4. Run `PassItOnApp.java` as the main class
5. Use one of the sample accounts below or register a new account

---

## Sample Accounts

| Email | Password |
|---|---|
| sallycopperfieldcts@gmail.com | sally1 |
| bradleybalramcts@gmail.com | bradley1 |

---

## Known Limitations

- No admin interface is currently accessible from the student-facing application
- The application is designed for local single-machine use and has not been tested in a networked environment
