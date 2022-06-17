package nextstep.subway.member.exception;

import nextstep.subway.common.exception.ValidationException;

public class MemberNotFoundException extends ValidationException {
    private static final long serialVersionUID = 2873105302039040040L;

    public MemberNotFoundException() {
        super("회원을 찾을 수 없습니다.");
    }
}
