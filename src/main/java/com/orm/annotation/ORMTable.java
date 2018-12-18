package com.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>设置表名</p>
 *
 * @author xiaodongsun
 * @date 2018/12/17
 */
@Retention(RetentionPolicy.RUNTIME) //运行期间保留注解信息
@Target(ElementType.TYPE) //注解作用在类上面
public @interface ORMTable {

    String name() default "";
}
