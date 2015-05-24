package com.github.kentyeh.controller;

import com.github.kentyeh.context.CustomUserInfo;
import com.github.kentyeh.manager.MemberManager;
import com.github.kentyeh.model.Member;
import java.security.Principal;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Kent Yeh
 */
@Controller
public class DefaultController {

    private static final Logger logger = LogManager.getLogger(DefaultController.class);
    @Autowired
    private MemberManager memberManager;

    @RequestMapping("/")
    public String root(Device device, Model model) {
        if (device.isMobile()) {
            logger.debug("Connect devcie is mobile");
            model.addAttribute("device", "mobile");
            //return "mobileIndex";
        } else if (device.isTablet()) {
            logger.debug("Connect devcie is tablet");
            model.addAttribute("device", "tablet");
            //return "tabletIndex";
        } else {
            model.addAttribute("device", "normal");
            logger.debug("Connect device is normal device.");
        }
        return "index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String login(Model model) throws Exception {
        model.addAttribute("members", memberManager.findAvailableUsers());
        return "login";
    }

    private String getPrincipalId(Principal principal) {
        Object p = principal instanceof AbstractAuthenticationToken ? ((AbstractAuthenticationToken) principal).getPrincipal() : principal;
        return p instanceof CustomUserInfo ? ((CustomUserInfo) p).getUsername() : principal.getName();
    }

    /**
     * Response all users' data as json.
     *
     * @return
     * @throws java.lang.Exception
     */
    @RequestMapping(value = "/admin/users", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String listuser() throws Exception {
        List<Member> users = memberManager.findAllUsers();
        if (users == null || users.isEmpty()) {
            return "{\"total\":0,\"users\":[]}";
        } else {
            StringBuilder sb = new StringBuilder("{\"total\":").append(users.size()).append(",\"users\":[");
            boolean isFirst = true;
            for (Member user : users) {
                if (!isFirst) {
                    sb.append(",");
                }
                sb.append("{\"account\":\"").append(user.getAccount()).append("\",\"name\":\"").append(user.getName())
                        .append("\",\"birthday\":\"").append(String.format("%tF", user.getBirthday())).append("\"}");
                isFirst = false;
            }
            sb.append("]}");
            return sb.toString();
        }
    }

    /**
     * Member's editor form.
     *
     * @param member
     * @param model
     * @return
     */
    @RequestMapping(value = "/member/edit/{member}", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    public String editMember(@PathVariable Member member, Model model) {
        model.addAttribute("member", member);
        return "memberEditor";
    }

    /**
     * update member data.
     *
     * @param member
     * @param model
     * @return
     */
    @RequestMapping(value = "/member/update", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String updateMember(@Valid @ModelAttribute Member member, Model model) {
        try {
            model.addAttribute("member", member);
            memberManager.updateMember(member);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            model.addAttribute("errorMsg", ex.getMessage());
        }
        return "memberEditor";
    }

    /**
     * display current user's info.
     *
     * @param request
     * @param principal
     * @return
     * @throws java.lang.Exception
     */
    @RequestMapping("/user/myinfo")
    public String myinfo(HttpServletRequest request, Principal principal) throws Exception {
        UsernamePasswordAuthenticationToken upat = (UsernamePasswordAuthenticationToken) principal;
        CustomUserInfo cui = (CustomUserInfo) upat.getPrincipal();
        request.setAttribute("member", cui.getMember());
        //Alternative approach,另外一種作法
        //request.setAttribute("member", memberManager.findMemberByPrimaryKey(getPrincipalId(principal)));
        return "index";
    }

    /**
     * Only adminstrator could display any user's info.
     *
     * @param account
     * @param request
     * @return
     */
    @RequestMapping("/admin/user/{account}")
    public String userinfo(@PathVariable Member account, HttpServletRequest request) {
        if (account != null) {
            request.setAttribute("member", account);
        }
        return "index";
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllException(Exception ex) {

        ModelAndView model = new ModelAndView("error");
        model.addObject("exception", ex);

        return model;

    }
}
