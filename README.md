# Toggl-CLI

A command line client for [Toggl](http://www.toggl.com/).

## Installation

Download the [latest release package](https://github.com/gabalese/toggl-cli/releases/latest).

Unzip the `.tar` somewhere on your filesystem:

```
tar vxf toggl-cli-0.x.tar.gz
```

Install the program using the Makefile:

```
make install
```

The client is now available as `toggl` in `~/local/bin`, therefore in `$PATH`.

## Setup

Requires an environment variable `TOGGL_KEY` set with the API key for the present user.
The key can be found in the [user profile](https://www.toggl.com/app/profile).

```sh
export TOGGL_KEY=<api key>
```

## Available commands

```
toggl me
```

Information about the current user.

```
toggl start [task name]
```

Start a new task named "task name".

```
toggl get current
```

Fetch the current running task.

```
toggl get last [n]
```

Fetch the last *n* tasks, or the latest non-running one.

```
toggl stop current
```

Stop the current running task.

```
toggl start last
```

Start a new time slot for the last task.
