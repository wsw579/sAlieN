package com.aivle.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "opportunities_history")
public class HistoryEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @Column(nullable = false, length = 255)
    private String historyTitle;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate historyDate;

    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime historyTime;

    @Column(nullable = false)
    private String customerRepresentative;

    @Column(nullable = false)
    private String meetingPlace;

    @Column(nullable = false)
    private String companySize;

    @Column(nullable = false, length = 1000)
    private String customerRequirements;

    @Column(nullable = false)
    private String actionTaken;

    // 외래키 부분
    @ManyToOne
    @JoinColumn(name = "opportunity_id", nullable = false)
    private OpportunitiesEntity opportunity;

}

