package mindware.com.utilities;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;


/**
 * Created by freddy on 10-03-17.
 */
public class MyBatisSqlSessionFactory {

    private static SqlSessionFactory sqlSessionFactory;
    private static SqlSessionFactory sqlSessionFactoryInformix;


    public static SqlSessionFactory getSqlSessionFactory(String environment)
    {

        if(sqlSessionFactory==null)
        {
            InputStream inputStream = null;
            try
            {

                InputStream fileInputStream =  Resources.getResourceAsStream("application.properties");
                Properties properties = new Properties();
                properties.load(fileInputStream);
                fileInputStream.close();

                Properties jdbcProp = new Properties();

                Path path = Paths.get(System.getProperties().get("user.home").toString());

                String file_private = path.toString()+ "/rsa_postgres.pri";
                String file_public = path.toString()+ "/rsa_postgres.pub";

                RSA rsa2 = new RSA();

                rsa2.openFromDiskPrivateKey(file_private);
                rsa2.openFromDiskPublicKey(file_public);
                String unsecure = rsa2.Decrypt(properties.getProperty("password2"));

                jdbcProp = properties;
                jdbcProp.setProperty("password", unsecure);

                inputStream = Resources.getResourceAsStream("/mybatis-config.xml");
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream,environment,jdbcProp);
            }catch (IOException e)
            {
                throw new RuntimeException(e.getCause());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return sqlSessionFactory;
    }

    public static SqlSessionFactory getSqlSessionFactoryInformix(String environment)
    {
        if(sqlSessionFactoryInformix==null)
        {
            InputStream inputStream = null;
            try
            {
                InputStream fileInputStream =  Resources.getResourceAsStream("application.properties");
                Properties properties = new Properties();
                properties.load(fileInputStream);
                fileInputStream.close();

                Properties jdbcProp = new Properties();

                Path path = Paths.get(System.getProperties().get("user.home").toString());

                String file_private = path.toString()+ "/rsa_informix.pri";
                String file_public = path.toString()+ "/rsa_informix.pub";

                RSA rsa2 = new RSA();

                //A diferencia de la anterior aca no creamos
                //un nuevo par de claves, sino que cargamos
                //el juego de claves que habiamos guadado
                rsa2.openFromDiskPrivateKey(file_private);
                rsa2.openFromDiskPublicKey(file_public);
                String unsecure = rsa2.Decrypt(properties.getProperty("password"));

                jdbcProp = properties;
                jdbcProp.setProperty("password", unsecure);

                inputStream = Resources.getResourceAsStream("/mybatis-config.xml");
                sqlSessionFactoryInformix = new SqlSessionFactoryBuilder().build(inputStream,environment,jdbcProp);
            }catch (IOException e)
            {
                throw new RuntimeException(e.getCause());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } finally {
                if(inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return sqlSessionFactoryInformix;
    }

    public static SqlSession getSqlSession(String environment)
    {
        if (environment=="development") 
            return getSqlSessionFactory(environment).openSession();
        else if (environment == "netbank")
            return getSqlSessionFactoryInformix(environment).openSession(true);
        else
        	return getSqlSessionFactory("development").openSession();

    }

}
