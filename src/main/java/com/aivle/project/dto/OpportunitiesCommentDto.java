package com.aivle.project.dto;


import com.aivle.project.entity.OpportunitiesEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OpportunitiesCommentDto {


    private Long opportunityCommentId;
    private String content;
    private LocalDateTime commentCreatedDate;
    private String author; // 추후 인사ID필드로 대체 예정

    // 외래키 부분
    private OpportunitiesEntity opportunityId;





}
