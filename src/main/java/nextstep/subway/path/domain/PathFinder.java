package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public final class PathFinder {
    private static final String ERROR_MESSAGE_CANNOT_FOUND_PATH = "%s ~ %s 경로를 조회할 수 없습니다.";

    private PathFinder() {
    }

    public static Path find(List<Line> lines, Station source, Station target) {
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = makeDijkstraShortestPath(lines);
        GraphPath<Station, DefaultWeightedEdge> graphPath = dijkstraShortestPath.getPath(source, target);
        if (graphPath == null) {
            throw new IllegalStateException(String.format(ERROR_MESSAGE_CANNOT_FOUND_PATH, source.getName(), target.getName()));
        }
        return Path.of(graphPath);
    }

    private static DijkstraShortestPath<Station, DefaultWeightedEdge> makeDijkstraShortestPath(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        initVertex(graph, lines);
        initEdge(graph, lines);
        return new DijkstraShortestPath<>(graph);
    }

    private static void initVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Line> lines) {
        lines.stream()
                .flatMap(line -> line.getStations().stream())
                .distinct()
                .forEach(graph::addVertex);
    }

    private static void initEdge(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Line> lines) {
        lines.stream()
                .flatMap(line -> line.getSections().stream())
                .forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }
}
