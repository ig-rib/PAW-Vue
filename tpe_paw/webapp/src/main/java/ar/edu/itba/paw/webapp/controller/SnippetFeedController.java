package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.models.Snippet;
import ar.edu.itba.paw.models.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class SnippetFeedController {

    @RequestMapping("/feed")
    public ModelAndView getHomeSnippetFeed() {
        final ModelAndView mav = new ModelAndView("snippet/snippetFeed");
        User u = new User("lollipop","pass", "myemail", "im a user", new Date());
        List<Snippet> snippetsHomeFeed = Arrays.asList(
                new Snippet(1, u, "CODIGO", "TEST CARD", "$normal: the default. The browser will break lines according to normal line breaking rules. Words or unbroken strings will not break, even if they overflow the container.normal: the default. The browser will break lines according to normal line breaking rules. Words or unbroken strings will not break, even if they overflow the container."),
                new Snippet(2, u, "This means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the conThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the conThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the conThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The This means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\nThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\nThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\nThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\nThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\nThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being tThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\noo large to create excess white space.\nThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\n ROKS", "HEy yo", ""),
                new Snippet(2, u, "JAVA ROKS", "TEST CARDS", "This means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\n"),
                new Snippet(2, u, "JAVA ROKS", "<script type=\"text/javascript\">\n$(document).ready(function() {\n$('.slideshow').cycle({\n", "This means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\nThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\nThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\nThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\n"),
                new Snippet(2, u, "JAVA ROKS", "TEST CARDS", ""),
                new Snippet(2, u, "<script type=\"text/javascript\">\n$(document).ready(function() {\n$('.slideshow').cycle({\n" +
                        "fx: 'fade' // choose your transition type, ex: fade, scrollUp, shuffle, etc...\n" +
                        "});\n" +
                        "});\n" +
                        "</script>Making this one a little longer \n doing this okaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaay", "TEST CARDS", ""),
                new Snippet(2, u, "At the moment the rows are automatically sizing to allow the largest items in each row to be fully displayed. We will instead restrict the row sizes to a predetermined height:\n", "TEST CARDS", ""),
                new Snippet(2, u, "JAVA ROKS", "TEST CARDS", ""),
                new Snippet(2, u, "This means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the conThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the conThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the conThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the conJAVA This means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\n", "TEST CARDS", "This means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\n"),
                new Snippet(2, u, "JAVA This means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\n", "TEST CARDS", ""),
                new Snippet(2, new User("lollipop","pass", "myemail", "im a user", new Date()), "JAVA ROKS", "TEST CARDS", "At the moment the rows are automatically sizing to allow the largest items in each row to be fully displayed. We will instead restrict the row sizes to a predetermined height:\n"),
                new Snippet(2, new User("lollipop","pass", "myemail", "im a user", new Date()), "JAVA ROKS", "TEST CARDS", "<script type=\\\"text/javascript\\\">\\n$(document).ready(function() {\\n$('.slideshow').cycle({\\n\" +\n" +
                        "                        \"fx: 'fade' // choose your transition type, ex: fade, scrollUp, shuffle, etc...\\n\" +\n" +
                        "                        \"});\\n\" +\n" +
                        "                        \"});\\n\" +\n" +
                        "                        \"</script>\""),
                new Snippet(2, new User("lollipop","pass", "myemail", "im a user", new Date()), "JAVA ROKS", "TEST CARDS", ""),
                new Snippet(2, new User("lollipop","pass", "myemail", "im a user", new Date()), "JAVA ROKS", "TEST This means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\nThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\n", "This means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\nThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\nThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\nThis means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\n"),
                new Snippet(2, new User("lollipop","pass", "myemail", "im a user", new Date()), "JAVA ROKS", "TEST CARDS", "This means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\n"),
                new Snippet(2, new User("lollipop","pass", "myemail", "im a user", new Date()), "JAVA ROKS", "This means we will be able to compare the height of this “content” div with the height of the grid item containing it. The aim will be to make the grid item just large enough to display all the content without being too large to create excess white space.\n", "")
        );

        mav.addObject("snippetList", snippetsHomeFeed);
        return mav;
    }
}
