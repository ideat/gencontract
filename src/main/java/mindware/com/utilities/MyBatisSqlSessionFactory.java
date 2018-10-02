package mindware.com.utilities;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;


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
                inputStream = Resources.getResourceAsStream("/mybatis-config.xml");
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream,environment);
            }catch (IOException e)
            {
                throw new RuntimeException(e.getCause());
            }finally {
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
                inputStream = Resources.getResourceAsStream("/mybatis-config.xml");
                sqlSessionFactoryInformix = new SqlSessionFactoryBuilder().build(inputStream,environment);
            }catch (IOException e)
            {
                throw new RuntimeException(e.getCause());
            }finally {
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
