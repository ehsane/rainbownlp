package rainbownlp.core;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;


@Configuration
@EnableAsync
public class AppConfiguration implements AsyncConfigurer {


	@Override
	public Executor getAsyncExecutor() {
		return new SimpleAsyncTaskExecutor();
	}
}
