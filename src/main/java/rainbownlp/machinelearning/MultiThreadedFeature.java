package rainbownlp.machinelearning;

import java.sql.SQLException;
import java.util.List;

import rainbownlp.util.FileUtil;

public abstract class MultiThreadedFeature extends Thread implements IFeatureCalculator {
	protected List<MLExample> examples;
	public MultiThreadedFeature(List<MLExample> examplesToProcess){
		examples = examplesToProcess;
	}
	@Override
	public abstract void calculateFeatures(MLExample exampleToProcess)
			throws SQLException, Exception;

	@Override
	public void run() {
		int counter = 0;
		for (MLExample example:examples)
		{
			try {
				calculateFeatures(example);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			counter++;
			FileUtil.logLine(null, "Processed : "+counter +"/"+examples.size());
		}
	}
}
