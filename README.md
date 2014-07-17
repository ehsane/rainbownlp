rainbownlp
==========

RainbowNLP (RNLP) is a framework for various natural language processing tasks including: supervised name entity recognition and relationship extraction.


Configuration
==========
RainbowNLP (RNLP) is based on Hibernate and can be easily configured to use different data source (e.g. mysql).
Hibernate settings are in hibernate.cfg.xml, make sure to set connection.url, connection.username 
and connection.password correctly. 
Other application settings are in configuration.conf, make sure the paths are updated and exist.  

The following instructions show how to use RNLP for different applications.

Relationship Extraction
==========
For relationship extraction, the system needs to load the text document and entities annotations first. 

1. Load test/train documents into the framework by using "SimpleDocumentLoader" or 
create a new document loader by implementing "IDocumentAnalyzer" interface (for test/train sets).

2. Load annotations by creating instances of "Phrase" and "PhraseLink" and make sure to persist them with HibernateUtil (for test/train sets).

3. Create machine learning examples by creating Phrase/PhraseLink and MLExample objects (for test/train sets)

4. Calculate features for each machine learning example (MLExample objects)
 
5. Train a machine learning model with train examples.

6. evaluate model using test examples.


How to cite in your publication?
==========
Please cite the following publcation if you sue RNLP in your experiments:

Emadzadeh, E.; Jonnalagadda, S.; Gonzalez, G., "Evaluating Distributional Semantic and Feature Selection for Extracting Relationships from Biological Text," Machine Learning and Applications and Workshops (ICMLA), 2011 10th International Conference on , vol.2, no., pp.66,71, 18-21 Dec. 2011

Question/Comments
==========
Feel free to contact us with you question/comments:
Ehsan Emadzadeh (eemadzadeh (at) gmail.com)
Azadeh Nikfarjam (azadeh.nikfarjam (at) gmail.com)
  