package com.mackena.Banking_Application_backend.dtos.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionHistoryResponse {

    private List<TransactionDetailResponse> transactionDetails;
    private int currentPage;
    private int totalPages;
    private int totalElements;
    private boolean hasNext;
    private boolean hasPrevious;
}
