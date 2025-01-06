package com.aivle.project.service;

import com.aivle.project.dto.orders.OrdersRequestDto;
import com.aivle.project.dto.orders.OrdersResponseDto;
import com.aivle.project.entity.orders.OrdersEntity;
import com.aivle.project.entity.orders.OrdersStatus;
import com.aivle.project.repository.OrdersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdersService {

    private final OrdersRepository ordersRepository;

    // 주문 생성 (Mustache 기반으로는 반환값을 void로 변경)
    public void ordersCreate(OrdersRequestDto requestDto) {
        OrdersEntity entity = toOrdersEntity(requestDto);
        ordersRepository.save(entity);
    }

    // 모든 주문 조회
    public List<OrdersResponseDto> getAllOrders() {
        List<OrdersEntity> orders = ordersRepository.findAll();
        return orders.stream()
                .map(this::toOrdersResponseDto)
                .collect(Collectors.toList());
    }

    // ID로 특정 주문 조회
    public OrdersResponseDto getOrderById(String id) {
        OrdersEntity order = ordersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));
        return toOrdersResponseDto(order);
    }

    // 주문 수정
    @Transactional
    public void ordersUpdate(String id, OrdersRequestDto requestDto) {
        OrdersEntity order = ordersRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        if (requestDto.getOrderDate() != null) order.setOrder_date(requestDto.getOrderDate());
        if (requestDto.getSalesDate() != null) order.setSales_date(requestDto.getSalesDate());
        if (requestDto.getOrderAmount() != null) order.setOrder_amount(requestDto.getOrderAmount());
        if (requestDto.getOrderStatus() != null) {
            try {
                order.setOrder_status(OrdersStatus.valueOf(requestDto.getOrderStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid order status: " + requestDto.getOrderStatus());
            }
        }
        if (requestDto.getContractId() != null) order.setContract_id(requestDto.getContractId());
        if (requestDto.getProductId() != null) order.setProduct_id(requestDto.getProductId());
        if (requestDto.getPartnerOpId() != null) order.setPartner_op_id(requestDto.getPartnerOpId());

        ordersRepository.save(order);
    }

    // 주문 삭제
    @Transactional
    public void ordersDelete(String id) {
        if (!ordersRepository.existsById(id)) {
            throw new RuntimeException("Order not found with ID: " + id);
        }
        ordersRepository.deleteById(id);
    }

    // Entity -> DTO 변환 메서드
    private OrdersResponseDto toOrdersResponseDto(OrdersEntity order) {
        return new OrdersResponseDto(
                order.getOrder_id(),
                order.getOrder_date(),
                order.getSales_date(),
                order.getOrder_amount(),
                order.getOrder_status().name(),
                order.getContract_id(),
                order.getProduct_id(),
                order.getPartner_op_id()
        );
    }

    // DTO -> Entity 변환 메서드
    public OrdersEntity toOrdersEntity(OrdersRequestDto dto) {
        OrdersEntity entity = new OrdersEntity();
        entity.setOrder_date(dto.getOrderDate());
        entity.setSales_date(dto.getSalesDate());
        entity.setOrder_amount(dto.getOrderAmount());
        entity.setOrder_status(OrdersStatus.valueOf(dto.getOrderStatus().toUpperCase()));
        entity.setContract_id(dto.getContractId());
        entity.setProduct_id(dto.getProductId());
        entity.setPartner_op_id(dto.getPartnerOpId());
        return entity;
    }
}
