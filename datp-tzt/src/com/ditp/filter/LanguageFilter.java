package com.ditp.filter;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.WebUtils;

import com.ditp.lang.LanguagesSupported;

public class LanguageFilter implements Filter {

	private static final String LANG_PARAM_NAME = "lang";

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String paramLang = request.getParameter(LANG_PARAM_NAME);
		if (null != paramLang && paramLang.trim().length() > 0) {
			try {
				WebUtils.setSessionAttribute((HttpServletRequest) request,
						SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, LanguagesSupported.getLocale(paramLang));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Locale locale = (Locale) WebUtils.getSessionAttribute((HttpServletRequest) request,
				SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		if (null == locale) {
			locale = LanguagesSupported.getDefaultLocale();
			WebUtils.setSessionAttribute((HttpServletRequest) request,
					SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locale);
		}
		
		LanguagesSupported.setCurrentLocale(locale);
		request.setAttribute("locale", LanguagesSupported.getLocaleTag(locale));
		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {

	}

}
