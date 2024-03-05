package io.goorm.ainfras.logpilot.utils;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@Getter
public class LogReader {
    private BufferedReader bufferedReader;
    private FileReader fileReader;

    public LogReader() {
    }

    public LogReader(String file) {
        try {
            this.fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.bufferedReader = new BufferedReader(this.fileReader);
    }

    public String readLine() throws IOException {
        return this.bufferedReader.readLine();
    }
}
