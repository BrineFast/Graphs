package GraphStructures;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Path implements Comparable<Path> {

    private List<String> way;
    private double cost;

    @Override
    public int compareTo(Path path2) {
        Double cost2 = path2.getCost();
        if (cost2.equals(cost))
            return 0;
        if (cost > cost2)
            return 1;
        return -1;
    }
}
