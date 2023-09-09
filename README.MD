## Sonata-Connect

Synchronize all your devices, control playback and switch between devices in one click!

# How it works?

Current state is stored in key/value storage(Redis by default), application provides HTTP endpoints that change the state.
Also, authorized clients can be connected to playback websocket to receive the incoming messages in realtime.

The messaging format is JSON in both cases.
