### Start/Resume Playback

Start a new context or resume existing one of the player on user active device.
Command does nothing if player is already active and the context is equal to currently playing context.

Note: the order of the commands are not guaranteed while using Player API.

Note: Before sending play command the user MUST have at least one connected device

#### Request

PUT /player/play

<details>
    <summary>
        Body application/json
    </summary>
<ul>
    <li>
        context uri - String. Optional. A context URI of the track, album or playlist. If not supplied, then current context URI will be resumed
    </li>
</ul>
</details>

#### Response

Status:
- 204 No Content - command has been received
- 400 Bad Request - invalid body, request or there is no active device. All responses with this status code have 'reason_code' in body, that can be used to determine type of the error
- 500 Server Error - should never happen, but if so - please, create a new issue that can be used to reproduce the issue 


See the [tests](../src/test/java/com/odeyalo/sonata/connect/controller/PlayResumeEndpointPlayerStateControllerTest.java) for further info