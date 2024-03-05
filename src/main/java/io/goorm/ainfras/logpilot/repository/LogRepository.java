package io.goorm.ainfras.logpilot.repository;

import io.goorm.ainfras.logpilot.domain.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
}
