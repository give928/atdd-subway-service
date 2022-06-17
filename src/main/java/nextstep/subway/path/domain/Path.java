package nextstep.subway.path.domain;

import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.util.List;

public class Path {
    private final GraphPath<Station, DefaultWeightedEdge> graphPath;

    private Path(GraphPath<Station, DefaultWeightedEdge> graphPath) {
        this.graphPath = graphPath;
    }

    public static Path of(GraphPath<Station, DefaultWeightedEdge> graphPath) {
        return new Path(graphPath);
    }

    public List<Station> getStations() {
        return graphPath.getVertexList();
    }

    public int getDistance() {
        return (int) graphPath.getWeight();
    }
}
