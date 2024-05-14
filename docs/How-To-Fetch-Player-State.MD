### Get the currently playing state

Returns an information about player that is currently active. If playback is paused, then returns nothing.

#### Request

```http request
GET /player/currently-playing
Authorization: Bearer user_access_token
```

#### Response

Returns an info about state

- shuffle_state - boolean, Not null, a shuffle mode of the player. True - shuffle enabled, false - disabled.
- is_playing - boolean, not null, always true.
- currently_playing_type - String, not null, type that is currently playing. Supported values are: TRACK, PODCAST
- repeat_state - String, not null, repeat state of the player. Supported values are: OFF, TRACK, CONTEXT
- progress_ms - long, not null, progress of the track
- playing_item - Object, type of the item that is playing one. Only one is supported now: TrackItemObject 

<details>
    <summary>TrackItemObject</summary>
    <ul>
        <li>
            Id - ID of the track
        </li>
        <li>
            URI - context uri of the track, currently always null
        </li>
        <li>
            type - type of the entity. Always TRACK
        </li>
    </ul>
</details>

- devices - Object, devices connected to the player. Array of DeviceObject


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
  "is_playing": true,
  "repeat_state": "OFF",
  "shuffle_state": false,
  "currently_playing_type": "track",
  "progress_ms": 4007,
  "playing_item": {
    "id": "miku123",
    "uri": null,
    "type": "TRACK"
  }
}
```
</details>

