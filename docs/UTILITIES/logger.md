# com.hypixel.hytale.logger

This package contains logging-related classes.

## Overview

The `logger` package provides a custom logging framework for the Hytale server. It integrates with Java's standard logging API and provides additional features such as file logging, Sentry integration for error reporting, and utility methods for formatting and displaying log messages.

### Key Classes

*   `HytaleLogger`: The main logger class, providing methods for logging messages at various levels.
*   `HytaleFileHandler`: A log handler that writes log messages to a file.
*   `HytaleLoggerBackend`: Provides backend functionality for the Hytale logger.
*   `HytaleSentryHandler`: A log handler that sends error reports to Sentry.
*   `SkipSentryException`: An exception that can be thrown to prevent Sentry from reporting an error.

### Sub-packages

*   `backend`: Contains backend implementations for the logging framework.
*   `sentry`: Contains classes for Sentry integration.
*   `util`: Contains utility classes for logging.
