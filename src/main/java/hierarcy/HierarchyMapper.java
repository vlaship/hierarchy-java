package hierarcy;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HierarchyMapper {

    public Map<Integer, Hierarchy> mapLeavesToAncestors(Node root) {
        if (root == null) {
            return Map.of();
        }

        var result = new HashMap<Integer, Hierarchy>();
        var currentPath = new LinkedList<Node>();
        findLeavesAndMapAncestors(root, currentPath, result);
        return result;
    }

    private void findLeavesAndMapAncestors(Node currentNode, List<Node> currentPath, Map<Integer, Hierarchy> result) {
        currentPath.add(currentNode);

        if (currentNode.leafNode()) {
            Integer bacId = null;
            Integer acId = null;
            Integer scId = null;
            Integer slId = null;

            for (var nodeOnPath : currentPath) {
                switch (nodeOnPath.tier()) {
                    case BAC -> bacId = nodeOnPath.id();
                    case AC  -> acId  = nodeOnPath.id();
                    case SC  -> scId  = nodeOnPath.id();
                    case SL  -> slId  = nodeOnPath.id();
                }
            }

            result.put(currentNode.id(), new Hierarchy(bacId, acId, scId, slId));

        } else {
            // Not a leaf, recurse into children
            if (currentNode.children() != null && !currentNode.children().isEmpty()) {
                for (var child : currentNode.children()) {
                    findLeavesAndMapAncestors(child, currentPath, result);
                }
            }
        }

        currentPath.removeLast();
    }
}
