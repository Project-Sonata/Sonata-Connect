### Disconnect Device

Disconnect the device from the player state. After that command device won't be visible for user and won't accept any Player API commands.

Note: Do nothing if device by provided ID does not exist. 

#### Request

```http request
DELETE /player/devices
Authorization: Bearer user_access_token
```

Query parameters:

- device_id - String. REQUIRED. ID of the device that should be disconnected

#### Response

Status:
- 204 No Content - Command has been received
- 500 Server Error - should never happen, but if so - please, create a new GitHub issue that can be used to reproduce the issue

See the [tests](../src/test/java/com/odeyalo/sonata/connect/controller/DisconnectDeviceEndpointTest.java) for further info