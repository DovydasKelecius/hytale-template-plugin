# Hytale Modding: Networking Protocol & Packets

Hytale's networking protocol is built on a robust Netty-based layer, supporting both QUIC (UDP) and TCP transports. Understanding its structure is crucial for defining custom network communication.

## Protocol Structure:

The fundamental framing of each packet consists of:

*   **Length Prefix:** A 4-byte length field.
*   **Packet ID:** A 4-byte packet ID.
*   **Payload:** The actual packet data, which is Zstd-compressed for larger packets to optimize bandwidth.

Key characteristics and encoding:

*   **Byte Order:** The protocol uses **little-endian** byte order. This is a vital detail for correct byte manipulation.
*   **VarInt Encoding:** Variable-length integers (VarInt) are used for small numeric values, providing efficient bandwidth usage by only consuming more bytes for larger numbers.
*   **String Encoding:** Strings are encoded as UTF-8, prefixed with a VarInt indicating their length.

## Packet Definition:

Custom packets are defined by implementing the `Packet` interface. This interface requires three key methods:

*   **`getId()`:** Returns a unique integer identifier for the packet type. This ID is used by the server and client to recognize and route the packet.
*   **`serialize(ByteBuf buffer)`:** Responsible for writing the packet's internal data into a Netty `ByteBuf` in the correct protocol format (little-endian, VarInts, etc.).
*   **`computeSize()`:** Calculates the size of the packet's payload before serialization, which is necessary for allocating buffer space and compression.

## Packet I/O Utilities:

Hytale provides utility functions for reading and writing various data types to `ByteBuf` instances, simplifying packet serialization:

*   Strings (UTF-8 with VarInt length prefix).
*   UUIDs.
*   Numeric arrays (e.g., `float[]`, `int[]`).

## Packet Interception and Handling:

Modders can interact with the network flow at various points:

*   **`PacketAdapters`:** Used to register custom logic for filtering and intercepting packets, both inbound and outbound. This allows for powerful control, such as blocking specific packets, modifying their content, or triggering custom events based on network traffic.
*   **Packet Handlers:** These are responsible for processing packets once they are received and identified.
*   **`CachedPacket`:** This utility is mentioned as a mechanism for efficient broadcasting, likely pre-serializing a packet once to avoid repeated serialization when sending it to multiple recipients.

### Code Example: CustomPacket

The documentation explicitly mentions a `CustomPacket` example, which defines a simple custom packet for demonstration. This example will be extracted and analyzed separately.
