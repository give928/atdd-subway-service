package nextstep.subway.favorite.exception;

public class FavoriteNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -7250912224028812820L;

    public FavoriteNotFoundException() {
        super("즐겨찾기를 찾을 수 없습니다.");
    }

    public FavoriteNotFoundException(String message) {
        super(message);
    }
}
