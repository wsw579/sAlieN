package com.aivle.project.repository;

import com.aivle.project.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, String> {
    boolean existsByEmployeeId(String username);
    MemberEntity findByEmployeeId(String username);
}
