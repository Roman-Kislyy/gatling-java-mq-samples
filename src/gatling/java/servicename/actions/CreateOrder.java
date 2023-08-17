package servicename.actions;

import helpers.VarsHelper;
import io.gatling.javaapi.core.ChainBuilder;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.jms.JmsDsl.jms;

/**
 * <h2>Только для примера. Класс создания заказов.</h2>
 * <p>
 *     Реализует создание разных вариантов клиентских заказов (покупок).
 * <p>
 *     Приводится в качестве примеры структуры организации тестов.
 * <p>
 * @author  Roman Kislyy
 * @since 2023-08-11
 */
public class CreateOrder {
    /**
     * <p>Запрсос на покупку красных носков</p>
     * <p> Тело JSON запроса берем в ресурсах
     * @return объект с типом ChainBuilder
     * @author  Roman Kislyy
     * @since 2023-08-11
     */
    public ChainBuilder buyRedSocks(int count){
        ChainBuilder chain =
              exec(VarsHelper.randomUUID("orderUuid")) // Генерируем UUID, будем использовать, как #{orderUuid}
             .exec(VarsHelper.set("count", count))  // Здесь мы создаем переменную #{count} в session, чтобы потом ее использоваться в шаблоне json запроса
             .exec(
                    jms("Buy red socks").requestReply()
                    .queue("GATLING.ORDERS.Q1.RQ")
                    .replyQueue("GATLING.ORDERS.Q1.RQ") // Указана одинаковая очередь с запросом только для демо. Потому что нам никто не отвечает
                            //.replyQueue("GATLING.ORDERS.Q1.RS")
                    .textMessage(ElFileBody("json/CreateOrder/requests/buyRedSocks.json"))
                    .property("test_header", "test_value")
                    .check(jsonPath("$.success").exists())
            );
        return chain;
    }
    /**
     * <p>Запрсос на покупку черных шляп</p>
     * <p> TODO
     * @return объект с типом ChainBuilder
     * @author  Roman Kislyy
     * @since 2023-08-11
     */
    public ChainBuilder buyBlackHat(int count){
        ChainBuilder chain = exec(/*You code here*/);
        return chain;
    }
}
