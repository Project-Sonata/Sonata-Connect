## Sonata-Connect

Synchronize all your devicesEntity, control playback and switch between devicesEntity in one click!

# How it works?

Current state is stored in key/value storage(Redis by default), application provides HTTP endpoints that change the state.
Also, authorized clients can be connected to playback websocket to receive the incoming messages in realtime.

The messaging format is JSON in both cases.

Available endpoints:

``GET /player/state - get the current playback state``

``PUT /player/playback/change - transfer playback to the another deviceEntity``

``GET /player/devicesEntity - get the devicesEntity that available now``

``GET /player/currently-playing - get the currently playing track``

The list of endpoints is not final and will be updated soon