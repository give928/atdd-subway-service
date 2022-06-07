package nextstep.subway.member.exception;

public class MemberNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 2873105302039040040L;

    public MemberNotFoundException() {
        super("회원을 찾을 수 없습니다.");
    }

    public MemberNotFoundException(String message) {
        super(message);
    }
}
