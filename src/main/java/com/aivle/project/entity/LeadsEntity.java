package com.aivle.project.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LeadsEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leadId;

    @Column(nullable = false, length = 50)
    private String leadStatus;

    @Column(nullable = false, length = 255)
    private String leadSource;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate targetCloseDate;

    @Column(nullable = false)
    private String customerRequirements;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String customerRepresentitive;

    @Column(nullable = false)
    private String c_tel;


    // 외래키 부분

    //@OneToMany(mappedBy = "leads", cascade = CascadeType.ALL)
    //private List<OpportunitiesCommentEntity> comments;
}
