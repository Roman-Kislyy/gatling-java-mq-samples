package servicename.execs.jms;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import io.gatling.javaapi.jms.JmsProtocolBuilder;
import javax.jms.JMSException;
import static io.gatling.javaapi.jms.JmsDsl.jms;
import static ru.tinkoff.gatling.javaapi.SimulationConfig.getIntParam;
import static ru.tinkoff.gatling.javaapi.SimulationConfig.getStringParam;

/**
 * <h2>Класс настроек подключения к MQ</h2>
 * <p>
 *     Параметры подключения к брокеру берутся из файла настроек <b>resources/simulation.conf</b>
 * <p>
 *     Если вам требуется несколько вариантов подключений (с разными логинами или брокерами), то создайте копии класса c другими именами и используйте их.
 * <p>
 *     Например, FromSiteMqClientConfiguration и FromMobileMqClientConfiguration
 * @author  Roman Kislyy
 * @since 2023-08-11
 */
public class MqClientConfiguration {
    private String host = getStringParam("mq.host");
    private Integer port = getIntParam("mq.port");
    private String channel = getStringParam("mq.channel");
    private String queueManager = getStringParam("mq.queueManager");
    private String login = getStringParam("mq.login");
    private String pass = getStringParam("mq.pass");
    private String keystore = getStringParam("mq.keystore");
    private String truststore = getStringParam("mq.truststore");
    private String chiperSpec = getStringParam("mq.chiperSpec");
    private String appName = getStringParam("mq.appName");

    // create a ConnectionFactory for ActiveMQ
    // search the documentation of your JMS broker
    private MQConnectionFactory connectionFactory = new MQConnectionFactory();
    private MQConnectionFactory getConnectionFactory() {
        try {
            connectionFactory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
            connectionFactory.setHostName(host);
            connectionFactory.setPort(port);
            connectionFactory.setChannel(channel);
            connectionFactory.setQueueManager(queueManager);
            connectionFactory.setAppName(appName);
            // Зачем setShareConvAllowed
            connectionFactory.setShareConvAllowed(1);
            connectionFactory.setIntProperty("XMSC_WMQ_SHARE_CONV_ALLOWED", 1);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
        return connectionFactory;
    }

    /**
     * <p>Настройка протокола JMS для использвоания в симуляции.</p>
     * @return объект с типом JmsProtocolBuilder
     * @author  Roman Kislyy
     * @since 2023-08-11
     */
    public JmsProtocolBuilder jmsProtocol(){
        return jms
                .connectionFactory(this.getConnectionFactory())
                .credentials(login, pass)
                // optional, default to non persistent
                .useNonPersistentDeliveryMode()
                // optional, default to 1
                // listener thread count
                // some JMS implementation (like IBM MQ) need more than one MessageListener
                // to achieve full readout performance
                .listenerThreadCount(7)
                // optional, default to `matchByMessageId`
                // specify how request and response messages should be matched when using `requestReply`
                // Use `matchByCorrelationId` for ActiveMQ.

                .matchByCorrelationId()
//                .matchByMessageId()
                // use a custom matching strategy
//                .messageMatcher((io.gatling.javaapi.jms.JmsMessageMatcher) null)

                // In seconds, optional, default to none
                // Если не выставить таймаут, скорее всего, ваши потоки генератра будут постоянно расти и займут доступную память и соединения на MQ
                // А также зависнет тест, возможно навсегда. Потребуется принудительное завершение.
                .replyTimeout(30);
    }
}
