# com.hypixel.hytale.codec

This package contains classes for encoding and decoding data.

## Overview

The `codec` package provides a flexible and extensible framework for encoding and decoding data, particularly for game assets and network packets. It supports various data formats and allows for schema-driven validation and inheritance.

### Key Classes

*   `Codec`: An interface for encoding and decoding data.
*   `KeyedCodec`: A codec that uses a key to identify data.
*   `DocumentContainingCodec`: A codec that contains a document.
*   `InheritCodec`: A codec that inherits from another codec.
*   `BuilderCodec`: A codec that can be built programmatically.
*   `CodecStore`: A store for codecs.
*   `ValidationResults`: Stores the results of a codec validation.

### Sub-packages

*   `builder`: Contains classes for building codecs.
*   `codecs`: Contains various codec implementations for common data types (e.g., arrays, maps, primitives).
*   `exception`: Contains exceptions related to codec operations.
*   `function`: Contains functional interfaces used by codecs.
*   `lookup`: Contains classes for looking up codecs.
*   `schema`: Contains classes for defining and validating data schemas.
*   `store`: Contains classes for storing and retrieving codecs.
*   `util`: Contains utility classes for codecs.
*   `validation`: Contains classes for validating data.
