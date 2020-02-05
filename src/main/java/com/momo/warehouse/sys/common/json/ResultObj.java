package com.momo.warehouse.sys.common.json;

import com.momo.warehouse.sys.common.Constast;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author momo
 * @create 2019-12-06 下午 16:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultObj {

    public static final ResultObj LOGIN_SUCCESS = new ResultObj(Constast.Ok,"登录成功");
    public static final ResultObj LOGIN_ERROR_PASS = new ResultObj(Constast.ERROR,"登录失败，用户名或密码不正确");
    public static final ResultObj LOGIN_ERROR_CODE = new ResultObj(Constast.ERROR,"验证码不正确");

    public static final ResultObj DELETE_SUCCESS = new ResultObj(Constast.Ok,"删除成功");
    public static final ResultObj DELETE_ERROR = new ResultObj(Constast.ERROR,"删除失败");

    public static final ResultObj UPDATE_SUCCESS = new ResultObj(Constast.Ok,"更新成功");
    public static final ResultObj UPDATE_ERROR = new ResultObj(Constast.ERROR,"更新失败");

    public static final ResultObj ADD_SUCCESS = new ResultObj(Constast.Ok,"添加成功");
    public static final ResultObj ADD_ERROR = new ResultObj(Constast.ERROR,"添加失败");

    public static final ResultObj RESET_SUCCESS = new ResultObj(Constast.Ok,"重置成功");
    public static final ResultObj RESET_ERROR = new ResultObj(Constast.ERROR,"重置失败");

    public static final ResultObj DISPATCH_SUCCESS = new ResultObj(Constast.Ok,"分配成功");
    public static final ResultObj DISPATCH_ERROR = new ResultObj(Constast.ERROR,"分配失败");

    private Integer code;
    private String msg;
}
