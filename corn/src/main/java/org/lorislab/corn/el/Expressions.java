package org.lorislab.corn.el;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ELContext;
import javax.el.ELManager;
import javax.el.ExpressionFactory;
import javax.el.StandardELContext;

public class Expressions {

    public final static Pattern PATTERN = Pattern.compile("#\\{(.+?)\\}");

    public final static ExpressionFactory EXPRESSION_FACTORY = ExpressionFactory.newInstance();

    protected StandardELContext context;

    private final ELManager manager;

    public Expressions() {
        manager = new ELManager();
        context = manager.getELContext();
    }

    public void addBean(String name, Object bean) {
        manager.defineBean(name, bean);
    }

    public ExpressionFactory getExpressionFactory() {
        return EXPRESSION_FACTORY;
    }

    public ELContext getContext() {
        return context;
    }

    public <T> T evaluateValueExpression(final String expression, final Class<T> expectedType) {
        final Object result = getExpressionFactory()
                .createValueExpression(context, expression, expectedType).getValue(context);
        return result != null ? expectedType.cast(result) : null;
    }

    public <T> T evaluateValueExpression(final String expression) {
        final Object result = evaluateValueExpression(expression, Object.class);
        return result != null ? Expressions.<T>cast(result) : null;
    }

    public <T> T evaluateMethodExpression(final String expression,
            final Class<T> expectedReturnType,
            final Object[] params,
            final Class<?>[] expectedParamTypes) {
        final Object result = getExpressionFactory()
                .createMethodExpression(context, expression, expectedReturnType,
                        expectedParamTypes).invoke(context, params);
        return result != null ? expectedReturnType.cast(result) : null;
    }

    public <T> T evaluateMethodExpression(final String expression, final Class<T> expectedReturnType) {
        return evaluateMethodExpression(
                expression, expectedReturnType, new Object[0], new Class[0]);
    }

    public <T> T evaluateMethodExpression(final String expression) {
        final Object result = evaluateMethodExpression(expression, Object.class);
        return result != null ? Expressions.<T>cast(result) : null;
    }

    public <T> T evaluateMethodExpression(final String expression, final Object... params) {
        final Object result = evaluateMethodExpression(
                expression, Object.class, params, new Class[params.length]
        );
        return result != null ? Expressions.<T>cast(result) : null;
    }

    public String toExpression(final String name) {
        return "#{" + name + "}";
    }

    public void addVariableValue(final String name, Object value) {
        if (value != null) {
            getContext().getVariableMapper().setVariable(
                    name, getExpressionFactory().createValueExpression(value, value.getClass())
            );
        }
    }

    public <T> void addVariableValue(final String name, final Class<T> type, final T value) {
        getContext().getVariableMapper().setVariable(
                name, getExpressionFactory().createValueExpression(value, type)
        );
    }

    public String evaluateAllValueExpressions(final String s) {
        final StringBuffer sb = new StringBuffer();
        final Matcher matcher = PATTERN.matcher(s);
        while (matcher.find()) {
            final String expression = toExpression(matcher.group(1));
            final Object result = evaluateValueExpression(expression);
            matcher.appendReplacement(sb, result != null ? result.toString() : "");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public static <T> T cast(Object obj) {
        return (T) obj;
    }

    public String evaluateString(String value, String defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return evaluateAllValueExpressions(value);
    }
    
    public Object evaluate(Object value) {
        if (value instanceof String) {
            return evaluateAllValueExpressions((String) value);
        }
        return value;
    }
}
