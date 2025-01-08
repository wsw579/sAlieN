package com.aivle.project.config;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
public class SequenceSynchronizeInitializer {
    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    @Transactional
    public void synchronizeOrderSequence() {
        Long maxId = (Long) entityManager.createQuery("SELECT COALESCE(MAX(o.orderId), 0) FROM OrdersEntity o")
                .getSingleResult();

        // maxId가 0이면 시퀀스를 1부터 시작
        Long nextVal = (maxId == 0) ? 1 : maxId + 1;

        entityManager.createNativeQuery("SELECT setval('orders_order_id_seq', :nextVal, false)")
                .setParameter("nextVal", nextVal)
                .getSingleResult();
    }

    @PostConstruct
    @Transactional
    public void synchronizeProductSequence() {
        // product_id의 최대값을 가져옵니다.
        Long maxId = (Long) entityManager.createQuery("SELECT COALESCE(MAX(p.productId), 0) FROM ProductsEntity p")
                .getSingleResult();

        // maxId가 0이면 시퀀스를 1부터 시작
        Long nextVal = (maxId == 0) ? 1 : maxId + 1;

        // PostgreSQL 시퀀스를 maxId 값으로 동기화합니다.
        entityManager.createNativeQuery("SELECT setval('products_product_id_seq', :nextVal, false)")
                .setParameter("nextVal", nextVal)
                .getSingleResult();
    }


}
