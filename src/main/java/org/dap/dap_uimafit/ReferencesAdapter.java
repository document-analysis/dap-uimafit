package org.dap.dap_uimafit;

import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.dap.data_structures.AnnotationReference;
import org.dap.data_structures.Document;

/**
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
