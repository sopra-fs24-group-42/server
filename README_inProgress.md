![image](/resources/forest%20copy.jpg)
# Survive the Night
<div style="text-align: justify"> 

Check out the front-end implementation [here](https://github.com/sopra-fs24-group-42/client).

## 📖 Table of Contents

1. [Introduction](#introduction)
2. [Technologies](#technologies)
3. [High-Level Components](#high-level-components)
- [Websocket Controller](#websocket-controller)
- [Websocket Service](#websocket-service)
- [Setup Controller](#setup-controller)
- [Game Service](#game-service)
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

### Stomp Controller <a id="websocket-controller"></a> 

Please find a reference here: [Stomp Controller](https://github.com/sopra-fs24-group-42/server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/StompController.java)

### Websocket Service <a id="websocket-service"></a> 

Please find a reference here: [Websocket Service](https://github.com/sopra-fs24-group-42/server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/WebsocketService.java)

### Setup Controller <a id="setup-controller"></a> 

Please find a reference here: [Setup Controller](https://github.com/sopra-fs24-group-42/server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/controller/SetupController.java)

### Game Service <a id="game-controller"></a> 

Please find a reference here: [Game Service](https://github.com/sopra-fs24-group-42/server/blob/main/src/main/java/ch/uzh/ifi/hase/soprafs24/service/GameService.java)

dentify your project’s 3-5 main components. What is their role?
How are they correlated? Reference the main class, file, or function in the README text with a link.

## Launch & Development <a id="launch--development"></a>

Write down the steps a new developer joining your team would
have to take to get started with your application. What commands are required to build and run your project locally? How can they run the tests? Do you have external dependencies or a database that needs to be running? How can they do releases?

## Building with Gradle
You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

You can verify that the server is running by visiting `localhost:8080` in your browser.

### Test

```bash
./gradlew test
```

### Development Mode
You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

## Debugging
If something is not working and/or you don't know what is going on. We recommend using a debugger and step-through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command), do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug "Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

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
In the next Sprint, we expect developers to complete the user stories left in the backlog that have low priority. The functionality specified in these user stories can help improve user satisfaction by letting eliminated players track the impacts of other players still in the game. For this task, there is a prepared user story. You may follow the link here [User Story #7](https://github.com/orgs/sopra-fs24-group-42/projects/1/views/1?pane=issue&itemId=57072825) to take a closer look.

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
