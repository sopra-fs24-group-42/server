# Introduction
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

## Technologies

During the development of the back-end, we used the following technologies:

* [Java](https://www.java.com/de/download/manual.jsp) - Programming language used in the server
* [Spring Boot](https://spring.io/projects/spring-boot) - Spring Boot Framework used in the server
* [WebSockets](https://docs.spring.io/spring-framework/reference/web/websocket.html) - WebSockets with SockJS and STOMP protocol
* [JPA](https://spring.io/projects/spring-data-jpa) - API for object-relational mapping to databases in Java applications
* [H2 DB](https://www.h2database.com/html/main.html) - Java SQL database
* [Google cloud](https://cloud.google.com/?hl=en) - Handles the deployment

## High-level components

### Websocket Controller 
### Websocket Serviver
### Setup Controller
### Game Service 

dentify your project’s 3-5 main components. What is their role?
How are they correlated? Reference the main class, file, or function in the README text with a link.

## Launch & Deployment

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
The top 2-3 features that new developers who want to contribute to your project could add.
* add more roles 
* leaderboard only for the cuurent lobby 
* custom error handling

## Authors and acknowledgment

* **Lukas Niedhart** - [lukasniedh](https://github.com/lukasniedh)
* **Charlotte Model** - [cmodel1](https://github.com/cmodel1)
* **Rafael Urech** - [DaKnechtCoder](https://github.com/DaKnechtCoder)
* **Polina Kuptsova** - [kuppolina](https://github.com/kuppolina)
  
See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
Say how your project is licensed (see License guide3).
