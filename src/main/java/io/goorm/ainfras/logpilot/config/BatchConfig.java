package io.goorm.ainfras.logpilot.config;


import io.goorm.ainfras.logpilot.parser.LogParser;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final LogParser logParser;

    @Bean
    public Job myJob(JobRepository jobRepository, Step step) {
        return new JobBuilder("parseLogJob", jobRepository)
                .start(step)
                .build();
    }

    @Bean
    public Step myStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        Tasklet myTasklet = (contribution, chunkContext) -> {
            logParser.parseLogFile();
            return RepeatStatus.FINISHED;
        };

        return new StepBuilder("parseLogStep", jobRepository)
                .tasklet(myTasklet, transactionManager)
                .transactionManager(transactionManager)
                .build();
    }
}
