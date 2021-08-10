package org.platanus.webboard.web.board;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.platanus.webboard.domain.*;
import org.platanus.webboard.web.user.UserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@Transactional
public class CommentServiceTest {
    private static BoardRepository boardRepository;
    private static BoardService boardService;
    private static UserRepository userRepository;
    private static UserService userService;
    private static ArticleRepository articleRepository;
    private static ArticleService articleService;
    private static CommentRepository commentRepository;
    private static CommentService commentService;
    private static Board board;
    private static User user;
    private static Article article;
    private Comment comment;

    @BeforeAll
    static void beforeAll() {
        DataSource dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:db/schema.sql")
                .build();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        boardRepository = new BoardRepository(jdbcTemplate);
        boardRepository.init();
        boardService = new BoardService(boardRepository);
        userRepository = new UserRepository(jdbcTemplate);
        userRepository.init();
        userService = new UserService(userRepository);
        articleRepository = new ArticleRepository(jdbcTemplate);
        articleRepository.init();
        articleService = new ArticleService(articleRepository, commentRepository, boardService, userService);
        commentRepository = new CommentRepository(jdbcTemplate);
        commentRepository.init();
        commentService = new CommentService(commentRepository, articleService);
        try {
            board = new Board();
            board.setName("board32");
            board.setDescription("description");
            board = boardService.create(board);
            user = new User();
            user.setUsername("user32");
            user.setPassword("aaa");
            user.setNickname("user32");
            user.setEmail("user32@gmail.com");
            user.setDeleted(false);
            user = userService.join(user);
            article = new Article();
            article.setBoardId(board.getId());
            article.setTitle("제목입니다.");
            article.setContent("내용입니다.");
            article.setAuthorId(user.getId());
            article.setCreatedDate(LocalDateTime.now());
            article.setModifiedDate(LocalDateTime.now());
            article.setDeleted(false);
            article = articleService.write(article);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @BeforeEach
    public void beforeEach() {
        comment = new Comment();
    }

    @Test
    public void write() {
        try {
            String content = "내용입니다.";
            comment.setArticleId(article.getId());
            comment.setAuthorId(user.getId());
            comment.setContent(content);
            comment.setDeleted(false);
            comment = commentService.write(comment);
            assertEquals(comment.getContent(), content);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void update() {
        try {
            String content = "수정된 내용입니다.";
            comment.setArticleId(article.getId());
            comment.setAuthorId(user.getId());
            comment.setContent("내용입니다.");
            comment.setDeleted(false);
            comment = commentService.write(comment);
            comment.setContent(content);
            comment = commentService.update(comment);
            assertEquals(comment.getContent(), content);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void updateDeleteFlag() {
        try {
            String content = "내용입니다.";
            comment.setArticleId(article.getId());
            comment.setAuthorId(user.getId());
            comment.setContent(content);
            comment.setDeleted(false);
            comment = commentService.write(comment);
            boolean result = commentService.updateDeleteFlag(comment);
            assertEquals(result, true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

//    @Test
//    public void delete() {
//
//    }

    @Test
    public void findAllComments() {
        try {
            String content = "내용입니다.";
            comment.setArticleId(article.getId());
            comment.setAuthorId(user.getId());
            comment.setContent(content);
            comment.setDeleted(false);
            comment = commentService.write(comment);
            List<Comment> comments = commentService.findAllComments();
            Comment findComment = comments.stream()
                    .filter(c -> c.getId() == comment.getId())
                    .findAny()
                    .get();
            assertEquals(findComment.getId(), comment.getId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

//    @Test
//    public void findAllDeletedComments(){
//
//    }

    @Test
    public void findCommentsByArticleId() {
        try {
            String content = "내용입니다.";
            comment.setArticleId(article.getId());
            comment.setAuthorId(user.getId());
            comment.setContent(content);
            comment.setDeleted(false);
            comment = commentService.write(comment);
            List<Comment> comments = commentService.findCommentsByArticleId(comment.getArticleId());
            Comment findComment = comments.stream()
                    .filter(c -> c.getId() == comment.getId())
                    .findAny()
                    .get();
            assertEquals(findComment.getId(), comment.getId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void findById() {
        try {
            String content = "내용입니다.";
            comment.setArticleId(article.getId());
            comment.setAuthorId(user.getId());
            comment.setContent(content);
            comment.setDeleted(false);
            comment = commentService.write(comment);
            Comment findComment = commentService.findById(comment.getId());
            assertEquals(findComment.getId(), comment.getId());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void isDeleted() {
        try {
            String content = "내용입니다.";
            comment.setArticleId(article.getId());
            comment.setAuthorId(user.getId());
            comment.setContent(content);
            comment.setDeleted(false);
            comment = commentService.write(comment);
            commentService.updateDeleteFlag(comment);
            boolean result = commentService.isDeleted(comment);
            assertEquals(result, true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

}