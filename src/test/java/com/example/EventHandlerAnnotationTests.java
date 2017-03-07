package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { EventHandlerAnnotationTests.TestConfig.class })
public class EventHandlerAnnotationTests {

	@Autowired
	@Qualifier("input")
	MessageChannel input;

	@Autowired
	MyEventHandler myEventHandler;
	

	@Test
	public void testConditionalListener() {
		Event event = new Event();
		event.eventType = "PolicyCreated";
		final Message<Event> message = MessageBuilder.withPayload(event).build();
		myEventHandler.handled = false;
		input.send(message);
		assertThat(myEventHandler.handled).isTrue();
	}

	@SpringBootApplication
	@Import({MyEventHandler.class})
	static class TestConfig {

	}
	
	@EnableBinding(Sink.class)
	static class MyEventHandler {
		public boolean handled;
		//@StreamListener(target = Sink.INPUT, condition = "payload.eventType=='PolicyCreated'")
		@EventHandler(target = Sink.INPUT, condition = "payload.eventType=='PolicyCreated'")
		public void handlePolicyCreatedEvent() {
			handled = true;
		}
	}
	
	static class Event {
		public String eventType;
	}

}
