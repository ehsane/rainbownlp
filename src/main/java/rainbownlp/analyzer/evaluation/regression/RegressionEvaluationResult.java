package rainbownlp.analyzer.evaluation.regression;

import java.util.ArrayList;

import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math.stat.ranking.NaturalRanking;

import rainbownlp.analyzer.evaluation.IEvaluationResult;



public class RegressionEvaluationResult implements IEvaluationResult {
	private ArrayList<Double> expectedValues = new ArrayList<Double>();
	private ArrayList<Double> predictedValues = new ArrayList<Double>();
	
	/**
	 * sum of squared errors
	 */
	Double sqError = 0.0;
	/**
	 * sum of errors
	 */
	Double error = 0.0;
	int size = 0;
	public Double getSqError() {
		return sqError;
	}
	
	public Double getError() {
		return error;
	}
	
	public int getSize() {
		return size;
	}
	
	public double getSpearmansCorrelation(){
		SpearmansCorrelation sc = new SpearmansCorrelation();
		double[] expecteds = new double[getExpectedValues().size()];
		double[] predicteds = new double[getPredictedValues().size()];
		for(int i=0;i<expecteds.length;i++){
			expecteds[i] =  getExpectedValues().get(i).doubleValue();
			predicteds[i] = getPredictedValues().get(i).doubleValue();
			
			System.out.println("Expected: "+expecteds[i] +" -- Predicted: "+predicteds[i]);
		}
		
		return sc.correlation(expecteds, predicteds);
	}
	
	public double getPearsonCorrelation(){
		PearsonsCorrelation pc = new PearsonsCorrelation();
		double[] expecteds = new double[getExpectedValues().size()];
		double[] predicteds = new double[getPredictedValues().size()];
		for(int i=0;i<expecteds.length;i++){
			expecteds[i] =  getExpectedValues().get(i).doubleValue();
			predicteds[i] = getPredictedValues().get(i).doubleValue();
		}
		
		return pc.correlation(predicteds, expecteds);
	}

	public String getReport()
	{
		String print_res = "MSE: "+getMSE()+"\n"; 
		print_res += "Pearson Correlation: "+getPearsonCorrelation()+"\n";
		print_res += "Spearmans Correlation: "+getSpearmansCorrelation()+"\n";
		print_res += "Examples count: "+size;
		return print_res;
	}
	public Double getMSE() {
		return sqError / size;
	}
	public void printResult() {
		System.out.print(getReport());
//		try {
//			RegressionGraph.createAndShowGui(getExpectedValues(), getPredictedValues());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	public void add(Double expected, Double predicted) {
		Double err = Math.abs(expected - predicted);
		error += err;
		sqError += Math.sqrt(err);
		size++;
		getExpectedValues().add(expected);
		getPredictedValues().add(predicted);
	}
	public void add(RegressionEvaluationResult partialResult) {
		sqError += partialResult.getSqError();
		error += partialResult.getError();
		size += partialResult.getSize();
		getExpectedValues().addAll(partialResult.getExpectedValues());
		getPredictedValues().addAll(partialResult.getPredictedValues());
	}

	@Override
	public Double getIntegratedMetric() {
		return getSqError();
	}

	public ArrayList<Double> getPredictedValues() {
		return predictedValues;
	}

	public void setPredictedValues(ArrayList<Double> predictedValues) {
		this.predictedValues = predictedValues;
	}

	public ArrayList<Double> getExpectedValues() {
		return expectedValues;
	}

	public void setExpectedValues(ArrayList<Double> expectedValues) {
		this.expectedValues = expectedValues;
	}

}
