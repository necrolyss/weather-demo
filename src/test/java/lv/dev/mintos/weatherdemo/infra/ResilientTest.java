package lv.dev.mintos.weatherdemo.infra;

import an.awesome.pipelinr.Command;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeoutException;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;


@ExtendWith(SpringExtension.class)
class ResilientTest {

    private static final int WANTED_NUMBER_OF_INVOCATIONS = 3;
    private static final int MAX_WAIT_DURATION = 100;

    @Mock
    private Command.Middleware.Next<String> next;

    private Resilient resilient;

    @Test
    void retriesCommandInvocation() {
        given(next.invoke()).will(invocation -> { throw new TimeoutException();});
        resilient = new Resilient(WANTED_NUMBER_OF_INVOCATIONS, MAX_WAIT_DURATION);

        resilient.invoke(new PingCommand(), next);

        then(next).should(times(WANTED_NUMBER_OF_INVOCATIONS)).invoke();
    }

    private static class PingCommand implements Command<String> {}

}