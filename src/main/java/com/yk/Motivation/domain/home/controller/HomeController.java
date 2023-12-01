package com.yk.Motivation.domain.home.controller;

import com.yk.Motivation.domain.lecture.entity.Lecture;
import com.yk.Motivation.domain.lecture.entity.LectureTag;
import com.yk.Motivation.domain.lecture.service.LectureService;
import com.yk.Motivation.domain.lecture.service.LectureTagService;
import com.yk.Motivation.domain.qna.entity.Question;
import com.yk.Motivation.domain.qna.service.QuestionService;
import com.yk.Motivation.domain.series.entity.Series;
import com.yk.Motivation.domain.series.service.SeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final SeriesService seriesService;
    private final LectureService lectureService;
    private final QuestionService questionService;
    private final LectureTagService lectureTagService;

    @GetMapping("/")
    public String showMain(Model model) {

        model.addAttribute("series", seriesService.findTop4Series());
        model.addAttribute("lectures", lectureService.findTop4Lectures());
        model.addAttribute("questions", questionService.findbyTop3Questions());
        model.addAttribute("tags", processLectureTags(lectureTagService.findAll()));

        return "usr/home/main";
    }

    @GetMapping("/new")
    public String showNew() {
        return "usr/home/new";
    }

    private List<List<LectureTag>> processLectureTags(List<LectureTag> lectureTags) {
        final int GROUP_SIZE = 4;
        List<List<LectureTag>> tags = new ArrayList<>();

        for (int i = 0; i < lectureTags.size(); i += GROUP_SIZE) {
            tags.add(lectureTags.subList(i, Math.min(i + GROUP_SIZE, lectureTags.size())));
        }

        return tags;
    }
}