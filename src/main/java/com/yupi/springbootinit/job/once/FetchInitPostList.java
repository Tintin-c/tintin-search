package com.yupi.springbootinit.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.PostService;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FetchInitPostList implements CommandLineRunner {

    @Resource
    PostService postService;

    @Override
    public void run(String... args) throws Exception {
        String json = "{\n" +
                "  \"sortField\": \"createTime\",\n" +
                "  \"sortOrder\": \"descend\",\n" +
                "  \"reviewStatus\": 1,\n" +
                "  \"current\": 1\n" +
                "}";
        String result = HttpRequest.post("https://www.code-nav.cn/api/post/list/page/vo")
                .body(json)
                .execute().body();

        Map resultMap = JSONUtil.toBean(result, Map.class);
        JSONObject data = (JSONObject) resultMap.get("data");
        JSONArray records = (JSONArray) data.get("records");

        List<Post> posts = new ArrayList<>();
        for (Object record : records) {
            JSONObject recordJSONObject = (JSONObject) record;

            Post post = new Post();
            // 进行非空校验
            Optional<Object> titleObj = Optional.ofNullable(recordJSONObject.get("title"));
            post.setTitle(titleObj.map(Object::toString).orElse(null));

            Optional<Object> contentObj = Optional.ofNullable(recordJSONObject.get("content"));
            post.setContent(contentObj.map(Object::toString).orElse(null));

            Optional<JSONArray> tagsObj = Optional.ofNullable(recordJSONObject.getJSONArray("tags"));
            post.setTags(tagsObj.map(JSONArray::toString).orElse(null));

            post.setUserId(1L);
            posts.add(post);
        }

        postService.saveBatch(posts);
    }
}
