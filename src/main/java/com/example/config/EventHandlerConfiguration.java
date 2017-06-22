/*
 * Copyright 2016 the original author or authors.
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
package com.example.config;

/**
 * @author David Turanski
 **/

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.binding.StreamListenerAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.cloud.stream.config.BindingServiceConfiguration.STREAM_LISTENER_ANNOTATION_BEAN_POST_PROCESSOR_NAME;

@Configuration
public class EventHandlerConfiguration {

    /*
	 * The SpEL expression used to allow the Spring Cloud Stream Binder to dispatch to methods
     * Annotated with @EventHandler
     */

	private static String eventHandlerSpelPattern = "payload.eventType=='%s'";

	/**
	 * Override the default {@link StreamListenerAnnotationBeanPostProcessor} to inject value of
	 * 'eventType' attribute into 'condition' expression.
	 *
	 * @return
	 */
	@Bean(name = STREAM_LISTENER_ANNOTATION_BEAN_POST_PROCESSOR_NAME)
	public static BeanPostProcessor streamListenerAnnotationBeanPostProcessor() {
		return new StreamListenerAnnotationBeanPostProcessor() {
			@Override
			protected StreamListener postProcessAnnotation(StreamListener originalAnnotation, Method annotatedMethod) {
				Map<String, Object> attributes = new HashMap<>(
						AnnotationUtils.getAnnotationAttributes(originalAnnotation));
				if (StringUtils.hasText(originalAnnotation.condition())) {
					String spelExpression = String.format(eventHandlerSpelPattern, originalAnnotation.condition());
					attributes.put("condition", spelExpression);
				}
				return AnnotationUtils.synthesizeAnnotation(attributes, StreamListener.class, annotatedMethod);
			}
		};
	}

}

