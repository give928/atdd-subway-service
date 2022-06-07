package nextstep.subway.favorite.exception;

import nextstep.subway.common.exception.ValidationException;

public class FavoriteNotFoundException extends ValidationException {
    private static final long serialVersionUID = -7250912224028812820L;

    public FavoriteNotFoundException() {
        super("즐겨찾기를 찾을 수 없습니다.");
    }
}
