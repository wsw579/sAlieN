package com.aivle.project.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "opportunities_comment")
public class OpportunitiesCommentEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long opportunityCommentId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime commentCreatedDate;

    @Column(nullable = false)
    private String author; // 작성자 임시 필드.(추후 인사ID 외부키로 대체하여 조회)

    // 외래키 부분

    @ManyToOne
    @JoinColumn(name = "opportunity_id", nullable = false)
    private OpportunitiesEntity opportunity;


    public OpportunitiesCommentEntity(String content, LocalDateTime now, String author, OpportunitiesEntity opportunity) {
    }
}
