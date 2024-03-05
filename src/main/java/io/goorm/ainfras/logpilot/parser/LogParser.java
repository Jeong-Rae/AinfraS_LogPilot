package io.goorm.ainfras.logpilot.parser;

import io.goorm.ainfras.logpilot.domain.Log;
import io.goorm.ainfras.logpilot.repository.LogRepository;
import io.goorm.ainfras.logpilot.utils.LogReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class LogParser {
    @Value("${log.file.path}")
    private String filePath;
    private static final String LOG_PATTERN = "(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+)(\\+\\d{2}:\\d{2})\\s+(\\w+)\\s+\\d+\\s+---\\s+\\[(.*?)\\]\\s+(.*?)\\s+:\\s+\\[(.*?)\\]\\s+(.*)";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final Pattern pattern = Pattern.compile(LOG_PATTERN);

    private final LogRepository logRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(LogParser.class);

    public int parseLogFile() {
        List<Log> logs = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log log = parseLine(line);
                if (log != null) {
                    logs.add(log);
                }
            }
        } catch (IOException e) {
            return -1;
        }
        return saveLogs(logs).size();
    }

    private Log parseLine(String line) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return Log.builder()
                    .timestamp(LocalDateTime.parse(matcher.group(1), FORMATTER))
                    .level(matcher.group(3))
                    .thread(matcher.group(4))
                    .logger(matcher.group(5).trim())
                    .methodName(matcher.group(6))
                    .body(matcher.group(7))
                    .build();
        }
        return null;
    }

    private List<Log> saveLogs(List<Log> logs) {
        return logRepository.saveAll(logs);
    }
}
