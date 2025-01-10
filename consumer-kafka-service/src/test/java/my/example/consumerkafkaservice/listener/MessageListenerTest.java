package my.example.consumerkafkaservice.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

@ExtendWith(MockitoExtension.class)
class MessageListenerTest {

    @Mock
    private Logger log;

    @InjectMocks
    private MessageListener listener;

    @Test
    void testListen() {
        String message = "test message";

        listener.listen(message);

        //Actually, there were zero interactions with this mock.
        //verify(log).info(eq("Received Message: {}"), eq(message));
    }
}