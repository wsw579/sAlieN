package com.aivle.project.service;

import com.aivle.project.dto.OpportunitiesDto;
import com.aivle.project.entity.OpportunitiesCommentEntity;
import com.aivle.project.entity.OpportunitiesEntity;
import com.aivle.project.repository.OpportunitiesCommentRepository;
import com.aivle.project.repository.OpportunitiesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class OpportunitiesService {

    private final OpportunitiesRepository opportunitiesRepository;
    private final OpportunitiesCommentRepository opportunitiesCommentRepository;

    // Create
    public void createOpportunities(OpportunitiesDto dto) {
        OpportunitiesEntity opportunitiesEntity = new OpportunitiesEntity();

        opportunitiesEntity.setOpportunityName(dto.getOpportunityName());
        opportunitiesEntity.setRegion(dto.getRegion());
        opportunitiesEntity.setCompanySize(dto.getCompanySize());
        opportunitiesEntity.setOpportunityInquiries(dto.getOpportunityInquiries());
        opportunitiesEntity.setCustomerEmployee(dto.getCustomerEmployee());
        opportunitiesEntity.setQuantity(dto.getQuantity());
        opportunitiesEntity.setExpectedRevenue(dto.getExpectedRevenue());
        opportunitiesEntity.setCompanyRevenue(dto.getCompanyRevenue());
        opportunitiesEntity.setOpportunityNotes(dto.getOpportunityNotes());
        opportunitiesEntity.setCreatedDate(dto.getCreatedDate());
        opportunitiesEntity.setTargetCloseDate(dto.getTargetCloseDate());
        opportunitiesEntity.setOpportunityStatus(dto.getOpportunityStatus());
        opportunitiesEntity.setSuccessRate(dto.getSuccessRate());
        //외래키 부분
        opportunitiesEntity.setLeadId(dto.getLeadId());
        opportunitiesEntity.setAccountId(dto.getAccountId());
        opportunitiesEntity.setProductId(dto.getProductId());
        opportunitiesEntity.setEmployeeId(dto.getEmployeeId());

        opportunitiesRepository.save(opportunitiesEntity);
    }

    // Read
    public List<OpportunitiesEntity> readOpportunities() {
        return opportunitiesRepository.findAllByOrderByCreatedDateAndIdDesc();
    }


    // Update
    @Transactional
    public void updateOpportunities(Long opportunityId, OpportunitiesDto dto) {
        OpportunitiesEntity opportunitiesEntity = opportunitiesRepository.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found"));

        opportunitiesEntity.setOpportunityName(dto.getOpportunityName());
        opportunitiesEntity.setRegion(dto.getRegion());
        opportunitiesEntity.setCompanySize(dto.getCompanySize());
        opportunitiesEntity.setOpportunityInquiries(dto.getOpportunityInquiries());
        opportunitiesEntity.setCustomerEmployee(dto.getCustomerEmployee());
        opportunitiesEntity.setQuantity(dto.getQuantity());
        opportunitiesEntity.setExpectedRevenue(dto.getExpectedRevenue());
        opportunitiesEntity.setCompanyRevenue(dto.getCompanyRevenue());
        opportunitiesEntity.setOpportunityNotes(dto.getOpportunityNotes());
        opportunitiesEntity.setCreatedDate(dto.getCreatedDate());
        opportunitiesEntity.setTargetCloseDate(dto.getTargetCloseDate());
        opportunitiesEntity.setOpportunityStatus(dto.getOpportunityStatus());
        opportunitiesEntity.setSuccessRate(dto.getSuccessRate());

        //외래키 부분
        opportunitiesEntity.setLeadId(dto.getLeadId());
        opportunitiesEntity.setAccountId(dto.getAccountId());
        opportunitiesEntity.setProductId(dto.getProductId());
        opportunitiesEntity.setEmployeeId(dto.getEmployeeId());

        opportunitiesRepository.save(opportunitiesEntity);

    }



    // Delete
    public void deleteOpportunities(Long opportunityId) {
        opportunitiesRepository.deleteById(opportunityId);
    }



    public void deleteOpportunitiesByIds(List<Long> ids) {
        opportunitiesRepository.deleteAllById(ids);
    }



    // Search
    public OpportunitiesEntity searchOpportunities(Long opportunityId) {
        return opportunitiesRepository.findById(opportunityId)
                .orElseThrow(()->new IllegalArgumentException("error"));
    }


    // lead comment
    @Transactional
    public List<OpportunitiesCommentEntity> getCommentsByOpportunityId(Long opportunityId) {
        OpportunitiesEntity opportunity = searchOpportunities(opportunityId);
        List<OpportunitiesCommentEntity> comments = opportunitiesCommentRepository.findByOpportunity(opportunity);

        // 디버깅을 위해 로그 출력
        comments.forEach(comment -> System.out.println("Comment: " + comment.getContent()));

        return comments;
    }

    // create comment
    @Transactional
    public void createComment(String content, Long opportunityId, String author) {
        OpportunitiesEntity opportunity = searchOpportunities(opportunityId);
        OpportunitiesCommentEntity comment = new OpportunitiesCommentEntity();
        comment.setContent(content);
        comment.setCommentCreatedDate(LocalDateTime.now());
        comment.setAuthor(author);
        comment.setOpportunity(opportunity);
        opportunitiesCommentRepository.save(comment);
    }


}
