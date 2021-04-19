package demo.service;

import demo.entity.Account;
import demo.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor(onConstructor_={@Autowired})
public class AccountService {

    private final AccountMapper accountMapper;

    Account selectByUserId(String userId){
        return accountMapper.selectByUserId(userId);
    }

    int updateById(Account record){
        return accountMapper.updateById(record);
    }

}
