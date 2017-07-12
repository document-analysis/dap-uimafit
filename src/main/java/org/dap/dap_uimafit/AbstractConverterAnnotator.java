package org.dap.dap_uimafit;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.dap.annotators.Annotator;
import org.dap.common.DapException;

/**
 * 
 *
 * <p>
 * Date: 12 Jul 2017
 * @author Asher Stern
 *
 */
public class AbstractConverterAnnotator extends Annotator
{
	public AbstractConverterAnnotator(AnalysisEngine uimaAnalysisEngine)
	{
		this(uimaAnalysisEngine, createJCas());
		this.casShouldBeReleased = true;
	}

	public AbstractConverterAnnotator(AnalysisEngine uimaAnalysisEngine, JCas jcas)
	{
		this.uimaAnalysisEngine = uimaAnalysisEngine;
		this.jcas = jcas;
		this.casShouldBeReleased = false;
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



	protected final AnalysisEngine uimaAnalysisEngine;
	protected final JCas jcas;
	private boolean casShouldBeReleased = false;
}
