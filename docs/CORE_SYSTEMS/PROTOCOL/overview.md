# Hytale Modding: Networking Overview

The Hytale server incorporates a sophisticated networking layer, built upon [Netty](https://netty.io/). This robust foundation supports both QUIC (UDP) and TCP transports, providing flexibility and performance for various communication needs within the game.

## Core Networking Characteristics:

*   **Transport Protocols:** Utilizes both QUIC (UDP-based) and TCP. QUIC offers advantages in terms of reduced connection establishment latency and improved multiplexing, while TCP provides reliable, ordered packet delivery.
*   **Byte Order:** The protocol uses **little-endian** byte order. This is a crucial detail for any modder dealing with raw byte manipulation or custom packet serialization.
*   **Compression:** Large packets are compressed using **Zstd**. This helps in reducing bandwidth usage and improving network performance.
*   **Variable-Length Integers:** The protocol employs variable-length integer encoding. This is an optimization to save bandwidth by using fewer bytes for smaller integer values and more bytes only when larger values are necessary.

## Packet Handling:

At the core of data exchange are `Packet` objects:

*   **`Packet` Interface:** This interface defines the fundamental contract for all network packets. It includes methods such as:
    *   `getId()`: Returns a unique identifier for the packet type.
    *   `serialize(ByteBuf buffer)`: Writes the packet's data into a `ByteBuf` (Netty's byte buffer implementation) for transmission.
    *   `computeSize()`: Calculates the size of the packet data, which is useful for buffer allocation and potentially for compression decisions.

## Packet Interception:

Modders can intercept and manipulate packets using `PacketAdapters`:

*   **`PacketAdapters.registerInbound()`:** This method allows a plugin to register a custom filter to inspect, modify, or even block incoming packets.
*   **`PlayerPacketFilter`:** When registering an inbound adapter, a `PlayerPacketFilter` can be used. This filter enables conditional processing of packets, potentially based on the player sending or receiving the packet, or the packet's content. It allows modders to:
    *   **Consume Packets:** Prevent a packet from reaching its intended recipient or further processing.
    *   **Block Packets:** Similar to consuming, but might indicate an explicit rejection.
    *   **Modify Packets:** Alter the content of a packet before it is processed by the server or client.

### No direct code examples were provided on the source page for this overview.
The content describes interfaces and methods but does not offer Java code snippets demonstrating their implementation or usage. For conceptual examples, refer to the general ECS patterns.
