package rainbownlp.machinelearning;

import java.sql.SQLException;

import org.springframework.scheduling.annotation.Async;


public interface IFeatureCalculator {
	@Async
	public void calculateFeatures(MLExample exampleToProcess) throws SQLException, Exception;
}
