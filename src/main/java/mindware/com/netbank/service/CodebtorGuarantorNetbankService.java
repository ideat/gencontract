package mindware.com.netbank.service;

import mindware.com.netbank.mappers.CodebtorGuarantorNetbankMapper;
import mindware.com.netbank.model.CodebtorGuarantorNetbank;
import mindware.com.utilities.MyBatisSqlSessionFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class CodebtorGuarantorNetbankService {
    public List<CodebtorGuarantorNetbank> findCodeptorByNumberLoan(int numberLoan){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("netbank");
        try{
            CodebtorGuarantorNetbankMapper codebtorGuarantorNetbankMapper = sqlSession.getMapper(CodebtorGuarantorNetbankMapper.class);
            return  codebtorGuarantorNetbankMapper.findCodeptorByNumberLoan(numberLoan);
        }finally {
            sqlSession.close();
        }
    }

    public List<CodebtorGuarantorNetbank> findGuarantorByNumberLoan(int numberLoan){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("netbank");
        try{
            CodebtorGuarantorNetbankMapper codebtorGuarantorNetbankMapper = sqlSession.getMapper(CodebtorGuarantorNetbankMapper.class);
            return  codebtorGuarantorNetbankMapper.findGuarantorByNumberLoan(numberLoan);
        }finally {
            sqlSession.close();
        }
    }
}
