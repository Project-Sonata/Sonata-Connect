### Consuming real time updates from the player

Sonata Player API supports WebSocket READ-ONLY message communication protocol.
All write commands are performed by REST API endpoints.

Sonata Player API message protocol is simple:

- Messages are consumed by devices
- All messages are in JSON format
- WebSocket write commands are ignored
- All messages contain player state 

#### Types of the events:
- PLAYER_STATE_UPDATED - player state has been changed
- QUEUE_STATE_CHANGED - queue of the player has been changed(added track, removed track, changed positions)
- NEW_DEVICE_CONNECTED - a new device has been connected to player
- DEVICE_DISAPPEARED - device has been disconnected

#### Connecting to WebSocket

To connect to WebSocket and consume messages in real time there is only one step.

You need:
- User's access token, that can be obtained using OAuth 2.0 Protocol.

```http request
WEBSOCKET v1/player/sync?token=user_access_token
```

After the switching protocols you will be able to receive the messages. 

#### Supported commands and their events

Currently supported events for commands are:

- Play command. When play command has been received by REST API endpoint it will send new player state with PLAYER_STATE_UPDATED
- Pause command. When play command has been received by REST API endpoint it will send new player state with PLAYER_STATE_UPDATED
- Connect device command. When play command has been received by REST API endpoint it will send new player state with NEW_DEVICE_CONNECTED
- Disconnect device command. When play command has been received by REST API endpoint it will send new player state with DEVICE_DISAPPEARED

