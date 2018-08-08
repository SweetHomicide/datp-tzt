package com.ditp.lang;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public final class LangResource {

	private LangResource() {

	}

	private static final String RESOURCE_PREFIX = "messages_";

	private static final Map<String, ResourceBundle> RESOURCE_BUNDLE_MAP = new HashMap<>();
	static {
		reloadResource();
	}

	public static final void reloadResource() {
		Locale[] supportLocales = LanguagesSupported.getSupportedLanguages();
		for (int i = 0; i < supportLocales.length; ++i) {
			String localeTag = LanguagesSupported.getLocaleTag(supportLocales[i]);
			RESOURCE_BUNDLE_MAP.put(localeTag,
					ResourceBundle.getBundle(RESOURCE_PREFIX + localeTag, supportLocales[i]));
		}
	}

	public static final String getMessage(String key, Object... params) {
		ResourceBundle bundle = RESOURCE_BUNDLE_MAP.get(LanguagesSupported.getCurrentLocaleTag());
		if (null == bundle) {
			return key;
		}

		String message = null;
		try {
			message = bundle.getString(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null == message) {
			return key;
		}

		if (null == params || params.length == 0) {
			return message;
		}

		return MessageFormat.format(message, params);
	}

}
