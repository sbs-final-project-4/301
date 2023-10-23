package com.yk.Motivation.base.initData;

import com.yk.Motivation.base.app.AppConfig;
import com.yk.Motivation.domain.article.entity.Article;
import com.yk.Motivation.domain.article.service.ArticleService;
import com.yk.Motivation.domain.board.entity.Board;
import com.yk.Motivation.domain.board.service.BoardService;
import com.yk.Motivation.domain.member.entity.Member;
import com.yk.Motivation.domain.member.service.MemberService;
import com.yk.Motivation.domain.post.service.PostService;
import com.yk.Motivation.standard.util.Ut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;


@Configuration
@Profile("!prod")
public class NotProd {
//    @Value("${custom.security.oauth2.client.registration.kakao.devUserOauthId}")
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

    @Bean
    public ApplicationRunner initNotProd() {
        return args -> {
            self.work1();
            self.work2();
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

//        Member memberByKakao = memberService.whenSocialLogin("KAKAO", "KAKAO__%s".formatted(kakaoDevUserOAuthId), "홍길동", "");

        Article article1 = articleService.write(board1, member1, "제목 1", "#자바 #HTML", "내용 1").getData();
        Article article2 = articleService.write(board1, member2, "제목 2", "#CSS #HTML #홍길동", "내용 2").getData();
        Article article3 = articleService.write(board2, member1, "제목 3", "#자바의 정석", "내용 3").getData();
        Article article4 = articleService.write(board2, member2, "제목 4", "#홍길동", "내용 4").getData();

        String file1Path = Ut.file.tempCopy(AppConfig.getResourcesStaticDirPath() + "/resource/common/common.css");
        String file2Path = Ut.file.tempCopy(AppConfig.getResourcesStaticDirPath() + "/resource/common/common.js");
        articleService.saveAttachmentFile(article1, file1Path, 1L);
        articleService.saveAttachmentFile(article1, file2Path, 2L);

        postService.write(member1, "제목 1", "#자바 #HTML", "내용 1");
        postService.write(member1, "제목 2", "#CSS #HTML #Python", "내용 2");
        postService.write(member1, "제목 3", "#Java #HTML", "내용 3");
        postService.write(member2, "제목 4", "#Python #Script", "내용 4");
        postService.write(member2, "제목 5", "#Java #JSP", "내용 5");
        postService.write(member2, "제목 6", "#CSS #Hungry #Python", "내용 6");
    }

    @Transactional
    public void work2() {
        Article article1 = articleService.findById(1L).get();
        articleService.modify(article1, "제목 1 수정", "#자바2 #HTML", "내용 1\n수정", "내용 1<br />수정");
    }
}