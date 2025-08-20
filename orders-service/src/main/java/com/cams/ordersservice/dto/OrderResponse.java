package com.cams.ordersservice.dto;

import java.math.BigDecimal;

public record OrderResponse(Long orderItemId, String product, Integer qty, BigDecimal total) {}
