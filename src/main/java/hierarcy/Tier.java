package hierarcy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Tier {
    BAC(1),
    AC(2),
    SC(3),
    SL(4);

    private final int level;
}
