### Connect Device

Connect device to the player. User can then switch playback to this device.

#### Request

```http request
POST /player/devices
Authorization: Bearer user_access_token
```

<details>
    <summary>
        Body application/json
    </summary>
<ul>
    <li>
        id - String. REQUIRED. unique identifier for device. Must be 16 characters(any characters)
    </li>
    <li>
        name - String. REQUIRED. name of the device that will be displayed for the user. Must be not null and must be more than 4 and less than 16 characters
    </li>
    <li>
        device_type - String. REQUIRED. type of device. Currently supported are: COMPUTER
    </li>
    <li>
        volume - integer. REQUIRED. volume of the device
    </li>
</ul>
</details>

#### Response

Status:

- 204 No Content - command has been received
- 400 Bad Request - invalid body or request. All responses with this status code have '
  reason_code' in body, that can be used to determine type of the error
- 500 Server Error - should never happen, but if so - please, create a new issue that can be used to reproduce the issue

See the [tests](../src/test/java/com/odeyalo/sonata/connect/controller/ConnectDeviceEndpointTest.java)
for further info