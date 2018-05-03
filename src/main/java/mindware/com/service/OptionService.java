package mindware.com.service;

import mindware.com.mappers.OptionMapper;
import mindware.com.model.Option;
import mindware.com.utilities.MyBatisSqlSessionFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class OptionService {
    public List<Option> findAllOptions(){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            OptionMapper optionMapper = sqlSession.getMapper(OptionMapper.class);
            return optionMapper.findAllOptions();
        }finally {
            sqlSession.close();
        }
    }
}
