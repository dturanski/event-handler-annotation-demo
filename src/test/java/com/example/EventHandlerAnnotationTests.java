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
package com.example;

import com.example.annotation.EventHandler;
import com.example.config.EventHandlerConfiguration;
import com.example.domain.AccountCreatedEvent;
import com.example.domain.CustomerCreatedEvent;
import com.example.domain.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EventHandlerAnnotationTests {

	@Autowired
	MessageChannel input;

	@Autowired
	MyEventHandler myEventHandler;


	@Test
	public void testConditionalListener() {
		Event event = new CustomerCreatedEvent();
		Message<Event> message = MessageBuilder.withPayload(event).build();
		myEventHandler.customerCreatedHandled = false;
		input.send(message);
		assertThat(myEventHandler.customerCreatedHandled).isTrue();
		assertThat(myEventHandler.accountCreatedHandled).isFalse();

		event = new AccountCreatedEvent();
		message = MessageBuilder.withPayload(event).build();
		input.send(message);
		myEventHandler.customerCreatedHandled = false;
		assertThat(myEventHandler.customerCreatedHandled).isFalse();
		assertThat(myEventHandler.accountCreatedHandled).isTrue();

	}

	@SpringBootApplication
	@Import({ EventHandlerConfiguration.class, MyEventHandler.class })
	static class TestConfig {

	}

	/**
	 * @author David Turanski
	 **/
	@EnableBinding(Sink.class)
	static class MyEventHandler {
		boolean customerCreatedHandled;
		boolean accountCreatedHandled;

		@EventHandler(eventType = CustomerCreatedEvent.EVENT_TYPE)
		public void handleCustomerCreated(Event event) {
			customerCreatedHandled = true;

		}

		@EventHandler(eventType = "accountCreated")
		public void handleAccountCreated(Event event) {
			accountCreatedHandled = true;
		}
	}

}
