package servicename.scenarios;

import io.gatling.javaapi.core.FeederBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import servicename.actions.CreateOrder;

import java.util.Random;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.jms.JmsDsl.jms;

/**
 * <h2>Сценарий покупки носков в воображаемом магазине товаров.</h2>
 * <p>
 *     В данном примере демонстрируется, как может выгдялеть сценарий большой сложный, если его шаги вынесены в actions
 * Шаги сценария:
 * <ul>
 *     <li> Авторизация</li>
 *     <li> Поиск</li>
 *     <li> Добавление в корзину</li>
 *     <li> Создание заказа</li>
 * </ul>
 *<p>
 * SLA:
 * <ul>
 *     <li> Длительность работы сценария 60 секунд</li>
 *</ul>
 * @author  Roman Kislyy
 * @since 2023-08-11
 */
public class BuySocksScenario {

    public static ScenarioBuilder scn = scenario("Buy socks scenario")
// TODO
//            .exec(new Login().byPassword())
//            .exec(new SearchProduct().socks())
//            .exec(new Basket().addProduct())
            .exec(new CreateOrder().buyRedSocks(7));

}

