package com.yupi.springbootinit.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.service.PictureService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PictureServicrImpl implements PictureService {
    @Override
    public Page<Picture> searchPicture(String searchText, long pageNum, long pageSize) {
        long current = (pageNum - 1) * pageSize;
        Document doc = null;
        List<Picture> pictureList;
        try {
            doc = Jsoup.connect(String.format("https://cn.bing.com/images/search?q=%s&first=%s", searchText, current)).get();

            pictureList = new ArrayList<>();
            // 在.iuscp.isv下获取murl地址
            Elements newsHeadlines = doc.select(".iuscp.isv");
            // 遍历获取单个图片地址
            for (Element headline : newsHeadlines) {
                Picture picture = new Picture();
                // 获取图片地址
                String m = headline.select(".iusc").get(0).attr("m");
                Map<String, Object> map = JSONUtil.toBean(m, Map.class);
                String murl = (String) map.get("murl");
                picture.setUrl(murl);

                // 获取标题
                String inflnk = headline.select(".inflnk").get(0).attr("aria-label");
                picture.setTitle(inflnk);

                pictureList.add(picture);
            }

        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取图片失败");
        }

        Page<Picture> page = new Page<>(pageNum, pageSize);
        page.setRecords(pictureList);
        return page;
    }
}
