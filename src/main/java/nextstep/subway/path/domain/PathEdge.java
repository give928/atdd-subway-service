package nextstep.subway.path.domain;

import org.jgrapht.graph.DefaultWeightedEdge;

public class PathEdge extends DefaultWeightedEdge {
    private static final long serialVersionUID = -3844898922741141875L;

    private final int extraFare;

    private PathEdge(int extraFare) {
        this.extraFare = extraFare;
    }

    public static PathEdge of(int extraFare) {
        return new PathEdge(extraFare);
    }

    public int getExtraFare() {
        return extraFare;
    }
}
