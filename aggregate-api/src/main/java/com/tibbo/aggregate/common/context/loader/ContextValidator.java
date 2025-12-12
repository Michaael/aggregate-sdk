package com.tibbo.aggregate.common.context.loader;

/**
 * Interface to validate the context
 *
 * @author Alexander Sidorov
 * @since 01.04.2023
 * @see <a href="https://tibbotech.atlassian.net/browse/AGG-14058">AGG-14058</a>
 */
public interface ContextValidator
{
  boolean validate(String contextPath);
}
