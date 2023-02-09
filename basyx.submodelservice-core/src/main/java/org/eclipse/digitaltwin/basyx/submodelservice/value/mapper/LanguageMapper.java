package org.eclipse.digitaltwin.basyx.submodelservice.value.mapper;

import java.util.AbstractMap.SimpleEntry;

import org.eclipse.digitaltwin.aas4j.v3.model.LangString;
import org.eclipse.digitaltwin.aas4j.v3.model.impl.DefaultLangString;
import org.eclipse.digitaltwin.basyx.submodelservice.value.LangStringValue;

public class LanguageMapper {
	
	private LanguageMapper() {
	    throw new IllegalStateException("Mapper Utility class");
	 }
	
	public static LangStringValue toLanguageValue(LangString langString) {
		return mapLangString(langString);
	}
	
	public static LangString toLangString(LangStringValue langStringValue) {
		return mapToLangString(langStringValue);
	}

	private static LangStringValue mapLangString(LangString langString) {
		return new LangStringValue(langString.getText(), langString.getLanguage());
	}
	
	private static LangString mapToLangString(LangStringValue langStringValue) {
		return new DefaultLangString(langStringValue.getLanguage().getValue(), langStringValue.getLanguage().getKey());
	}
}
