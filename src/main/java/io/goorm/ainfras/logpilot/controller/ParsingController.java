package io.goorm.ainfras.logpilot.controller;

import io.goorm.ainfras.logpilot.dto.ParsingRequest;
import io.goorm.ainfras.logpilot.parser.LogParser;
import io.goorm.ainfras.logpilot.utils.BasicResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class ParsingController {
    private final LogParser logParser;

    @PostMapping("/parsing")
    @ResponseBody
    public ResponseEntity<BasicResponse> runParsing(@RequestBody ParsingRequest request) {
        int successCount = logParser.parseLogFile();
        BasicResponse response = new BasicResponse(String.valueOf(successCount), "", "");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
