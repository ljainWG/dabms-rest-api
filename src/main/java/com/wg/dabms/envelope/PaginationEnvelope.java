package com.wg.dabms.envelope;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginationEnvelope<T> {
	
	private List<T> content;
	private Integer currentPageNo;
	private Integer totalNoOfRecords;
	private Integer totalNoOfPages;
	private Integer recordsPerPage;
	
}
