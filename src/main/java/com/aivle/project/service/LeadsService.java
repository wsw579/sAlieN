package com.aivle.project.service;

import com.aivle.project.dto.LeadsDto;
import com.aivle.project.entity.LeadsEntity;
import com.aivle.project.repository.LeadsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


// A service layer component that contains business logic
@Service
@Transactional
@RequiredArgsConstructor
public class LeadsService {
    // Dependency Injection
    private final LeadsRepository leadsRepository;

    // Create
    // by mapping data from a LeadsDto to a LeadsEntity

    // Declares a public method named createLeads that takes a LeadsDto object as input
    public void createLeads(LeadsDto dto){
        //Creates a new instance of LeadsEntity to store data that will eventually be saved in the database.
        LeadsEntity leadsEntity = new LeadsEntity();

        //calls each method(setLeadStatus, setLeadSource...) on the leadsEntity object to assign a value to its field
        //the value is retrieved from the LeadsDto object using dto.get___()
        leadsEntity.setLeadStatus(dto.getLeadStatus());
        leadsEntity.setLeadSource(dto.getLeadSource());
        leadsEntity.setCreatedDate(dto.getCreatedDate());
        leadsEntity.setTargetCloseDate(dto.getTargetCloseDate());
        leadsEntity.setCustomerRequirements(dto.getCustomerRequirements());
        leadsEntity.setCustomerRepresentitive(dto.getCustomerRepresentitive());
        leadsEntity.setCompanyName(dto.getCompanyName());
        leadsEntity.setC_tel(dto.getC_tel());

        //Saves the leadsEntity object to the database using the leadsRepository
        leadsRepository.save(leadsEntity);
    }


    //Read :retrieving a list of all leads from the database, ordered by their createdDate and id in descending order
    public List<LeadsEntity> readLeads(){
        return leadsRepository.findAllByOrderByCreatedDateAndIdDesc();
    }


    // Update
    // executes within a database transaction
    @Transactional

    // leadId (type Long) -> input : identifies the specific lead to be updated
    public void  updateLeads(Long leadId, LeadsDto dto){
        LeadsEntity leadsEntity = leadsRepository.findByLeadId(leadId);
                // throwing a custom exception:IllegalArgumentException("message to show")
            if (leadsEntity == null) {
                throw new IllegalArgumentException("Leads not found");
            } // .orElseThrow(() -> new IllegalArgumentException("Leads not found"));
        leadsEntity.setLeadStatus(dto.getLeadStatus());
        leadsEntity.setLeadSource(dto.getLeadSource());
        leadsEntity.setCreatedDate(dto.getCreatedDate());
        leadsEntity.setTargetCloseDate(dto.getTargetCloseDate());
        leadsEntity.setCustomerRequirements(dto.getCustomerRequirements());
        leadsEntity.setCustomerRepresentitive(dto.getCustomerRepresentitive());
        leadsEntity.setCompanyName(dto.getCompanyName());
        leadsEntity.setC_tel(dto.getC_tel());
        leadsRepository.save(leadsEntity);
    }

    // Delete
    public void deleteLeads(Long leadId){
        leadsRepository.deleteById(leadId);
    }

    public void deleteLeadsByIds(List<Long> ids){
        leadsRepository.deleteAllById(ids);
    }

    //Search by lead id
    public LeadsEntity searchLeads(Long leadId) {
        LeadsEntity leadsEntity = leadsRepository.findByLeadId(leadId);
        if (leadsEntity == null) {
            throw new IllegalArgumentException("No leads found");
        }

        return leadsEntity;  // This is called after the null check
    }
}
