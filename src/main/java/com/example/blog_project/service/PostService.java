package com.example.blog_project.service;

import com.example.blog_project.domain.Post;
import com.example.blog_project.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public void savePost(Post post) {
        postRepository.save(post);
    }

    //이미지 태그를 제외한 나머지 태그 모두 제거하는 로직
    public String sanitizePostContent(String content) {
        Document document = Jsoup.parse(content);

        // HTML 태그 제거하면서 이미지 태그 유지
        Whitelist whitelist = Whitelist.none().addTags("img").addAttributes("img", "src");
        String sanitizedContent = Jsoup.clean(document.html(), whitelist);

        return sanitizedContent;
    }

    // post.content에 저장되어있는 이미지 중에서 첫 번째 이미지 scr 추출하는 로직
    public String extractFirstImageUrl(String content) {
        Document doc = Jsoup.parse(content);
        Elements imgTags = doc.select("img");

        if (!imgTags.isEmpty()) {
            Element firstImg = imgTags.first();
            return firstImg.attr("src");
        }

        // 이미지가 없는 경우 null을 반환한다
        return null;
    }

//    public Post getPostById(String postId) {
//        return postRepository.findById(Integer.valueOf(postId)).orElseGet(null);
//    }

    public Post getPostByBlogIdAndPostId(Long blogId, Long postId) {
        return postRepository.findByBlogIdAndId(blogId, postId);
    }

    public List<Post> getPostBySeriesId(Long seriesId) {
        return postRepository.findBySeriesId(seriesId);
    }

    public Post getPostById(Long postId) {
        return postRepository.findById(postId).orElse(null);
    }
}
