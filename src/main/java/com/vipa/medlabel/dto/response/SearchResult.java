package com.vipa.medlabel.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResult<T> {
    private List<T> content;
    private Boolean last;
    private Boolean first;
    private Integer totalPages;
    private Long totalElements;
    private Integer page;
    private Integer size;
    private Boolean empty;
    private Boolean isPaged;

    public void setPageInfo(Integer page, Integer size, Integer totalPages, Long totalElements, Boolean first,
            Boolean last, Boolean empty) {
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.first = first;
        this.last = last;
        this.empty = empty;
        this.isPaged = true;
    }
}
