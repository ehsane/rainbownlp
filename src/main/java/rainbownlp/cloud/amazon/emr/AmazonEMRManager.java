package rainbownlp.cloud.amazon.emr;

import java.io.IOException;

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
	public void runOnEMR(){
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

		// predefined steps. See StepFactory for list of predefined steps
//		StepConfig hive = new StepConfig("Hive", new StepFactory().newInstallHiveStep());
		
		// A custom step
		HadoopJarStepConfig hadoopConfig1 = new HadoopJarStepConfig()
			.withJar("s3://mybucket/my-jar-location1")
			.withMainClass("com.my.Main1") // optional main class, this can be omitted if jar above has a manifest
			.withArgs("--verbose"); // optional list of arguments
		StepConfig customStep = new StepConfig("Step1", hadoopConfig1);

		AddJobFlowStepsResult result = client.addJobFlowSteps(new AddJobFlowStepsRequest()
			.withJobFlowId("j-1HTE8WKS7SODR")
			.withSteps(customStep));
		System.out.println(result.getStepIds());
	}
}
