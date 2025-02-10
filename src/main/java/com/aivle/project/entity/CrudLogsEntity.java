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
@Table(name="crud_logs")
public class CrudLogsEntity {

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

    @Column(nullable = true)
    private String contentId;

    @Column(nullable = true)
    private String isSuccess;

    @Column(nullable = true)
    private String logMessage;

}
