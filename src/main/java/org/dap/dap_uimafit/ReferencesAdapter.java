package org.dap.dap_uimafit;

import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.dap.data_structures.AnnotationReference;
import org.dap.data_structures.Document;

/**
 * Invoked by {@link ConverterAnnotator} to perform further annotations on the given DAP document, right after
 * the UIMA annotations were converted and added to the document.
 * 
 * <p>
 * Typically, this involves setting references to other annotations in some DAP annotations. Setting those references
 * is typically impossible because the referenced annotations haven't yet been created, so adding it has to be done at the end,
 * after all the DAP annotations have been created.
 * 
 * <p>
 * A {@link ReferencesAdapter} is optional for {@link ConverterAnnotator},
 * and can be set by {@link ConverterAnnotator#setReferencesAdapter(ReferencesAdapter)}.
 *  
 *
 * <p>
 * Date: 9 Jul 2017
 * @author Asher Stern
 *
 */
public interface ReferencesAdapter
{
	public void adapt(
			Document document,
			JCas jcas,
			Map<AnnotationReference, org.apache.uima.jcas.tcas.Annotation> mapDapToUima,
			Map<org.apache.uima.jcas.tcas.Annotation, AnnotationReference> mapUimaToDap
			);
}
