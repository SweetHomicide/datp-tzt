package com.ditp.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class LanguagesSupported {

	private LanguagesSupported() {

	}

	private static final InheritableThreadLocal<String> CURRENT_LOCALE_TAG = new InheritableThreadLocal<>();

	private static final Locale DEFAULT_LOCALE = Locale.CHINA;

	private static final Locale[] SUPPORTED_LOCALES = { DEFAULT_LOCALE, Locale.US };

	private static final Map<String, Locale> LOCALE_MAP = new HashMap<>();
	static {
		for (int i = 0; i < SUPPORTED_LOCALES.length; ++i) {
			LOCALE_MAP.put(getLocaleTag(SUPPORTED_LOCALES[i]), SUPPORTED_LOCALES[i]);
		}
	}

	public static final void setCurrentLocale(Locale locale) {
		CURRENT_LOCALE_TAG.set(getLocaleTag(locale));
	}

	public static final String getCurrentLocaleTag() {
		String localeTag = CURRENT_LOCALE_TAG.get();
		if (null == localeTag || localeTag.trim().length() == 0) {
			localeTag = getLocaleTag(DEFAULT_LOCALE);
		}
		return localeTag;
	}

	public static final Locale getCurrentLocale() {
		return getLocale(CURRENT_LOCALE_TAG.get());
	}

	public static final String getLocaleTag(Locale locale) {
		return String.valueOf(locale);
	}

	public static final Locale getDefaultLocale() {
		return DEFAULT_LOCALE;
	}

	public static final boolean isLanguageSupported(Locale locale) {
		return LOCALE_MAP.containsKey(getLocaleTag(locale));
	}

	public static final Locale getLocale(String localeTag) {
		return LOCALE_MAP.getOrDefault(localeTag, DEFAULT_LOCALE);
	}

	public static final Locale[] getSupportedLanguages() {
		return SUPPORTED_LOCALES;
	}
}
