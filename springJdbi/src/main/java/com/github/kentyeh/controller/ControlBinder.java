package com.github.kentyeh.controller;

import com.github.kentyeh.tools.DatePropertyEditor;
import com.github.kentyeh.manager.AbstractDaoManager;
import com.github.kentyeh.manager.MemberManager;
import com.github.kentyeh.tools.LocalDatePropertyEditor;
import com.github.kentyeh.tools.LocalDateTimePropertyEditor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 *
 * @author Kent Yeh
 */
@ControllerAdvice
public class ControlBinder {

    private static final Logger logger = LogManager.getLogger(ControlBinder.class);

    @Autowired
    private MemberManager memberManager;
    private List<AbstractDaoManager<? extends Serializable, ?>> managers;

    @Autowired
    protected void setManagers(List<AbstractDaoManager<? extends Serializable, ?>> managers) {
        this.managers = managers;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new DatePropertyEditor());
        binder.registerCustomEditor(LocalDate.class, new LocalDatePropertyEditor());
        binder.registerCustomEditor(LocalDateTime.class, new LocalDateTimePropertyEditor());
        for (AbstractDaoManager<?, ?> manager : managers) {
            Class<?> clazz = ClassUtils.getUserClass(manager);
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals("findByPrimaryKey") && Object.class != method.getReturnType()) {
                    logger.trace("Regist {} with {}", method.getReturnType(), manager);
                    binder.registerCustomEditor(method.getReturnType(), manager);
                    break;
                }
            }
        }
    }
}
