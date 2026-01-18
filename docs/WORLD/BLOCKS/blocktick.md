# com.hypixel.hytale.builtin.blocktick

This package contains features related to block ticking.

## Overview

The `blocktick` package provides a system for scheduling and executing block ticks. This can be used to create blocks that change over time, such as plants that grow or furnaces that smelt ore.

### Key Classes

*   `BlockTickPlugin`: The main plugin class for the block tick feature.
*   `ChunkBlockTickSystem`: A system that manages block ticks for a chunk.
*   `MergeWaitingBlocksSystem`: A system that merges waiting blocks.
*   `BasicChanceBlockGrowthProcedure`: A procedure that grows a block based on a chance.
*   `SplitChanceBlockGrowthProcedure`: A procedure that grows a block based on a split chance.

### Sub-packages

*   `procedure`: Contains procedures that can be executed on a block tick.
*   `system`: Contains systems that manage block ticks.
