package nextstep.subway.path.domain;

import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;

import java.util.List;

public class Path {
    private final GraphPath<Station, PathEdge> graphPath;

    private Path(GraphPath<Station, PathEdge> graphPath) {
        this.graphPath = graphPath;
    }

    public static Path of(GraphPath<Station, PathEdge> graphPath) {
        return new Path(graphPath);
    }

    public List<Station> getStations() {
        return graphPath.getVertexList();
    }

    public int getDistance() {
        return (int) graphPath.getWeight();
    }

    public int getMaxExtraFare() {
        return graphPath.getEdgeList().stream()
                .mapToInt(PathEdge::getExtraFare)
                .max()
                .orElse(0);
    }
}
