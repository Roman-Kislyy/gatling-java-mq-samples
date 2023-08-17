package servicename.configurations.jms;

import com.ibm.mq.jms.MQConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import helpers.ResourceHelper;
import helpers.ssl.JksHelper;
import io.gatling.javaapi.jms.JmsProtocolBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static io.gatling.javaapi.jms.JmsDsl.jms;
import static ru.tinkoff.gatling.javaapi.SimulationConfig.getIntParam;
import static ru.tinkoff.gatling.javaapi.SimulationConfig.getStringParam;

/**
 * <h2>Класс настроек подключения к MQ с использованием SSL сертификатов</h2>
 * <p>
 *     Параметры подключения к брокеру берутся из файла настроек <b>resources/simulation.conf</b>
 * <p>
 *     Сертификаты находятся в truststore в <b>resources/keys/mq</b>
 * <p>
 *     Если вам требуется несколько вариантов подключений (с разными логинами или брокерами), то создайте копии класса c другими именами и используйте их.
 * @author  Roman Kislyy
 * @since 2023-08-11
 */
public class MqSslClientConfiguration {
    private final Logger log = LoggerFactory.getLogger(MqSslClientConfiguration.class);
    private String host = getStringParam("mq.host");
    private Integer port = getIntParam("mq.port");
    private String channel = getStringParam("mq.channel");
    private String queueManager = getStringParam("mq.queueManager");
    private String login = getStringParam("mq.login");
    private String pass = getStringParam("mq.pass");
    private String appName = getStringParam("mq.appName");
    private String chiperSpec = getStringParam("mq.chiperSpec");
    private String keystore = getStringParam("mq.keystore");
    private String keystorePass = getStringParam("mq.keystorePass");
    private String truststore = getStringParam("mq.truststore");
    private String truststorePass = getStringParam("mq.truststorePass");

    private MQConnectionFactory cf = new MQConnectionFactory();
    private MQConnectionFactory getConnectionFactory() {
        try {

            cf.setTransportType(WMQConstants.WMQ_CM_CLIENT);
            cf.setHostName(host);
            cf.setPort(port);
            cf.setChannel(channel);
            cf.setQueueManager(queueManager);
            cf.setAppName(appName);
            // If is true that multiple MQ client connections can use a single TCP/IP network connection
            cf.setShareConvAllowed(1);
            cf.setIntProperty("XMSC_WMQ_SHARE_CONV_ALLOWED", 1);

            if (!chiperSpec.equals("")) {
                log.info("Connecting with SSL keys. ChiperSpec = {}", chiperSpec);
                keystore = ResourceHelper.gatlingResourcePath(keystore);
                truststore = ResourceHelper.gatlingResourcePath(truststore);
                JksHelper.isValid(keystore, keystorePass);
                JksHelper.isValid(truststore, truststorePass);
                System.setProperty("com.ibm.mq.cfg.preferTLS", "true");
                System.setProperty("com.ibm.mq.cfg.useIBMCipherMappings", "false");
                System.setProperty("javax.net.ssl.keyStore", keystore);
                System.setProperty("javax.net.ssl.keyStorePassword", keystorePass);
                System.setProperty("javax.net.ssl.trustStore", truststore);
                System.setProperty("javax.net.ssl.trustStorePassword", truststorePass);
                cf.setStringProperty(WMQConstants.WMQ_SSL_CIPHER_SPEC, chiperSpec);
                cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cf;
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
                // Если не выставить таймаут, скорее всего, ваши потоки генератора будут постоянно расти и займут всю доступную память и соединения на MQ
                // А также зависнет тест, возможно навсегда. Потребуется принудительное завершение.
                .replyTimeout(10);
    }
}
