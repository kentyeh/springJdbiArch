package com.github.kentyeh.model;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Types;
import java.util.Collection;
import lombok.extern.log4j.Log4j2;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;

/**
 *
 * @author Kent Yeh
 * @see
 * <a href="http://stackoverflow.com/questions/19424573/how-to-do-in-query-in-jdbi#answer-38668779">Stack
 * Overflow</a>
 */
@BindingAnnotation(BindStringList.BindFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface BindStringList {

    String value();

    @Log4j2
    class BindFactory implements BinderFactory {

        @Override
        public Binder build(Annotation t) {
            return new Binder<BindStringList, Collection<String>>() {
                @Override
                public void bind(SQLStatement<?> q, BindStringList bind, Collection<String> arg) {
                    q.bindBySqlType(bind.value(), arg.toArray(), Types.ARRAY);
                }
            };
        }
    }
}
