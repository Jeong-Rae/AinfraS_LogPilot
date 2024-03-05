package io.goorm.ainfras.logpilot.config;


import io.goorm.ainfras.logpilot.parser.LogParser;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableBatchProcessing
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final JobLauncher jobLauncher;
    private final LogParser logParser;

    @Bean
    public Job logParserJob() {
        return jobBuilderFactory.get("logParserJob")
                .start(parseLogStep())
                .build();
    }

    @Bean
    public Step parseLogStep() {
        return stepBuilderFactory.get("parseLogStep")
                .tasklet(parseLogTasklet())
                .build();
    }

    @Bean
    public Tasklet parseLogTasklet() {
        return (contribution, chunkContext) -> {
            logParser.parseLogFile();
            return RepeatStatus.FINISHED;
        };
    }

    @Scheduled(cron = "0 */30 * * * *") // 매 30분마다 실행
    public void runJob() throws Exception {
        jobLauncher.run(logParserJob(), new JobParametersBuilder()
                .addLong("uniqueness", System.nanoTime())
                .toJobParameters());
    }

}
