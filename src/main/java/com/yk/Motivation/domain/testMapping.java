package com.yk.Motivation.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class testMapping {
    @GetMapping("/t1")
    public String t1() {
        return "usr/test/list";
    }
    @GetMapping("/t2")
    public String t2() {
        return "usr/test/detail";
    }
}
