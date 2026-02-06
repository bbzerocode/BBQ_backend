package com.bbqpos.backend.dto.finance;

import javax.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceDto {

    @NotNull
    private BigDecimal balance;

}
