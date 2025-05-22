package io.hhplus.tdd.point.controller.model;

import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class PointChargeRequest {
    private int amount;
}
