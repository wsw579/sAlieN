package com.aivle.project.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;



import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "additionalopportunities")
public class AdditionalOpportunitiesEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long additionalopportunityId;

    @Column(nullable = false, length = 255)
    private String additionalopportunityQuantity;

    @Column(nullable = false, length = 50)
    private String additionalopportunitySales;

    @Column(nullable = false)
    private String additionalopportunityStatus;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createdDate;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate targetCloseDate;


    //내부 외래키
    @ManyToOne
    @JoinColumn(name = "opportunity_id", nullable = false, foreignKey = @ForeignKey(name = "fk_additionalopportunities_opportunity_id"))
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private OpportunitiesEntity opportunityId;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, foreignKey = @ForeignKey(name = "fk_additionalopportunities_account_id"))
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private AccountEntity accountId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_additionalopportunities_product_id"))
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ProductsEntity productId;




}

