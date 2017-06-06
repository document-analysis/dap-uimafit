package org.dap.dap_uimafit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.dap.annotators.Annotator;
import org.dap.common.DapException;
import org.dap.data_structures.AnnotationContents;
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
	}
	
	public ConverterAnnotator(AnalysisEngine uimaAnalysisEngine, JCas jcas)
	{
		this(
				Collections.singletonMap(org.apache.uima.jcas.tcas.Annotation.class, DefaultAnnotationConverter.INSTANCE),
				uimaAnalysisEngine, jcas);
	}
	
	public ConverterAnnotator(Map<Class<?>, AnnotationConverter<?>> converters, AnalysisEngine uimaAnalysisEngine)
	{
		this(converters, uimaAnalysisEngine, createJCas());
	}

	public ConverterAnnotator(Map<Class<?>, AnnotationConverter<?>> converters, AnalysisEngine uimaAnalysisEngine, JCas jcas)
	{
		this.converters = converters;
		this.sortedAnnotationClasses = sortClasses(converters.keySet());
		this.uimaAnalysisEngine = uimaAnalysisEngine;
		this.jcas = jcas;
	}
	
	

	@Override
	public synchronized void annotate(Document document) // it must be synchronized, because jcas is a member field
	{
		try
		{
			jcas.setDocumentLanguage(LanguageFeature.getDocumentLanguage(document));
			jcas.setDocumentText(document.getText());
			uimaAnalysisEngine.process(jcas);
			for (org.apache.uima.jcas.tcas.Annotation uimaAnnotation : jcas.getAnnotationIndex())
			{
				Class<?> uimaAnnotationClass = uimaAnnotation.getClass();
				for (Class<?> converterUimaAnnotationClass : sortedAnnotationClasses)
				{
					if (converterUimaAnnotationClass.isAssignableFrom(uimaAnnotationClass))
					{
						AnnotationContents annotationContents = converters.get(converterUimaAnnotationClass).convert(uimaAnnotation);
						document.addAnnotation(uimaAnnotation.getBegin(), uimaAnnotation.getEnd(), annotationContents);
						break;
					}
				}
				// If not converted, then the caller just don't want this type of annotation to be converted.
			}
		}
		catch(AnalysisEngineProcessException e)
		{
			throw new DapException(e);
		}
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
	
	private static final ClassComparator classComparator = new ClassComparator();
}

