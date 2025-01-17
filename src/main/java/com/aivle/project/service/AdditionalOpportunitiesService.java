package com.aivle.project.service;

import com.aivle.project.dto.AdditionalOpportunitiesDto;
import com.aivle.project.entity.AdditionalOpportunitiesEntity;
import com.aivle.project.repository.AdditionalOpportunitiesRepository;
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
public class AdditionalOpportunitiesService {
    private final AdditionalOpportunitiesRepository additionalopportunitiesRepository;
    //create
    public void createAdditionalOpportunities(AdditionalOpportunitiesDto dto) {
        AdditionalOpportunitiesEntity additionalopportunitiesEntity = new AdditionalOpportunitiesEntity();

        additionalopportunitiesEntity.setadditionalopportunityQuantity(dto.getadditionalopportunityQuantity());
        additionalopportunitiesEntity.setadditionalopportunitySales(dto.getadditionalopportunitySales());
        additionalopportunitiesEntity.setadditionalopportunityStatus(dto.getadditionalopportunityStatus());
        additionalopportunitiesEntity.setcreatedDate(dto.getcreatedDate());
        additionalopportunitiesEntity.settargetCloseDate(dto.gettargetCloseDate());

        //외래키
        additionalopportunitiesEntity.setOpportunityId(dto.getOpportunityId());
        additionalopportunitiesEntity.setProductId(dto.getProductId());
        additionalopportunitiesEntity.setAccountId(dto.getAccountId());

        additionalopportunitiesRepository.save(additionalopportunitiesEntity);
    }
    //read
    public List<AdditionalOpportunitiesEntity> readAdditionalOpportunities(){
        return AdditionalOpportunitiesRepository.findAllByOrderByCreatedDateAndIdDesc();
    }
    //update
    @Transactional
    public void updateAdditionalOpportunities(Long additionalopportunityId, AdditionalOpportunitiesDto dto){
        AdditionalOpportunitiesEntity additionalopportunitiesEntity = additionalopportunitiesRepository.findByAdditionalopportunityId();
            if (additionalopportunitiesEntity == null) {
                throw new IllegalArgumentException("Additional Opportunities not found");
            }
        additionalopportunitiesEntity.setadditionalopportunityQuantity(dto.getadditionalopportunityQuantity());
        additionalopportunitiesEntity.setadditionalopportunitySales(dto.getadditionalopportunitySales());
        additionalopportunitiesEntity.setadditionalopportunityStatus(dto.getadditionalopportunityStatus());
        additionalopportunitiesEntity.setcreatedDate(dto.getcreatedDate());
        additionalopportunitiesEntity.settargetCloseDate(dto.gettargetCloseDate());

        //외래키
        additionalopportunitiesEntity.setOpportunityId(dto.getOpportunityId());
        additionalopportunitiesEntity.setProductId(dto.getProductId());
        additionalopportunitiesEntity.setAccountId(dto.getAccountId());

        additionalopportunitiesRepository.save(additionalopportunitiesEntity);

    }
    //delete
    public void deleteAdditionalOpportunities(Long additionalopportunityId){
        additionalopportunitiesRepository.deleteById(additionalopportunityId);
    }
    public void deleteAdditionalOpportunitiesByIds(List<Long> ids){
        additionalopportunitiesRepository.deleteAllById(ids);
    }
    //search by id
    public AdditionalOpportunitiesEntity searchAdditionalOpportunities(Long additionalopportunityId){
        AdditionalOpportunitiesEntity additionalopportunitiesEntity = additionalopportunitiesRepository.findByAdditionalopportunityId(additionalopportunityId);
        if (additionalopportunitiesEntity == null){
            throw new IllegalArgumentException("Additional Opportunities not found");
        }
        return additionalopportunitiesEntity;
    }

}
