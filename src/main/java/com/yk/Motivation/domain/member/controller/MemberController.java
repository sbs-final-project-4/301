package com.yk.Motivation.domain.member.controller;

import com.yk.Motivation.domain.member.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usr/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/join")
    public String showJoin() {
        return "usr/member/join";
    }

    @Getter
    @AllArgsConstructor
    public static class JoinForm {

        @NotBlank
        private String username;

        @NotBlank
        private String password;
    }

    @PostMapping("/join")
    public String join(@Valid JoinForm joinForm) {
        memberService.join(joinForm.getUsername(), joinForm.getPassword(), joinForm.getUsername());

        return "redirect:/";
    }
}