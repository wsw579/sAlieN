package com.aivle.project.dto;
import com.aivle.project.entity.OpportunitiesEntity;
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

    private Long historyId;
    private String historyTitle;
    private LocalDate historyDate;
    private LocalTime historyTime;
    private String customerRepresentative;
    private String meetingPlace;
    private String companySize;
    private String customerRequirements;
    private String actionTaken;

    //외래키
    private OpportunitiesEntity opportunityId;
}
