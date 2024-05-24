![image](/resources/forest%20copy.jpg)
# Survive the Night
<div style="text-align: justify"> 

Check out the front-end implementation [here](https://github.com/sopra-fs24-group-42/client).

## Table of Contents

1. [Introduction](#introduction)  
2. [Technologies](#technologies) 
3. [High-Level Components](#high-level-components)
4. [Launch & Development](#launch--development)
    - [Getting started](#getting-started)
    - [Prerequisites & installation](#prerequisites-installation)
    - [Running locally](#running-locally)
    - [Debugging](#running-locally)    
    - [Running tests](#running-tests)
5. [Roadmap](#roadmap)
6. [Authors](#authors)
7. [Acknowledgments](#acknowledgments)
8. [License](#license)

## Introduction <a name="introduction"></a>
In the evolving world of digital interaction, traditional role-playing games like Werewolf
require a modern solution to bridge the gap between virtual and physical game spaces. Our
project seeks to digitize these beloved social activities, allowing users to engage in
immersive narrative-driven experiences from any location. By developing a web application
that supports game setup, role assignment, and real-time interaction through voice-to-text
technology, we aim to replicate the communal atmosphere of these games online. Utilizing
technologies such as React for the frontend and Node.js for the backend, alongside
WebSocket for real-time communication and third-party APIs for voice recognition, this
project stands as a testament to the innovative application of web development skills and AI
integration. This initiative not only aligns with the course's focus on creating cutting-edge
web applications but also offers a solution to the limitations posed by physical distance in
social gaming.

## Technologies <a id="technologies"></a>
During the development of the back-end, we used the following technologies:

* [Java](https://www.java.com/de/download/manual.jsp) - Programming language used in the server
* [Spring Boot](https://spring.io/projects/spring-boot) - Spring Boot Framework used in the server
* [WebSockets](https://docs.spring.io/spring-framework/reference/web/websocket.html) - WebSockets with SockJS and STOMP protocol
* [JPA](https://spring.io/projects/spring-data-jpa) - API for object-relational mapping to databases in Java applications
* [H2 DB](https://www.h2database.com/html/main.html) - Java SQL database
* [Google cloud](https://cloud.google.com/?hl=en) - Handles the deployment

## High-Level Components <a id="high-level-components"></a>
The Stomp Controller component handles the logic for receiving data from the client and invokes the relevant methods to process this data. Additionally, it calls a method to broadcast the lobby back to the client. In this controller data transmission is managed over TCP using STOMP protocol.

Below is a table that lists all the mappings used for the Stomp Controller in our application:

|Mapping|Method|Parameter type|Parameter|Description| 
|-------|------|--------------|---------|-----------|
|/game|CONNECT|||upgrade to WebSocket connection|
|/game|DISCONNECT|||remove WebSocket connection|
|/topic/lobby/{lobbyId}|SUBSCRIBE|Pathvariable|lobbyId|subscribes to a Lobby with lobbyId; update about the Lobby will be sent to all subscribed clients|
|/topic/lobby/{lobbyId}|UNSUBSCRIBE|Pathvariable|lobbyId|stop receiving lobby information|
|/app/startgame|SEND|Body|lobbyId<Long>|starts game (distributes roles) and broadcasts the lobby|
|/app/ready|SEND|Body|username<string>, gameState<string>|sets player to ready. If all Players in a lobby are ready the Lobby goes to the next gameState and resets the player to not ready|
|/app/{roleName}/nightaction|Send|Pathvariable, Body|roleName<string>, SelectionRequest|performs nightaction|
|/app/voting|SEND|Body|SelectionRequest|performs vote during voting phase|
|/app/settings/{lobbyId}|SEND|Pathvariable, Body|LobbyId<long>, UpdatedGameSettings|updates lobby settings|

Please find a reference to a file here: [Stomp Controller](https://github.com/sopra-fs24-group-42/server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/StompController.java)

### Websocket Service <a id="websocket-service"></a> 
Websocket Service includes the logic that regulates broadcast of a lobby to client. In the function broadcastLobby we check is lobby is null at first to ensure that it exists and can be found in the database. Further, the dictionary is created by mapping the player information to its username. After setting the destination we can send the data with updations back to the client so the players can see game outcomes. 
Please find a reference to a file WebsocketService.java here: [Websocket Service](https://github.com/sopra-fs24-group-42/server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/WebsocketService.java)

### Setup Controller <a id="setup-controller"></a> 
Setup Controller includes handels the logic for receiving data from the client and invokes the relevant methods to prcess this data. In this controller, data transmission is managed through HTTP requests using the REST API.
In this table we have compposed all the mappings for Setup Controller that we have used in our application: 

|Mapping|Method|Parameter type|Parameter|Description| 
|-------|------|--------------|---------|-----------|
|/players|POST|Body|username<string>, lobbyCode<string>|creates a new player|
|/lobbies|POST|Body|hostUsername<string>, numberOfPlayers<int>|creates a new lobby|
|/players/{username}|DELETE|Pathvariable||deletes a player|
|/leaderboards/{maxNumberOfTopPlayers}|GET|Pathvariable|MaxNumberOfTopPlayers<int>|gets top MaxNumberOfTopPlayers Players|

Please find a reference to a file here: [Setup Controller](https://github.com/sopra-fs24-group-42/server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/SetupController.java)

### Game Service <a id="game-controller"></a> 
The GameService is one of the most crucial components in our application. It includes the core logic of the game, from creating the lobby and processing user inputs that define the game's flow to concluding the outcomes needed for the game leaderboard.

Below is a list of critical methods to consider before continuing work on the project:

The goToNextPhase method is a crucial part of our game implementation. It transitions the game from one phase to another, ensuring the correct flow and synchronization between the server and the client. The method takes the lobbyId as a parameter to identify the specific lobby and its players. It uses a switch statement to determine which phase processing methods to execute based on the current game state. Here are the phases included in our implementation:

- WAITINGROOM: When a new game is created, all players are redirected to the waiting room. Here, they wait until all players have joined the lobby before the host can start the game.
- PRENIGHT: In this phase, all users are set to "not ready," a flag that helps transition from one phase to another, ensuring synchronization between the server and the client.
- NIGHT: During the Night phase, the werewolves, seers, protectors, and sacrifices make their actions, while the villagers click a button to simulate an action and keep their identities secret. After choices are made, the server receives the selections with the players' usernames. The outcomes are then calculated, and data for the next phase, REVEALNIGHT, is prepared.
- REVEALNIGHT: Upon entering the REVEALNIGHT phase, additional methods from PlayerService and LobbyService are invoked. A method responsible for checking if the game has ended is also called.
- DISCUSSION: In the Discussion phase, players discuss and try to identify potential werewolves. Upon entering this phase, all players' "Ready" flags are reset to False.
- VOTING: During the Voting phase, players vote for potential werewolves. The processVoting method is invoked to calculate who received the most votes. Additionally, the method ifHostDeadSetNewHost handles the scenario where the host is eliminated.
- REVEALVOTING: This phase resets the players' fields to ensure the correct flow of the game in the next round.
- ENDGAME: The final phase of the game, ENDGAME, resets the lobby and updates the leaderboard, allowing players to see the results of the round played.

In our implementation, it is imperative to specify the correlation between the GameService and smaller services, particularly PlayerService and LobbyService, in conjunction with the StompController.

The StompController class has direct access to the GameService and accesses PlayerService and LobbyService through a ServiceProvider. This design preserves modularity and encapsulation, ensuring that each component interacts seamlessly while maintaining a clear separation of concerns.

Moreover, this GameService component interacts with PlayerService, LobbyService, and classes for each role. These are additional, smaller components that support GameService by providing access to extra methods for processing user input.

Please find a reference to a file here: [Game Service](https://github.com/sopra-fs24-group-42/server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/GameService.java)

## Launch & Development <a id="launch--development"></a>
### [Getting started](#getting-started)
To start we recommend to get familiarezed with the following documentation and resources:  
-   Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
-   Guides: http://spring.io/guides
-   Building a RESTful Web Service: http://spring.io/guides/gs/rest-service/
-   Building REST services with Spring: https://spring.io/guides/tutorials/rest/

### [Prerequisites & installation](#prerequisites-installation)
Download your IDE of choice (e.g., [IntelliJ](https://www.jetbrains.com/idea/download/), [Visual Studio Code](https://code.visualstudio.com/), or [Eclipse](http://www.eclipse.org/downloads/)). Make sure Java 17 is installed on your system (for Windows, please make sure your `JAVA_HOME` environment variable is set to the correct version of Java).

### IntelliJ
If you consider to use IntelliJ as your IDE of choice, you can make use of your free educational license [here](https://www.jetbrains.com/community/education/#students).
1. File -> Open... -> SoPra server template
2. Accept to import the project as a `gradle project`
3. To build right click the `build.gradle` file and choose `Run Build`

### VS Code
The following extensions can help you get started more easily:
-   `vmware.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`

### [Running locally](#running-locally)
### Building with Gradle
Local Gradle Wrapper can be used to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

#### Build

```bash
./gradlew build
```

#### Run

```bash
./gradlew bootRun
```

You can verify that the server is running by visiting `localhost:8080` in your browser.

### Development Mode
You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

[Debugging](#running-locally)    
If something is not working and/or you don't know what is going on. We recommend using a debugger and step-through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command), do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug "Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time



[Running tests](#running-tests)
You can run test with the following command:
```bash
./gradlew test
```

## Roadmap
In the Roadmap, we have specified the next steps that can benefit application development and be a valuable contribution to this project:
- **Adding more roles** 
At the moment, we have developed one of the most exciting roles, including Werewolf, Sacrifice, Seer, Villager, and Protector. In the future, we plan to improve the application by implementing the logic for the following roles:
- Mayor: The Mayor typically has additional voting power during the day. Their vote might count as two, or they might have a tie-breaking ability. The Mayor's primary goal is to lead the village and help identify and eliminate werewolves while protecting the villagers.
- Jester: The Jester is a chaotic role that aims to get themselves eliminated by the village. The Jester does not have any special powers other than their unique win condition. The Jester wins if they are successfully lynched by the villagers during the day. They do not win if killed by other means, such as by werewolves at night.
- Sheriff: The Sheriff is a trusted figure with an investigative role within the village. Each night, the Sheriff can investigate one player to determine if they are a werewolf or not. The Sheriff must be careful when revealing their information to avoid being targeted by the werewolves. The Sheriff's goal is to identify and help eliminate the werewolves to protect the village.
- Amour: The Amour is a role that connects two players, making them fall in love. At the beginning of the game, the Amour chooses two players who become Lovers. If one Lover dies, the other one dies of heartbreak as well. If both Lovers are villagers, their goal is to survive together and help the village win. If one Lover is a werewolf, their goal becomes to survive together, which usually means working against their respective teams to stay alive.
- Swapper: The Swapper is a role that can change the outcomes of votes during the day. During the day, the Swapper can swap the votes between two players. This ability can be used to save an innocent player or trick the village into eliminating someone else. The Swapper's goal aligns with the village in rooting out and eliminating werewolves, using their power to manipulate votes strategically.
 
The implementation of the new classes for roles has to follow the pattern that has been developed earlier and requires the development of new WebSocket endpoints.

- **Leaderboard only for the cuurent lobby** 
During Sprint 2, we implemented a leaderboard for all players to compare their results after each game round. However, we also believe that it is important to support competition within one lobby. This will enhance players' entertainment and bring more joy to the game. Players should be able to access the leaderboard to see the outcomes of the one game round just after it has ended. After that, players should be redirected to the waiting room, where the host can start the game or change the game settings.

- **Eliminated players stay in the game as spectators**
In the next Sprint, we expect developers to complete the user stories left in the backlog that have low priority. The functionality specified in these user stories can help improve user satisfaction by letting eliminated players track the impacts of other players still in the game. For this task, there is a prepared user story. You may follow this link [User Story #7](https://github.com/orgs/sopra-fs24-group-42/projects/1/views/1?pane=issue&itemId=57072825) to take a closer look.

## Authors <a id="authors"></a>
* [Charlotte Model](https://github.com/cmodel1)
* [Polina Kuptsova](https://github.com/kuppolina)
* [Lukas Niedhart](https://github.com/lukasniedh)
* [Rafael Urech](https://github.com/DaKnechtCoder)

## Acknowledgments <a id="acknowledgements"></a>
We want to thank our Teaching Assistant [Marco Leder](https://github.com/marcoleder) for guiding us through the course!
  
## License
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
Say how your project is licensed (see License guide3).
