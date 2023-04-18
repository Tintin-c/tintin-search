package com.yupi.springbootinit.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

@Data
public class PictureQueryRequest implements Serializable {

    long current;
    long pageSize;
    String searchText;

    private static final long serialVersionUID = 1L;
}
