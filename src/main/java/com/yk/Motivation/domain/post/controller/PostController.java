package com.yk.Motivation.domain.post.controller;

import com.yk.Motivation.base.exception.NeedHistoryBackException;
import com.yk.Motivation.base.rq.Rq;
import com.yk.Motivation.base.rsData.RsData;
import com.yk.Motivation.domain.genFile.entity.GenFile;
import com.yk.Motivation.domain.post.entity.Post;
import com.yk.Motivation.domain.post.service.PostService;
import com.yk.Motivation.standard.util.Ut;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/usr/post")
@RequiredArgsConstructor
@Validated
public class PostController {
    private final PostService postService;
    private final Rq rq;

    @GetMapping("/myList")
    public String showMyList(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "") String kw,
            @RequestParam(defaultValue = "all") String kwType
    ) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));
        Page<Post> postPage = postService.findByKw(rq.getMember(), kwType, kw, pageable);
        model.addAttribute("postPage", postPage);

        return "usr/post/myList";
    }

    @GetMapping("/detail/{id}")
    public String showDetail(
            Model model,
            @PathVariable long id
    ) {
        Post post = postService.findById(id).get();

        Map<String, GenFile> filesMap = postService.findGenFilesMapKeyByFileNo(post, "common", "attachment");

        model.addAttribute("post", post);
        model.addAttribute("filesMap", filesMap);

        return "usr/post/detail";
    }

    @GetMapping("/listByTag/{tagContent}")
    public String showListByTag(
            Model model,
            @PathVariable String tagContent,
            @RequestParam(defaultValue = "1") int page
    ) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));
        Page<Post> postPage = postService.findByTag(tagContent, pageable);
        model.addAttribute("postPage", postPage);

        return "usr/post/listByTag";
    }

    @GetMapping("/myListByTag/{tagContent}")
    public String showMyListByTag(
            Model model,
            @PathVariable String tagContent,
            @RequestParam(defaultValue = "1") int page
    ) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(sorts));
        Page<Post> postPage = postService.findByTag(rq.getMember(), tagContent, pageable);
        model.addAttribute("postPage", postPage);

        return "usr/post/listByTag";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String showWrite() {
        return "usr/post/write";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String write(
            @Valid PostController.PostWriteForm writeForm
    ) {
        RsData<Post> rsData = postService.write(rq.getMember(), writeForm.getSubject(), writeForm.getTagsStr(), writeForm.getBody(), writeForm.getBodyHtml());

        if (Ut.file.exists(writeForm.getAttachment__1()))
            postService.saveAttachmentFile(rsData.getData(), writeForm.getAttachment__1(), 1);
        if (Ut.file.exists(writeForm.getAttachment__1()))
            postService.saveAttachmentFile(rsData.getData(), writeForm.getAttachment__2(), 2);

        return rq.redirectOrBack("/usr/post/detail/%d".formatted(rsData.getData().getId()), rsData);
    }

    @AllArgsConstructor
    @Getter
    public static class PostWriteForm {
        @NotBlank
        @Length(min = 2)
        private String subject;
        private String tagsStr;
        @NotBlank
        private String body;
        @NotBlank
        private String bodyHtml;
        private MultipartFile attachment__1;
        private MultipartFile attachment__2;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String showModify(
            Model model,
            @PathVariable long id
    ) {
        Post post = postService.findById(id).get();

        postService
                .checkActorCanModify(rq.getMember(), post)
                .optional()
                .filter(RsData::isFail)
                .ifPresent(rsData -> {
                    throw new NeedHistoryBackException(rsData);
                });

        Map<String, GenFile> filesMap = postService.findGenFilesMapKeyByFileNo(post, "common", "attachment");

        model.addAttribute("post", post);
        model.addAttribute("filesMap", filesMap);

        return "usr/post/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modify(
            @PathVariable long id,
            @Valid PostModifyForm modifyForm
    ) {
        Post post = postService.findById(id).get();

        postService
                .checkActorCanModify(rq.getMember(), post)
                .optional()
                .filter(RsData::isFail)
                .ifPresent(rsData -> {
                    throw new NeedHistoryBackException(rsData);
                });

        RsData<Post> rsData = postService.modify(post, modifyForm.getSubject(), modifyForm.getTagsStr(), modifyForm.getBody(), modifyForm.getBodyHtml());

        if (modifyForm.attachmentRemove__1)
            postService.removeAttachmentFile(rsData.getData(), 1);

        if (modifyForm.attachmentRemove__2)
            postService.removeAttachmentFile(rsData.getData(), 2);

        if (Ut.file.exists(modifyForm.getAttachment__1()))
            postService.saveAttachmentFile(rsData.getData(), modifyForm.getAttachment__1(), 1);
        if (Ut.file.exists(modifyForm.getAttachment__2()))
            postService.saveAttachmentFile(rsData.getData(), modifyForm.getAttachment__2(), 2);

        return rq.redirectOrBack("/usr/post/detail/%d".formatted(rsData.getData().getId()), rsData);
    }

    @Getter
    @Setter
    public static class PostModifyForm {
        @NotBlank
        @Length(min = 2)
        private String subject;
        private String tagsStr;
        @NotBlank
        private String body;
        @NotBlank
        private String bodyHtml;
        private MultipartFile attachment__1;
        private MultipartFile attachment__2;
        private boolean attachmentRemove__1;
        private boolean attachmentRemove__2;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/remove/{id}")
    public String remove(
            @PathVariable long id
    ) {
        Post post = postService.findById(id).get();

        postService
                .checkActorCanDelete(rq.getMember(), post)
                .optional()
                .filter(RsData::isFail)
                .ifPresent(rsData -> {
                    throw new NeedHistoryBackException(rsData);
                });

        RsData<?> rsData = postService.remove(post);

        return rq.redirectOrBack("/usr/post/myList", rsData);
    }
}