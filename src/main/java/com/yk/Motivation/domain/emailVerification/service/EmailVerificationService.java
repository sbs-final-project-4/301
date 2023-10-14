package com.yk.Motivation.domain.emailVerification.service;

import com.yk.Motivation.base.app.AppConfig;
import com.yk.Motivation.base.rsData.RsData;
import com.yk.Motivation.domain.attr.service.AttrService;
import com.yk.Motivation.domain.email.service.EmailService;
import com.yk.Motivation.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailVerificationService {
    private final EmailService emailService;
    private final AttrService attrService;

    public CompletableFuture<RsData> send(Member member) { // 인증이메일 발사
        String subject = "[%s 이메일인증] 안녕하세요 %s님. 링크를 클릭하여 회원가입을 완료해주세요.".formatted(AppConfig.getSiteName(), member.getUsername());
        String body = genEmailVerificationUrl(member);

        return emailService.send(member.getEmail(), subject, body);
    }

    public String genEmailVerificationUrl(Member member) {
        return genEmailVerificationUrl(member.getId());
    }

    public String genEmailVerificationUrl(long memberId) {
        String code = genEmailVerificationCode(memberId);

        return AppConfig.getSiteBaseUrl() + "/emailVerification/verify?memberId=%d&code=%s".formatted(memberId, code);
    }

    public String genEmailVerificationCode(long memberId) {
        String code = UUID.randomUUID().toString();
        attrService.set("member__%d__extra__emailVerificationCode".formatted(memberId), code, LocalDateTime.now().plusSeconds(60 * 60 * 1));

        return code;
    }

    public RsData verifyVerificationCode(long memberId, String code) {
        String foundCode = attrService.get("member__%d__extra__emailVerificationCode".formatted(memberId), "");

        if (foundCode.equals(code) == false) {
            return RsData.of("F-1", "만료되었거나 유효하지 않은 코드입니다.");
        }

        return RsData.of("S-1", "인증된 코드 입니다.");
    }
}