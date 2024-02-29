package entity;

import com.alibaba.dashscope.aigc.generation.GenerationResult;
import io.reactivex.Flowable;
import lombok.Data;

@Data
public class StreamResult {
    String streamResultText;
    Flowable<GenerationResult> resultFlux;
}