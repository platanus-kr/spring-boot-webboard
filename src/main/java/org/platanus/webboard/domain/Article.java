package org.platanus.webboard.domain;

import lombok.*;
import org.platanus.webboard.controller.board.dto.ArticleListDto;
import org.platanus.webboard.controller.board.dto.ArticleWriteDto;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long boardId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private Long authorId;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime modifiedDate;

    private boolean deleted;

    private Long recommend;

    private Long viewCount;

    public static Article fromListDto(ArticleListDto a) {
        return Article.builder()
                .id(a.getId())
                .boardId(a.getBoardId())
                .title(a.getTitle())
                .authorId(a.getAuthorId())
                .createdDate(a.getCreatedDate())
                .modifiedDate(LocalDateTime.now())
                .deleted(false)
                .recommend(a.getRecommend())
                .viewCount(a.getViewCount())
                .build();
    }

    public static Article fromWriteDto(ArticleWriteDto a) {
        return Article.builder()
                .title(a.getTitle())
                .content(a.getContent())
                .build();
    }
}
