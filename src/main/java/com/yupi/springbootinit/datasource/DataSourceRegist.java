package com.yupi.springbootinit.datasource;

import com.yupi.springbootinit.model.enums.SearchTypeEnum;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;

@Component
public class DataSourceRegist {

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private PictureDataSource pictureDataSource;

    private HashMap<String, DataSource<T>> typeDataSourceMap;

    @PostConstruct
    public void doInit(){
        typeDataSourceMap = new HashMap(){{
            // 将所有查找方式放入map，在根据条件获取一个，简化代码（原需要switch）
            put(SearchTypeEnum.USER.getValue(), userDataSource);
            put(SearchTypeEnum.POST.getValue(), postDataSource);
            put(SearchTypeEnum.PICTURE.getValue(), pictureDataSource);
        }};
    }

    public DataSource<T> getTypeDataSourceMap(String type){
        if (typeDataSourceMap == null){
            return null;
        }else {

            return typeDataSourceMap.get(type);
        }

    }


}
