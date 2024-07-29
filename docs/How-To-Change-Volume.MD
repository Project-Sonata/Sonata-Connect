### Change volume of the player

Set the volume for the user’s current playback device.

Note: the order of the commands are not guaranteed while using Player API.

#### Request

```http request
PUT /player/volume
Authorization: Bearer user_access_token
```

Query parameters:
- volume_percent - an integer from 0 to 100, where 0 means that device is muted and 100 max volume

#### Response

Status:
- 204 No Content - command has been received
- 400 Bad Request - invalid body, request or there is no active device. All responses with this status code have 'reason_code' in body, that can be used to determine type of the error
- 500 Server Error - should never happen, but if so - please, create a new GitHub issue that can be used to reproduce the issue.

See the [tests](../src/test/java/com/odeyalo/sonata/connect/controller/ChangePlayerVolumeEndpointTest.java) for further info


