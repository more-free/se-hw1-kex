
/* First created by JCasGen Sat Oct 06 11:37:21 CST 2012 */

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Sat Oct 06 11:37:21 CST 2012
 * @generated */
public class GeneNameEntity_Type extends Annotation_Type {
  /** @generated */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (GeneNameEntity_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = GeneNameEntity_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new GeneNameEntity(addr, GeneNameEntity_Type.this);
  			   GeneNameEntity_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new GeneNameEntity(addr, GeneNameEntity_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = GeneNameEntity.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("GeneNameEntity");
 
  /** @generated */
  final Feature casFeat_sentenceID;
  /** @generated */
  final int     casFeatCode_sentenceID;
  /** @generated */ 
  public String getSentenceID(int addr) {
        if (featOkTst && casFeat_sentenceID == null)
      jcas.throwFeatMissing("sentenceID", "GeneNameEntity");
    return ll_cas.ll_getStringValue(addr, casFeatCode_sentenceID);
  }
  /** @generated */    
  public void setSentenceID(int addr, String v) {
        if (featOkTst && casFeat_sentenceID == null)
      jcas.throwFeatMissing("sentenceID", "GeneNameEntity");
    ll_cas.ll_setStringValue(addr, casFeatCode_sentenceID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_name;
  /** @generated */
  final int     casFeatCode_name;
  /** @generated */ 
  public String getName(int addr) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "GeneNameEntity");
    return ll_cas.ll_getStringValue(addr, casFeatCode_name);
  }
  /** @generated */    
  public void setName(int addr, String v) {
        if (featOkTst && casFeat_name == null)
      jcas.throwFeatMissing("name", "GeneNameEntity");
    ll_cas.ll_setStringValue(addr, casFeatCode_name, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public GeneNameEntity_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_sentenceID = jcas.getRequiredFeatureDE(casType, "sentenceID", "uima.cas.String", featOkTst);
    casFeatCode_sentenceID  = (null == casFeat_sentenceID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_sentenceID).getCode();

 
    casFeat_name = jcas.getRequiredFeatureDE(casType, "name", "uima.cas.String", featOkTst);
    casFeatCode_name  = (null == casFeat_name) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_name).getCode();

  }
}



    