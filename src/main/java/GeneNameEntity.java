

/* First created by JCasGen Sat Oct 06 11:37:21 CST 2012 */

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * the 'type system' component for CPE, which is for both input and output. 
 * XML source: src/main/resources/NerTypeSystem.xml
 * @generated */
public class GeneNameEntity extends Annotation {  
  private int gid;
  public int getGid(){return gid;}
  public void setGid(int gid){this.gid = gid;}
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(GeneNameEntity.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected GeneNameEntity() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public GeneNameEntity(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public GeneNameEntity(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public GeneNameEntity(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: sentenceID

  /** getter for sentenceID - gets ID of sentence that contains the gene name entity
   * @generated */
  public String getSentenceID() {
    if (GeneNameEntity_Type.featOkTst && ((GeneNameEntity_Type)jcasType).casFeat_sentenceID == null)
      jcasType.jcas.throwFeatMissing("sentenceID", "GeneNameEntity");
    return jcasType.ll_cas.ll_getStringValue(addr, ((GeneNameEntity_Type)jcasType).casFeatCode_sentenceID);}
    
  /** setter for sentenceID - sets ID of sentence that contains the gene name entity 
   * @generated */
  public void setSentenceID(String v) {
    if (GeneNameEntity_Type.featOkTst && ((GeneNameEntity_Type)jcasType).casFeat_sentenceID == null)
      jcasType.jcas.throwFeatMissing("sentenceID", "GeneNameEntity");
    jcasType.ll_cas.ll_setStringValue(addr, ((GeneNameEntity_Type)jcasType).casFeatCode_sentenceID, v);}    
   
    
  //*--------------*
  //* Feature: name

  /** getter for name - gets name of the gene entity
   * @generated */
  public String getName() {
    if (GeneNameEntity_Type.featOkTst && ((GeneNameEntity_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "GeneNameEntity");
    return jcasType.ll_cas.ll_getStringValue(addr, ((GeneNameEntity_Type)jcasType).casFeatCode_name);}
    
  /** setter for name - sets name of the gene entity 
   * @generated */
  public void setName(String v) {
    if (GeneNameEntity_Type.featOkTst && ((GeneNameEntity_Type)jcasType).casFeat_name == null)
      jcasType.jcas.throwFeatMissing("name", "GeneNameEntity");
    jcasType.ll_cas.ll_setStringValue(addr, ((GeneNameEntity_Type)jcasType).casFeatCode_name, v);}    
  }

    