package com.nuctech.platform.xss;

import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by @author wangzunhui on 2017/8/15.
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
    public static final Map<CharSequence, CharSequence> BASIC_ESCAPE;
    static {
        final Map<CharSequence, CharSequence> initialMap = new HashMap<>();
        initialMap.put("&", "&amp;");   // & - ampersand
        initialMap.put("<", "&lt;");    // < - less-than
        initialMap.put(">", "&gt;");    // > - greater-than
        BASIC_ESCAPE = Collections.unmodifiableMap(initialMap);
    }

    public static final CharSequenceTranslator ESCAPE_HTML4 =
            new AggregateTranslator(
                    new LookupTranslator(XssHttpServletRequestWrapper.BASIC_ESCAPE),
                    new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE),
                    new LookupTranslator(EntityArrays.HTML40_EXTENDED_ESCAPE)
            );

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getHeader(String name) {
        return XssHttpServletRequestWrapper.escapeHtml4(super.getHeader(name));
    }

    @Override
    public String getQueryString() {
        return XssHttpServletRequestWrapper.escapeHtml4(super.getQueryString());
    }

    @Override
    public String getParameter(String name) {
        return XssHttpServletRequestWrapper.escapeHtml4(super.getParameter(name));
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return new String[0];
        }

        int length = values.length;
        String[] escapeValues = new String[length];
        for (int i = 0; i < length; i++) {
            escapeValues[i] = XssHttpServletRequestWrapper.escapeHtml4(values[i]);
        }

        return escapeValues;
    }

    public static final String escapeHtml4(String input) {
        return ESCAPE_HTML4.translate(input);
    }
}
