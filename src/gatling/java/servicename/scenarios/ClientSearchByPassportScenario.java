package servicename.scenarios;

import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.jms.JmsDsl.jms;

/**
 * <h2>Сценарий поиска клиента по паспорту</h2>
 * <p>
 * Шаги сценария:
 * <ul>
 *     <li> Пользователь отправляет запрос на поиск в очередь GATLING.TEST.Q1.RQ</li>
 *     <li> Ждет ответ из очереди GATLING.TEST.Q1.RS</li>
 * </ul>
 *<p>
 * SLA:
 * <ul>
 *     <li> Длительность поиска не более 5 секунд</li>
 *</ul>
 * @author  Roman Kislyy
 * @since 2023-08-11
 */
public class ClientSearchByPassportScenario {
    /**
     * Пул паспортов CSV feeder pools/passports.csv
     **/
    public static FeederBuilder<String> passports = csv("pools/passports.csv").random();

    public static ScenarioBuilder scn = scenario("Async client search (demo requestReply)")
            .feed(passports)
            .exec(jms("Search by passport").requestReply()
                        .queue("GATLING.TEST.Q1.RQ")
                        .replyQueue("GATLING.TEST.Q1.RQ") // Указана одинаковая очередь с запросом только для демо. Потому что нам никто не отвечает
//                        .replyQueue("GATLING.TEST.Q1.RS")
                        .textMessage(ElFileBody("json/ClientSearch/requests/byPassport.json"))
                        .property("test_header", "test_value")
                        .check(jsonPath("$.passport").exists())
                ).exec();

}

