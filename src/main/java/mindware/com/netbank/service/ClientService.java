package mindware.com.netbank.service;

import mindware.com.netbank.mappers.ClientMapper;
import mindware.com.netbank.model.Client;
import mindware.com.utilities.MyBatisSqlSessionFactory;
import org.apache.ibatis.session.SqlSession;

public class ClientService {

    public Client findClientNetbankById(int clientId){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("netbank");
        try {
            ClientMapper clientMapper = sqlSession.getMapper(ClientMapper.class);

            return clientMapper.findClientNetbankById(clientId);

        }finally {

            sqlSession.close();
        }
    }
}
