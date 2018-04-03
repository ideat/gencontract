package mindware.com.netbank.service;

import mindware.com.netbank.mappers.WarrantyNetBankMapper;
import mindware.com.netbank.model.WarrantyNetbank;
import mindware.com.utilities.MyBatisSqlSessionFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class WarrantyNetBankService {
    public List<WarrantyNetbank> findWarrantyNetbankByCreCod(int prgarnpre){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("netbank");
        try{
            WarrantyNetBankMapper warrantyNetBankMapper = sqlSession.getMapper(WarrantyNetBankMapper.class);
            return  warrantyNetBankMapper.findWarrantyNetbankByCreCod(prgarnpre);
        }finally {
            sqlSession.close();
        }
    }
}
