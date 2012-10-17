
import java.io.*;
import java.util.*;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;

/**
 * the 'analysis engine (or annotator)' component for CPE.
 * It drives the entire recognization process and add annotations into the input JCas.
 * 
 * @author xuke
 *
 */
public class GeneNameAnnotator extends JCasAnnotator_ImplBase{
  // TODO set dictionary as configuration parameters
  private EntityRecognizer entityRecognizer;
  
  public void initialize(UimaContext aContext) throws ResourceInitializationException{
    super.initialize(aContext);
    entityRecognizer = new EntityRecognizer();
    entityRecognizer.initialize();
  }
  
  public void process(JCas aJCas){
    // get the entire context of a txt file 
    String fulltext = aJCas.getDocumentText();
    entityRecognizer.recognizeEntities(fulltext);
    
    ArrayList<MyAnnotation> entities = entityRecognizer.getEntities();
    for(MyAnnotation entity : entities){
      GeneNameEntity gene = new GeneNameEntity(aJCas);  
      gene.setBegin(entity.getBegin());
      gene.setEnd(entity.getEnd());
      gene.setSentenceID(entity.getSentenceID());
      gene.setName(entity.getName());
      gene.setGid(entity.getID());
      
      gene.addToIndexes();
      
    }
  }
 
}
