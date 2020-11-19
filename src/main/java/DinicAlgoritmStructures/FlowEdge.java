package DinicAlgoritmStructures;

import lombok.Data;

@Data
public class FlowEdge {

    private String targerVertex;
    private Integer reversedVertex;
    private Double value;
    private Double flow;

    public FlowEdge(String targerVertex, Integer reversedVertex, Double value){
        this.targerVertex = targerVertex;
        this.reversedVertex = reversedVertex;
        this.value = value;
        this.flow = 0.0;
    }

}
