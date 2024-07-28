## Sonata-Connect

### Synchronize all your devices, control playback and switch between devices in one click!

Sonata-Connect is a part of Sonata-Project that provides the ability to control your devices using HTTP endpoints,
allows to authenticate your device without using your password in secure way and switch between devices without any
problem!

## Table of contents

* [API endpoints](#api-endpoints)
* [Device authentication](#device-authentication)
* [How it works?](#how-it-works)


### API endpoints

- [Get connected devices](docs/How-To-Fetch-Connected-Devices.MD)
- [Connect device](docs/How-To-Connect-Device.MD)
- [Disconnect device](docs/How-To-Disconnect-Device.MD)
- [Switch between devices](docs/How-To-Switch-Devices.MD)
- [Consume updates from player in real time](docs/How-To-Consume-Real-Time-Updates-From-The-Player.MD)
- [Get the current player state](docs/How-To-Fetch-Player-State.MD)
- [Start or resume playback](docs/How-To-Start-Playback.MD)
- [Pause playback](docs/How-To-Pause-Playback.MD)

# Device authentication

Here is described how Sonata-Connect works to authenticate device without using password

Terminology:

- Device X - not connected device.
- Device A - already connected device to Sonata-Connect that capable for music streaming and playback manipulation.
- SCAT - Sonata-Connect authentication token. Used to authenticate the device without password.

Requirements:

- Device supports MDNS
- Device supports JSON

When device X is launched, then it registers itself in MDNS network.
Device A listens to event when a Device X has been registered and then sends an HTTP request to connect device X

Types of devices that can be registered with Sonata-Connect flow:

- TV
- Speakers

After that a device is appeared on user screen and user can start playback on Device X.

- User clicks on device X on Sonata-Connect form. 
- Device A sends request to Sonata-Connect,
- Sonata-Connect generate SCAT that contains info about target device.
- Device A receives in response SCAT token, then SCAT is being sent to Device X using MDNS in local WI-FI area.

Then target device X do:

- retrieve SCAT from request
- send request to Sonata-Connect to exchange SCAT for access token
- Sonata-Connect checks token validity and if it's valid sends access token
- target device got access token and now can stream music and control playback.

# How it works?

Microservice store the current player state for each user in cache(In memory or Redis for better performance).

All operations with playback is performed using HTTP endpoints, any write operation to WebSocket connection is ignored.

Each operation that changes a player state sends a real-time update to WebSocket connection that every device can update its own state.

Messaging format is JSON.