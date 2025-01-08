package com.aivle.project.service;

import com.aivle.project.dto.ContractsDto;
import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.repository.ContractsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class ContractsService {

    private final ContractsRepository contractsRepository;

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
        contractsRepository.save(contractsEntity);
    }

    // Read
    public List<ContractsEntity> readContracts() {
        return contractsRepository.findAllByOrderByCreatedDateAndIdDesc();
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
//    @Transactional
//    public List<OrderEntity> getOrdersByContractId(Long contractId) {
//        ContractsEntity contract = searchContracts(contractId);
//        List<OrderEntity> orders = OrderRepository.findByContract(contract);
//
//        // 디버깅을 위해 로그 출력
//        orders.forEach(comment -> System.out.println("Order: " + order.getName()));
//
//        return orders;
//    }

    // create order
//    @Transactional
//    public void createOrder(String content, Long contractId, String author) {
//        ContractsEntity contract = searchContracts(contractId);
//        OrderEntity order = new OrderEntity();
//        order.setContent(content);
//        order.setCommentCreatedDate(LocalDateTime.now());
//        order.setAuthor(author);
//        order.setContract(contract);
//        OrderRepository.save(order);
//    }


}
