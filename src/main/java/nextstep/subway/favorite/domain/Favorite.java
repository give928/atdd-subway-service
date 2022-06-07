package nextstep.subway.favorite.domain;

import nextstep.subway.member.domain.Member;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Favorite {
    private static final String ERROR_MESSAGE_SAME_SOURCE_TARGET = "출발역과 도착역을 다른역으로 즐겨찾기를 등록해주세요.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="source_station_id", nullable = false)
    private Station source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="target_station_id", nullable = false)
    private Station target;

    public Favorite() {
    }

    public Favorite(Member member, Station source, Station target) {
        this(null, member, source, target);
    }

    public Favorite(Long id, Member member, Station source, Station target) {
        validate(source, target);
        this.id = id;
        this.member = member;
        this.source = source;
        this.target = target;
    }

    private void validate(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_SAME_SOURCE_TARGET);
        }
    }

    public boolean isOwner(Long memberId) {
        return member.getId().equals(memberId);
    }

    public Long getId() {
        return id;
    }

    public Station getSource() {
        return source;
    }

    public Station getTarget() {
        return target;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Favorite favorite = (Favorite) o;
        return Objects.equals(getId(), favorite.getId()) && Objects.equals(getSource(),
                                                                           favorite.getSource()) && Objects.equals(
                getTarget(), favorite.getTarget()) && Objects.equals(getMember(), favorite.getMember());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSource(), getTarget(), getMember());
    }
}
