package demo.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * create by hanshin on 2021/4/20
 */
//注意：实体类要可序列化
@Getter
@Setter
public class User implements Serializable {
    private Integer id;
    private String username;
    private String password;
    private String tel;
    private String address;
}
