package com.vipa.medlabel.dto.response.group;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.vipa.medlabel.dto.response.SearchResult;
import com.vipa.medlabel.model.ImageGroup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GroupSearchResult extends SearchResult<ImageGroup> {
    private List<List<Integer>> imageStatus;
}
