package com.example.blog_project.service.impl;

import com.example.blog_project.domain.Draft;
import com.example.blog_project.domain.Post;
import com.example.blog_project.dto.PostDto;
import com.example.blog_project.repository.PostRepository;
import com.example.blog_project.service.LikeService;
import com.example.blog_project.service.PostService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final LikeService likeService;

    public void savePost(Post post) {
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void savePostByDraft(Draft draft) {
        Post post = new Post();
        post.setBlog(draft.getBlog());
        post.setTitle(draft.getTitle());
        post.setContent(draft.getContent());
        post.setCreatedAt(draft.getCreatedAt());
        post.setUpdatedAt(draft.getUpdatedAt());
        post.setSeries(draft.getSeries());
        post.setTagSet(draft.getTagSet());
        postRepository.save(post);
    }

    //이미지 태그를 제외한 나머지 태그 모두 제거하는 로직
//    public String sanitizePostContent(String content) {
//        Document document = Jsoup.parse(content);
//
//        // HTML 태그 제거하면서 이미지 태그 유지
//        Whitelist whitelist = Whitelist.none().addTags("img").addAttributes("img", "src");
//        String sanitizedContent = Jsoup.clean(document.html(), whitelist);
//
//        return sanitizedContent;
//    }
    @PersistenceContext
    private EntityManager entityManager;
    //이미지 태그 내용 제거 및 나머지 태그 제거
    public List<PostDto> extractPlainTextFromPosts(List<Post> posts) {
        List<PostDto> postDtoList = new ArrayList<>();
        for (Post post : posts) {
            PostDto postDto = new PostDto();


            String content = post.getContent();
            // Jsoup을 사용하여 HTML을 파싱합니다.
            Document doc = Jsoup.parse(content);
            System.out.println(222222);

            // 이미지 태그를 모두 제거합니다.
            Elements elements = doc.select("img");
            elements.remove();

            // 태그를 제거하고 순수한 텍스트만을 추출하여 리스트에 추가합니다.
            String plainText = doc.text();

            postDto.setId(post.getId());
            postDto.setTitle(post.getTitle());
            postDto.setContent(plainText);
            postDto.setCoverImage(elements.attr("src"));
            postDto.setCreatedAt(post.getCreatedAt());
            postDto.setBlogId(post.getBlog().getId());
            postDtoList.add(postDto);
        }

        return postDtoList;
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

    @Override
    public void deletePost(Long postId) {
        postRepository.deleteById(postId);
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public List<Post> searchPostsByBlogAndKeyword(Long blogId, String keyword) {
        return postRepository.searchPostsByBlogAndTitleOrContent(blogId, keyword);
    }

    public List<Post> findAllByOrderByCreatedAtDesc() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Post> findAllByOrderByLikes() {
        List<Post> posts = postRepository.findAll();
        posts.sort((p1, p2) -> likeService.getLikeByPostId(p2.getId()).size() - likeService.getLikeByPostId(p1.getId()).size());
        return posts;
    }

}
