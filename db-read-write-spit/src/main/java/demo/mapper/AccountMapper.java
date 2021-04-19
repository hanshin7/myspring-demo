package demo.mapper;


import demo.anno.Slave;
import demo.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountMapper {

    @Slave
    Account selectByUserId(@Param("userId") String userId);

    int updateById(@Param("account") Account record);

}
