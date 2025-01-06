package com.aivle.project.dto;


import com.aivle.project.entity.OpportunitiesEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OpportunitiesCommentDto {


    private Long opportunityCommentId;
    private String content;
    private LocalDate commentCreatedDate;


    // 외래키 부분
    private OpportunitiesEntity opportunityId;





}
