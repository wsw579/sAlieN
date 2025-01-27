package com.aivle.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="alarm_logs")
public class AlarmLogsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logsId;

    @Column(nullable = false)
    private String logDate;

    @Column(nullable = false)
    private String logFrom;

    @Column(nullable = false)
    private String alarmContents;

    @Column(nullable = false)
    private String isRead;

}
