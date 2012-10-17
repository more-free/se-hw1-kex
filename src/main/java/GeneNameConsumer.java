/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/* 
 *******************************************************************************************
 * N O T E :     The XML format (XCAS) that this Cas Consumer outputs, is eventually 
 *               being superceeded by the more standardized and compact XMI format.  However
 *               it is used currently as the expected form for remote services, and there is
 *               existing tooling for doing stand-alone component development and debugging 
 *               that uses this format to populate an initial CAS.  So it is not 
 *               deprecated yet;  it is also being kept for compatibility with older versions.
 *               
 *               New code should consider using the XmiWriterCasConsumer where possible,
 *               which uses the current XMI format for XML externalizations of the CAS
 *******************************************************************************************               
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIndexRepository;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.impl.XCASSerializer;
import org.apache.uima.cas_data.FeatureStructure;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;

/**
 * 
 * The 'CAS consumer' component for CPE.
 * A simple CAS consumer that generates XCAS (XML representation of the CAS) files in the
 * filesystem.
 * <p>
 * This CAS Consumer takes one parameters:
 * <ul>
 * <li><code>OutputDirectory</code> - path to directory into which output files will be written</li>
 * </ul>
 * 
 * 
 */
public class GeneNameConsumer extends CasConsumer_ImplBase {
  /**
   * Name of configuration parameter that must be set to the path of a directory into which the
   * output files will be written.
   */
  public static final String PARAM_OUTPUTFILE = "outputFile";

  private File mOutputFile;

  public void initialize() throws ResourceInitializationException {
    super.initialize();
    mOutputFile = new File((String) getConfigParameterValue(PARAM_OUTPUTFILE));
  }

  /**
   * Processes the CasContainer which was populated by the TextAnalysisEngines. <br>
   * In this case, the CAS is converted to XML and written into the output file .
   * 
   * @param aCAS
   *          CasContainer which has been populated by the TAEs
   * 
   * @throws ResourceProcessException
   *           if there is an error in processing the Resource
   * 
   * @see org.apache.uima.collection.base_cpm.CasObjectProcessor#processCas(org.apache.uima.cas.CAS)
   */
  public void processCas(CAS aCAS) throws ResourceProcessException {
    JCas jcas;
    try {
      jcas = aCAS.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }
    
    // write GeneNameEntity instances to output file
    try{
      PrintWriter pw = new PrintWriter(mOutputFile);
     
      
      // following code return all entities with sorted order.
      FSIndex geneNameIndex = jcas.getAnnotationIndex(GeneNameEntity.type);
      Iterator geneNameIterator = geneNameIndex.iterator();
      
      ArrayList<MyAnnotation> res = new ArrayList<MyAnnotation>();
      while(geneNameIterator.hasNext()){
        GeneNameEntity gene = (GeneNameEntity) geneNameIterator.next();
        
        MyAnnotation ann = new MyAnnotation();
        ann.setBegin(gene.getBegin());
        ann.setEnd(gene.getEnd());
        ann.setName(gene.getName());
        ann.setSentenceID(gene.getSentenceID());
        ann.setID(gene.getGid());
        
        res.add(ann);
      }
      
      Collections.sort(res);
      
      for(MyAnnotation ann : res)
        writeGeneName(ann, pw);
      
      pw.close();
    }
    catch(Exception e){
      
    }
  }

  private void writeGeneName(MyAnnotation gene, PrintWriter pw){
    StringBuilder str = new StringBuilder();
    str.append(gene.getSentenceID());
    str.append("|");
    
    str.append(gene.getBegin());
    str.append(" ");
    str.append(gene.getEnd());
    str.append("|");
    
    str.append(gene.getName().trim());
    str.append("\n");
    
    pw.write(str.toString());
  }
}

