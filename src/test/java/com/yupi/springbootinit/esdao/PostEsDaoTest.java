package com.yupi.springbootinit.esdao;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryVariant;
import com.google.common.collect.Lists;

import com.yupi.springbootinit.model.dto.post.PostEsDTO;
import com.yupi.springbootinit.model.dto.post.PostQueryRequest;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.service.PostService;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

/**
 * 帖子 ES 操作测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
public class PostEsDaoTest {

    @Resource
    private PostEsDao postEsDao;

    @Resource
    private PostService postService;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    void test() {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Post> page =
                postService.searchFromEs(postQueryRequest);
        System.out.println(page);
    }

    @Test
    void testSelect() {
        System.out.println(postEsDao.count());
        Page<PostEsDTO> PostPage = postEsDao.findAll(
                PageRequest.of(0, 5, Sort.by("createTime")));
        List<PostEsDTO> postList = PostPage.getContent();
        System.out.println(postList);
    }

    @Test
    void testAdd() {
        PostEsDTO postEsDTO = new PostEsDTO();
        postEsDTO.setId(1L);
        postEsDTO.setTitle("测试Es");
        postEsDTO.setContent("这是在测试Es");
        postEsDTO.setTags(Arrays.asList("test", "Elasticsearch"));
        postEsDTO.setUserId(1L);
        postEsDTO.setCreateTime(new Date());
        postEsDTO.setUpdateTime(new Date());
        postEsDTO.setIsDelete(0);
        postEsDao.save(postEsDTO);
        System.out.println(postEsDTO.getId());
    }

    @Test
    void testFindById() {
        Optional<PostEsDTO> postEsDTO = postEsDao.findById(1L);
        System.out.println(postEsDTO);
    }

    @Test
    void testCount() {
        System.out.println(postEsDao.count());
    }

    @Test
    void testFindByCategory() {
        List<PostEsDTO> postEsDaoTestList = postEsDao.findByUserId(1L);
        System.out.println(postEsDaoTestList);
    }

    @Test
    void testDaoFindByTitle(){
        List<PostEsDTO> postlist = postEsDao.findByTitle("测EsTemplate");
        System.out.println(postlist);
    }

    @Test
    void testEsTemplate(){
        PostEsDTO postEsDTO = elasticsearchRestTemplate.get("1", PostEsDTO.class);
        System.out.println(postEsDTO);

        PostEsDTO post = new PostEsDTO();
        post.setId(2L);
        post.setTitle("测试EsTemplate");
        post.setContent("这个是在测试EsTemplate");
        post.setTags(Lists.newArrayList("template"));
        post.setUserId(0L);
        post.setCreateTime(new Date());
        post.setUpdateTime(new Date());
        post.setIsDelete(0);


        elasticsearchRestTemplate.save(post);
    }
    @Test
    void testEsTemplate2(){
        System.out.println(elasticsearchRestTemplate.get("2", PostEsDTO.class));
    }

}
