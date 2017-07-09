package org.dap.dap_uimafit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.dap.annotators.Annotator;
import org.dap.common.DapException;
import org.dap.data_structures.AnnotationContents;
import org.dap.data_structures.AnnotationReference;
import org.dap.data_structures.Document;
import org.dap.data_structures.LanguageFeature;

/**
 * 
 *
 * <p>
 * Date: 2 Jun 2017
 * @author Asher Stern
 *
 */
public class ConverterAnnotator extends Annotator
{
	public ConverterAnnotator(AnalysisEngine uimaAnalysisEngine)
	{
		this(uimaAnalysisEngine, createJCas());
		this.casShouldBeReleased = true;
	}
	
	public ConverterAnnotator(AnalysisEngine uimaAnalysisEngine, JCas jcas)
	{
		this(
				Collections.singletonMap(org.apache.uima.jcas.tcas.Annotation.class, DefaultAnnotationConverter.INSTANCE),
				uimaAnalysisEngine, jcas);
		this.casShouldBeReleased = false;
	}
	
	public ConverterAnnotator(Map<Class<?>, AnnotationConverter<?>> converters, AnalysisEngine uimaAnalysisEngine)
	{
		this(converters, uimaAnalysisEngine, createJCas());
		this.casShouldBeReleased = true;
	}

	public ConverterAnnotator(Map<Class<?>, AnnotationConverter<?>> converters, AnalysisEngine uimaAnalysisEngine, JCas jcas)
	{
		this.converters = converters;
		this.sortedAnnotationClasses = sortClasses(converters.keySet());
		this.uimaAnalysisEngine = uimaAnalysisEngine;
		this.jcas = jcas;
		this.casShouldBeReleased = false;
	}
	
	
	public void setReferencesAdapter(ReferencesAdapter referencesAdapter)
	{
		this.referencesAdapter = referencesAdapter;
	}

	@Override
	public synchronized void annotate(Document document) // it must be synchronized, because jcas is a member field
	{
		try
		{
			Map<AnnotationReference, org.apache.uima.jcas.tcas.Annotation> mapDapToUima = new LinkedHashMap<>();
			Map<org.apache.uima.jcas.tcas.Annotation, AnnotationReference> mapUimaToDap = new LinkedHashMap<>();
			
			jcas.setDocumentLanguage(LanguageFeature.getDocumentLanguage(document));
			jcas.setDocumentText(document.getText());
			uimaAnalysisEngine.process(jcas);
			for (org.apache.uima.jcas.tcas.Annotation uimaAnnotation : jcas.getAnnotationIndex())
			{
				Class<?> uimaAnnotationClass = uimaAnnotation.getClass();
				if (DocumentAnnotation.class != uimaAnnotationClass)
				{
					for (Class<?> converterUimaAnnotationClass : sortedAnnotationClasses)
					{
						if (converterUimaAnnotationClass.isAssignableFrom(uimaAnnotationClass))
						{
							AnnotationContents annotationContents = converters.get(converterUimaAnnotationClass).convert(uimaAnnotation);
							AnnotationReference reference = document.addAnnotation(uimaAnnotation.getBegin(), uimaAnnotation.getEnd(), annotationContents);
							map(mapDapToUima, mapUimaToDap, reference, uimaAnnotation);
							break;
						}
					}
					// If not converted, then the caller just don't want this type of annotation to be converted.
				}
			}
			if (referencesAdapter!=null)
			{
				referencesAdapter.adapt(document, jcas, mapDapToUima, mapUimaToDap);
			}
		}
		catch(AnalysisEngineProcessException e)
		{
			throw new DapException(e);
		}
	}
	
	@Override
	public void close()
	{
		super.close();
		if ( (jcas!=null) && (casShouldBeReleased) )
		{
			jcas.release();
		}
	}
	
	
	private void map(
			Map<AnnotationReference, org.apache.uima.jcas.tcas.Annotation> mapDapToUima,
			Map<org.apache.uima.jcas.tcas.Annotation, AnnotationReference> mapUimaToDap,
			AnnotationReference dapAnnotation,
			org.apache.uima.jcas.tcas.Annotation uimaAnnotation)
	{
		if (mapDapToUima.containsKey(dapAnnotation)) {throw new DapException("Duplicate annotation to map: "+dapAnnotation.toString());}
		if (mapUimaToDap.containsKey(uimaAnnotation)) {throw new DapException("Duplicate annotation to map.");}
		mapDapToUima.put(dapAnnotation, uimaAnnotation);
		mapUimaToDap.put(uimaAnnotation, dapAnnotation);
	}


	private static class ClassComparator implements Comparator<Class<?>>
	{
		@Override
		public int compare(Class<?> o1, Class<?> o2)
		{
			if (o1.isAssignableFrom(o2)) {return 1;}
			else if (o2.isAssignableFrom(o1)) {return -1;}
			else {return 0;}
		}
	}
	
	private static ArrayList<Class<?>> sortClasses(Collection<Class<?>> classes)
	{
		ArrayList<Class<?>> list = new ArrayList<>(classes.size());
		list.addAll(classes);
		list.sort(classComparator);
		return list;
	}
	
	private static JCas createJCas()
	{
		try
		{
			return JCasFactory.createJCas();
		}
		catch (UIMAException e)
		{
			throw new DapException(e);
		}
	}
	
	
	

	private final Map<Class<?>, AnnotationConverter<?>> converters;
	private final List<Class<?>> sortedAnnotationClasses;
	private final AnalysisEngine uimaAnalysisEngine;
	private final JCas jcas;
	private boolean casShouldBeReleased = false;
	private ReferencesAdapter referencesAdapter = null;
	
	
	private static final ClassComparator classComparator = new ClassComparator();
}

