package mindware.com.service;

import mindware.com.mappers.MenuOptionMapper;
import mindware.com.model.MenuOption;
import mindware.com.utilities.MyBatisSqlSessionFactory;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

public class MenuOptionService {
    public void insertMenuOption(MenuOption menuOption){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            MenuOptionMapper menuOptionMapper = sqlSession.getMapper(MenuOptionMapper.class);
            menuOptionMapper.insertMenuOption(menuOption);
            sqlSession.commit();
        }finally {
            sqlSession.close();
        }

    }


    public void deleteOptionMenu(int rolId) {
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            MenuOptionMapper menuOptionMapper = sqlSession.getMapper(MenuOptionMapper.class);
            menuOptionMapper.deleteMenuOption(rolId);
            sqlSession.commit();
        }finally {
            sqlSession.close();
        }
    }

    public List<MenuOption> findMenuOptionByRolId(int rolId){
        SqlSession sqlSession = MyBatisSqlSessionFactory.getSqlSession("development");
        try{
            MenuOptionMapper menuOptionMapper = sqlSession.getMapper(MenuOptionMapper.class);
            return menuOptionMapper.findMenuOptionByRolId(rolId);

        }finally {
            sqlSession.close();
        }
    }
}
