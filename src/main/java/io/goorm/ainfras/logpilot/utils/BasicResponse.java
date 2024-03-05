package io.goorm.ainfras.logpilot.utils;

public record BasicResponse(
        String response,
        String error,
        String detail
) {
}
