package io.goorm.ainfras.logpilot.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Entity
public class Log {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private LocalDateTime timestamp;
    private String level;
    private String thread;
    private String logger;
    private String methodName;
    private String body;

    @Builder
    public Log(LocalDateTime timestamp, String level, String thread, String logger, String methodName, String body) {
        this.timestamp = timestamp;
        this.level = level;
        this.thread = thread;
        this.logger = logger;
        this.methodName = methodName;
        this.body = body;
    }

    public Log() {
    }
}
