# Reproducer for `RST_STREAM` issue with Bidirection Streaming Python gRPC Client

## Setup python

We recommend setting up a venv and installing known dependencies. These can be found in `requirements.txt`.

## Generate python stubs from the proto file

Run:
```shell
./generate_from_proto.sh
```

## Starting the server and envoy
To start the java server and envoy, run

```shell
./gradlew installDist && docker compose up
```

To change to a python server implementation, edit docker-compose.yml to disable `java-server` and enable `python-server`,
then edit `envoy.yaml` to likewise swap the `java-server`/`python-server` endpoints, at the end of the file. Run
`docker-compose build` to create the required python image, then `docker-compose up` to start both envoy and python.

Note that `docker-compose down` may be required to fully clean up when switching between server implementations.

## Running the Python client

We haven't reproduced the bug on any client except the python client.

To verify the python client connecting to the plain server, run

```shell
python python/client.py localhost:10000
```

Change the port to 8000 to test against Envoy. (envoy is unnecessary to repro this issue)
