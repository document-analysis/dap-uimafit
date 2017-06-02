package org.dap.dap_uimafit;

import java.util.Map;

import org.dap.data_structures.AnnotationContents;

/**
 * 
 *
 * <p>
 * Date: 2 Jun 2017
 * @author Asher Stern
 *
 */
public class DefaultUimaAnnotationContents extends AnnotationContents
{
	private static final long serialVersionUID = 1075786220031982431L;
	
	public DefaultUimaAnnotationContents(String uimaAnnotationClassName, String uimaAnnotationClassSimpleName, Map<String, String> fields)
	{
		super();
		this.uimaAnnotationClassName = uimaAnnotationClassName;
		this.uimaAnnotationClassSimpleName = uimaAnnotationClassSimpleName;
		this.fields = fields;
	}

	
	public String getUimaAnnotationClassName()
	{
		return uimaAnnotationClassName;
	}
	public String getUimaAnnotationClassSimpleName()
	{
		return uimaAnnotationClassSimpleName;
	}
	public Map<String, String> getFields()
	{
		return fields;
	}


	private final String uimaAnnotationClassName;
	private final String uimaAnnotationClassSimpleName;
	private final Map<String, String> fields;
}
