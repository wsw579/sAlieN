package com.aivle.project.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.io.Serializable;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "contracts")
public class ContractsEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractId;

    @Column(nullable = false)
    private String contractStatus;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate terminationDate;

    @Column(nullable = true)
    private String contractDetail;

    @Column(nullable = false)
    private float contractSales;

    @Column(nullable = false)
    private float contractAmount;

    @Column(nullable = true)
    private String contractClassification;

    @Lob
    @JdbcTypeCode(Types.BINARY)
    @Column(name = "file_data", columnDefinition = "bytea")
    private byte[] fileData;

    // 업로드된 파일의 이름
    @Column(name = "file_name", nullable = true)
    private String fileName;

    // 업로드된 파일의 MIME 타입
    @Column(name = "mime_type", nullable = true)
    private String mimeType;

//    @Column(nullable = true)
//    private String uploadedFilePath;


    // 외래키 부분

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity accountId;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeEntity employeeId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductsEntity productId;

    @ManyToOne
    @JoinColumn(name = "opportunity_id", nullable = true)
    private OpportunitiesEntity opportunityId;

    @OneToMany(mappedBy = "contractId", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    private List<OrdersEntity> orders;



}
