package com.yupi.springbootinit.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.model.dto.post.PostQueryRequest;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.model.vo.PostVO;
import com.yupi.springbootinit.service.PostService;
import com.yupi.springbootinit.service.impl.PostServiceImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Component
public class PostDataSource implements DataSource{

    @Resource
    PostService postService;

    @Resource
    PostServiceImpl postServiceImpl;

    @Override
    public Page<PostVO> doSearch(String searchText, long pageNum, long pageSize) {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        postQueryRequest.setSearchText(searchText);
        postQueryRequest.setPageSize(pageSize);
        postQueryRequest.setCurrent(pageNum);
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
//        Page<PostVO> postVOPage = postService.listPostVOByPage(postQueryRequest, request);
        // 从es中查询
        Page<Post> postPage = postServiceImpl.searchFromEs(postQueryRequest);
        Page<PostVO> postVOPage = postService.getPostVOPage(postPage, request);

        return postVOPage;
    }
}
