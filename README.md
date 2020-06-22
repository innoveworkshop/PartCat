# PartCat

Electronic components cataloger and organizer application.

![Application Screenshot](/screenshots/2020-05-11.png?raw=true)

## How it Works

This application uses a very simple directory and file based approach to store
its files, no databases involved. This way you can easily move your library
around, sync it between computers, and even place your whole library inside a
Git (or whatever you prefer) repository and manage it that way.

## Ports

Even though this application is written in Java in order to maximize
portability, some platforms require special attention. This list contains ports
of this application to other systems:

  - [Windows CE/PocketPC](https://github.com/innoveworkshop/PartCatCE)

## Installation

Well, you can always just run the `PartCat.jar` file located inside `dist/`.

### Linux

Go to the `dist/` directory and run the `install.sh` script there to
automatically create system-wide files and make things easier on you.

### Windows

Go to the `dist/` directory inside the repository and copy the `PartCat.exe`
executable to the appropriate places. It's entirely portable, given you also
bundle Java with it, so you can place it together with a workspace inside a
flash drive and have everything you need anywhere.

## Requirements

  - Java Runtime Environment 8

## License

This project is licensed under the **MIT License**.

