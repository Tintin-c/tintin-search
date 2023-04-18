package com.yupi.springbootinit;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.PostService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
public class CrawlerTest {

    @Resource
    PostService postService;

    @Test
    public void crawlerPicture(){
        String searchText = "wrr";
        String currentPage = "1";
        Document doc = null;
        try {
//            String.format("https://www.bing.com/images/search?q=%s&first=%s", searchText, currentPage)
            doc = Jsoup.connect("https://cn.bing.com/images/search?q=wrr&first=1").get();

            // 在.iuscp.isv下获取murl地址
            Elements newsHeadlines = doc.select(".iuscp.isv");
            // 遍历获取单个图片地址
            for (Element headline : newsHeadlines) {
                // 图片存在m中
                String m = headline.select(".iusc").get(0).attr("m");
                // 转换成map便于获取
                Map<String, Object> map = JSONUtil.toBean(m, Map.class);
                String murl = (String) map.get("murl");
                System.out.println(murl);

                // 获取标题 infopt
                String inflnk = headline.select(".inflnk").get(0).attr("aria-label");
                System.out.println(inflnk);
            }

        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取图片失败");
        }

    }

    @Test
    public void crawlerPage(){
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
