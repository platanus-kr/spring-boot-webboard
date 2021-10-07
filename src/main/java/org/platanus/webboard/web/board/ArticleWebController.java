package org.platanus.webboard.web.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.platanus.webboard.domain.Article;
import org.platanus.webboard.domain.ArticleRecommend;
import org.platanus.webboard.domain.Comment;
import org.platanus.webboard.domain.User;
import org.platanus.webboard.web.board.dto.*;
import org.platanus.webboard.web.login.argumentresolver.Login;
import org.platanus.webboard.web.login.dto.UserSessionDto;
import org.platanus.webboard.web.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/article")
public class ArticleWebController {
    private final BoardService boardService;
    private final ArticleService articleService;
    private final ArticleRecommendService articleRecommendService;
    private final CommentService commentService;
    private final UserService userService;

    @GetMapping(value = "/{articleId}")
    public String view(@PathVariable("articleId") long articleId,
                       Model model) throws Exception {
        Article article = articleService.findById(articleId);
        articleService.updateViewCount(articleId);
        String authorNickname = userService.findById(article.getAuthorId()).getNickname();
        List<Comment> comments = commentService.findCommentsByArticleId(articleId);
        List<CommentViewDto> commentViewDtos = new ArrayList<>();
        comments.stream().forEach(c -> {
            CommentViewDto commentViewDto = CommentViewDto.from(c);
            commentViewDtos.add(commentViewDto);
        });
        ArticleViewDto articleViewDto = ArticleViewDto.fromView(article, authorNickname);
        String boardName = boardService.findById(article.getBoardId()).getName();
        CommentWriteDto commentWriteDto = new CommentWriteDto();
        model.addAttribute("board_name", boardName);
        model.addAttribute("article", articleViewDto);
        model.addAttribute("comment_write", commentWriteDto);
        model.addAttribute("comments", commentViewDtos);
        return "board/board_view";
    }

    @GetMapping(value = "/{articleId}/delete")
    public String remove(@PathVariable("articleId") long articleId,
                         @Login UserSessionDto user) throws Exception {
        Article article = articleService.findById(articleId);
        long redirectBoardId = article.getBoardId();
        User userFromDto = User.fromLoginSessionDto(user);
        if (!articleService.updateDeleteFlag(article, userFromDto))
            log.info("ArticleController remove #{}: Service Error.", article.getId());
        return "redirect:/board/" + redirectBoardId;
    }

    @GetMapping(value = "/{articleId}/modify")
    public String articleModifyView(@PathVariable("articleId") long articleId,
                                    @Login UserSessionDto user,
                                    Model model) throws Exception {
        if (user.getId() != articleService.findById(articleId).getAuthorId()) {
            log.info("ArticleController modify #{}: 글쓴이가 아닙니다. by User {}", articleId, user.getId());
            return "redirect:/article/{articleId}";
        }
        Article article = articleService.findById(articleId);
        String boardName = boardService.findById(article.getBoardId()).getName();
        String boardId = String.valueOf(article.getBoardId());
        model.addAttribute("board_id", boardId);
        model.addAttribute("article_id", articleId);
        model.addAttribute("board_name", boardName);
        model.addAttribute("article", ArticleViewDto.fromModify(article));
        return "board/board_modify";
    }

    @PostMapping(value = "/{articleId}/modify")
    public String articleModify(@PathVariable("articleId") long articleId,
                                @Login UserSessionDto user,
                                @Valid @ModelAttribute("article") ArticleWriteDto articleRequest,
                                BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            log.info("error = {}", bindingResult);
            return "board/board_modify";
        }
        Article article = articleService.findById(articleId);
        article.setTitle(articleRequest.getTitle());
        article.setContent(articleRequest.getContent());
        User userFromDto = User.fromLoginSessionDto(user);
        articleService.update(article, userFromDto);
        return "redirect:/article/{articleId}";
    }

    @PostMapping(value = "/{articleId}")
    public String commentWrite(@PathVariable("articleId") long articleId,
                               @Login UserSessionDto user,
                               @Valid @ModelAttribute("comment") CommentWriteDto commentRequest,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("ArticleController comment-write #{} : {}", articleId, bindingResult);
            return "error/has_message";
        }
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setAuthorId(user.getId());
        comment.setContent(commentRequest.getContent());
        try {
            commentService.write(comment);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/article/{articleId}";
    }

    @GetMapping(value = "/{articleId}/recommend")
    public String articleRecommendUpdate(@PathVariable("articleId") long articleId,
                                         @Login UserSessionDto user,
                                         @ModelAttribute("error") ErrorDto errorDto) {
        ArticleRecommend articleRecommend = new ArticleRecommend();
        articleRecommend.setArticleId(articleId);
        articleRecommend.setUserId(user.getId());
        try {
            articleRecommendService.save(articleRecommend);
        } catch (Exception e) {
            log.info("ArticleController recommend-update #{} : 이미 추천한 게시물 by #{} {}", articleId, user.getId(), user.getUsername());
            errorDto.setContent("이미 추천한 게시물 입니다.");
            return "error/has_message";
        }
        return "redirect:/article/{articleId}";
    }
}
