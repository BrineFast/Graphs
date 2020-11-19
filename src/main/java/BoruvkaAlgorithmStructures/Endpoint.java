package BoruvkaAlgorithmStructures;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Endpoint {

    private String nodeU;
    private String nodeV;
    private Integer weight;

}
