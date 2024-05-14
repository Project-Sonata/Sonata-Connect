### Transfer playback

Transfer playback to a new device.


#### Request 

```http request
PUT /player/device/switch
Authorization: Bearer user_access_token
```

Body:

Array of device IDs to switch playback to. Note, that currently there is NO support for multiple devices and array CAN accept only one value. If received a request with more or less than 1 array length, then error will be returned.

<details>
    <summary>application/json</summary>
    <ul>
        <li>
            device_ids array of the device IDs, see the docs above.
        </li>
    </ul>
</details>


#### Response

Status:

- 204 No Content - command has been received
- 400 Bad Request - there is a problem in request or request body. All responses with this status code have 'reason_code' and small description about the error.
- 500 Server Error - should never happen, but if so - please, create a new GitHub issue that can be used to reproduce the issue 

See the [tests](../src/test/java/com/odeyalo/sonata/connect/controller/DeviceEntitySwitchPlayerControllerTests.java) for further info