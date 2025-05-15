package hierarcy;

import java.util.Arrays;
import java.util.List;

public record Node(int id, List<Node> children, boolean leafNode, Tier tier) {
    public Node(int id, Tier tier, Node... children) {
        this(id, children != null ? Arrays.asList(children) : List.of(), children == null || children.length == 0, tier);
    }
}
