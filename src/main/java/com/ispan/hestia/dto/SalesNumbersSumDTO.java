package com.ispan.hestia.dto;

import java.util.List;

public record SalesNumbersSumDTO(List<SalesNumbersDTO> salesNumber, Long totalSalesSum, Long totalCountSum) {

}
