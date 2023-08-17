package servicename.simulations;

import io.gatling.javaapi.core.Simulation;
import servicename.configurations.jms.MqClientConfiguration;
import servicename.scenarios.ClientSearchByPassportScenario;

import static io.gatling.javaapi.core.CoreDsl.*;

/**
 * <h2>Тест стабильности для демонстрации работы метода requestReply без SSL для IBM MQ</h2>
 * <p>
 * Тут можно рассказать, как собирать профиль нагрузки. В чем особенность теста.
 * @author  Roman Kislyy
 * @since 2023-08-11
 */
public class RequestReplyNoSslSimulation extends Simulation {
    {
        setUp(
                ClientSearchByPassportScenario.scn.injectOpen(
                        rampUsersPerSec(0).to(50).during(10),
                        constantUsersPerSec(50).during(60))
        ).protocols(new MqClientConfiguration()
                        .jmsProtocol()
        ).assertions(forAll().responseTime().percentile(90).lte(5000));
    }
}
