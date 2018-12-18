package com.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>设置id注解</p>
 *
 * @author xiaodongsun
 * @date 2018/12/17
 */
@Retention(RetentionPolicy.RUNTIME) //运行期间保留注解信息，允许反射获取信息
@Target(ElementType.FIELD)
public @interface ORMId {

}
