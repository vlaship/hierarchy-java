package hierarcy;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HierarchyMapper {
    public Map<Instrument, Hierarchy> mapInstrumentsToHierarchy(Node root) {
        if (root == null) {
            return Map.of();
        }

        var result = new HashMap<Instrument, Hierarchy>();
        var currentPath = new LinkedList<Node>();
        findInstrumentsAndMapHierarchy(root, currentPath, result);
        return result;
    }

    private void findInstrumentsAndMapHierarchy(
            Node currentNode,
            List<Node> currentPath,
            Map<Instrument, Hierarchy> result
    ) {
        currentPath.add(currentNode);

        if (currentNode.instruments() != null && !currentNode.instruments().isEmpty()) {
            Integer bacId = null;
            Integer acId = null;
            Integer scId = null;
            Integer slId = null;

            for (var nodeOnPath : currentPath) {
                switch (nodeOnPath.tier()) {
                    case BAC -> bacId = nodeOnPath.id();
                    case AC -> acId = nodeOnPath.id();
                    case SC -> scId = nodeOnPath.id();
                    case SL -> slId = nodeOnPath.id();
                }
            }

            Hierarchy hierarchy = new Hierarchy(bacId, acId, scId, slId);

            for (Instrument instrument : currentNode.instruments()) {
                result.put(instrument, hierarchy);
            }
        }

        if (currentNode.children() != null && !currentNode.children().isEmpty()) {
            for (var child : currentNode.children()) {
                findInstrumentsAndMapHierarchy(child, currentPath, result);
            }
        }

        currentPath.removeLast();
    }
}
