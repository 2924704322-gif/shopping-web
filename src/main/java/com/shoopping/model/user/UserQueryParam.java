package com.shoopping.model.user;

import com.shoopping.model.common.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询参数
 *
 * @author shopping-team
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryParam extends PageParam {

    /** 关键字（模糊匹配 username/nickname/email/phone） */
    private String keyword;

    /** 状态筛选: 0=禁用, 1=启用（null 查全部） */
    private Integer status;
}
