package com.momo.warehouse.sys.common;

import com.momo.warehouse.sys.bean.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author momo
 * @create 2019-12-04 下午 22:15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
public class ActiverUser {

    private User user;

    private List<String> roles;

    private List<String> permissions;

}
