package com.aivle.project.service;

import com.aivle.project.entity.CrudLogsEntity;
import com.aivle.project.repository.CrudLogsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class CrudLogsService {

    private final CrudLogsRepository crudLogsRepository;

    // Read
    public Page<CrudLogsEntity> readCrudLogs(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size);
        return crudLogsRepository.findAllByKeyword(search, pageable); // 내림차순으로 로그 가져오기
    }

    // 각 게시물 log 기록
    public void logCrudOperation(String crudOperation, String tableName, String contentId, String isSuccess, String logMessage) {

        String userId = getCurrentUserId();

        CrudLogsEntity log = new CrudLogsEntity();
        log.setLogDate(LocalDateTime.now().toString());
        log.setUserId(userId);
        log.setCrudOperations(crudOperation);
        log.setTableName(tableName);
        log.setContentId(contentId);
        log.setIsSuccess(isSuccess);
        log.setLogMessage(logMessage);
        crudLogsRepository.save(log); // 테이블에 로그 엔티티 저장
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser"))
                ? authentication.getName()
                : null;
    }

}
