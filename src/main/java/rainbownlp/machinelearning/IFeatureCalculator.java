package rainbownlp.machinelearning;

import java.sql.SQLException;

public interface IFeatureCalculator {
	public void calculateFeatures(MLExample exampleToProcess) throws SQLException, Exception;
}
