package com.scrapeverything.app.util

object ErrorMessages {
    fun getMessage(code: String): String = when (code) {
        "EMAIL_ALREADY_EXISTS" -> "이미 가입된 이메일입니다"
        "EMAIL_SEND_ERROR" -> "이메일 전송에 실패했습니다"
        "EMAIL_NOT_CHECKED" -> "이메일 인증이 완료되지 않았습니다"
        "NICKNAME_DUPLICATED" -> "이미 사용 중인 닉네임입니다"
        "MEMBER_NOT_FOUND" -> "존재하지 않는 계정입니다"
        "PASSWORD_NOT_MATCH" -> "비밀번호가 일치하지 않습니다"
        "MEMBER_WITHDRAWAL" -> "탈퇴한 계정입니다"
        "INVALID_AUTH_HEADER" -> "인증 정보가 올바르지 않습니다"
        "INVALID_JWT_TOKEN" -> "로그인이 만료되었습니다"
        "INVALID_REFRESH_TOKEN" -> "다시 로그인해주세요"
        "REFRESH_TOKEN_COOKIE_NOT_FOUND" -> "다시 로그인해주세요"
        "AUTH_HEADER_NOT_FOUND" -> "로그인이 필요합니다"
        "BACKUP_NOT_FOUND" -> "백업된 데이터가 없습니다"
        "CONTAINS_FORBIDDEN_WORD" -> "사용할 수 없는 단어가 포함되어 있습니다"
        "400" -> "잘못된 요청입니다"
        "401" -> "로그인이 필요합니다"
        "409" -> "이미 존재하는 값입니다"
        else -> "오류가 발생했습니다"
    }
}
