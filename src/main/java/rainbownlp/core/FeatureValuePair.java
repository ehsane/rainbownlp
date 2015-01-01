/**
 * @author ehsan
 */
package rainbownlp.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.Session;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;

import rainbownlp.util.FileUtil;
import rainbownlp.util.HibernateUtil;

/**
 * Store feature and value binded together
 */
@Entity
@Table( name = "FeatureValuePair" )
public class FeatureValuePair {
	public static void main(String[] args){
		FeatureValuePair.resetIndexes();
	}
	public enum FeatureName {
		// Document Features
		JournalTitle,
		CompletedYear,
		CreatedYear,
		RevisedYear,
		MESHHeading,


		// Paragraph Features
		// PositionInDoc,

		// Sentence Features
		ProteinCountInSentence,
		SentenceTFIDF,

		// Words Features
		POS,
		POSNext1,
		POSNext2,
		POSPre1,
		POSPre2,

		PorterStem, 
		WordnetStem, 
		OriginalWord, 
		NameEntity, 
		StartWithUppercase, 
		AllUppercase, 
		AllLowercase, 
		HasSpecialChars, 
		HasDigit, 

		CommaLeftCount,
		CommaRightCount,
		QuoteLeftCount,
		QuoteRightCount,
		ProteinCountInWindow,

		SimilarityToGene_expression, 
		SimilarityToTranscription, 
		SimilarityToProtein_catabolism, 
		SimilarityToLocalization, 
		SimilarityToBinding, 
		SimilarityToPhosphorylation, 
		SimilarityToRegulation, 
		SimilarityToPositive_regulation, 
		SimilarityToNegative_regulation,
		PositionInDoc,

		//coreference features
		AnaphoraIsSubject,
		AntecedentInFirstSubject,
		AntecedentInHeader,
		AntecedentIsSubject,
		Appositive,
		NumberAgreement,
		SentenceDistance, TWOGram, TWOGramBackward, ThreeGram, ThreeGramBackward, NellLink, 
		ProblemCountInSentence, TestCountInSentence, TreatmentCountInSentence, TestsBeforeWord, 
		TreatmentsBeforeWord, ProblemsBeforeWord, ProblemPossibleCountInSentence, ProblemHypoCountInSentence, 
		ProblemConditionalCountInSentence, ProblemAWSECountInSentence, ProblemAbsentCountInSentence, ProblemPresentCountInSentence, EdgeType,
		WordWindowNext, WordWindowPre, EdgeParsePath, EdgeParseDistance, DependencyLinkedTokens,

		TimexCount, ClinicalEventsCount, LinkWordBetween, LinkArgumentType, LinkFromPhrasePolarity, LinkFromPhraseModality, LinkFromPhraseType, LinkToPhraseModality, LinkToPhraseType, 
		LinkToPhrasePolarity, LinkToPhraseTimexMod, LinkFromPhraseTimexMod, 
		InterMentionLocationType, AreDirectlyConnected, HaveCommonGovernors, AreConjunctedAnd,
		//NGrams
		NonNormalizedNGram2, NonNormalizedNGram3, NorBetweenNGram2, NorBetweenNGram3, Link2GramBetween, 
		Link2GramFrom,Link2GramTo,

		//Link Args basic features
		FromPhraseContent, ToPhraseContent, FromPhrasePOS, ToPhrasePOS,

		LinkBetweenWordCount, LinkBetweenPhraseCount, 

		//ParseDependency features
		FromPhraseRelPrep, ToPhraseRelPrep, FromPhraseGovVerb, ToPhraseGovVerb,  FromPhraseGovVerbTense,
		FromPhraseGovVerbAux, toPhraseGovVerbAux, areGovVerbsConnected,
		normalizedDependencies,
		//pattern statistics
		POverlapGivenPattern, PBeforeGivenPattern, PAfterGivenPattern, PNoLinkGivenPattern, hasFeasibleLink, 
		POverlapGivenPatternTTO, PBeforeGivenPatternTTO, PAfterGivenPatternTTO, PNoLinkGivenPatternTTO,
		maxProbClassByPattern,

		ParseTreePath, ParseTreePathSize,


		//sectime features
		relatedSectionInDoc, AdmissionOrDischarge, 
		//normalized 
		fromPrepArg, toPrepArg, isToPhDirectPrepArgOfFromPh, isEventAfterProblem, norToTypeDep,
		fromToToPathExist, toToFromPathExist, fromToToPathSize, toToFromPathSize, customGraphPath,
		//custom graph

		LabeledGraphNorDepPath,  customGraphIndividualPath, customGraphPathString,


		TemporalSignal, FromPhrasePOS1By1, ToPhrasePOS1By1, 
		FromPhrasePOSWindowBefore, ToPhrasePOSWindowBefore,  
		FromPhrasePOSWindowAfter, ToPhrasePOSWindowAfter, 
		ToPhrasePOSBigramAfter, FromPhrasePOSBigramBefore,
		ToPhrasePOSBigramBefore, FromToPhrasePOSBigram, LinkFromToWordDistance, LinkPOSBetween,


		betweenChunck,




	}

	String featureName;
	String featureValue;

	//For string multi features this can be used to handle real value 
	// othewise 1 or 0 would be used for values(for tf_idfs)
	private String featureValueAuxiliary;

	private int featureValuePairId = -1;

	//reset every time used for training a model;-1 means not used in training
	private int tempFeatureIndex = -1;



	public int getTempFeatureIndex() {
		return tempFeatureIndex;
	}
	public void setTempFeatureIndex(int tempFeatureIndex) {
		this.tempFeatureIndex = tempFeatureIndex;
	}
	public void setFeatureName(String _featureName) {
		featureName = _featureName;
	}
	@NaturalId
	public String getFeatureName() {
		return featureName;
	}


	public void setFeatureValue(String _featureValue) {
		featureValue = _featureValue;
	}
	@NaturalId
	public String getFeatureValue() {
		return featureValue;
	}

	public FeatureValuePair()
	{

	}



	public void setFeatureValuePairId(int featureValuePairId) {
		this.featureValuePairId = featureValuePairId;
	}
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	public int getFeatureValuePairId() {
		return featureValuePairId;
	}


	void setFeatureValueAuxiliary(String featureValueAuxiliary) {
		this.featureValueAuxiliary = featureValueAuxiliary;
	}
	@NaturalId
	public String getFeatureValueAuxiliary() {
		return featureValueAuxiliary;
	}

	@Override public String toString()
	{
		return featureName+" = "+ featureValue + 
				" ("+featureValueAuxiliary+")";
	}

	@Override public boolean equals(Object pFeatureValuePair)
	{
		if(!(pFeatureValuePair instanceof FeatureValuePair))
			return false;
		FeatureValuePair fvp = (FeatureValuePair)pFeatureValuePair;
		if(fvp.getFeatureValuePairId() == featureValuePairId ||
				(fvp.getFeatureName() == featureName &&
				fvp.getFeatureValue().equals(featureValue)&&
				fvp.getFeatureValueAuxiliary() == featureValueAuxiliary))
			return true;
		else 
			return false;

	}
	@Override public int hashCode()
	{
		return featureValuePairId;
	}

	public static synchronized FeatureValuePair getInstance(String pFeatureName, 
			String pFeatureValue,
			String pFeatureValueAuxiliary)
	{
		FeatureValuePair feature_value;


		String hql = "from FeatureValuePair where featureName= :featureName "+
				" AND featureValue= :featureValue ";

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("featureValue", pFeatureValue);
		params.put("featureName", pFeatureName);

		if(pFeatureValueAuxiliary!=null)
		{
			hql += " AND featureValueAuxiliary= :featureValueAuxiliary ";
			params.put("featureValueAuxiliary", pFeatureValueAuxiliary);
		}

		List<FeatureValuePair> featurev_list = 
				(List<FeatureValuePair>) HibernateUtil.executeReader(hql, params);

		if(featurev_list.size() == 0)
		{
			feature_value = new FeatureValuePair();
			feature_value.setFeatureName(pFeatureName);
			feature_value.setFeatureValue(pFeatureValue);
			if(pFeatureValueAuxiliary!=null)
				feature_value.setFeatureValueAuxiliary(pFeatureValueAuxiliary);
			HibernateUtil.save(feature_value);
		}else
			feature_value = featurev_list.get(0);
		return feature_value;
	}

	public static FeatureValuePair getInstance(String pFeatureName, 
			String pFeatureValue)
	{

		return getInstance(pFeatureName, pFeatureValue, null);
	}

	public static List<String> multiValueFeatures = new ArrayList<String>();
	static
	{
		multiValueFeatures.add(FeatureName.MESHHeading.name());
		multiValueFeatures.add(FeatureName.SentenceTFIDF.name());
		multiValueFeatures.add(FeatureName.LinkWordBetween.name());
	}
	@Transient
	//TODO make it better
	public boolean isMultiValue()
	{
		boolean res = false;

		if(multiValueFeatures.contains(featureName))
			res = true;

		return res;
	}
	public static void resetIndexes() {
		resetIndexes(null, null);
	}
	/**
	 * Assign new index to features and exclude the given features from indexing
	 * @param excludedFeatures
	 */
	public static void resetIndexes(List<String> excludedFeatures, List<String> includeOnlyFeatures) {
		String hql = "update FeatureValuePair set tempFeatureIndex = "+Integer.MAX_VALUE;
		HibernateUtil.executeNonReader(hql, true);
		Session temp_Session = HibernateUtil.sessionFactory.openSession();
		String selecthql = "from FeatureValuePair order by featureName";


		List<FeatureValuePair> features = 
				(List<FeatureValuePair>) HibernateUtil.executeReader(selecthql,null,null, temp_Session);
		int new_feature_index = 0;
		int count =0;
		int featureSize = features.size();
		for(int i=0;i<featureSize;i++) {
			count++;

			FileUtil.logLine(null,"resetIndexes--------feature processed: "+count+"/"+features.size());
			temp_Session = HibernateUtil.clearSession(temp_Session);

			//load again fvp to get effect of bulk update
			String selectfvp = "from FeatureValuePair where featureValuePairId = "+features.get(i).getFeatureValuePairId();

			FeatureValuePair fvp  = 
					((List<FeatureValuePair>) HibernateUtil.executeReader(selectfvp,null,null, temp_Session)).get(0);

			int featureIndex = 
					fvp.getTempFeatureIndex();
			if(featureIndex==Integer.MAX_VALUE && 
					(excludedFeatures==null || !excludedFeatures.contains(fvp.getFeatureName())) &&
					(includeOnlyFeatures==null || includeOnlyFeatures.contains(fvp.getFeatureName())))
			{//not indexed before and not excluded? then assign feature index
				new_feature_index ++;
				if(fvp.getFeatureValueAuxiliary()!=null)
				{// separate feature index if it has auxiliary value
					featureIndex = new_feature_index;
					fvp.setTempFeatureIndex(featureIndex);
					HibernateUtil.save(fvp, temp_Session);
				}else
				{
					//feature index for attribute not set before
					//find one for the attribute
					featureIndex = new_feature_index;
					String update_attribute_index_hql = 
							"UPDATE FeatureValuePair set tempFeatureIndex = "+ featureIndex
							+ " where featureName='"+fvp.getFeatureName()+"'";
					HibernateUtil.executeNonReader(update_attribute_index_hql);
				}
			}
		}
		temp_Session.clear();
		temp_Session.close();
	}
	public static int getMaxIndex() {
		Session tmp_session = HibernateUtil.sessionFactory.openSession();
		String hql = "select max(tempFeatureIndex) from FeatureValuePair where tempFeatureIndex<"+Integer.MAX_VALUE;
		List res = HibernateUtil.executeReader(hql, null,null,tmp_session);
		tmp_session.clear();
		tmp_session.close();
		return (res.get(0)==null)?0:Integer.valueOf(res.get(0).toString());
	}
	public static int getMinIndexForAttribute(String attributeName) {
		String hql = "select min(tempFeatureIndex) from FeatureValuePair where " +
				"featureName='" + attributeName + "' and tempFeatureIndex>-1";
		List res = HibernateUtil.executeReader(hql);
		return (res.get(0)==null)?0:Integer.valueOf(res.get(0).toString());
	}
	public static FeatureValuePair getInstance(FeatureName linkType,
			String pFeatureValue) {

		return getInstance(linkType.name(), pFeatureValue);
	}

	public static FeatureValuePair getInstance(FeatureName linkType,
			String pFeatureValue, String auxValue) {

		return getInstance(linkType.name(), pFeatureValue, auxValue);
	}
	public static Integer getRelatedFromEventTypeFValuePairIds(
			String pFeatureValue)
	{
		Integer feature_value_id =null;

		//		String featureValueString = "";
		//		for(String val: pFeatureValues)
		//		{
		//			featureValueString = featureValueString.concat(", '"+val+"'");
		//		}
		//		featureValueString = featureValueString.replaceFirst(",", "");

		String hql = "from FeatureValuePair where featureName in ('LinkFromPhraseType') "+
				" AND featureValue = '"+pFeatureValue+"' ";


		List<FeatureValuePair> featurev_list = 
				(List<FeatureValuePair>) HibernateUtil.executeReader(hql);
		if (featurev_list.size() !=0)
		{
			feature_value_id = featurev_list.get(0).getFeatureValuePairId();
		}

		return feature_value_id;
	}
	public static Integer getRelatedToEventTypeFValuePairIds(
			String pFeatureValue)
	{
		Integer feature_value_id =null;

		String hql = "from FeatureValuePair where featureName in ('LinkToPhraseType') "+
				" AND featureValue ='"+pFeatureValue+"' ";


		List<FeatureValuePair> featurev_list = 
				(List<FeatureValuePair>) HibernateUtil.executeReader(hql);
		if (featurev_list.size() !=0)
		{
			feature_value_id = featurev_list.get(0).getFeatureValuePairId();
		}

		return feature_value_id;
	}
	public static FeatureValuePair getInstance(String name, boolean booleanValue) {
		return getInstance(name, (booleanValue?"1":"0"));
	}
	public static List<String> getAllFeatureNames() {
		String hql = "from FeatureValuePair group by featureName";


		List<FeatureValuePair> featurev_list = 	(List<FeatureValuePair>) HibernateUtil.executeReader(hql);
		List<String> featureNames = new ArrayList<String>();
		for(FeatureValuePair fvp : featurev_list)
			featureNames.add(fvp.getFeatureName());
		return featureNames;
	}
	public static List<FeatureValuePair> getAllFeatures() {
		String hql = "from FeatureValuePair group by featureName";


		List<FeatureValuePair> featurev_list = 	(List<FeatureValuePair>) HibernateUtil.executeReader(hql);
		return featurev_list;
	}


}


