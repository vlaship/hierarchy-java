# Hierarchy Leaf Node to Ancestor Mapping

This project aims to traverse a specific multi-tier hierarchy and generate a mapping from each "leaf" node to its corresponding ancestor node IDs at
each defined tier level.

## Problem Description

We have a hierarchical structure composed of nodes. The hierarchy is strictly defined across four tiers:

1. **BAC** (Broad Asset Class)
2. **AC** (Asset Class)
3. **SC** (Sub-Class)
4. **SL** (Style)

Each node in this hierarchy is defined by the following structure:

```
node {
    int id,          // Unique identifier for the node
    node[] children, // Array of child nodes
    bool leafNode,   // Flag indicating if this node is a leaf
    int tier         // The tier level of this node (1, 2, 3, or 4)
}
```

A node is considered a **leaf node** if it has no children (`children` array is null or empty), regardless of its tier level. This means a node at
tier 1, 2, 3, or 4 could potentially be a leaf node if it doesn't have any nodes below it.

The objective is to process this hierarchy and produce a map where:

* The **key** is the `id` of a **leaf node**.
* The **value** is an object (or record) containing the `id`s of its direct ancestors at each of the four tier levels (BAC, AC, SC, SL).

The mapping for a leaf node should include the ID of the ancestor node found at each tier on the path from the root down to the leaf. If a specific
tier is skipped on the path to a leaf (e.g., an AC node is a leaf, so there's no SC or SL node below it), the corresponding ancestor ID in the output
structure should be represented as `null`. Similarly, if the leaf node itself resides at one of the defined tiers, its own ID should be used for that
tier in the mapping.

The desired output structure for the value in the map should be something like:

```
Hierarchy {
    Integer bacNodeId,
    Integer acNodeId,
    Integer scNodeId,
    Integer slNodeId
}
```
*(Note: Using `Integer` allows for `null` values if a specific tier is not present on the path to the leaf)*

## Requirements

1.  Implement the solution using **Java 17**. Leverage modern Java 17 features where appropriate (e.g., records, pattern matching, switch expressions).
2.  The input is the root `Node` of the hierarchy (expected to be a BAC node, though the code should handle other root tiers or a null root gracefully).
3.  The output is a `Map<Integer, Hierarchy>` as described above.
4.  Write comprehensive **JUnit 5** tests to verify the correctness of the mapping logic across various hierarchy structures and leaf node placements (including leaves at different tiers, skipped tiers, null children arrays, etc.).

## Implementation Details (Suggestions)

* A recursive depth-first traversal is a natural fit for exploring the hierarchy and tracking the path from the root to each node.
* Maintain the current path during traversal to easily identify ancestors at each tier when a leaf node is encountered.
* The `Node` and `Hierarchy` structures can be effectively modeled using Java 17 `record` types for conciseness.
* Use an `enum` for the tier levels to provide type safety and clarity over using raw integer constants.
