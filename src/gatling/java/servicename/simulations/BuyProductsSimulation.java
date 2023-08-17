package servicename.simulations;

import io.gatling.javaapi.core.Simulation;
import servicename.configurations.jms.MqSslClientConfiguration;
import servicename.scenarios.BuySocksScenario;

import static io.gatling.javaapi.core.CoreDsl.*;

/**
 * <h2>Тест стабильности, демонстрация симуляции с несколькими сценариями</h2>
 * <p>
 * Тут можно рассказать, как собирать профиль нагрузки. В чем особенность теста.
 * <p>
 *     Наша симуляция тестирует покупку носков и шляп.
 *<p>
 *     Обычно носки требуются чаще, чем шляпы. Статистика с продуктива получена такая:
 *  <ul>
 *      <li> BuySocksScenario - 50 rps</li>
 *      <li> BuyHatsScenario - 5 rps</li>
 *  </ul>
 *
 * @author  Roman Kislyy
 * @since 2023-08-11
 */
public class BuyProductsSimulation extends Simulation {
    {
        setUp(
                BuySocksScenario.scn.injectOpen(
                                                rampUsersPerSec(0).to(50).during(10),
                                                constantUsersPerSec(50).during(60))//,
// TODO
//                BuyHatsScenario.scn.injectOpen(
//                        rampUsersPerSec(0).to(5).during(10),
//                        constantUsersPerSec(5).during(60))
        ).protocols(new MqSslClientConfiguration()
                        .jmsProtocol()
        ).assertions(forAll().responseTime().percentile(90).lte(5000));
    }
}
