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

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public void savePost(Post post) {
        postRepository.save(post);
    }

    public String sanitizePostContent(String content) {
        Document document = Jsoup.parse(content);

        // 이미지 태그만 유지하고 나머지 태그 제거
        Elements images = document.select("img");
        for (Element img : images) {
            String originalSrc = img.attr("src");
            File imageFile = new File(originalSrc);  // 경로 그대로 사용

            if (imageFile.exists()) {
                String newSrc = "/local-image?filename=" + imageFile.getName();
                img.attr("src", newSrc);
            } else {
                img.attr("src", "/Users/jeonhyeonjin/blog_project/dev-jeans.png");
            }
        }

        // HTML 태그 제거하면서 이미지 태그 유지
        Whitelist whitelist = Whitelist.none().addTags("img").addAttributes("img", "src");
        String sanitizedContent = Jsoup.clean(document.html(), whitelist);

        return sanitizedContent;
    }

    // post.content에 저장되어있는 이미지 중에서 첫 번째 이미지 URL 추출하는 메서드
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
}
