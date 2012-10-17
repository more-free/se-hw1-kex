import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.FileUtils;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

/**
 * The 'collection reader' component for CPE. 
 * It takes as input an entire text, which might contain multiple lines of formatted text,
 * and update the document text information in the input CAS instance, make it ready for the annotator.
 *   
 * @author xuke
 *
 */
public class FileCollectionReader extends CollectionReader_ImplBase {
  /* three input parameters : the directory of input files, the encoding and the language. */
  public static final String PARAM_INPUTDIR = "InputDirectory";
  public static final String PARAM_ENCODING = "Encoding";
  public static final String PARAM_LANGUAGE = "Language";
  
  private ArrayList<File> mFiles;
  private int mCurrentIdx;
  private String mEncoding;
  private String mLanguage;
  
  /**
   * acquire parameters and generate file text (as String)
   */
  public void initialize() throws ResourceInitializationException {
    // get InputDirectory parameter of collection reader, which might be a single file
    File file = new File(((String) getConfigParameterValue(PARAM_INPUTDIR)).trim());
    
    if(!file.exists()){
      throw new ResourceInitializationException(ResourceConfigurationException.DIRECTORY_NOT_FOUND,
              new Object[] { PARAM_INPUTDIR, this.getMetaData().getName(), file.getPath() });
    }
    
    mFiles = new ArrayList<File>();
    if(file.isFile()){
      mFiles.add(file);
    }else if(file.isDirectory()){
      File [] files = file.listFiles();
      for(File f : files)
        mFiles.add(f);
    }

    mCurrentIdx = 0;
    // use the default platform encoding if mEncoding is null, with a maximum buffer of 10000 chars
    mEncoding =  (String)getConfigParameterValue(PARAM_ENCODING);
    mLanguage  = (String)getConfigParameterValue(PARAM_LANGUAGE);
  }
  
  /**
   * get the entire text ready for annotator
   * @override 
   */
  public void getNext(CAS aCAS) throws IOException, CollectionException {
    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new CollectionException(e);
    }
    
    // open input stream to file
    File file = (File) mFiles.get(mCurrentIdx++);
    String text = FileUtils.file2String(file, mEncoding);
    // put document in CAS
    jcas.setDocumentText(text);

    // set language if it was explicitly specified as a configuration parameter
    if (mLanguage != null) {
      ((DocumentAnnotation) jcas.getDocumentAnnotationFs()).setLanguage(mLanguage);
    }

  }

  @Override
  public void close() throws IOException {
    // TODO Auto-generated method stub

  }

  @Override
  public Progress[] getProgress() {
    return new Progress[] { new ProgressImpl(mCurrentIdx, mFiles.size(), Progress.ENTITIES) };
  }

  @Override
  public boolean hasNext() throws IOException, CollectionException {
    return mCurrentIdx < mFiles.size();
  }

}
