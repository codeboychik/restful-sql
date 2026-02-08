package com.example.restfulsql.service;

import com.example.restfulsql.api.Condition;
import com.example.restfulsql.api.CountRequest;
import com.example.restfulsql.api.Operator;
import com.example.restfulsql.web.InvalidQueryException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class CountServiceTest {

    @Autowired
    private CountService countService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void countsAllRowsWhenNoConditions() {
        CountRequest request = new CountRequest();
        request.setTable("customers");

        long count = countService.countRows(request);

        assertThat(count).isEqualTo(3L);
    }

    @Test
    void countsWithConditions() {
        CountRequest request = new CountRequest();
        request.setTable("orders");
        Condition condition = new Condition();
        condition.setColumn("status");
        condition.setOperator(Operator.EQ);
        condition.setValue(objectMapper.valueToTree("OPEN"));
        request.setConditions(List.of(condition));

        long count = countService.countRows(request);

        assertThat(count).isEqualTo(2L);
    }

    @Test
    void countsViewRows() {
        CountRequest request = new CountRequest();
        request.setTable("active_customers");

        long count = countService.countRows(request);

        assertThat(count).isEqualTo(2L);
    }

    @Test
    void countsWithInOperator() {
        CountRequest request = new CountRequest();
        request.setTable("customers");
        Condition condition = new Condition();
        condition.setColumn("status");
        condition.setOperator(Operator.IN);
        condition.setValue(objectMapper.valueToTree(List.of("ACTIVE")));
        request.setConditions(List.of(condition));

        long count = countService.countRows(request);

        assertThat(count).isEqualTo(2L);
    }

    @Test
    void rejectsUnknownTable() {
        CountRequest request = new CountRequest();
        request.setTable("missing_table");

        assertThatThrownBy(() -> countService.countRows(request))
            .isInstanceOf(InvalidQueryException.class)
            .hasMessageContaining("Unknown table");
    }

    @Test
    void rejectsUnknownColumn() {
        CountRequest request = new CountRequest();
        request.setTable("customers");
        Condition condition = new Condition();
        condition.setColumn("missing_column");
        condition.setOperator(Operator.EQ);
        condition.setValue(objectMapper.valueToTree("ACTIVE"));
        request.setConditions(List.of(condition));

        assertThatThrownBy(() -> countService.countRows(request))
            .isInstanceOf(InvalidQueryException.class)
            .hasMessageContaining("Unknown column");
    }
}
