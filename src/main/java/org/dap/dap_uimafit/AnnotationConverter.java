package org.dap.dap_uimafit;

import org.dap.data_structures.AnnotationContents;

/**
 * 
 *
 * <p>
 * Date: 2 Jun 2017
 * @author Asher Stern
 *
 * 
 * @param <A>
 */
public interface AnnotationConverter<A extends AnnotationContents>
{
	public A convert(org.apache.uima.jcas.tcas.Annotation uimaAnnotation);
}
