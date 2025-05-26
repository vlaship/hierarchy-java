package hierarcy;

import java.util.Arrays;
import java.util.List;

public record Node(int id, List<Node> children, List<Instrument> instruments, Tier tier) {

    public Node(int id, Tier tier, List<Instrument> instruments, Node... children) {
        this(id,
                children != null ? Arrays.asList(children) : List.of(),
                instruments != null ? instruments : List.of(),
                tier);
    }

    public Node(int id, Tier tier, Node... children) {
        this(id, tier, List.of(), children);
    }
}
