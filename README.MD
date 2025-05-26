# Hierarchy Leaf Node to Ancestor Mapping

This project aims to traverse a specific multi-tier hierarchy and generate a mapping from each "leaf" node's associated instruments to its
corresponding ancestor node IDs at each defined tier level.

## Problem Description

We have a hierarchical structure composed of nodes. The hierarchy is strictly defined across four tiers:

1. **BAC** (Broad Asset Class)
2. **AC** (Asset Class)
3. **SC** (Sub-Class)
4. **SL** (Style)

Each node in this hierarchy is defined by the following structure:

```
Node {
    int nodeId,
    Node[] children,
    Instrument[] instruments,
    int tier
}
```

A node is considered a **leaf node** in the context of instrument mapping if it has associated instruments (`instruments` list is not null or empty),
regardless of whether it has children or its tier level. This means a node at tier 1, 2, 3, or 4 could potentially have instruments.

The objective is to process this hierarchy and produce a map where:

* The **key** is an `Instrument` found at a node.
* The **value** is an object (or record) containing the `id`s of its direct ancestors at each of the four tier levels (BAC, AC, SC, SL).

The mapping for an instrument should include the ID of the ancestor node found at each tier on the path from the root down to the node where the
instrument is located. If a specific tier is skipped on the path to the instrument's node (e.g., an AC node contains instruments, so there's no SC or
SL node below it), the corresponding ancestor ID in the output structure should be represented as `null`. Similarly, if the node containing the
instrument itself resides at one of the defined tiers, its own ID should be used for that tier in the mapping.

The desired output structure for the value in the map should be something like:

```
Hierarchy {
    Integer bacNodeId,
    Integer acNodeId,
    Integer scNodeId,
    Integer slNodeId
}
```

*(Note: Using `Integer` allows for `null` values if a specific tier is not present on the path to the instrument's node)*

## Requirements

1. Implement the solution using **Java 17**. Leverage modern Java 17 features where appropriate (e.g., records, pattern matching, switch expressions).
2. The input is the root `Node` of the hierarchy (expected to be a BAC node, though the code should handle other root tiers or a null root
   gracefully).
3. The output is a `Map<Instrument, Hierarchy>` as described above.
4. Write comprehensive **JUnit 5** tests to verify the correctness of the mapping logic across various hierarchy structures and instrument
   placements (including instruments at different tiers, skipped tiers, null children arrays, etc.).

## Implementation Details (Suggestions)

* A recursive depth-first traversal is a natural fit for exploring the hierarchy and tracking the path from the root to each node.
* Maintain the current path during traversal to easily identify ancestors at each tier when a node with instruments is encountered.
* The `Node`, `Instrument`, and `Hierarchy` structures can be effectively modeled using Java 17 `record` types for conciseness.
* Use an `enum` for the tier levels to provide type safety and clarity over using raw integer constants.
