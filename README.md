# Mycelium Cloud Framework with WebWeaver web crawler

This is a repository of a custom cloud framework (Mycelium) written originally for my Master's Thesis and a demo application (WebWeaver).

## Installation

* ./gradlew idea - creates an Idea project downloading all dependencies etc.
* prepare MongoDB, RabbitMQ
* update configuration in cz.pechdavid.webweaver.Launcher
* launch cz.pechdavid.webweaver.Launcher

## Running the application

Running the application requires at least:

* RabbitMQ server (default user guest)
* MongoDB (without authentication)
* Running the application from the application root - ./gradlew run
* After booting the application can be reached on [http://localhost:8080/](http://localhost:8080/).

## Credits

* Author: David Pech for his [Master's Thesis](https://www.fit.vutbr.cz/)
* ![Built on...](https://www.cloudbees.com/sites/default/files/Button-Built-on-CB-1.png)
