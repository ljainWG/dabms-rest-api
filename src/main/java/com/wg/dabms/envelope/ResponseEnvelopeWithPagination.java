package com.wg.dabms.envelope;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wg.dabms.api_response.ApiResponseStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseEnvelopeWithPagination {
    
    @JsonProperty("status")
    private ApiResponseStatus status;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("data")
    private Object data;
    
    @JsonProperty("error")
    private Object error;
    
    @JsonProperty("current_page_no")
    private Integer currentPageNo;
    
    @JsonProperty("total_no_of_records")
    private Integer totalNoOfRecords;
    
    @JsonProperty("total_no_of_pages")
    private Integer totalNoOfPages;
    
    @JsonProperty("records_per_page")
    private Integer recordsPerPage;
}
