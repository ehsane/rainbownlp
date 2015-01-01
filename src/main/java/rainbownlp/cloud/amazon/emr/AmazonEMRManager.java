package rainbownlp.cloud.amazon.emr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClient;
import com.amazonaws.services.elasticmapreduce.model.AddJobFlowStepsRequest;
import com.amazonaws.services.elasticmapreduce.model.AddJobFlowStepsResult;
import com.amazonaws.services.elasticmapreduce.model.HadoopJarStepConfig;
import com.amazonaws.services.elasticmapreduce.model.StepConfig;
import com.amazonaws.services.elasticmapreduce.util.StepFactory;

public class AmazonEMRManager {
	public void runOnEMR(List<HadoopJarStepConfig> steps){
		AWSCredentials credentials = null;
		try {
			credentials = new PropertiesCredentials(
					AmazonEMRManager.class.getResourceAsStream("AwsCredentials.properties"));
		} catch (IOException e1) {
			System.out.println("Credentials were not properly entered into AwsCredentials.properties.");
			System.out.println(e1.getMessage());
			System.exit(-1);
		}

		AmazonElasticMapReduce client = new AmazonElasticMapReduceClient(credentials);

		List<StepConfig> stepsConfig = new ArrayList<StepConfig>();
		int counter = 0;
		for(HadoopJarStepConfig step : steps){
			counter++;
			stepsConfig.add(new StepConfig("Step"+counter, step));
		}

		AddJobFlowStepsResult result = client.addJobFlowSteps(new AddJobFlowStepsRequest()
			.withJobFlowId("j-1HTE8WKS7SODR")
			.withSteps(stepsConfig));
		System.out.println(result.getStepIds());
	}
}
