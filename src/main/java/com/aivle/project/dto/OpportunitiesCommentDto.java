package com.aivle.project.dto;


import com.aivle.project.entity.OpportunitiesEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OpportunitiesCommentDto {

    @NotNull(message = "OpportunityComment ID는 필수입니다.")
    private Long opportunityCommentId;

    private String content;

    @NotNull(message = "코멘트 생성 일자는 필수입니다.")
    private LocalDateTime commentCreatedDate;

    @NotBlank(message = "작성자는 필수입니다.")
    private String author; // 추후 인사ID필드로 대체 예정

    // 외래키 부분
    private OpportunitiesEntity opportunityId;





}
