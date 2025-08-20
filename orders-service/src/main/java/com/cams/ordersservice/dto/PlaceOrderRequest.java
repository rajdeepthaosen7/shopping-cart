package com.cams.ordersservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlaceOrderRequest(
        @NotNull Long productId,
        @NotNull @Min(1) Integer qty
) {}
