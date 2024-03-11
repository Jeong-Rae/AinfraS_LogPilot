package io.goorm.ainfras.logpilot.parser;

import io.goorm.ainfras.logpilot.domain.Log;
import io.goorm.ainfras.logpilot.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    private static final Pattern LOG_PATTERN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3})\\s+\\[(.*?)\\]\\s+(\\S+)\\s+(.*?)\\s+-\\s+\\[(.*?)\\](.*)");
    private static final Pattern REQUEST_ID_PATTERN = Pattern.compile("RequestId:\\s+(.*?)(,|\\s|$)");
    private static final Pattern PARAMETERS_PATTERN = Pattern.compile("Parameters:\\s+(.*)");

    //private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss,SSS");

    private final LogRepository logRepository;
    private final Logger LOGGER = LoggerFactory.getLogger(LogParser.class);

    public int parseLogFile() {
        List<Log> logs = new ArrayList<>();
        int seq = 0;
        LOGGER.info("[parseLogFile] 로그 파싱 시작. path: {}", filePath);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                //LOGGER.info("line: {}", line);
                Log log = parseLine(line);
                if (log != null) {
                    //LOGGER.info("Log: {}", log);
                    System.out.println(log.getRequestId());
                    logs.add(log);
                    seq++;
                    if (seq % 100 == 0) {
                        LOGGER.info("[parseLogFile] {}번째 로그 처리", seq);
                    }
                }
            }
        } catch (IOException e) {
            return -1;
        }
        LOGGER.info("[parseLogFile] 로그 처리 완료. 처리량 {} 줄", logs.size());
        return saveLogs(logs).size();
    }

    public int parseLogFile(String filePath) {
        List<Log> logs = new ArrayList<>();
        int seq = 0;
        LOGGER.info("[parseLogFile] 로그 파싱 시작. path: {}", filePath);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log log = parseLine(line);
                if (log != null) {
                    LOGGER.info("Log: {}", log);
                    logs.add(log);
                    seq++;
                    if (seq % 100 == 0) {
                        LOGGER.info("[parseLogFile] {}번째 로그 처리", seq);
                    }
                }
            }
        } catch (IOException e) {
            return -1;
        }
        LOGGER.info("[parseLogFile] 로그 처리 완료. 처리량 {} 줄", logs.size());
        return saveLogs(logs).size();
    }

    private Log parseLine(String line) {
        Matcher matcher = LOG_PATTERN.matcher(line);
        if (matcher.find()) {
            String body = matcher.group(6).trim();


            String requestID = "";
            String parameters = "";
            Matcher requestIdMatcher = REQUEST_ID_PATTERN.matcher(body);
            if (requestIdMatcher.find()) {
                requestID = requestIdMatcher.group(1);
                //System.out.println(requestID);
            }

            Matcher parametersMatcher = PARAMETERS_PATTERN.matcher(body);
            if (parametersMatcher.find()) {
                parameters =  parametersMatcher.group(1);
            }

            return Log.builder()
                    .timestamp(LocalDateTime.parse(matcher.group(1), FORMATTER))
                    .level(matcher.group(2))
                    .thread(matcher.group(3))
                    .logger(matcher.group(4).trim())
                    .methodName(matcher.group(5))
                    .body(body)
                    .requestId(requestID)
                    .parameters(parameters)
                    .build();
        }
        return null;
    }

    private List<Log> saveLogs(List<Log> logs) {
        return logRepository.saveAll(logs);
    }
}
