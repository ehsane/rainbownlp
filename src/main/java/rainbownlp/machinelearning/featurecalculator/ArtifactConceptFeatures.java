package rainbownlp.machinelearning.featurecalculator;

public enum ArtifactConceptFeatures {
	ConceptContent,
	ArtifactContent, ConceptInArtifactContent, CountInDocument, CountInSentence, 
	ConceptSectionType, SentenceWordSize,
	NonNormalizedNGram2, DoesIncludeGeneNameNumber, PreBigram, hasGeneFunctionTerm, 
	NormalizedDependencies, IsNegated,
	MaxSemanticSimilarityScore, SentenceHasVerb, SemanticSimilarityToAbstract
}
