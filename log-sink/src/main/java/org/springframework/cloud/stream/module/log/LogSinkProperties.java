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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties of the log sink module.
 *
 * @author Mark Fisher
 */
@ConfigurationProperties
public class LogSinkProperties {

	static enum Level { ERROR, WARN, INFO, DEBUG, TRACE }

	/**
	 * Name of the logger.
	 */
	private String name;

	/**
	 * Optional SpEL expression for generating the log output from a Message.
	 * If not provided, the output will be the Message payload.
	 */
	private String expression;

	/**
	 * Log level to use. Default is <code>info</code>.
	 */
	private Level level = Level.INFO;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getExpression() {
		return this.expression;
	}

	public void setLevel(String level) {
		this.level = Level.valueOf(level.toUpperCase());
	}

	public Level getLevel() {
		return this.level;
	}
}
