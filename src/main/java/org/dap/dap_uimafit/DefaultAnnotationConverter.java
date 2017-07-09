package org.dap.dap_uimafit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.uima.jcas.tcas.Annotation;
import org.dap.common.DapException;

/**
 * 
 *
 * <p>
 * Date: 2 Jun 2017
 * @author Asher Stern
 *
 */
public class DefaultAnnotationConverter implements AnnotationConverter<DefaultUimaAnnotationContents>
{
	public static final DefaultAnnotationConverter INSTANCE = new DefaultAnnotationConverter();
	
	public static final String[] EXCLUDE_GETTERS_ARRAY = new String[]{"get", "getTypeIndexID", "getBegin", "getEnd", "getCoveredText", "getStart"};
	public static final Set<String> EXCLUDE_GETTERS;
	static
	{
		EXCLUDE_GETTERS = new LinkedHashSet<>();
		for (String getter : EXCLUDE_GETTERS_ARRAY)
		{
			EXCLUDE_GETTERS.add(getter);
		}
	}
	
	@Override
	public DefaultUimaAnnotationContents convert(Annotation uimaAnnotation)
	{
		try
		{
			Map<String, String> fields = new LinkedHashMap<>();
			
			Method[] methods = uimaAnnotation.getClass().getMethods();
			for (Method method : methods)
			{
				if (Modifier.isPublic(method.getModifiers()) && (!Modifier.isStatic(method.getModifiers())) && (method.getParameterCount()==0) )
				{
					final String name = method.getName();
					if (name.startsWith("get"))
					{
						if (!EXCLUDE_GETTERS.contains(name))
						{
							String fieldName = lowerFirstLetter(name.substring("get".length()));
							Object valueObject = method.invoke(uimaAnnotation);
							if (valueObject!=null)
							{
								String value = valueObject.toString();
								fields.put(fieldName, value);
							}
						}
					}
				}
			}
			
			return new DefaultUimaAnnotationContents(uimaAnnotation.getClass().getName(), uimaAnnotation.getClass().getSimpleName(), fields);
		}
		catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new DapException(e);
		}
	}
	
	private static String lowerFirstLetter(String str)
	{
		return String.valueOf(Character.toLowerCase(str.charAt(0)))+str.substring(1);
		
	}
}
