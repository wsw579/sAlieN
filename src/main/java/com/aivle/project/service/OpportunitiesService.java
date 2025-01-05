package com.aivle.project.service;

import com.aivle.project.dto.OpportunitiesDto;
import com.aivle.project.entity.OpportunitiesEntity;
import com.aivle.project.repository.OpportunitiesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class OpportunitiesService {

    private final OpportunitiesRepository opportunitiesRepository;

    // Create
    public void createOpportunities(OpportunitiesDto dto) {
        OpportunitiesEntity opportunitiesEntity = new OpportunitiesEntity();
        opportunitiesEntity.setOpportunityName(dto.getOpportunityName());
        opportunitiesEntity.setRegion(dto.getRegion());
        opportunitiesEntity.setCompanySize(dto.getCompanySize());
        opportunitiesEntity.setOpportunityInquiries(dto.getOpportunityInquiries());
        opportunitiesEntity.setOpportunityDetails(dto.getOpportunityDetails());
        opportunitiesEntity.setQuantity(dto.getQuantity());
        opportunitiesEntity.setExpectedRevenue(dto.getExpectedRevenue());
        opportunitiesEntity.setCompanyRevenue(dto.getCompanyRevenue());
        opportunitiesEntity.setOpportunityNotes(dto.getOpportunityNotes());
        opportunitiesEntity.setCreatedDate(dto.getCreatedDate());
        opportunitiesEntity.setTargetCloseDate(dto.getTargetCloseDate());
        opportunitiesEntity.setOpportunityStatus(dto.getOpportunityStatus());
        opportunitiesEntity.setSuccessRate(dto.getSuccessRate());
        opportunitiesRepository.save(opportunitiesEntity);
    }

    // Read
    public List<OpportunitiesEntity> readOpportunities() {
        return opportunitiesRepository.findAll();
    }


    // Update
    public void updateOpportunities(Long opportunityId, OpportunitiesDto dto) {
        OpportunitiesEntity opportunitiesEntity = opportunitiesRepository.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity not found"));

        opportunitiesEntity.setOpportunityName(dto.getOpportunityName());
        opportunitiesEntity.setRegion(dto.getRegion());
        opportunitiesEntity.setCompanySize(dto.getCompanySize());
        opportunitiesEntity.setOpportunityInquiries(dto.getOpportunityInquiries());
        opportunitiesEntity.setOpportunityDetails(dto.getOpportunityDetails());
        opportunitiesEntity.setQuantity(dto.getQuantity());
        opportunitiesEntity.setExpectedRevenue(dto.getExpectedRevenue());
        opportunitiesEntity.setCompanyRevenue(dto.getCompanyRevenue());
        opportunitiesEntity.setOpportunityNotes(dto.getOpportunityNotes());
        opportunitiesEntity.setCreatedDate(dto.getCreatedDate());
        opportunitiesEntity.setTargetCloseDate(dto.getTargetCloseDate());
        opportunitiesEntity.setOpportunityStatus(dto.getOpportunityStatus());
        opportunitiesEntity.setSuccessRate(dto.getSuccessRate());
        opportunitiesRepository.save(opportunitiesEntity);
    }

    // Delete
    public void deleteOpportunities(Long opportunityId) {
        opportunitiesRepository.deleteById(opportunityId);
    }

    // Search
    public OpportunitiesEntity searchOpportunities(Long opportunityId) {
        return opportunitiesRepository.findById(opportunityId).orElseThrow(()->new IllegalArgumentException("error"));
    }

}
