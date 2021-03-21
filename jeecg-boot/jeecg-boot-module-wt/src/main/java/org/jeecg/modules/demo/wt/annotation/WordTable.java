package org.jeecg.modules.demo.wt.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WordTable {
    // 表头
    String value();
    // 表头顺序
    int solt() default 1;
    //  表头是否加粗加黑
    boolean b_Bold() default false;
    // 表格列颜色
    String color() default "black";
}
