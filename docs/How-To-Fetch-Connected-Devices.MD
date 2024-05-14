#### List of available devices

Returns an array of devices that are connected to the player.

#### Request

```http request
GET /player/devices
Authorization: Bearer user_access_token
```

#### Response

Array of the DeviceObject.


<details>
    <summary>
        Body application/json
    </summary>
<ul>
    <li>
        id - String. REQUIRED. unique identifier of the device.
    </li>
    <li>
        name - String. REQUIRED. name of the device.
    </li>
    <li>
        device_type - String. REQUIRED. type of device. Currently supported are: COMPUTER
    </li>
    <li>
        volume - integer. REQUIRED. volume of the device
    </li>
    <li>
        active - boolean. REQUIRED. Determine if device is active(playback will start on this device)
    </li>
</ul>
</details>

<details>

<summary>Example of response</summary>

```json
{
    "devices": [
        {
            "volume": 40,
            "active": true,
            "id": "ilovemikunakano2",
            "name": "MIKU_PHONE",
            "type": "COMPUTER"
        },
        {
            "volume": 70,
            "active": false,
            "id": "ilovemikunakano1",
            "name": "Miku I love you",
            "type": "COMPUTER"
        }
    ],
    "size": 2
}

```
</details>

See the [tests](../src/test/java/com/odeyalo/sonata/connect/controller/AvailableDevicesPlayerStateControllerTest.java) for further info.