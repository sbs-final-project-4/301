package com.yk.Motivation.base.initData;

import com.yk.Motivation.base.app.AppConfig;
import com.yk.Motivation.domain.article.entity.Article;
import com.yk.Motivation.domain.article.service.ArticleService;
import com.yk.Motivation.domain.board.entity.Board;
import com.yk.Motivation.domain.board.service.BoardService;
import com.yk.Motivation.domain.lecture.entity.Lecture;
import com.yk.Motivation.domain.lesson.entity.Lesson;
import com.yk.Motivation.domain.lesson.service.LessonService;
import com.yk.Motivation.domain.product.service.ProductService;
import com.yk.Motivation.domain.qna.entity.Question;
import com.yk.Motivation.domain.qna.service.QuestionService;
import com.yk.Motivation.domain.series.service.SeriesService;
import com.yk.Motivation.domain.lecture.service.LectureService;
import com.yk.Motivation.domain.member.entity.Member;
import com.yk.Motivation.domain.member.service.MemberService;
import com.yk.Motivation.domain.post.entity.Post;
import com.yk.Motivation.domain.post.service.PostService;
import com.yk.Motivation.domain.postKeyword.entity.PostKeyword;
import com.yk.Motivation.domain.system.service.SystemService;
import com.yk.Motivation.standard.util.Ut;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


@Configuration
@Profile("!prod")  // 이 구성은 'prod' 프로파일이 활성화되지 않은 경우에만 적용된다.
public class NotProd {

//    @Value("${custom.security.oauth2.client.registration.kakao.devUserOauthId}") // 개발 중 테스트를 위해 사용 했었음.
//    private String kakaoDevUserOAuthId;

    @Autowired
    @Lazy
    private NotProd self;
    @Autowired
    private BoardService boardService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private PostService postService;
    @Autowired
    private LectureService lectureService;
    @Autowired
    private SystemService systemService;
    @Autowired
    private SeriesService seriesService;
    @Autowired
    private LessonService lessonService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ProductService productService;


    @Bean
    public ApplicationRunner initNotProd() {
        return args -> {
            if (systemService.isNotProdInitDataConfigured() == false) {
                self.work1();
                self.work2();
                self.work5();
                self.work6();
                self.work7();

                systemService.setNotProdInitDataConfigured(true);
            }

            self.work3();

        };
    }

    @Transactional
    public void work1() {
        Board board1 = boardService.make("notice1", "공지사항", "<i class=\"fa-regular fa-flag\"></i>").getData();
        Board board2 = boardService.make("free1", "자유", "<i class=\"fa-solid fa-face-grin-tears\"></i>").getData();

        Member member1 = memberService.join("admin", "1234", "admin", "admin@test.com", "").getData();
        Member member2 = memberService.join("user1", "1234", "nickname1", "user1@test.com", "").getData();
        Member member3 = memberService.join("user2", "1234", "nickname2", "user2@test.com", "").getData();
        Member member4 = memberService.join("user3", "1234", "nickname3", "user3@test.com", "").getData();

        memberService.setEmailVerified(member1);
        memberService.setEmailVerified(member2);
        memberService.setEmailVerified(member3);

        memberService.beProducer(member1.getId(), "장필우");
        memberService.beProducer(member2.getId(), "고니");

//        Member memberByKakao = memberService.whenSocialLogin("KAKAO", "KAKAO__%s".formatted(kakaoDevUserOAuthId), "홍길동", "");

        Article article1 = articleService.write(board1, member1, "제목 1", "#자바 #HTML", "내용 1").getData();
        Article article2 = articleService.write(board1, member2, "제목 2", "#CSS #HTML #홍길동", "내용 2").getData();
        Article article3 = articleService.write(board2, member1, "제목 3", "#자바의 정석", "내용 3").getData();
        Article article4 = articleService.write(board2, member2, "제목 4", "#홍길동", "내용 4").getData();

        String file1Path = Ut.file.tempCopy(AppConfig.getResourcesStaticDirPath() + "/resource/common/common.css");
        String file2Path = Ut.file.tempCopy(AppConfig.getResourcesStaticDirPath() + "/resource/common/common.js");
        articleService.saveAttachmentFile(article1, file1Path, 1L);
        articleService.saveAttachmentFile(article1, file2Path, 2L);

        Post post1 = postService.write(member2, "제목 1", "#자바 #HTML", "내용 1", true).getData();

        PostKeyword postKeywordHtml = post1.getPostTags()
                .stream()
                .filter(postTag -> postTag.getContent().equals("HTML"))
                .map(postTag -> postTag.getPostKeyword())
                .findFirst()
                .get();

        String file3Path = Ut.file.tempCopy(AppConfig.getResourcesStaticDirPath() + "/resource/common/common.css");
        String file4Path = Ut.file.tempCopy(AppConfig.getResourcesStaticDirPath() + "/resource/common/common.js");
        postService.saveAttachmentFile(post1, file3Path, 1L);
        postService.saveAttachmentFile(post1, file4Path, 2L);

        Post post2 = postService.write(member1, "제목 2", "#CSS #HTML #Python", "내용 2", true).getData();
        Post post3 = postService.write(member1, "제목 3", "#Java #HTML", "내용 3", true).getData();
        Post post4 = postService.write(member2, "제목 4", "#HTML #Script", "내용 4", false).getData();
        Post post5 = postService.write(member2, "제목 5", "#Java #JSP", "내용 5", false).getData();
        Post post6 = postService.write(member2, "제목 6", "#CSS #Hungry #HTML", "내용 6", true).getData();

        seriesService.write(member2, postKeywordHtml, "나의 HTML 이야기", "#HTML", true);

//        lectureService.write(member1, "제목1", "#HTML", "내용1", true);
//        lectureService.write(member1, "제목2", "#HTML", "내용1", true);


    }

    @Transactional
    public void work2() {
        Article article1 = articleService.findById(1L).get();
        articleService.modify(article1, "제목 1 수정", "#자바2 #HTML", "내용 1\n수정", "내용 1<br />수정");
    }


    @Transactional
    public void work3() {

        Board board = boardService.findByCode("free1").get();
        Member member = memberService.join("user8", "1234", "자유게시판덕후", "user8@test.com", "").getData();
        memberService.setEmailVerified(member);

        IntStream.rangeClosed(1, 100).forEach(i -> {
            articleService.write(board, member, "제목" + i, "dummy" + i, "내용" + i);
        });
    }

    @Transactional
    public void work4() {

        Member member1 = memberService.join("user4", "1234", "nickname4", "user4@test.com", "").getData();
        Member member2 = memberService.join("user5", "1234", "nickname5", "user5@test.com", "").getData();

        memberService.setEmailVerified(member1);
        memberService.setEmailVerified(member2);

    }

    @Transactional
    public void work5() {

        Member member = memberService.join("user5", "1234", "nickname5", "user5@test.com", "").getData();
        memberService.beProducer(member.getId(), "코딩의 신");
        memberService.setEmailVerified(member);

        Lecture lecture1 = lectureService.write(member, "점프 투 스프링부트", "#Spring #SpringBoot", "내용", true).getData();
        productService.create(lecture1, member, 0, true);

        Lecture lecture2 = lectureService.write(member, "점프 투 리액트", "#React #리액트", "내용", true).getData();
        productService.create(lecture2, member, 0, true);

        Lecture lecture3 = lectureService.write(member, "점프 투 자바", "#Java #자바", "내용", true).getData();
        productService.create(lecture3, member, 0, true);

        Lecture lecture4 = lectureService.write(member, "점프 투 html/css", "#HTML #CSS", "내용", true).getData();
        productService.create(lecture4, member, 0, true);

        Lecture lecture5 = lectureService.write(member, "점프 투 Node.js", "#node.js #노드", "내용", true).getData();
        productService.create(lecture5, member, 0, true);

    }

    @Transactional
    public void work6() {

        Member member = memberService.join("user6", "1234", "초보개발자영규", "user6@test.com", "").getData();
        memberService.setEmailVerified(member);

        Post post1 = postService.write(member, "제목 1", "#자바 #HTML #ALGORITHM #SPRING", "내용 1", true).getData();

        PostKeyword postKeywordJava = post1.getPostTags()
                .stream()
                .filter(postTag -> postTag.getContent().equals("자바"))
                .map(postTag -> postTag.getPostKeyword())
                .findFirst()
                .get();

        PostKeyword postKeywordAlgorithm = post1.getPostTags()
                .stream()
                .filter(postTag -> postTag.getContent().equals("ALGORITHM"))
                .map(postTag -> postTag.getPostKeyword())
                .findFirst()
                .get();

        PostKeyword postKeywordSpring = post1.getPostTags()
                .stream()
                .filter(postTag -> postTag.getContent().equals("SPRING"))
                .map(postTag -> postTag.getPostKeyword())
                .findFirst()
                .get();

        postService.write(member, "제목 12", "#자바", "내용 12", true);
        postService.write(member, "제목 14", "#SPRING", "내용 14", true);

        postService.write(member, "프로그래머스 - 예산", "#ALGORITHM",
                "[문제링크 - 프로그래머스 - 예산\n" +
                "](https://school.programmers.co.kr/learn/courses/30/lessons/12982)\n" +
                "\n" +
                "```java\n" +
                "import java.util.Arrays;\n" +
                "\n" +
                "class Solution {\n" +
                "    public int solution(int[] d, int budget) {\n" +
                "\n" +
                "        Arrays.sort(d);\n" +
                "        int answer = 0;\n" +
                "        \n" +
                "        for (int i : d ) {\n" +
                "            \n" +
                "            budget -= i;\n" +
                "            \n" +
                "            if ( budget < 0) return answer;\n" +
                "            \n" +
                "            answer++;\n" +
                "        }\n" +
                "\n" +
                "        return answer;\n" +
                "    }\n" +
                "}", true);
        postService.write(member, "프로그래머스 - 옹알이 (2)", "#ALGORITHM",
                "[문제링크 - 프로그래머스 - 옹알이 (2)\n" +
                        "](https://school.programmers.co.kr/learn/courses/30/lessons/120803?language=java)\n" +
                        "\n" +
                        "```java\n" +
                        "public class Solution {\n" +
                        "    public int solution(String[] babbling) {\n" +
                        "\n" +
                        "\n" +
                        "        int answer = 0;\n" +
                        "\n" +
                        "        String[] canBabbling = {\"aya\", \"ye\", \"woo\", \"ma\"};\n" +
                        "\n" +
                        "        f1 : for (int i = 0; i < babbling.length; i++) {\n" +
                        "\n" +
                        "            for (int j = 0; j < 4; j++) {\n" +
                        "\n" +
                        "                for (String s : canBabbling) {\n" +
                        "                    if( babbling[i].contains(s + s)) continue f1;\n" +
                        "                }\n" +
                        "\n" +
                        "                if(babbling[i].trim().isEmpty()) {\n" +
                        "                    answer++;\n" +
                        "                    break;\n" +
                        "                }\n" +
                        "\n" +
                        "                if(babbling[i].contains(canBabbling[j])) {\n" +
                        "                    babbling[i] = babbling[i].replaceFirst(canBabbling[j], \" \");\n" +
                        "                    i--;\n" +
                        "                    break;\n" +
                        "                }\n" +
                        "            }\n" +
                        "        }\n" +
                        "\n" +
                        "        return answer;\n" +
                        "\n" +
                        "    }\n" +
                        "\n" +
                        "}\n" +
                        "```\n" +
                        "\n" +
                        "와 푸는데 너무 오래걸렸다;", true);
        postService.write(member, "프로그래머스 - 로또의 최고 순위와 최저 순위", "#ALGORITHM",
                "[문제링크 - 프로그래머스 - 로또의 최고 순위와 최저 순위\n" +
                        "](https://school.programmers.co.kr/learn/courses/30/lessons/77484)\n" +
                        "\n" +
                        "```java\n" +
                        "class Solution {\n" +
                        "    public int[] solution(int[] lottos, int[] win_nums) {\n" +
                        "\n" +
                        "        int count = 0;\n" +
                        "        int otherCount = 0;\n" +
                        "\n" +
                        "        for (int ln : lottos) {\n" +
                        "\n" +
                        "            if(ln == 0) count++;\n" +
                        "            else {\n" +
                        "                for ( int wn : win_nums) {\n" +
                        "                    if(ln == wn) otherCount++;\n" +
                        "                }\n" +
                        "            }\n" +
                        "        }\n" +
                        "\n" +
                        "        return new int[] {Math.min(7 - count - otherCount, 6), Math.min(7 - otherCount, 6)};\n" +
                        "    }\n" +
                        "}\n" +
                        "```", true);
        postService.write(member, "프로그래머스 - [1차] 다트 게임", "#ALGORITHM",
                "[문제링크 - 프로그래머스 - [1차] 다트 게임\n" +
                        "](https://school.programmers.co.kr/learn/courses/30/lessons/120803?language=java)\n" +
                        "\n" +
                        "```java\n" +
                        "public class Solution {\n" +
                        "    public int solution(String dartResult) {\n" +
                        "        int[] scores = new int[3];\n" +
                        "        int index = 0; \n" +
                        "\n" +
                        "        for (int i = 0; i < dartResult.length(); i++) {\n" +
                        "            char c = dartResult.charAt(i);\n" +
                        "\n" +
                        "            if (Character.isDigit(c)) {\n" +
                        "                int score = c - '0';\n" +
                        "                \n" +
                        "                if (score == 1 && i + 1 < dartResult.length() && dartResult.charAt(i + 1) == '0') {\n" +
                        "                    score = 10;\n" +
                        "                    i++;\n" +
                        "                }\n" +
                        "\n" +
                        "                scores[index] = score;\n" +
                        "            } else {\n" +
                        "                switch (c) {\n" +
                        "                    case 'S':\n" +
                        "                        scores[index] = (int) Math.pow(scores[index], 1);\n" +
                        "                        index++;\n" +
                        "                        break;\n" +
                        "                    case 'D':\n" +
                        "                        scores[index] = (int) Math.pow(scores[index], 2);\n" +
                        "                        index++;\n" +
                        "                        break;\n" +
                        "                    case 'T':\n" +
                        "                        scores[index] = (int) Math.pow(scores[index], 3);\n" +
                        "                        index++;\n" +
                        "                        break;\n" +
                        "                    case '*':\n" +
                        "                        if (index > 1) {\n" +
                        "                            scores[index - 2] *= 2;\n" +
                        "                        }\n" +
                        "                        scores[index - 1] *= 2;\n" +
                        "                        break;\n" +
                        "                    case '#':\n" +
                        "                        scores[index - 1] *= -1;\n" +
                        "                        break;\n" +
                        "                }\n" +
                        "            }\n" +
                        "        }\n" +
                        "\n" +
                        "        return scores[0] + scores[1] + scores[2];\n" +
                        "    }\n" +
                        "}\n" +
                        "```\n" +
                        "\n" +
                        "오늘 카카오 코딩테스트한테 뚜들겨 맞았다...\n" +
                        "\n" +
                        "후... 2018년 문제한테 복수했다.", true);



        seriesService.write(member, postKeywordJava, "자바를 자바", "#Java", true);
        seriesService.write(member, postKeywordSpring, "스프링은 봄", "#Spring", true);
        seriesService.write(member, postKeywordAlgorithm, "알고리즘 문제풀이", "#Algorithm", true);
    }

    @Transactional
    public void work7() {

        Member member = memberService.join("user7", "1234", "질문이많은학생", "user7@test.com", "").getData();
        memberService.setEmailVerified(member);

        Question question1 = Question.builder()
                .body("Lorem ipsum dolor sit amet, consectetur adipisicing elit. Voluptatibus quasi consectetur quaerat minima incidunt ut qui alias. Aperiam corrupti veritatis doloribus placeat ducimus numquam a quae! Debitis totam harum rem." +
                        "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Voluptatibus quasi consectetur quaerat minima incidunt ut qui alias. Aperiam corrupti veritatis doloribus placeat ducimus numquam a quae! Debitis totam harum rem.")
                .bodyHtml("<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Voluptatibus quasi consectetur quaerat minima incidunt ut qui alias. Aperiam corrupti veritatis doloribus placeat ducimus numquam a quae! Debitis totam harum rem." +
                        "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Voluptatibus quasi consectetur quaerat minima incidunt ut qui alias. Aperiam corrupti veritatis doloribus placeat ducimus numquam a quae! Debitis totam harum rem.</p>")
                .subject("자바 상속에 관하여")
                .viewCount(12)
                .voteCount(0)
                .lecture(null)
                .lesson(null)
                .member(member)
                .build();

        Question question2 = Question.builder()
                .body("내용")
                .bodyHtml("<p>저녁 메뉴 추천받아용</p>")
                .subject("저녁은 뭐 먹을까요?")
                .viewCount(22)
                .voteCount(0)
                .lecture(null)
                .lesson(null)
                .member(member)
                .build();

        Question question3 = Question.builder()
                .body("Lorem ipsum dolor sit amet, consectetur adipisicing elit. Voluptatibus quasi consectetur quaerat minima incidunt ut qui alias. Aperiam corrupti veritatis doloribus placeat ducimus numquam a quae! Debitis totam harum rem." +
                        "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Voluptatibus quasi consectetur quaerat minima incidunt ut qui alias. Aperiam corrupti veritatis doloribus placeat ducimus numquam a quae! Debitis totam harum rem.")
                .bodyHtml("<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Voluptatibus quasi consectetur quaerat minima incidunt ut qui alias. Aperiam corrupti veritatis doloribus placeat ducimus numquam a quae! Debitis totam harum rem." +
                        "Lorem ipsum dolor sit amet, consectetur adipisicing elit. Voluptatibus quasi consectetur quaerat minima incidunt ut qui alias. Aperiam corrupti veritatis doloribus placeat ducimus numquam a quae! Debitis totam harum rem.</p>")
                .subject("강의 어떻게 올려요?")
                .viewCount(19)
                .voteCount(0)
                .lecture(null)
                .lesson(null)
                .member(member)
                .build();

        questionService.save(question1);
        questionService.save(question2);
        questionService.save(question3);

    }
}