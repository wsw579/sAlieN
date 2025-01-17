package com.aivle.project.service;

import com.aivle.project.dto.ContractsDto;
import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.repository.ContractsRepository;
import com.aivle.project.repository.OrdersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class ContractsService {

    private final ContractsRepository contractsRepository;
    private final OrdersRepository ordersRepository;

    // Create
    public void createContracts(ContractsDto dto) {
        ContractsEntity contractsEntity = new ContractsEntity();

        contractsEntity.setContractStatus(dto.getContractStatus());
        contractsEntity.setStartDate(dto.getStartDate());
        contractsEntity.setTerminationDate(dto.getTerminationDate());
        contractsEntity.setContractDetail(dto.getContractDetail());
        contractsEntity.setContractSales(dto.getContractSales());
        contractsEntity.setContractAmount(dto.getContractAmount());
        contractsEntity.setContractClassification(dto.getContractClassification());

        contractsEntity.setOpportunityId(dto.getOpportunityId());
        contractsEntity.setAccountId(dto.getAccountId());
        contractsEntity.setProductId(dto.getProductId());
        contractsEntity.setEmployeeId(dto.getEmployeeId());
        contractsEntity.setOpportunityId((dto.getOpportunityId()));
        contractsRepository.save(contractsEntity);
    }

    // Read
    public List<ContractsEntity> readContracts() {
        return contractsRepository.findAllByOrderByCreatedDateAndIdDescActive();
    }


    // Update
    @Transactional
    public void updateContracts(Long contractId, ContractsDto dto) {
        ContractsEntity contractsEntity = contractsRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));

        contractsEntity.setContractStatus(dto.getContractStatus());
        contractsEntity.setStartDate(dto.getStartDate());
        contractsEntity.setTerminationDate(dto.getTerminationDate());
        contractsEntity.setContractDetail(dto.getContractDetail());
        contractsEntity.setContractSales(dto.getContractSales());
        contractsEntity.setContractAmount(dto.getContractAmount());
        contractsEntity.setContractClassification(dto.getContractClassification());

        contractsEntity.setOpportunityId(dto.getOpportunityId());
        contractsEntity.setAccountId(dto.getAccountId());
        contractsEntity.setProductId(dto.getProductId());
        contractsEntity.setEmployeeId(dto.getEmployeeId());
        contractsEntity.setOpportunityId((dto.getOpportunityId()));

        contractsRepository.save(contractsEntity);

    }


    // Delete
    public void deleteContracts(Long contractId) {
        contractsRepository.deleteById(contractId);
    }



    public void deleteContractsByIds(List<Long> ids) {
        contractsRepository.deleteAllById(ids);
    }



    // Search
    public ContractsEntity searchContracts(Long contractId) {
        return contractsRepository.findById(contractId)
                .orElseThrow(()->new IllegalArgumentException("error"));
    }


    // lead order
    @Transactional
    public List<OrdersEntity> getOrdersByContractId(Long contractId) {
        ContractsEntity contract = searchContracts(contractId);
        List<OrdersEntity> orders = ordersRepository.findByContractId(contract);

        // 디버깅을 위해 로그 출력
        //orders.forEach(comment -> System.out.println("Order: " + orders.getorderId()));

        return orders;
    }


}
