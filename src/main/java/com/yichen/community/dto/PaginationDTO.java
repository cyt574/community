package com.yichen.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO<T> {
    private List<T> data;

    private boolean showPrevious;
    private boolean showNext;
    private boolean showFistPage;
    private boolean showEndPage;
    private Integer currentPage;
    private Integer totalPage;
    private List<Integer> pages = new ArrayList<>();

    //
    public void setPagination(Integer totalPage, Integer page) {
        this.totalPage = totalPage;
        this.currentPage = page;
        pages.add(page);
        for (int i = 1; i < 3; i++) {
            if(page - i > 0) {
                pages.add(0,page - i);
            }

            if(page + i <= totalPage) {
                pages.add(page + i);
            }
        }

        if (page == 1) {
            showPrevious = false;
        } else {
            showPrevious = true;
        }

        if (page == totalPage) {
            showNext = false;
        } else {
            showNext = true;
        }

        if(pages.contains(1)) {
            showFistPage = false;
        } else {
            showFistPage = true;
        }

        if(pages.contains(totalPage)) {
            showEndPage = false;
        } else {
            showEndPage = true;
        }
    }
}
