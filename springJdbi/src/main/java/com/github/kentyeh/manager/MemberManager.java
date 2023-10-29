package com.github.kentyeh.manager;

import com.github.kentyeh.context.ValidationUtils;
import com.github.kentyeh.model.Authority;
import com.github.kentyeh.model.Dao;
import com.github.kentyeh.model.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdbi.v3.core.JdbiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Kent Yeh
 */
@Repository("memberManager")
public class MemberManager extends AbstractDaoManager<String, Member> {

    private static final Logger logger = LogManager.getLogger(MemberManager.class);
    private MessageSourceAccessor messageAccessor;
    private PasswordEncoder encoder;
    private ValidationUtils vu;

    @Autowired
    @Qualifier("messageAccessor")
    public void setMessageAccessor(MessageSourceAccessor messageAccessor) {
        this.messageAccessor = messageAccessor;
    }

    @Autowired
    public void setEncoder(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Autowired
    public void setVu(ValidationUtils vu) {
        this.vu = vu;
    }

    @Override
    public String text2Key(String text) {
        return text;
    }

    private Member postConstruct(Dao dao, Member member) {
        if (member != null) {
            member.setAuthorities(dao.findAuthorityByAccount(member.getAccount()));
        }
        return member;
    }

    private List<Member> postConstruct(Dao dao, List<Member> members) {
        if (members != null && !members.isEmpty()) {
            members.forEach(member -> member.setAuthorities(dao.findAuthorityByAccount(member.getAccount())));
        }
        return members;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = JdbiException.class)
    public Member findByPrimaryKey(String account) {
        try (Dao dao = getContext().getBean(Dao.class)) {
            Member member = dao.findMemberByPrimaryKey(account);
            return postConstruct(dao, member);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = JdbiException.class)
    public List<Member> findAvailableUsers() throws Exception {
        try (Dao dao = getContext().getBean(Dao.class)) {
            return postConstruct(dao, getContext().getBean(Dao.class).findAvailableUsers());
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = JdbiException.class)
    public List<Member> findAllUsers() throws Exception {
        try (Dao dao = getContext().getBean(Dao.class)) {
            return postConstruct(dao, dao.findAllUsers());
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS, rollbackFor = JdbiException.class)
    public List<Member> findAdminUser() throws Exception {
        try (Dao dao = getContext().getBean(Dao.class)) {
            return postConstruct(dao, dao.findUsersByAuthoritues(Arrays.asList(new String[]{"ROLE_ADMIN", "ROLE_USER"})));
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void newMember(Member member) throws Exception {
        try (Dao dao = getContext().getBean(Dao.class)) {
            Member original = dao.findMemberByPrimaryKey(member.getAccount());
            if (original != null) {
                throw jdbiException(messageAccessor.getMessage("exception.userAlreadyExists", member.getAccount()));
            }
            vu.validateMessage(member, RuntimeException.class);
            dao.newMember(member);
            List<Authority> authories = member.getAuthorities();
            if (authories != null) {
                for (Authority authority : authories) {
                    vu.validateMessage(authority, RuntimeException.class);
                    dao.newAuthority(authority);
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = JdbiException.class)
    public boolean updateMember(Member member) throws Exception {
        try (Dao dao = getContext().getBean(Dao.class)) {
            vu.validateMessage(member, RuntimeException.class);
            if (dao.updateMember(member) == 1) {
                List<Authority> oriauthories = dao.findAuthorityByAccount(member.getAccount());
                List<Authority> authories = member.getAuthorities();
                List<Authority> newauthories = new ArrayList<>();
                if (authories != null && !authories.isEmpty()) {
                    for (Authority authority : authories) {
                        if (!oriauthories.contains(authority)) {
                            vu.validateMessage(authority, RuntimeException.class);
                            authority.setAid(dao.newAuthority(authority));
                            newauthories.add(authority);
                            oriauthories.remove(authority);
                        } else {
                            newauthories.add(authority);
                            oriauthories.remove(authority);
                        }
                    }
                    for (Authority authority : oriauthories) {
                        dao.removeAuthority(authority.getAid());
                    }
                    member.setAuthorities(newauthories);
                } else {
                    dao.removeAuthories(member.getAccount());
                }
                return true;
            } else {
                return false;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = JdbiException.class)
    public int updatePass(String account, String oldPass, String newPass) throws Exception {
        try (Dao dao = getContext().getBean(Dao.class)) {
            Member original = dao.findMemberByPrimaryKey(account);
            if (original == null) {
                throw jdbiException(messageAccessor.getMessage("exception.userNotExists", account));
            } else {
                return dao.changePasswd(account, original.getPassword(), encoder.encode(newPass));
            }
        }
    }

}
