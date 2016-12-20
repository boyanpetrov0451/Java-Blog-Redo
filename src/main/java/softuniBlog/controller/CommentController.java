package softuniBlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import softuniBlog.bindingModel.CommentBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.Comment;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.CommentRepository;
import softuniBlog.repository.UserRepository;

@Controller
public class CommentController {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/article/comment/create/{id}")
    @PreAuthorize("isAuthenticated()")
    public String comment(Model model, @PathVariable Integer id) {



        Article article = this.articleRepository.findOne(id);

        model.addAttribute("article", article);
        model.addAttribute("view", "article/comment/create");

        return "base-layout";
    }

    @PostMapping("/article/comment/create/{id}")
    @PreAuthorize("isAuthenticated()")
    public String createProcess(CommentBindingModel commentBindingModel, @PathVariable Integer id) {

        if (!this.articleRepository.exists(id)) {
            return "redirect:/";
        }

        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User userEntity = this.userRepository.findByEmail(user.getUsername());

        Article article = this.articleRepository.findOne(id);

        Comment comment = new Comment(commentBindingModel
                .getContent(),userEntity,
                article);

        this.commentRepository.saveAndFlush(comment);

        return "redirect:/article/" + id;
    }

    @GetMapping("/article/comment/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String edit(Model model, @PathVariable Integer id) {

        if (!this.commentRepository.exists(id)) {
            return "redirect:/";
        }

        Comment comment = commentRepository.findOne(id);
        Integer article = comment.getArticle().getId();

        if(!isUserAuthorOrAdmin(comment)) {

            return "redirect:/article/" + comment.getArticle().getId();
        }

        model.addAttribute("article", article);
        model.addAttribute("comment", comment);
        model.addAttribute("view", "article/comment/edit");

        return "base-layout";
    }

    @PostMapping("/article/comment/edit/{id}")
    @PreAuthorize("isAuthenticated()")
    public String editProcess(CommentBindingModel commentBindingModel, @PathVariable Integer id) {

        if (!this.commentRepository.exists(id)) {
            return "redirect:/";
        }

        Comment comment = this.commentRepository.findOne(id);

        if (!isUserAuthorOrAdmin(comment)) {
            return "redirect:/article/" + comment.getArticle().getId();
        }

        comment.setContent(commentBindingModel.getContent());

        this.commentRepository.saveAndFlush(comment);

        return "redirect:/article/" + comment.getArticle().getId();
    }

    @GetMapping("/article/comment/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String delete(Model model, @PathVariable Integer id) {
        if (!this.commentRepository.exists(id)) {
            return "redirect:/";
        }

        Comment comment = commentRepository.findOne(id);
        Integer article = comment.getArticle().getId();

        if(!isUserAuthorOrAdmin(comment)) {
            return "redirect:/article/" + comment.getArticle().getId();
        }

        model.addAttribute("article", article);
        model.addAttribute("comment", comment);
        model.addAttribute("view", "article/comment/delete");

        return "base-layout";
    }

    @PostMapping("/article/comment/delete/{id}")
    @PreAuthorize("isAuthenticated()")
    public String deleteProcess(@PathVariable Integer id) {

        if (!this.commentRepository.exists(id)) {
            return "redirect:/";
        }

        Comment comment = commentRepository.findOne(id);

        if (!isUserAuthorOrAdmin(comment)) {
            return "redirect:/article/" + comment.getArticle().getId();
        }

        this.commentRepository.delete(comment);

        return "redirect:/article/" + comment.getArticle().getId();
    }

    private boolean isUserAuthorOrAdmin(Comment comment) {

        UserDetails user = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User userEntity = this.userRepository.findByEmail(user.getUsername());

        return userEntity.isAdmin() || userEntity.isCommentAuthor(comment);
    }
}
