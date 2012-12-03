import java.io.*;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

/**
 * The EntityRecognizer class encapsulates core routine for recognizing gene name entities, by
 * first calling the initialize() method and then calling the recognizeEntities(String inputStr) method.
 * Specifically, initialize() method load dictionaries that are helpful for lexical preprocessing and future 
 * recognization, then recognizeEntities(String inputStr) accepts a formatted text, recognizes gene names in it,
 * and populate the member 'entities' with a mid-structure MyAnnotation. After recognization, results can be
 * used for 'Cas Consumer' by calling getEntities() method.
 * 
 * @author xuke
 *
 */
public class EntityRecognizer{
  /* dictionary files, which actually should be written into a resource file. */
  private String GENE_TERM_DIC = "/dictionary/gene_term_dictionary.txt";
  private String COMMON_WORDS = "/dictionary/common_words.txt";
  private String FULL_WORDS = "/dictionary/full_dictionary.txt";
  private String CORPUS = "/dictionary/gone_with_the_wind.txt";
  
  // the Part-Of-Speech tagger, which is used to assist the final filtering of gene names.
  private PosTagger posTagger = new PosTagger();
  
  // stores known entities (from dictionaries) of 1-word length.
  private Set<String> singleKnownEntities = new HashSet<String>();
  // stores all kinds of known entities (from dictionaries)
  private Set<String> allKnownEntities = new HashSet<String>();
  // stores a full English word list which contains about 180,000 English words 
  private Set<String> fullDictionary = new HashSet<String>();
  // stores 'common words' which refer to those unlikely to be gene names. e.g. those appear in fictions.
  private Set<String> commonWords = new HashSet<String>();
  // stores the results (gene name entities) by MyAnnotation, which is used as a middle structure between 
  // the algorithms and UIMA framework.
  private ArrayList<MyAnnotation> entities = new ArrayList<MyAnnotation>();
  private int entityID = 0;
  
  EntityRecognizer(){
    
  }
  
  /**
   * calling after initialize() and recognizeEntities(String inputStr), return the analysis results
   * @return gene name entities, each of which is stored by a MyAnnotation instance.
   */
  public ArrayList<MyAnnotation> getEntities(){
    return entities;
  }
  
  /**
   * first-called method after an EntityRecognizer instance is created.
   * load all kinds of dictionaries and do all kinds of preprocessing.
   */
  public void initialize(){
    loadKnownEntities(GENE_TERM_DIC);
    loadDictionary(FULL_WORDS);
  
    loadForbiddenWords(COMMON_WORDS);
    loadForbiddenWords(CORPUS);
    loadForbiddenWords(FULL_WORDS, allKnownEntities);
  }
  
  /**
   * each calling to this method leads to a thoroughly analysis towards the inputStr.
   * @param inputStr loaded from an input text, which is usually the entire formatted text gotten by 
   * collection reader.
   */
  public void recognizeEntities(String inputStr){
    this.clear();
    
    try{
      Scanner sc = new Scanner(inputStr);
      while(sc.hasNextLine()){
        String line = sc.nextLine();
        parseLine(line);
      }
      
      sc.close();
      
    }
    catch(Exception e){
      System.out.println("No such file found!");
    }
  }
  
  /**
   * overloaded version of recognizeEntities(String inputStr). The parameter is a File instance rather than
   * a String.
   * @param inputFile indicates the File instance which is to be analyzed.
   */
  public void recognizeEntities(File inputFile){
    this.clear();
    
    try{
      Scanner sc = new Scanner(inputFile);
      while(sc.hasNextLine()){
        String line = sc.nextLine();
        parseLine(line);
      }
      
      sc.close();
    }
    catch(Exception e){
      System.out.println("No such file found!");
    }
  }
  
  /**
   * print the analyzed results to file indicated by output parameter.
   * @param output
   */
  protected void printEntities(String output){
    try{
      PrintWriter pw = new PrintWriter(new File(output));
      for(MyAnnotation ann : entities){
        String line = ann.getSentenceID() + " " + ann.getName() + " " + ann.getBegin() + " " + ann.getEnd() + "\n";
        pw.write(line);
      }
      
      pw.close();
    }
    catch(Exception e){
      System.out.println("No such file found!");
    }
  }
  
  /*
   * restore all members to initial status.
   */
  private void clear(){
    this.entities.clear();
  }
  
  /*
   * load known names from a gene name dictionary
   */
  private void loadKnownEntities(String dic){
    try{
      BufferedReader reader = new BufferedReader(
              new InputStreamReader(this.getClass().getResourceAsStream(dic))
              );
      String line = "";
      while( ( line = reader.readLine() ) != null ){
        String [] terms  = line.split(" ");
        for(String t : terms)
          allKnownEntities.add(t.toLowerCase());
        
        singleKnownEntities.add(terms[0]);
      }
      
      reader.close();
    }
    catch(Exception e){
      System.out.println("No such file found!");
    }
  }
  
  /*
   * load English words from full words dictionary
   */
  private void loadDictionary(String dic){
    Morphology mor = new Morphology();
    
    InputStream stream = this.getClass().getResourceAsStream(dic);
    try{
      Scanner sc = new Scanner(stream);
      while(sc.hasNext()){
        String line = sc.next();
        fullDictionary.add(line);
        fullDictionary.add(mor.stem(line));
      }
      
      stream.close();
      sc.close();
    }
    catch(Exception e){
      System.out.println("No such file found!");
    }
  }
  
  /*
   * load words that are unlikely to appear to be a gene name
   */
  private void loadForbiddenWords(String file){
    try{
      Morphology mor = new Morphology();
      
      InputStream stream = this.getClass().getResourceAsStream(file);
      Scanner sc = new Scanner(stream);
      while(sc.hasNext()){
        String token = sc.next().trim();
         commonWords.add(token);
         commonWords.add(mor.stem(token)); 
      }
      
      stream.close();
      sc.close();
    }
    catch(Exception e){
      System.out.println("No such file found!");
    }
  }
  
  /*
   * load words that are unlikely to appear to be a gene name, except when the words are already in dic.
   */
  private void loadForbiddenWords(String file, Set<String> dic){
    try{
      InputStream stream = this.getClass().getResourceAsStream(file);
      Scanner sc = new Scanner(stream);
      while(sc.hasNext()){
        String token = sc.next().trim();
        
        if(!dic.contains(token))
          commonWords.add(token);
      }
      
      stream.close();
      sc.close();
    }
    catch(Exception e){
      System.out.println("No such file found!");
    }
  }
   
  private boolean isAllLetters(String token){
    for(int i=0; i<token.length(); i++){
      char c = token.charAt(i);
      if(!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')))
        return false;
    }
    return true;
  }
  
  /*
   * mainly recognization routine, including stemming, part-of-speech tagging, recognizing continuous words
   * and sotring recognization results.
   */
  private void parseLine(String line){
    // POS
    posTagger.recognizePos(line);
    
    // tokenize 
    TokenizerFactory<Word> factory = PTBTokenizerFactory.newTokenizerFactory();
    Tokenizer<Word> tokenizer = factory.getTokenizer(new StringReader(line));
    List<Word> words = tokenizer.tokenize();
    
    // stem
    Morphology mor = new Morphology();     
    ArrayList<String> tokens = new ArrayList<String>();
    for(Word word : words){
      tokens.add(word.toString());
    }
    
    
    int i = 1; // token[0] is sentence ID
    int curLen = 0;
    while(i < tokens.size()){
      String ti = tokens.get(i);
      String tiLow = ti.toLowerCase();
      
      if(!isValidToken(tokens.get(i)) || 
              (!allKnownEntities.contains(tiLow) && !isAllUpperCase(ti)) || 
                commonWords.contains(tiLow) || 
                  commonWords.contains(mor.stem(tiLow))){ 
        curLen += tokens.get(i).length();
        i++;
        continue;
      }
      
      boolean multicase = false;
      int j = i + 1;
      for(; j < tokens.size(); j++){
        String tj = tokens.get(j);
        String tjLow = tj.toLowerCase();
        
        if( isValidToken(tj) && 
                ( allKnownEntities.contains(tjLow) || isAllUpperCase(tj)) &&
                  !commonWords.contains(tjLow) &&
                    !commonWords.contains(mor.stem(tjLow)))
          multicase = true;
        else
          break;
      }
      
      // merge token [i - j)
      StringBuilder name = new StringBuilder();
      int len = 0;
      for(int k = i; k < j; k++){
        len += tokens.get(k).length();
        name.append(tokens.get(k));
        if(k+1 < j)
          name.append(" ");
      }
      
      // set MyAnnotation
      MyAnnotation ann = new MyAnnotation();
      ann.setSentenceID(tokens.get(0));
      ann.setBegin(curLen);
      ann.setEnd(curLen + len - 1);
      ann.setName(name.toString());
      ann.setID(++this.entityID);
      
     
      if(multicase)
        entities.add(ann);
      else{
        if(singleKnownEntities.contains(ann.getName()) && !commonWords.contains(ann.getName())){
          if(!fullDictionary.contains(ann.getName()) && !fullDictionary.contains(mor.stem(ann.getName())))
            entities.add(ann);
          else
            if(posTagger.getPosDictionary().get(ann.getName().trim()) != null)  // only add noun.
              entities.add(ann);
        }
      }
     
      curLen += len;
      i = j;
    }
    
  }
  
  private boolean isValidToken(String context){
    if(context.matches("[,.?:;'{}!*+-=_]") || 
                 isNumber(context) || hasNoLetter(context) || 
                 (context.charAt(0) == '-' && context.charAt(context.length()-1) == '-'))
      return false;
    return true;
  }

  private boolean isNumber(String context){
    for(int i = 0; i < context.length(); i++){
      char c = context.charAt(i);
      if(c > '9' || c < '0')
        return false;
    }
    return true;
  }
  
  private boolean hasNoLetter(String context){
    for(int i=0; i<context.length(); i++){
      char c = context.charAt(i);
      if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
        return false;
    }
    
    return true;
  }
  
  private boolean isAllUpperCase(String context){
    for(int i=0; i<context.length(); i++){
      char c = context.charAt(i);
      if(!(c >= 'A' && c <= 'Z'))
        return false;
    }
    return true;
  }
  
  //for test only
  public static void main(String [] args){
    /*
    InputStream stream = EntityRecognizer.class.getResourceAsStream("/dictionary/common_words.txt");
    System.out.println(stream != null);
    stream = EntityRecognizer.class.getClassLoader()
        .getResourceAsStream("dictionary/common_words.txt");
    System.out.println(stream != null);
    */
    
  }
}


/*
 * internal used to communicate between UIMA and my program
 */
class MyAnnotation implements Comparable<MyAnnotation>{
private int begin, end;
private String sentenceID;
private String name;
private int id;  // processing order for internal use.

public int getBegin(){
 return begin;
}

public int getEnd(){
 return end;
}

public String getSentenceID(){
 return sentenceID;
}

public String getName(){
 return name;
}

public int getID(){
 return id;
}

public void setID(int id){
 this.id = id;
}

public void setBegin(int begin){
 this.begin = begin;
}
public void setEnd(int end){
 this.end = end;
}
public void setSentenceID(String id){
 sentenceID = id;
}
public void setName(String name){
 this.name = name;
}

@Override
public int compareTo(MyAnnotation o) {
 if(this.id < o.id)
   return -1;
 else if(this.id == o.id)
   return 0;
 else
   return 1;
}
}

/*
 * internal used for better recognizing gene names.
 */
class PosTagger {

private StanfordCoreNLP pipeline;
private Map<String, String> posDic = new HashMap<String, String>();

public Map<String, String> getPosDictionary(){
 return posDic;
}

public PosTagger(){
 Properties props = new Properties();
 props.put("annotators", "tokenize, ssplit, pos");
 pipeline = new StanfordCoreNLP(props);
}


public void recognizePos(String text) {
 posDic.clear();
 
 Annotation document = new Annotation(text);
 pipeline.annotate(document);
 List<CoreMap> sentences = document.get(SentencesAnnotation.class);
 for (CoreMap sentence : sentences) {
   for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
     String pos = token.get(PartOfSpeechAnnotation.class);
     if (pos.startsWith("NN")) 
       posDic.put(token.toString(), pos);
   }
 }
}
}

