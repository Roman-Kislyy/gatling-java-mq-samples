package helpers.ssl;

import helpers.ResourceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * <h2>Проверки jks сертификатов</h2>
 * <p>
 *     Функционал проверки валидности указанных сертификатов и доступов к ним
 * @author  Roman Kislyy
 * @since 2023-08-11
 */
public class JksHelper {
    private static Logger log = LoggerFactory.getLogger(JksHelper.class);
    /**
     * <p> Проверяем существует ли указанный файл и корректность указанного пароля
     * @param path Путь в папке gatling/resources или абсолютный путь.
     * @param pass Пароль к сертификату
     * @author  Roman Kislyy
     * @since 2023-08-11
     */
    public static  boolean isValid (String path, String pass) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {
        String resourcePath = ResourceHelper.gatlingResourcePath(path);
        File jks = new File(resourcePath);
        if (jks.exists()){
            log.info("JKS file {} exists.", resourcePath);
        } else {
            log.error("JKS file {} not found.", resourcePath);
            throw new FileNotFoundException();
        }
        FileInputStream is = new FileInputStream(resourcePath);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(is, pass.toCharArray());
        log.info("Password valid. Keystore {} size {}.", resourcePath, ks.size());
        return true;
    }
}
