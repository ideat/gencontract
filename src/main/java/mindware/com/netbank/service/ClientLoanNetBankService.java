package mindware.com.netbank.service;

import mindware.com.netbank.mappers.ClientLoanNetbankMapper;
import mindware.com.netbank.model.ClientLoanNetbank;
import mindware.com.utilities.MyBatisSqlSessionFactory;
import org.apache.ibatis.session.SqlSession;

public class ClientLoanNetBankService {

    public ClientLoanNetbank findClientNetbankById(int prmprnpre){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("netbank");
        try {
            ClientLoanNetbankMapper clientLoanNetbankMapper = sqlSession.getMapper(ClientLoanNetbankMapper.class);

            return clientLoanNetbankMapper.findClientLoanNetbankByCreCod(prmprnpre);

        }finally {

            sqlSession.close();
        }
    }
}
