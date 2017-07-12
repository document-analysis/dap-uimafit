package org.dap.dap_uimafit;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.dap.data_structures.AnnotationReference;
import org.dap.data_structures.Document;

/**
 * 
 *
 * <p>
 * Date: 12 Jul 2017
 * @author Asher Stern
 *
 */
public class AggregateReferencesAdapter implements ReferencesAdapter
{
	public AggregateReferencesAdapter(ReferencesAdapter... adapters)
	{
		this (Arrays.asList(adapters));
	}
	
	public AggregateReferencesAdapter(List<ReferencesAdapter> adapters)
	{
		super();
		this.adapters = adapters;
	}

	@Override
	public void adapt(Document document, JCas jcas, Map<AnnotationReference, Annotation> mapDapToUima, Map<Annotation, AnnotationReference> mapUimaToDap)
	{
		for (ReferencesAdapter adapter : adapters)
		{
			adapter.adapt(document, jcas, mapDapToUima, mapUimaToDap);
		}
	}
	
	private final List<ReferencesAdapter> adapters;
}
