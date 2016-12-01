package com.msun.luckyBlog.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.msun.luckyBlog.persistence.domain.BlogView;
import com.msun.luckyBlog.persistence.domain.Info;
import com.msun.luckyBlog.persistence.service.*;

/**
 * 前端页面显示的控制器 共包括archives,login,projects,tags,about,post,login这几个页面
 */
@Controller
public class FontEndController {

    @Autowired
    private BlogSer       blogSer;
    @Autowired
    private ProjectSer    projectSer;
    @Autowired
    private InfoSer       infoSer;
    @Autowired
    private FileUploadSer fileUploadSer;

    @GetMapping("/archives/{page}")
    public String archives(@PathVariable int page, Model model) {
        model.addAttribute("info", infoSer.getInfo());
        model.addAttribute("archives", blogSer.getArchive(page));
        model.addAttribute("pageNum", blogSer.getArchiveNum());
        model.addAttribute("current", page);
        return "archives";
    }

    @GetMapping("/projects")
    public String projects(Model model) {
        model.addAttribute("info", infoSer.getInfo());
        return "projects";
    }

    @GetMapping("/projects/{page}")
    public String projectPage(@PathVariable int page, Model model) {
        model.addAttribute("info", infoSer.getInfo());
        model.addAttribute("projects", projectSer.getPros(page));
        model.addAttribute("pageNum", projectSer.getPageNum());
        model.addAttribute("current", page);
        return "projects";
    }

    @GetMapping("/tags")
    public String tags(Model model) {
        model.addAttribute("info", infoSer.getInfo());
        model.addAttribute("tags", blogSer.getTagList());
        return "tags";
    }

    @GetMapping("/tags/{tagName}")
    public String tagName(@PathVariable String tagName, Model model) {
        model.addAttribute("info", infoSer.getInfo());
        List<BlogView> views = blogSer.getBlogByTag(tagName);
        model.addAttribute("views", views);
        model.addAttribute("tagName", tagName);
        return "tagView";
    }

    @GetMapping("/about")
    public String about(Model model, HttpServletResponse response) {
        model.addAttribute("info", infoSer.getInfo());
        model.addAttribute("resume", infoSer.getResumeView());
        return "about";
    }

    @GetMapping("/post/{id}")
    public String post(@PathVariable int id, Model model) {
        model.addAttribute("info", infoSer.getInfo());
        BlogView blogView = blogSer.getBlog(id);
        BlogView prev = blogSer.getPrevBlog(id);
        BlogView next = blogSer.getNextBlog(id);
        model.addAttribute("prev", prev);
        model.addAttribute("next", next);
        model.addAttribute("article", blogView.getArticle());
        return "post";
    }

    @GetMapping("/")
    public String welcome(Model model) {
        model.addAttribute("info", infoSer.getInfo());
        return "index";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, Model model) {
        model.addAttribute("avatar", infoSer.getInfo().getAvatar());
        String result = request.getParameter("result");
        if (result != null && result.equals("fail")) {
            model.addAttribute("success", 0);
        }
        return "login";
    }

    @PostMapping("/login.action")
    public String doLogin(Info user, HttpServletRequest request) {
        boolean result = infoSer.login(user);
        if (result) {
            infoSer.addSession(request, user);
            return "redirect:/admin";
        } else {
            return "redirect:/login?result=fail";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        infoSer.destroySession(request);
        return "redirect:/login";
    }

    @GetMapping(value = "/pic/{dir}/{picName:.+}")
    public ResponseEntity<byte[]> gainUserAvatar(@PathVariable String dir, @PathVariable String picName)
                                                                                                        throws RuntimeException {
        return fileUploadSer.gainPic(dir, picName);
    }
}