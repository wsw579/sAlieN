package com.aivle.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="chatbot_logs")
public class ChatbotLogsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logsId;

    @Column(nullable = true)
    private String logDate;

    @Column(nullable = true)
    private String userId;

    @Column(nullable = true)
    private String crudOperations;

    @Column(nullable = true)
    private String tableName;

    @Column(nullable = true, length = 2000)
    private String requests;

    @Column(nullable = true, length = 2000)
    private String userResponse;

    @Column(nullable = true, length = 2000)
    private String systemResponse;



}
