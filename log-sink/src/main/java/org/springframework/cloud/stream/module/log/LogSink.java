/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.module.log;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableModule;
import org.springframework.cloud.stream.annotation.Sink;
import org.springframework.cloud.stream.module.log.LogSinkProperties.Level;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.messaging.Message;
import org.springframework.util.StringUtils;

/**
 * @author Dave Syer
 * @author Mark Fisher
 */
@EnableModule(Sink.class)
@EnableConfigurationProperties(LogSinkProperties.class)
public class LogSink {

	private static SpelExpressionParser PARSER = new SpelExpressionParser();

	private Logger logger;

	private Expression expression;

	private EvaluationContext evaluationContext;

	@Autowired
	private LogSinkProperties properties;

	@Autowired
	private BeanFactory beanFactory;

	@PostConstruct
	public void configureLogger() {
		String loggerName = properties.getName();
		logger = StringUtils.hasText(loggerName) ? LoggerFactory.getLogger(loggerName)
				: LoggerFactory.getLogger(LogSink.class);
		String expressionString = properties.getExpression();
		if (StringUtils.hasText(expressionString)) {
			expression = PARSER.parseExpression(expressionString);
		}
		evaluationContext = IntegrationContextUtils.getEvaluationContext(beanFactory);
	}

	@ServiceActivator(inputChannel=Sink.INPUT)
	public void log(Message<?> message) {
		Level level = properties.getLevel();
		String logMessage = (expression != null) ? expression.getValue(evaluationContext, message, String.class)
				: String.format("%s", message.getPayload());
		switch (level) {
		case ERROR:
			if (logger.isErrorEnabled()) {
				logger.error(logMessage);
			}
			break;
		case WARN:
			if (logger.isWarnEnabled()) {
				logger.warn(logMessage);
			}
			break;
		case INFO:
			if (logger.isInfoEnabled()) {
				logger.info(logMessage);
			}
			break;
		case DEBUG:
			if (logger.isDebugEnabled()) {
				logger.debug(logMessage);
			}
			break;
		case TRACE:
			if (logger.isTraceEnabled()) {
				logger.trace(logMessage);
			}
			break;
		default:
			throw new IllegalStateException("Level '" + level + "' is not supported");
		}
	}
}
