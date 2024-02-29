package entity;

import lombok.Data;

@Data
public class JsonRequest {
    // 指明需要调用的模型，固定值(wanx-background-generation-v2)
    private String model;
    private Input input;
    private Parameters parameters;
}
