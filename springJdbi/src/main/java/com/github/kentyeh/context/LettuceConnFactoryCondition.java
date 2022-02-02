package com.github.kentyeh.context;

import java.util.Map;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.context.annotation.Bean;

/**
 *
 * @author Kent Yeh
 */
public class LettuceConnFactoryCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String profile = context.getEnvironment().getProperty("spring.profiles.active", "prod");
        Map<String, Object> attributes = metadata.getAnnotationAttributes(Bean.class.getName());
        String beanName = attributes == null ? "" : ((String[]) attributes.get("value"))[0];
        return beanName.startsWith(profile);
    }
}
