package nextstep.subway.line.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> values = new ArrayList<>();

    public List<Section> get() {
        return values;
    }

    public void add(Section section) {
        values.add(section);
    }
}
