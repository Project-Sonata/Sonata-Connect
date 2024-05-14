### Pause playback

Pauses the player playback on the user's account.

Do nothing if playback is already paused.

Note: the order of the commands are not guaranteed while using Player API.

#### Request

```http request
PUT /player/pause
Authorization: Bearer user_access_token
```

#### Response

Status:
- 204 No Content - command has been received
- 400 Bad Request - invalid body, request or there is no active device. All responses with this status code have 'reason_code' in body, that can be used to determine type of the error
- 500 Server Error - should never happen, but if so - please, create a new GitHub issue that can be used to reproduce the issue.

See the [tests](../src/test/java/com/odeyalo/sonata/connect/controller/PauseCommandEndpointTest.java) for further info


