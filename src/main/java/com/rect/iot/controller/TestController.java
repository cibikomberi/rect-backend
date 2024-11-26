package com.rect.iot.controller;

import org.springframework.web.bind.annotation.RestController;

import com.rect.iot.model.Edge;
import com.rect.iot.model.Flow;
import com.rect.iot.repository.FlowRepo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class TestController {

    @Autowired
    FlowRepo flowRepo;

    @GetMapping("/test")
    public String getMethodName() {
        Edge e1 = new Edge();
        Edge e2 = new Edge();
        e1.setId("2L");
        e2.setId("4L");
        List<Edge> e = new ArrayList<>();
        e.add(e2);
        e.add(e1);
        Flow flow = Flow.builder().id(1L).edges(e).build();
        flowRepo.save(flow);
        return new String("ok");
    }

}
