package com.aivle.project.dto;
import com.aivle.project.entity.OpportunitiesEntity;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDto {
    @NotNull(message = "History ID는 필수입니다.")
    private Long historyId;

    @NotBlank(message = " 이름 입력은 필수입니다.")
    @Size(max = 255, message = "제목은 255자 이내로 입력해야 합니다.")
    private String historyTitle;

    @NotNull(message = "미팅 날짜는 필수입니다.")
    private LocalDate historyDate;

    @NotNull(message = "미팅 시간은 필수입니다.")
    private LocalTime historyTime;

    @NotBlank(message = "담당자 입력은 필수입니다.")
    private String customerRepresentative;

    @NotBlank(message = "미팅 장소 입력은 필수입니다.")
    private String meetingPlace;

    @NotBlank(message = "회사 규모 입력은 필수입니다.")
    @Pattern(regexp = "^(대기업|중견기업|중소기업|기타)$", message = "회사 규모는 '대기업', '중견기업', '중소기업', '기타' 중 하나여야 합니다.")
    private String companySize;

    @NotBlank(message = "고객 문의사항는 필수입니다.")
    @Size(max = 1000, message = "고객 문의사항은 1,000자 이내로 입력해야 합니다.")
    private String customerRequirements;

    @NotBlank(message = "액션 입력은 필수입니다.")
    @Pattern(regexp = "^(후속 미팅 요청|제안서 전달 및 검토|계약 조율|계약 및 클로징|고객 관리 및 대응)$", message = "액션은 '후속 미팅 요청', '제안서 전달 및 검토', '계약 조율', '계약 및 클로징', '고객 관리 및 대응 중 하나여야 합니다.")
    private String actionTaken;

    //외래키
    private OpportunitiesEntity opportunityId;
}
