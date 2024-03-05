package io.goorm.ainfras.logpilot.config;


import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulingConfig {
    private final JobLauncher jobLauncher;
    private final Job job;

    @Scheduled(cron = "0 30 * * * *") // 매시 30분마다 실행
    public void runJob() {
        try {
            jobLauncher.run(job, new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
